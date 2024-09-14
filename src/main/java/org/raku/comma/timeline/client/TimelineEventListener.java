package org.raku.comma.timeline.client;

public interface TimelineEventListener {
    void onEvent(ClientEvent e);
    void onError(Throwable e);
}
