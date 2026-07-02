const socket = new WebSocket("ws://localhost:8080/ws");
const form = document.querySelector("[textfeld]");
const input = document.querySelector("[textfeldinput]");
const messages = document.querySelector("[nachrichten]");
const sender = document.querySelector("h1")?.textContent?.trim() || "???";

function addMessage(text) {
  messages.insertAdjacentHTML("beforeend", `<li>${text}</li>`);
}

socket.onopen = () => console.log("WebSocket verbunden");
socket.onmessage = (event) => addMessage(event.data);
socket.onerror = () => console.error("WebSocket-Fehler");
socket.onclose = () => console.error("WebSocket-Verbindung geschlossen");

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
