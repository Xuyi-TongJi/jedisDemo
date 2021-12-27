package edu.seu.jedisdemo.controller;

import edu.seu.jedisdemo.entity.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/lock")
public class DistributedLockTestController {

    private final RedisTemplate<String, String> redisTemplate;

    public DistributedLockTestController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/testLock")
    public Message testLock(@RequestParam("serialId") Integer serialId) {
        String uuid = UUID.randomUUID().toString();
        String lockKey = "lock:" + serialId;
        // （尝试）获得锁:原子操作
        Boolean lock
                = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            String value = redisTemplate.opsForValue().get(String.valueOf(serialId));
            if (StringUtils.isEmpty(value)) {
                return new Message(false, "无对应商品");
            }
            int num = Integer.parseInt(value);
            redisTemplate.opsForValue().set(String.valueOf(serialId), String.valueOf(--num));
            // 释放锁：原子操作，使用lua脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Collections.singletonList(lockKey), uuid);
        } else {
            try {
                // 睡眠100毫秒再次尝试获取锁
                Thread.sleep(100);
                testLock(serialId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new Message(true, "购买成功");
    }
}
