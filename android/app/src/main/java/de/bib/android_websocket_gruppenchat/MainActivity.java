package de.bib.android_websocket_gruppenchat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {

    private WebSocket webSocket;
    private EditText messageEditText;
    private TextView messagesTextView;
    private Button sendButton;
    private StringBuilder messageLog;
    private Handler mainHandler;

    private static final String WS_URL = "ws://192.168.0.109:8080/ws";
    private static final String HEARTBEAT_PAYLOAD = "__heartbeat__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        messageEditText = findViewById(R.id.messageEditText);
        messagesTextView = findViewById(R.id.messagesTextView);
        sendButton = findViewById(R.id.sendButton);
        messageLog = new StringBuilder();
        mainHandler = new Handler(Looper.getMainLooper());

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty() && webSocket != null) {
                webSocket.send("Jeanette: " + message);
                messageEditText.setText("");
            }
        });

        // WEBSOCKET :O !!!!!!!!!!!!!!!!!!!!!!!!!!!
        connectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                // Nachrichten auslesen ohne Heartbeat
                if (!text.equals(HEARTBEAT_PAYLOAD)) {
                    addMessageToLog(text);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
            }
        };

        webSocket = client.newWebSocket(request, listener);
    }

    private void addMessageToLog(String message) {
        messageLog.append(message).append("\n");
        mainHandler.post(() -> {
            messagesTextView.setText(messageLog.toString());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App closed");
        }
    }
}