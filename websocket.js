let socket = new WebSocket("ws://localhost:8080/ws");
console.log("Websocket-Verbindungsversuch")
socket.onopen = () => {
    console.log("Websocket-Verbindung erfolgreich");
    socket.send("Hallo vom Clienten!")
}
socket.onclose = (event) => {
    console.log("Websocket-Verbindung geschlossen", event)
}
socket.onmessage = (msg) => {
    console.log(msg);
}
socket.onerror = (error) => {
    console.log("Websocket-Fehler", error);
}
