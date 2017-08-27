package com.mook.toutiao.async.handler;

import com.mook.toutiao.async.EventHandler;
import com.mook.toutiao.async.EventModel;
import com.mook.toutiao.async.EventType;
import com.mook.toutiao.model.Message;
import com.mook.toutiao.service.MessageService;
import com.mook.toutiao.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登陆IP异常");
        //设置系统账户, userId = 3
        message.setFromId(3);
        message.setCreatedDate(new Date());
//        int fromId = model.getActorId();
        int toId = model.getActorId();
        message.setConversationId(3 > toId ? String.format("%d_%d", toId, 3) : String.format("%d_%d", 3, toId));
        messageService.addMessage(message);

        //发送邮件给用户: 应该根据userId获取到用户的邮箱
        Map<String, Object> map = new HashMap();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("to"), "欢迎加入极客资讯",
                "mails/welcome.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
