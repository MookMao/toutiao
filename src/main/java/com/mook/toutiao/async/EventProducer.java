package com.mook.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import com.mook.toutiao.util.JedisAdapter;
import com.mook.toutiao.util.RedisKeyUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    //将事件放入队列
    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel); //序列化事件
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
