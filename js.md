# JavaScript-Funktionen (`websocket.js`)

- `addMessage(text)`  
  Fuegt eine empfangene Nachricht als neues `<li>` in die Nachrichtenliste ein.

- `socket.onopen = () => ...`  
  Event-Handler, der beim erfolgreichen Aufbau der WebSocket-Verbindung eine Log-Meldung ausgibt.

- `socket.onmessage = (event) => ...`  
  Event-Handler fuer eingehende WebSocket-Nachrichten; uebergibt den Text an `addMessage`.

- `socket.onerror = () => ...`  
  Event-Handler bei WebSocket-Fehlern; schreibt eine Fehlermeldung in die Konsole.

- `socket.onclose = () => ...`  
  Event-Handler beim Schliessen der WebSocket-Verbindung; protokolliert den Verbindungsabbruch.

- `form.addEventListener("submit", (event) => { ... })`  
  Submit-Handler des Formulars: verhindert Reload, prueft Eingabe/Verbindungsstatus und sendet die Nachricht als `${sender}: ${text}` ueber den Socket.

