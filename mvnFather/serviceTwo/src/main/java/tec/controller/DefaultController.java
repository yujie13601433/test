package tec.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RefreshScope
@RequestMapping(value = "/test.do")
public class DefaultController {
    @Value("${user.testValue}")
    private String testValue;
    @ResponseBody
    @RequestMapping(value = "")
    public String toTest(){
        return testValue;
    }

    @RequestMapping(value = "/TEST.html")
    public String toTest2(){
        return "/1.html";
    }
}
