// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.timeline;

import org.edument.moarvm.ConnectState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TimelineClient {
    private final String host;
    private final int port;

    public TimelineClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static AsynchronousSocketChannel getSocket(String host, int port) throws InterruptedException {
        Queue<Double> backoff = new LinkedList<>(Arrays.asList(0.0, 0.5, 0.5, 1.0, 1.0, 2.0, 2.0, 4.0, 4.0));
        AsynchronousSocketChannel result = null;
        InetSocketAddress address = new InetSocketAddress(host, port);
        while (!backoff.isEmpty()) {
            try {
                AsynchronousSocketChannel conn = AsynchronousSocketChannel.open();
                conn.connect(address).get();
                result = conn;
                break;
            } catch (ExecutionException | IOException e) {
                Double sleep = backoff.remove();
                Thread.sleep((long) (1000 * sleep));
            }
        }
        return result;
    }
}
