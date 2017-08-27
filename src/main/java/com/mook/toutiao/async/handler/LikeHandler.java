package com.mook.toutiao.async.handler;

import com.mook.toutiao.async.EventHandler;
import com.mook.toutiao.async.EventModel;
import com.mook.toutiao.async.EventType;
import com.mook.toutiao.model.Message;
import com.mook.toutiao.model.User;
import com.mook.toutiao.service.MessageService;
import com.mook.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        int fromId = model.getActorId();
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        User sender = userService.getUser(fromId);
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + sender.getName() +
                " 赞了你的资讯,http://127.0.0.1:9090/news/"
                + String.valueOf(model.getEntityId()));
        message.setFromId(fromId);
        message.setCreatedDate(new Date());
        message.setConversationId(fromId > toId ? String.format("%d_%d", toId, fromId) : String.format("%d_%d", fromId, toId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
