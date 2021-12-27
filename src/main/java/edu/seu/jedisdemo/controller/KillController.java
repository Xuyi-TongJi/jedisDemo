package edu.seu.jedisdemo.controller;

import com.sun.istack.internal.NotNull;
import edu.seu.jedisdemo.entity.Message;
import edu.seu.jedisdemo.service.KillService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Random;

@Controller
@RequestMapping("/service")
public class KillController {

    private final KillService killService;

    public KillController(KillService killService) {
        this.killService = killService;
    }

    @GetMapping("/kill")
    public ModelAndView kill(@NotNull @RequestParam(name = "prodId") Integer prodId) {
        // 暂时不做登录功能
        String uId = String.valueOf(new Random().nextInt(10000));
        Message message = killService.doSecKill(uId, String.valueOf(prodId));
        ModelAndView mv = new ModelAndView();
        mv.addObject("message", message.getMessage());
        if (message.isExecution()) {
            // 秒杀成功
            mv.setViewName("success");
        } else {
            mv.setViewName("failure");
        }
        return mv;
    }
}