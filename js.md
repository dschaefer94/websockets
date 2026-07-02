# JavaScript-Funktionen (`websocket.js`)

- `heartbeatWatchdog` (Intervall-Callback)  
  Prueft periodisch, ob seit `HEARTBEAT_TIMEOUT_MS` noch ein Server-Signal einging; bei Timeout wird der Socket mit Grund geschlossen.

- `addMessage(text)`  
  Fuegt eine empfangene Nachricht als neues `<li>` in die Nachrichtenliste ein.

- `socket.onopen = () => ...`  
  Event-Handler, der beim erfolgreichen Aufbau der WebSocket-Verbindung den Zeitstempel aktualisiert und eine Log-Meldung ausgibt.

- `socket.onmessage = (event) => ...`  
  Event-Handler fuer eingehende Nachrichten; aktualisiert den Zeitstempel, ignoriert Heartbeat-Nachrichten (`__heartbeat__`) und zeigt normale Nachrichten an.

- `socket.onerror = () => ...`  
  Event-Handler bei WebSocket-Fehlern; schreibt eine Fehlermeldung in die Konsole.

- `socket.onclose = () => ...`  
  Event-Handler beim Schliessen der WebSocket-Verbindung; stoppt den Watchdog und protokolliert Code/Grund des Verbindungsabbruchs.

- `closeButton.addEventListener("click", () => { ... })`  
  Klick-Handler fuer den Button `[socketbeenden]`; beendet die Verbindung manuell per `socket.close(...)`.

- `form.addEventListener("submit", (event) => { ... })`  
  Submit-Handler des Formulars: verhindert Reload, prueft Eingabe/Verbindungsstatus und sendet die Nachricht als `${sender}: ${text}` ueber den Socket.

