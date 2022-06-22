import com.alibaba.fastjson.JSONObject;
import tec.util.Sendhttp;

import java.util.HashMap;
import java.util.Map;

public class Test {
//    public static void main(String[] args) {
//        Map<String, String> headMap = new HashMap<String, String>();
//        headMap.put("Content-Type", "application/json;charset=UTF-8");
//        JSONObject json = new JSONObject();
//        json.put("key", "myFriendController");
//        json.put("value", "loginNode2");
//
//        System.out.println(json.toJSONString());
//        String res = Sendhttp.doPostRequestPoolNewSetTime("http://localhost:8081/one/test.do/getredis", json.toJSONString(), headMap, 1000);
//        System.out.println(res);
//    }
        public static void main(String[] args) {
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Content-Type", "application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        json.put("exchangeName", "DirectExchange-One");
        json.put("routingKey", "Two");

        System.out.println(json.toJSONString());
        String res = Sendhttp.doPostRequestPoolNewSetTime("http://localhost:8081/one/test.do/esTest", json.toJSONString(), headMap, 1000);
        System.out.println(res);
    }
}
