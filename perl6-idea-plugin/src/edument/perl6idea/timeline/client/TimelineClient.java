package edument.perl6idea.timeline.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class TimelineClient {
    private final String host;
    private final int port;
    private TimelineEventListener listener;

    public TimelineClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(TimelineEventListener listener) {
        if (this.listener != null)
            throw new IllegalStateException("Can only connect a single listener to a TimelineClient instance");
        this.listener = listener;
        establishConnection()
                .thenAccept(conn -> dispatchEvents(conn, listener))
                .exceptionally(error -> { listener.onError(error); return null; });
    }

    private CompletableFuture<AsynchronousSocketChannel> establishConnection() {
        CompletableFuture<AsynchronousSocketChannel> outcome = new CompletableFuture<>();
        attemptConnection(outcome, Executors.newScheduledThreadPool(1),
                          new LinkedList<>(Arrays.asList(
                                  0.0, 0.5, 0.5, 1.0,
                                  2.0, 2.0, 2.0, 2.0,
                                  4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0)));
        return outcome;
    }

    private void attemptConnection(CompletableFuture<AsynchronousSocketChannel> result,
                                   ScheduledExecutorService executor,
                                   LinkedList<Double> backoff) {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            AsynchronousSocketChannel conn = AsynchronousSocketChannel.open();
            conn.connect(address, null, new CompletionHandler<>() {
                @Override
                public void completed(Void r, Object attachment) {
                    executor.shutdown();
                    result.complete(conn);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    retryOrFail(result, backoff, executor, exc);
                }
            });
        }
        catch (IOException e) {
            retryOrFail(result, backoff, executor, e);
        }
    }

    private void retryOrFail(CompletableFuture<AsynchronousSocketChannel> result,
                             LinkedList<Double> backoff,
                             ScheduledExecutorService executor,
                             Throwable e) {
        if (backoff.isEmpty()) {
            result.completeExceptionally(e);
            executor.shutdown();
        }
        else {
            double delay = backoff.removeFirst();
            executor.schedule(() -> attemptConnection(result, Executors.newScheduledThreadPool(1), backoff), (long)(delay * 1000), TimeUnit.MILLISECONDS);
        }
    }

    private static class ConnectionState {
        private final AsynchronousSocketChannel connection;
        public final ByteBuffer outstanding = ByteBuffer.allocate(1024 * 1024);
        public final TimelineEventListener listener;
        public boolean handshook = false;

        ConnectionState(AsynchronousSocketChannel connection, TimelineEventListener listener) {
            this.connection = connection;
            this.listener = listener;
        }
    }

    private void dispatchEvents(AsynchronousSocketChannel conn, TimelineEventListener listener) {
        conn.write(ByteBuffer.wrap("{ \"min\": 1, \"max\": 1 }\n".getBytes(StandardCharsets.US_ASCII)));
        read(new ConnectionState(conn, listener));
    }

    private void read(ConnectionState state) {
        ByteBuffer buffer = ByteBuffer.allocate(32768);
        state.connection.read(buffer, null, new CompletionHandler<>() {
            @Override
            public void completed(Integer bytesRead, Object attachment) {
                if (bytesRead >= 0) {
                    buffer.flip();
                    state.outstanding.put(buffer);
                    processBuffer(state);
                    read(state);
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                listener.onError(exc);
                closeConnection(state);
            }
        });
    }

    private static void closeConnection(ConnectionState state) {
        try {
            state.connection.close();
        }
        catch (IOException e) {
            // Already reported an error, so nothing to do.
        }
    }

    private static void processBuffer(ConnectionState state) {
        boolean again = true;
        while (again) {
            int end = state.outstanding.position();
            again = false;
            for (int i = 0; i < end; i++) {
                if (state.outstanding.get(i) == '\n') {
                    byte[] message = new byte[i + 1];
                    state.outstanding.position(0);
                    state.outstanding.get(message);
                    processMessage(message, state);
                    state.outstanding.limit(end);
                    state.outstanding.compact();
                    again = true;
                    break;
                }
            }
        }
    }

    private static final Map<String, Object> EMPTY_DATA = new HashMap<>();
    private static void processMessage(byte[] message, ConnectionState state) {
        try {
            String decoded = new String(message, StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(decoded).getAsJsonObject();
            if (state.handshook) {
                String module = json.get("m").getAsString();
                String category = json.get("c").getAsString();
                String name = json.get("n").getAsString();
                int kind = json.get("k").getAsInt();
                double timestamp = json.get("t").getAsDouble();
                int id = json.has("i") ? json.get("i").getAsInt() : 0;
                int parent = json.has("p") ? json.get("p").getAsInt() : 0;
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> data = json.has("d")
                                           ? new Gson().fromJson(json.get("d"), type)
                                           : EMPTY_DATA;
                state.listener.onEvent(new ClientEvent(module, category, name, kind, timestamp, id, parent, data));
            }
            else {
                if (json.has("err")) {
                    state.listener.onError(new Exception(json.get("err").getAsString()));
                    closeConnection(state);
                }
                else if (!json.has("ver")) {
                    state.listener.onError(new Exception("Server handshake failed; missing ver"));
                    closeConnection(state);
                }
                else {
                    state.handshook = true;
                }
            }
        }
        catch (Exception e) {
            state.listener.onError(e);
        }
    }
}
