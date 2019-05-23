package edument.perl6idea.timeline.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.concurrent.*;

public class TimelineClient {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

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
        attemptConnection(outcome, new LinkedList<>(Arrays.asList(0.0, 0.5, 0.5, 1.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0)));
        return outcome;
    }

    private void attemptConnection(CompletableFuture<AsynchronousSocketChannel> result, LinkedList<Double> backoff) {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            AsynchronousSocketChannel conn = AsynchronousSocketChannel.open();
            conn.connect(address, null, new CompletionHandler<Void, Object>() {
                @Override
                public void completed(Void r, Object attachment) {
                    result.complete(conn);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    retryOrFail(result, backoff, exc);
                }
            });
        }
        catch (IOException e) {
            retryOrFail(result, backoff, e);
        }
    }

    private void retryOrFail(CompletableFuture<AsynchronousSocketChannel> result, LinkedList<Double> backoff, Throwable e) {
        if (backoff.isEmpty())
            result.completeExceptionally(e);
        else {
            double delay = backoff.removeFirst();
            executor.schedule(() -> attemptConnection(result, backoff), (long)(delay * 1000), TimeUnit.MILLISECONDS);
        }
    }

    private void dispatchEvents(AsynchronousSocketChannel conn, TimelineEventListener listener) {
        ByteBuffer outstanding = ByteBuffer.allocate(1024 * 1024);
        read(conn, outstanding, listener);
    }

    private void read(AsynchronousSocketChannel conn, ByteBuffer outstanding, TimelineEventListener listener) {
        ByteBuffer buffer = ByteBuffer.allocate(32768);
        conn.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer bytesRead, Object attachment) {
                buffer.flip();
                outstanding.put(buffer);
                processBuffer(outstanding, listener);
                read(conn, outstanding, listener);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                listener.onError(exc);
                try {
                    conn.close();
                }
                catch (IOException e) {
                    // Already reported an error, so nothing to do.
                }
            }
        });
    }

    private void processBuffer(ByteBuffer outstanding, TimelineEventListener listener) {
        boolean again = true;
        while (again) {
            int end = outstanding.position();
            again = false;
            for (int i = 0; i < end; i++) {
                if (outstanding.get(i) == '\n') {
                    byte[] message = new byte[i + 1];
                    outstanding.position(0);
                    outstanding.get(message);
                    processMessage(message, listener);
                    outstanding.limit(end);
                    outstanding.compact();
                    again = true;
                    break;
                }
            }
        }
    }

    private static final Map<String, Object> EMPTY_DATA = new HashMap<>();
    private void processMessage(byte[] message, TimelineEventListener listener) {
        try {
            String decoded = new String(message, "UTF-8");
            JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
            String module = json.get("m").getAsString();
            String category = json.get("c").getAsString();
            String name = json.get("n").getAsString();
            int kind = json.get("k").getAsInt();
            double timestamp = json.get("t").getAsDouble();
            int id = json.has("i") ? json.get("i").getAsInt() : 0;
            int parent = json.has("p") ? json.get("p").getAsInt() : 0;
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = json.has("d")
                    ? new Gson().fromJson(json.get("d"), type)
                    : EMPTY_DATA;
            listener.onEvent(new ClientEvent(module, category, name, kind, timestamp, id, parent, data));
        }
        catch (Exception e) {
            listener.onError(e);
        }
    }
}
