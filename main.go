package main

import (
	"log"
	"net/http"
	"time"

	"github.com/gorilla/websocket"
)

type client struct {
	conn *websocket.Conn
	send chan []byte
}

type hub struct {
	register   chan *client
	unregister chan *client
	broadcast  chan []byte
	clients    map[*client]struct{}
}

func newHub() *hub {
	return &hub{
		register:   make(chan *client),
		unregister: make(chan *client),
		broadcast:  make(chan []byte),
		clients:    make(map[*client]struct{}),
	}
}

func (h *hub) run() {
	for {
		select {
		case c := <-h.register:
			h.clients[c] = struct{}{}
		case c := <-h.unregister:
			if _, ok := h.clients[c]; ok {
				delete(h.clients, c)
				close(c.send)
			}
		case msg := <-h.broadcast:
			for c := range h.clients {
				select {
				case c.send <- msg:
				default:
					delete(h.clients, c)
					close(c.send)
				}
			}
		}
	}
}

var (
	upgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
		CheckOrigin:     func(r *http.Request) bool { return true },
	}
	chatHub = newHub()
)

const (
	heartbeatInterval = 10 * time.Second
	heartbeatPayload  = "__heartbeat__"
)

func setupRoutes() {
	http.Handle("/ws", http.HandlerFunc(wsEndpoint))
	http.Handle("/", http.FileServer(http.Dir(".")))
}

func readPump(h *hub, c *client) {
	defer func() {
		h.unregister <- c
		_ = c.conn.Close()
	}()

	for {
		_, message, err := c.conn.ReadMessage()
		if err != nil {
			return
		}
		h.broadcast <- message
	}
}

func writePump(c *client) {
	ticker := time.NewTicker(heartbeatInterval)
	defer func() {
		ticker.Stop()
		_ = c.conn.Close()
	}()

	for {
		select {
		case message, ok := <-c.send:
			if !ok {
				return
			}
			if err := c.conn.WriteMessage(websocket.TextMessage, message); err != nil {
				return
			}
		case <-ticker.C:
			if err := c.conn.WriteMessage(websocket.TextMessage, []byte(heartbeatPayload)); err != nil {
				return
			}
		}
	}
}

func wsEndpoint(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println("websocket upgrade:", err)
		return
	}

	c := &client{
		conn: conn,
		send: make(chan []byte, 16),
	}

	chatHub.register <- c
	go writePump(c)
	readPump(chatHub, c)
}

func main() {
	go chatHub.run()

	setupRoutes()
	log.Println("Websocket POC läuft auf http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
