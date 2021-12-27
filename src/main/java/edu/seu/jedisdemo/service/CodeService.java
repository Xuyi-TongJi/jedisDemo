package edu.seu.jedisdemo.service;

import com.sun.istack.internal.NotNull;
import edu.seu.jedisdemo.entity.CodeMessage;
import edu.seu.jedisdemo.entity.Message;
import edu.seu.jedisdemo.util.CodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CodeService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CodeService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CodeMessage sendCode(String phone) {
        if (phone == null || phone.length() == 0) {
            return new CodeMessage(false, null , "手机号码输入有误");
        }
        String code = CodeUtil.getCode();
        // 手机发送次数key
        String countKey = "VerifyCode" + phone + ":count";
        // 验证码key
        String codeKey = "VerifyCode" + phone + ":code";
        String count = (String) redisTemplate.opsForValue().get(countKey);
        if (count == null) {
            redisTemplate.opsForValue().set(countKey, 1, 24 * 60 * 60, TimeUnit.SECONDS);
        } else if (Integer.parseInt(count) <= 2) {
            redisTemplate.opsForValue().increment(countKey);
        } else {
            // 已经发送三次
            return new CodeMessage(false, null , "今天发送次数超过三次，不能再发送");
        }
        // 验证码放到redis中
        redisTemplate.opsForValue().set(codeKey, code, 120, TimeUnit.SECONDS);
        return new CodeMessage(true, code, "发送成功");
    }

    // 验证码校验
    public Message verifyCode(@NotNull String code, @NotNull String phone) {
        String codeKey = "VerifyCode" + phone + ":code";
        String realCode = (String) redisTemplate.opsForValue().get(codeKey);
        if (code.equals(realCode)) {
            return new Message(true, "登录成功");
        } else {
            return new Message(false, "手机号或验证码错误");
        }
    }
}
