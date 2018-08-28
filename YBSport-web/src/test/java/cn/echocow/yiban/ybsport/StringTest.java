package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.utils.StringToPojoJson;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 字符串处理测试
 * @date 2018-08-24 21:44
 * <p>
 * -----------------------------
 **/
public class StringTest {
    @Test
    public void test() {
        String s = "[2,2000,20,true]";
        JsonObject s1 = StringToPojoJson.toYbSportType(s);
        System.out.println(s1);
    }

    @Test
    public void test1() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("a", "123");
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.put("a", "43534");
        jsonObject.mergeIn(jsonObject1);
        System.out.println(jsonObject.toString());
    }

    @Test
    public void test2() {
        String s = "[1,\"{\"userid\":\"11291273\",\"username\":\"EchoCow\",\"usernick\":\"EchoCow\",\"usersex\":\"M\"}\",10000,1,\"2018-08-25\"]";
        String split = s.substring(1, s.length() - 1);
        System.out.println(split);
        s = s.replace("[", "{");
        s = s.replace("]", "}");
        System.out.println(s);

//        System.out.println(new JsonObject(s));
    }

    @Test
    public void test3() {
        JsonObject body = new JsonObject("{\"status\":\"success\",\"info\":{\"list\":[{\"sport_steps\":\"7步\",\"date_time\":\"2018-08-25\"}]}}");
        JsonObject jsonObject = body.getJsonObject("info").getJsonArray("list").getJsonObject(0);
        System.out.println(jsonObject);
    }

    @Test
    public void name() {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
