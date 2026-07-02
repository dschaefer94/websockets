# Go-Funktionen (`main.go`)

- `newHub() *hub`  
  Erstellt und initialisiert den zentralen Chat-Hub mit `register`, `unregister`, `broadcast` und `clients`.

- `(h *hub) run()`  
  Endlosschleife des Hubs: verarbeitet neue Clients, entfernt getrennte Clients und verteilt eingehende Nachrichten an alle.

- `setupRoutes()`  
  Registriert HTTP-Routen: WebSocket-Endpunkt unter `/ws` und statische Dateien unter `/`.

- `readPump(h *hub, c *client)`  
  Liest laufend Nachrichten von einem Client und leitet sie in den Broadcast-Channel des Hubs weiter.

- `writePump(c *client)`  
  Sendet alle Nachrichten aus `c.send` als WebSocket-Textnachrichten an den Client.

- `wsEndpoint(w http.ResponseWriter, r *http.Request)`  
  Führt WebSocket-Upgrade durch, erstellt den Client, registriert ihn im Hub und startet die Sende-/Empfangslogik.

- `CheckOrigin: func(r *http.Request) bool` (im `upgrader`)  
  Callback zur Origin-Prüfung beim Upgrade; aktuell wird jede Origin erlaubt (`true`).

- `main()`  
  Programmstart: startet den Hub, richtet Routen ein und startet den HTTP-Server auf Port `8080`.

