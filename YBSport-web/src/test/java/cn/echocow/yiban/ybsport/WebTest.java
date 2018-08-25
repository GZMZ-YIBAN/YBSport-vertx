package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.utils.ConstEnum;
import cn.echocow.yiban.ybsport.utils.VertxSingleton;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.junit.Test;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 数据库测试
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
        parameter.put("access_token","8a29e5174ab7e0db51ddfccf77ef0a8566780038");
        parameter.put("days",1);
        WebClient client = WebClient.create(Vertx.vertx());

        client
                .getAbs(ConstEnum.YB_STEPS.getName())
                .as(BodyCodec.jsonObject())
                .sendJson(parameter, ar -> {
                    if (ar.succeeded()) {
                        System.out.println("success");
                        HttpResponse<JsonObject> response = ar.result();
                        System.out.println(response.statusCode());
                        JsonObject body = ar.result().body();
                        System.out.println(body);
                    } else {
                        System.out.println("error");
                        System.err.println(ar.cause().getMessage());
                    }
                });
        client
                .getAbs("https://www.apiopen.top/journalismApi")
                .send(ar -> {
                    if (ar.succeeded()) {
                        // 获取响应
                        HttpResponse<Buffer> response = ar.result();

                        System.out.println("Received response with status code" + response.statusCode());
                    } else {
                        System.out.println("Something went wrong " + ar.cause().getMessage());
                    }
                });
        client.get(443, "www.apiopen.top", "/journalismApi")
                .ssl(true)
                .send(ar -> {
                    if (ar.succeeded()) {
                        // 获取响应
                        HttpResponse<Buffer> response = ar.result();

                        System.out.println("Received response with status code" + response.statusCode());
                    } else {
                        System.out.println("Something went wrong " + ar.cause().getMessage());
                    }
                });

    }


}
