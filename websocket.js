const socket = new WebSocket("ws://localhost:8080/ws");
const form = document.querySelector("[textfeld]");
const input = document.querySelector("[textfeldinput]");
const messages = document.querySelector("[nachrichten]");
const closeButton = document.querySelector("[socketbeenden]");
const sender = document.querySelector("h1")?.textContent?.trim() || "???";
const HEARTBEAT_TOKEN = "__heartbeat__";
const HEARTBEAT_TIMEOUT_MS = 10000;

let lastServerSignal = Date.now();
const heartbeatWatchdog = setInterval(() => {
  if (socket.readyState !== WebSocket.OPEN) {
    return;
  }

  if (Date.now() - lastServerSignal > HEARTBEAT_TIMEOUT_MS) {
    console.error("WebSocket-Verbindung verloren (Heartbeat-Timeout)");
    socket.close(4000, "Heartbeat timeout");
  }
}, 5000);

function addMessage(text) {
  messages.insertAdjacentHTML("beforeend", `<li>${text}</li>`);
}

socket.onopen = () => {
  lastServerSignal = Date.now();
  console.log("WebSocket verbunden");
};
socket.onmessage = (event) => {
  lastServerSignal = Date.now();

  if (event.data === HEARTBEAT_TOKEN) {
    return;
  }

  addMessage(event.data);
};
socket.onerror = (event) => console.error("WebSocket-Fehler", event);
socket.onclose = (event) => {
  clearInterval(heartbeatWatchdog);
  console.error(
    `WebSocket-Verbindung geschlossen (Code: ${event.code}, Grund: ${event.reason || "-"})`
  );
};

if (closeButton) {
  closeButton.addEventListener("click", () => {
    if (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING) {
      socket.close(1000, "Manuell durch Nutzer beendet");
    }
  });
}

form.addEventListener("submit", (event) => {
  event.preventDefault();

  const text = input.value.trim();
  if (!text || socket.readyState !== WebSocket.OPEN) {
    return;
  }

  socket.send(`${sender}: ${text}`);
  input.value = "";
  input.focus();
});
