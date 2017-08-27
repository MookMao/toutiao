package com.mook.toutiao.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }
/*
    public static void main(String[] args) {
        Jedis jedis = new Jedis(); //默认连接本地
        jedis.flushAll(); //删除所有数据库里的keys

        //map
        jedis.set("he", "man");
        print(1, jedis.get("he"));

        jedis.set("he", "woman");

        print(1, jedis.get("he"));

        jedis.rename("he", "hi");

//        jedis.keys("*");

        //设置带有过期时间的key-value
        jedis.setex("ex", 5, "value");
        print(2, jedis.get("ex"));


        //KV: 单一数值
        //对数值型value的操作
        //高并发访问
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(3, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(3, jedis.get("pv"));



        //list: 列表(双向列表)操作
        //最近访问
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 10)); //[a9, a8, a7, a6, a5, a4, a3, a2, a1, a0]
        print(5, jedis.llen(listName)); //10
        print(6, jedis.lpop(listName)); //a9
        print(7, jedis.llen(listName)); //9
        print(8, jedis.lindex(listName, 3)); //a5
        //插入操作
        jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "ff");
        jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb");
        print(9, jedis.lrange(listName, 0, jedis.llen(listName))); //a8, a7, a6, a5, bb, a4, ff, a3, a2, a1, a0]




        //hash: 对象属性,属性个数不确定
        String userKey = "user12";
        jedis.hset(userKey, "name", "kimi");
        jedis.hset(userKey, "age", "23");
        jedis.hset(userKey, "phone", "123456");
        print(10, jedis.hget(userKey, "name")); //kimi
        print(11, jedis.hgetAll(userKey)); //{name=kimi, phone=123456, age=23}
        jedis.hdel(userKey, "phone");
        print(12, jedis.hkeys(userKey)); //[name, age]
        print(13, jedis.hvals(userKey)); //[kimi, 23]
        print(14, jedis.hexists(userKey, "email"));
        print(14, jedis.hexists(userKey, "age"));
        //
        jedis.hset(userKey, "name", "Tom");
        print(15, jedis.hget(userKey, "name")); //Tom
        jedis.hsetnx(userKey, "name", "Kid"); //不存在该属性时才进行set
        print(15, jedis.hget(userKey, "name")); //Tom




        //set: 无序不重复集合. 点赞点踩,抽奖,已读,共同好友
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";

        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i)); //添加集合元素
            jedis.sadd(likeKeys2, String.valueOf(i * i));

        }
        print(16, jedis.smembers(likeKeys1)); //打印集合
        print(17, jedis.smembers(likeKeys2));
        //集合操作
        print(18, jedis.sinter(likeKeys1, likeKeys2)); //集合交 共同好友,共同关注
        print(18, jedis.sunion(likeKeys1, likeKeys2)); //集合并
        print(18, jedis.sdiff(likeKeys1, likeKeys2)); //集合差

        jedis.srem(likeKeys1, "5"); //删除集合元素
        print(19, jedis.sismember(likeKeys1, "5"));
        print(20, jedis.scard(likeKeys1)); //统计集合元素个数



        //SortedSet: 带score的Set. 排行榜,优先队列
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "yim");
        jedis.zadd(rankKey, 5, "uim");
        jedis.zadd(rankKey, 26, "oim");
        jedis.zadd(rankKey, 14, "rim");
        jedis.zadd(rankKey, 16, "nim");
        print(21, jedis.zrange(rankKey, 0, jedis.zcard(rankKey))); //按score排列升序输出元素
        print(22, jedis.zcount(rankKey, 15, 20)); //输出score在某一区间的元素的个数
        print(23, jedis.zscore(rankKey, "nim")); //查看某元素的score
        jedis.zincrby(rankKey, 5, "rim"); //给某人加分

        //排名
        print(24, jedis.zrange(rankKey, 0, jedis.zcard(rankKey)));
        print(24, jedis.zrange(rankKey, 0, 3)); //默认升序,score值最小的四人(索引是[0,3])
        print(24, jedis.zrevrange(rankKey, 0, 3)); //逆序打印,score值最大的四人

        //打印某一score区间值的元素
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 0, 100)) {
            print(25, tuple.getElement() + ": " + tuple.getScore());
        }

        print(26, jedis.zrank(rankKey, "oim")); //"oim"的下标(下标从0开始计算),即排名
        print(26, jedis.zrevrank(rankKey, "oim")); //逆序排序(根据score由大到小排序),然后输出"oim"的下标(下标从0开始计算),即排名




        //资源池: redis连接资源(默认只有8个连接资源),所以每次使用完需要关闭
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis jedis1 = pool.getResource();
            System.out.println("POOL" + i);

            jedis1.close(); //使用完毕放回资源池
         }

    }
*/
    private Jedis jedis = null;
    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        //jedis = new Jedis("localhost");
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        //return jedis;
        return pool.getResource();
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setex(String key, String value) {
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //通过JSON来实现序列化和反序列化
    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }
}