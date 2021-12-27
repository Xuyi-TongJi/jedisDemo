package edu.seu.jedisdemo.jedisTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class JedisTest {

    private static Jedis jedis;

    @BeforeAll
    public static void beforeAll() {
        jedis = new Jedis("127.0.0.1", 6379);
    }


    @Test
    public void method01() {
        Set<String> keys = jedis.keys("*");
        for (String s:
             keys) {
            System.out.println(s);
        }
        System.out.println(jedis.get("k1"));
    }

    @Test
    public void method02() {
        jedis.lpush("myList", "l1", "l2");
        List<String> myList = jedis.lrange("myList", 0, -1);
        for (String s:
             myList) {
            System.out.println(s);
        }
    }

    @Test
    public void method03() {
        jedis.sadd("names", "xuyi", "songruizhi");
        Set<String> names = jedis.smembers("names");
        for (String s:
             names) {
            System.out.println(s);
        }
    }

    @Test
    public void method04() {
        jedis.hset("myHash", "name", "xuyi");
        String name = jedis.hget("myHash", "name");
        System.out.println(name);
    }

    @Test
    public void method05() {
        jedis.zadd("myZSet", 100d, "Shanghai");
        jedis.zadd("myZSet", 90d, "NanJing");
        Set<String> myZSet = jedis.zrangeByScore("myZSet", 95d, 110d);
        for (String set:
             myZSet) {
            System.out.println(set);
        }
    }

    @Test
    public void method06() {
        String prodId = "6379";
        System.out.println(jedis.get("pId:" + prodId + ":total"));
        String userSetKey = "uId:" + prodId + "userSet";
        Set<String> users = jedis.smembers(userSetKey);
        for (String user:
             users) {
            System.out.println(user);
        }
    }

    @AfterAll
    public static void close() {
        jedis.close();
    }
}