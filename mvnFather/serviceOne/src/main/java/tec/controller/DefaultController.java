package tec.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tec.cache.ICacheDAO;
import tec.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RefreshScope
@RequestMapping(value = "/test.do")
public class DefaultController {
    @Value("${user.testValue}")
    private String testValue;
    @Autowired
    TestService testService;
    @Autowired
    ICacheDAO commonCache;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @ResponseBody
    @RequestMapping(value = "")
    public String toTest(){
//        testService.test();
        return testValue;
    }
    @ResponseBody
    @RequestMapping(value = "/setredis", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String setredis(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jObject){
        commonCache.add(jObject.getString("key"),jObject.getString("value"));
        return "0k";
    }
    @ResponseBody
    @RequestMapping(value = "/getredis", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getredis(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jObject){
        return (String) commonCache.get(jObject.getString("key"));
    }

    @RequestMapping(value = "/TEST.html")
    public String toTest2(){
        return "/1.html";
    }

    @ResponseBody
    @RequestMapping(value = "/pushMQ", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String pushMQ(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jObject){
        String exchangeName = jObject.getString("exchangeName");
        String routingKey = jObject.getString("routingKey");
        rabbitTemplate.convertAndSend(exchangeName,routingKey,jObject);
        return jObject.toJSONString();
    }
    @ResponseBody
    @RequestMapping(value = "/esTest", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String esTest(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jObject){
        testService.testES();
        return "ok";
    }
}
