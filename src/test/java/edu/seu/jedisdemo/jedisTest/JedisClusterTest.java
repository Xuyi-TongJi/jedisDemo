package edu.seu.jedisdemo.jedisTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JedisClusterTest {

    private static JedisCluster jedisCluster;

    @BeforeAll
    public static void beforeAll() {
        HostAndPort hostAndPort = new HostAndPort("127.0.0.1", 6379);
        jedisCluster = new JedisCluster(hostAndPort);
    }

    @Test
    public void test01() {
        jedisCluster.set("k1", "v1");
        System.out.println(jedisCluster.get("k1"));
    }

    @AfterAll
    public static void afterAll() {
        jedisCluster.close();
    }
}
