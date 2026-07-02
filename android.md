# MainActivity.java - Funktionsbeschreibung

## Übersicht
Die `MainActivity` ist die Hauptaktivität der Android WebSocket Gruppenchat-Anwendung. Sie verwaltet die WebSocket-Verbindung zum Go-Server und ermöglicht Benutzern, Nachrichten zu senden und zu empfangen.

---

## Funktionen

### `onCreate(Bundle savedInstanceState)`
**Zweck:** Initialisiert die Activity beim Starten der App.

**Ablauf:**
- Setzt das Layout aus `activity_main` XML ein
- Initialisiert UI-Komponenten (EditText für Nachrichteninput, TextView für Nachrichtengeschichte, Button zum Senden)
- Erstellt einen `StringBuilder` zur Speicherung der Nachrichtenhistorie
- Initialisiert einen `Handler` für die sichere Kommunikation mit dem UI-Thread
- Registriert einen OnClickListener für den Send-Button
- Ruft `connectWebSocket()` auf, um die WebSocket-Verbindung herzustellen

---

### `connectWebSocket()`
**Zweck:** Stellt eine WebSocket-Verbindung zum Go-Server her und registriert einen Listener für WebSocket-Events.

**Ablauf:**
1. Erstellt einen `OkHttpClient` für HTTP/WebSocket-Requests
2. Erstellt einen `Request` mit der URL `ws://192.168.0.109:8080/ws`
3. Definiert einen `WebSocketListener` mit vier Callbacks:
   - **`onOpen()`:** Wird aufgerufen, wenn die Verbindung erfolgreich hergestellt ist
   - **`onMessage()`:** Empfängt Nachrichten vom Server; filtert Heartbeat-Pakete (`__heartbeat__`) und fügt echte Nachrichten ins Log
   - **`onFailure()`:** Wird aufgerufen bei Verbindungsfehlern
   - **`onClosed()`:** Wird aufgerufen, wenn die Verbindung geschlossen wird
4. Speichert die WebSocket-Instanz in der Klassenvariable `webSocket`

---

### `addMessageToLog(String message)`
**Zweck:** Fügt empfangene Nachrichten zur Nachrichtenhistorie hinzu und aktualisiert die UI.

**Ablauf:**
- Hängt die neue Nachricht mit Zeilenumbruch an `messageLog` an
- Postet eine Runnable auf dem Main-Thread (UI-Thread), die:
  - Den aktuellen Nachrichtenlog im `messagesTextView` anzeigt
  - Gewährleistet, dass UI-Änderungen vom Main-Thread aus erfolgen

**Wichtig:** Verwendet `mainHandler.post()` für Thread-Sicherheit, da Netzwerk-Callbacks auf einem Hintergrund-Thread ausgeführt werden.

---

### `onDestroy()`
**Zweck:** Bereinigt Ressourcen, wenn die Activity zerstört wird.

**Ablauf:**
- Prüft, ob `webSocket` nicht null ist
- Schließt die WebSocket-Verbindung mit Status-Code `1000` (normaler Abschluss) und der Nachricht "App closed"

---

## Klassenvariablen

| Variable | Typ | Beschreibung |
|----------|-----|-------------|
| `webSocket` | `WebSocket` | Die aktive WebSocket-Verbindung zum Server |
| `messageEditText` | `EditText` | Eingabefeld für neue Nachrichten |
| `messagesTextView` | `TextView` | Anzeige aller empfangenen Nachrichten |
| `sendButton` | `Button` | Button zum Senden von Nachrichten |
| `messageLog` | `StringBuilder` | Speichert die komplette Nachrichtenhistorie |
| `mainHandler` | `Handler` | Verwaltet Kommunikation mit dem UI-Thread |
| `WS_URL` | String (static final) | WebSocket-URL: `ws://192.168.0.109:8080/ws` |
| `HEARTBEAT_PAYLOAD` | String (static final) | Marker für Heartbeat-Pakete: `__heartbeat__` |

---

## Nachrichtenfluss

1. **Senden:** Benutzer gibt Text ein → klickt Send-Button → Nachricht wird als `"Jeanette: <text>"` zum Server gesendet → EditText wird geleert
2. **Empfangen:** Server sendet Nachricht → `onMessage()` wird aufgerufen → Nachricht wird mit `addMessageToLog()` ins TextView geschrieben
3. **Heartbeat:** Server sendet periodisch `__heartbeat__` Pakete → werden gefiltert und nicht angezeigt

---

## Dependencies
- **OkHttp3:** Für WebSocket-Client-Funktionalität
- **AndroidX:** Für moderne Android-Kompatibilität
