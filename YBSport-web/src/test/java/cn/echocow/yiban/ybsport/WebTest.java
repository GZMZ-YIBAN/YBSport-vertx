package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.utils.ConstEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 易班api请求测试
 * @date 2018-08-24 18:39
 * <p>
 * -----------------------------
 **/
public class WebTest extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(WebTest.class.getName());
    }

    @Override
    public void start() {
        JsonObject parameter = new JsonObject();
        parameter.put("access_token", "8a29dfccf77ef0a8566780038");
        parameter.put("days", 1);
        WebClient client = WebClient.create(Vertx.vertx());

//        client
//                .getAbs(ConstEnum.YB_STEPS.getName())
//                .sendJson(parameter, ar -> {
//                    if (ar.succeeded()) {
//                        System.out.println("success");
//                        HttpResponse<Buffer> response = ar.result();
//                        System.out.println(response.statusCode());
//                        JsonObject body = ar.result().bodyAsJsonObject();
//                        System.out.println(body);
//                    } else {
//                        System.out.println("error");
//                        System.err.println(ar.cause().getMessage());
//                    }
//                });
//        client
//                .getAbs("https://www.apiopen.top/journalismApi")
//                .send(ar -> {
//                    if (ar.succeeded()) {
//                        // 获取响应
//                        HttpResponse<Buffer> response = ar.result();
//
//                        System.out.println("Received response with status code" + response.statusCode());
//                    } else {
//                        System.out.println("Something went wrong " + ar.cause().getMessage());
//                    }
//                });
//        client.get(443, "www.apiopen.top", "/journalismApi")
//                .ssl(true)
//                .send(ar -> {
//                    if (ar.succeeded()) {
//                        // 获取响应
//                        HttpResponse<Buffer> response = ar.result();
//
//                        System.out.println("Received response with status code" + response.statusCode());
//                    } else {
//                        System.out.println("Something went wrong " + ar.cause().getMessage());
//                    }
//                });

//        MultiMap form = MultiMap.caseInsensitiveMultiMap();
//        form.set("access_token", "68e53198deae45b6f2a681a67141e831d719381a");
//        form.set("to_yb_uid", "11291273");
//        form.set("content","呵呵哈哈哈");
//        form.set("template","system");
//        client.postAbs("https://openapi.yiban.cn/msg/letter")
//                .putHeader("content-type", "multipart/form-data")
//                .sendForm(form,ar->{
//                    if (ar.succeeded()) {
//                        JsonObject body = ar.result().bodyAsJsonObject();
//                    } else {
//                        System.out.println("failed");
//                    }
//                });

    }


}
