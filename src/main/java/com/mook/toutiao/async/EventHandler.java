package com.mook.toutiao.async;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes(); //该EventHandler所关注的EventType
}
