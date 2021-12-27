package edu.seu.jedisdemo.service;

import com.sun.istack.internal.NotNull;
import edu.seu.jedisdemo.entity.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KillService {

    private final RedisTemplate<String, Object> redisTemplate;

    public KillService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Message doSecKill(@NotNull String uId, @NotNull String prodId) {
        // 1.拼接key
        // 1.1库存key
        String inventoryKey = "pId:" + prodId + ":total";
        // 1.2秒杀成功用户集合key
        String userSetKey = "uId:" + prodId + "userSet";

        // 监视库存
        redisTemplate.watch(inventoryKey);

        // 2.获取库存，如果库存null，秒杀还没有开始
        String kc = String.valueOf(redisTemplate.opsForValue().get(inventoryKey));
        if (kc == null) {
            return new Message(false, "秒杀尚未开始，请等待");
        }
        // 3.开始秒杀 一个用户只能秒杀一次
        // 3.1 判断用户是否重复秒杀操作
        if (Boolean.TRUE
                .equals(redisTemplate.opsForSet().isMember(userSetKey, uId))) {
            return new Message(false, "您已经参与秒杀，不能重复秒杀");
        }
        // 3.2 判断商品数量小于1，秒杀结束
        if (Integer.parseInt(kc) <= 0) {
            return new Message(false, "已经没有库存，秒杀失败");
        }

        // 4. 秒杀过程，必须加入事务
        // 组队
        redisTemplate.multi();

        // 4.1 库存-1
        redisTemplate.opsForValue().decrement(inventoryKey);
        // 4.2 将秒杀成功的用户添加到秒杀成功清单中
        redisTemplate.opsForSet().add(userSetKey, uId);

        // 执行
        List<Object> results = redisTemplate.exec();

        if (results == null || results.size() == 0) {
            return new Message(false, "并发量限制，秒杀失败");
        }
        return new Message(true, "秒杀成功");
    }
}