package cn.echocow.yiban.ybsport.convert;

import cn.echocow.yiban.ybsport.pojo.YbUser;
import io.vertx.core.MultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.echocow.yiban.ybsport.pojo.YbSportBuy;
import cn.echocow.yiban.ybsport.utils.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * -----------------------------
 * rest 接口请求
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 22:37 2018/8/22
 */
public class ConvertRestVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOCATION = ConstEnum.YB_AUTH.getName() + "?client_id=" + ConstEnum.APP_ID.getName() +
            "&redirect_uri=" + ConstEnum.REDIRECT_URI.getName() + "&state=" + ConstEnum.STATE.getName();
    private String flag = ConstEnum.FLAG.getName();
    private WebClient client;

    @Override
    public void start(Future<Void> startFuture) {
        HttpServer server = VertxSingleton.VERTX.createHttpServer();
        Router router = Router.router(VertxSingleton.VERTX);
        client = WebClient.create(VertxSingleton.VERTX);
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(VertxSingleton.VERTX)));
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);
        router.route().handler(BodyHandler.create());
        // 跨域请求处理
        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

        router.route("/").handler(ctx -> {
            ctx.response().putHeader("content-type", "application/json");
            ctx.response().setStatusCode(ReasultBuilder.SUCCESS_CODE);
            ctx.next();
        });
        router.options("/").handler(this::options);
        router.get("/").handler(this::getInfo);
        router.get("/steps").handler(this::getSteps);
        router.get("/buyList").handler(this::buyList);
        router.get("/status").handler(this::getStatus);
        router.post("/buy").handler(this::buy);
        ConfigFactory.retriever.getConfig(res -> {
            if (res.succeeded()) {
                JsonObject httpConfig = res.result().getJsonObject("http");
                server.requestHandler(router::accept)
                        .listen(httpConfig.getInteger("port"), httpConfig.getString("host"),
                                listenResult -> {
                                    if (listenResult.failed()) {
                                        LOGGER.error("Http Server failed!" + listenResult.cause());
                                    } else {
                                        LOGGER.info("Http Server started on " + httpConfig.getString("host") + ":" + httpConfig.getInteger("port") + "!");
                                    }
                                });
            } else {
                LOGGER.error("Config get error! Something went wring during getting the config!" + res.cause());
                throw new RuntimeException("Config get error! Something went wring during getting the config");
            }
        });
    }

    /**
     * 用于获取是否在活动期间内  /status
     *
     * @param context 路由上下文
     */
    private void getStatus(RoutingContext context) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "status");
        VertxSingleton.VERTX.eventBus().<JsonObject>send(ConvertDbVerticle.class.getName(), new JsonObject(), options, reply -> {
            if (reply.succeeded()) {
                LOGGER.info("Status Get Succeeded!");
                JsonObject result = ReasultBuilder.buildSuccess(reply.result().body());
                context.response().end(result.toString());
            } else {
                LOGGER.error("Status Route Error!" + reply.cause().getMessage());
                context.fail(reply.cause());
                context.response().end(ReasultBuilder.buildError().toString());
            }
        });
    }

    /**
     * 用于获取已经兑换的列表
     *
     * @param context 路由上下文
     */
    private void buyList(RoutingContext context) {
        JsonObject auth = checkAuth(context);
        if (!auth.getBoolean(flag)) {
            return;
        }
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "buyList");
        VertxSingleton.VERTX.eventBus().<JsonObject>send(ConvertDbVerticle.class.getName(), auth, options, reply -> {
            if (reply.succeeded()) {
                LOGGER.info("buyList Info Succeeded!");
                JsonObject result = ReasultBuilder.buildSuccess(reply.result().body());
                context.response().end(result.toString());
            } else {
                LOGGER.error("Route Error!" + reply.cause().getMessage());
                context.fail(reply.cause());
                context.response().end(ReasultBuilder.buildError().toString());
            }
        });
    }

    /**
     * 跨域处理，什么都不做
     *
     * @param context 路由上下文
     */
    private void options(RoutingContext context) {
        context.response().end();
    }

    /**
     * 提交兑换信息
     *
     * @param context 路由上下文
     */
    private void buy(RoutingContext context) {
        JsonObject auth = checkAuth(context);
        if (!auth.getBoolean(flag)) {
            return;
        }
        Long typeId = Long.parseLong(context.request().getParam("typeId"));
        Long sportSteps = Long.parseLong(context.request().getParam("sportSteps"));
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "buy");
        YbSportBuy buy = new YbSportBuy(auth.getJsonObject("user").toString(), sportSteps, typeId, LocalDate.now().toString());
        VertxSingleton.VERTX.eventBus().<JsonObject>send(ConvertDbVerticle.class.getName(), buy.toJsonObject(), options, reply -> {
            if (reply.succeeded()) {
                JsonObject body = reply.result().body();
                if (ReasultBuilder.SUCCESS.equals(body.getString(ReasultBuilder.STATUS))) {
                    LOGGER.info("Add and buy Succeeded!");
                    sendMessage(auth, body.getLong("steps", 0L), body.getLong("money", 0L));
                }
                JsonObject result = ReasultBuilder.buildSuccess(body);
                context.response().end(result.toString());
            } else {
                LOGGER.error("Route Error!" + reply.cause().getMessage());
                context.fail(reply.cause());
                context.response().end(ReasultBuilder.buildError().toString());
            }
        });
    }


    /**
     * 获取所有基础信息
     *
     * @param context 路由上下文
     */
    private void getInfo(RoutingContext context) {
        JsonObject auth = checkAuth(context);
        if (!auth.getBoolean(flag)) {
            return;
        }
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "info");

        VertxSingleton.VERTX.eventBus().<JsonObject>send(ConvertDbVerticle.class.getName(), auth, options, reply -> {
            if (reply.succeeded()) {
                LOGGER.info("Get Info Succeeded!");
                JsonObject result = ReasultBuilder.buildSuccess(reply.result().body());
                context.response().end(result.toString());
            } else {
                LOGGER.error("Route Error!" + reply.cause());
                context.fail(reply.cause());
                context.response().end(ReasultBuilder.buildError().toString());
            }
        });


    }

    /**
     * 获取步数记录
     *
     * @param context 路由上下文
     */
    private void getSteps(RoutingContext context) {
        JsonObject auth = checkAuth(context);
        if (!auth.getBoolean(flag)) {
            return;
        }
        try {
            client
                    .getAbs(ConstEnum.YB_STEPS.getName())
                    .addQueryParam("access_token", auth.getJsonObject("oauth").getString("access_token"))
                    .addQueryParam("days", "30")
                    .send(ar -> {
                        if (ar.succeeded() && ReasultBuilder.SUCCESS.equals(ar.result().bodyAsJsonObject().getString(ReasultBuilder.STATUS))) {
                            LOGGER.info("Steps Info Succeeded!");
                            JsonObject body = ar.result().bodyAsJsonObject();
                            JsonArray list = body.getJsonObject("info").getJsonArray("list");
                            if (list == null) {
                                context.response().end(ReasultBuilder.buildError("无运动数据").toString());
                                return;
                            }
                            JsonObject steps = ReasultBuilder.buildSuccess();
                            steps.put("data", new JsonObject().put("sport_steps", "0步"));
                            for (int i = 0; i < list.size(); i++) {
                                JsonObject object = list.getJsonObject(i);
                                if (LocalDate.now().toString().equals(object.getString("date_time"))) {
                                    steps.put("data", new JsonObject(list.getValue(0).toString()));
                                    break;
                                }
                            }
                            steps.getJsonObject("data").put("list", list);
                            context.response().end(steps.toString());
                        } else {
                            JsonObject result = ar.result().bodyAsJsonObject();
                            String msg = "";
                            if (result != null) {
                                msg = result.getJsonObject("info").getString("msgCN");
                            }
                            context.response().end(ReasultBuilder.buildError(msg).toString());
                            context.fail(ar.cause());
                            LOGGER.error("Steps Info Failed!");
                        }
                    });
        } catch (UnknownError exception) {
            LOGGER.error("Steps Info Failed!");
            context.response().end(ReasultBuilder.buildError("易班获取步数出现异常...请重新尝试...").toString());
        }

    }


    /**
     * 易班登录过滤
     *
     * @param context 路由上下文
     * @return 登录结果
     */
    private JsonObject checkAuth(RoutingContext context) {
        String verifyRequest = context.request().getParam("verifyRequest");
        String state = context.request().getParam("state");
        JsonObject auth = new JsonObject();
        auth.put(flag, false);
        if (verifyRequest == null || state == null) {
            context.response().end(ReasultBuilder.buildError("非法请求!").toString());
            return auth;
        }
        if (verifyRequest.isEmpty() || verifyRequest.equals(ConstEnum.STRING_NULL.getName()) || !state.equals(ConstEnum.STATE.getName())) {
            auth.put("location", LOCATION);
            context.response().end(ReasultBuilder.buildError(auth, ReasultBuilder.RRDORECT, "未授权").toString());
            return auth;
        }
        auth.put(flag, true);
        try {
            byte[] decrypt = new MCrypt(ConstEnum.APP_ID.getName(), ConstEnum.APP_SECRET.getName()).decrypt(verifyRequest);
            JsonObject yiban = new JsonObject(new String(decrypt));
            auth.put("user", yiban.getJsonObject("visit_user"))
                    .put("oauth", yiban.getJsonObject("visit_oauth"));
        } catch (Exception e) {
            auth.put(flag, false);
            LOGGER.error("User Decrypt failed!" + e.getMessage());
            context.response().end(ReasultBuilder.buildError("授权失败，请重新尝试！").toString());
        }
        return auth;
    }

    /**
     * 易班消息通知
     *
     * @param auth 授权成功的授权json
     * @param steps 兑换花费的步数
     * @param money 兑换获得的网薪
     */
    private void sendMessage(JsonObject auth, Long steps, Long money) {
        YbUser user = new YbUser(auth.getJsonObject("user"));
        MultiMap form = MultiMap.caseInsensitiveMultiMap();
        form.set("access_token", auth.getJsonObject("oauth").getString("access_token"));
        form.set("to_yb_uid", user.getUserId());
        form.set("content", "Hi！恭喜你~" + user.getUserNick() + user.getUserSex() + ",你在易运动里运动了超过 " + steps + " 步," +
                "成功兑换了 " + money + " 网薪哦~" + "易小熊会在24小时内给你奉上哈！请耐心等待~~");
        client.postAbs("https://openapi.yiban.cn/msg/letter")
                .putHeader("content-type", "multipart/form-data")
                .sendForm(form, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("Send Message Succeeded!");
                    } else {
                        LOGGER.error("Send Message failed!");
                    }
                });
    }
}
