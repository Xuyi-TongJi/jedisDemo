package edu.seu.jedisdemo.controller;

import edu.seu.jedisdemo.entity.Message;
import edu.seu.jedisdemo.service.CodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class CodeController {

    private final CodeService codeService;

    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @PostMapping("/sendCode")
    public Object sendCode(@RequestParam(name = "phone") String phone) {
        return codeService.sendCode(phone);
    }

    @PostMapping("/verifyCode")
    public Object verifyCode(@RequestParam(name = "phone") String phone,
                             @RequestParam(name = "code") String code,
                             HttpSession session) {
        Message message = codeService.verifyCode(code, phone);
        if (message.isExecution()) {
            session.setAttribute("message", message.getMessage());
        }
        return message;
    }
}
