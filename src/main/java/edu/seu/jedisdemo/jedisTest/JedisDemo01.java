package edu.seu.jedisdemo.jedisTest;

import redis.clients.jedis.Jedis;

public class JedisDemo01 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        System.out.println(jedis.ping());
    }
}