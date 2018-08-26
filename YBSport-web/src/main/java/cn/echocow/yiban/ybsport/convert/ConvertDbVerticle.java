package cn.echocow.yiban.ybsport.convert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.echocow.yiban.ybsport.pojo.YbSportBuy;
import cn.echocow.yiban.ybsport.utils.ConfigFactory;
import cn.echocow.yiban.ybsport.utils.ReasultBuilder;
import cn.echocow.yiban.ybsport.utils.StringToPojoJson;
import cn.echocow.yiban.ybsport.utils.VertxSingleton;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 数据库
 * @date 2018-08-24 14:01
 * <p>
 * -----------------------------
 **/
public class ConvertDbVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ACTION = "action";
    private AsyncSQLClient postgreSQLClient;


    @Override
    public void start(Future<Void> startFuture) {
        ConfigFactory.retriever.getConfig(res -> {
            if (res.succeeded()) {
                postgreSQLClient = PostgreSQLClient.createShared(VertxSingleton.VERTX, res.result().getJsonObject("database"));
                VertxSingleton.VERTX.eventBus().consumer(getClass().getName(), this::onMessage);
                startFuture.complete();
            } else {
                LOGGER.error("Config get error! Something went wring during getting the config!" + res.cause());
                throw new RuntimeException("Config get error! Something went wring during getting the config");
            }
        });
    }

    private void onMessage(Message<JsonObject> message) {
        if (!message.headers().contains(ACTION)) {
            LOGGER.error("No action header specified for message with headers {} and body {}",
                    message.headers(), message.body().encodePrettily());
            message.fail(1, "No action header specified");
            return;
        }
        String action = message.headers().get(ACTION);
        switch (action) {
            case "info":
                getInfo(message);
                break;
            case "buy":
                buy(message);
                break;
            case "buyList":
                buyList(message);
                break;
            default:
                break;
        }
    }

    private void buyList(Message<JsonObject> message) {
        JsonObject auth = message.body();
        JsonArray params = new JsonArray();
        params.add(auth.getJsonObject("user").getString("userid"));
        postgreSQLClient.getConnection(ar -> {
            if (ar.succeeded()){
                SQLConnection connection = ar.result();
                connection.queryWithParams("SELECT b.date,t.get_money,b.is_enable FROM ybsport_buy b " +
                        "LEFT JOIN ybsport_type t ON t.id = b.type AND (b.yb_user::json#>>'{userid}')::text = ? " +
                        "ORDER BY b.date DESC,t.get_money ASC",params,res -> {
                    connection.close();
                    if (res.succeeded()) {
                        List<JsonObject> list = res.result().getResults()
                                .stream()
                                .map(StringToPojoJson::toBuyJson)
                                .collect(Collectors.toList());
                        message.reply(new JsonObject().put("list",list));
                    } else {
                        reportQueryError(message, ar.cause(), "Result failed!");
                    }
                });
            } else {
                reportQueryError(message, ar.cause(), "Connection failed!");
            }
        });
    }

    private void buy(Message<JsonObject> message) {
        YbSportBuy buy = YbSportBuy.fromJsonObject(message.body());
        Future<SQLConnection> connectionFuture = Future.future();
        Future<ResultSet> compareFuture = Future.future();
        Future<ResultSet> queryFuture = Future.future();
        Future<UpdateResult> updateFuture = Future.future();
        postgreSQLClient.getConnection(connectionFuture);
        connectionFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                ar.result().queryWithParams("SELECT t.* FROM public.ybsport_type t WHERE t.id = ?",
                        new JsonArray().add(buy.getType()), compareFuture);
            } else {
                reportQueryError(message, ar.cause(), "Compare Query Error!");
            }
        });


        compareFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                List<JsonArray> collect = ar.result().getResults()
                        .stream()
                        .filter(json -> buy.getSportSteps() >= json.getLong(1))
                        .collect(Collectors.toList());
                if (collect.size() == 0) {
                    connectionFuture.result().close();
                    message.reply(new JsonObject().put("status", "failed").put("message", "您今日的运动步数不足哦~请运动下再来吧！"));
                } else {
                    JsonArray params = new JsonArray().add(new JsonObject(buy.getYbUser()).getString("userid"))
                            .add(buy.getDate().toString()).add(buy.getType());
                    connectionFuture.result().queryWithParams("SELECT t.* FROM public.ybsport_buy t " +
                            "WHERE (t.yb_user::json#>>'{userid}')::text = ? " +
                            "AND t.date = ? AND t.type = ?", params, queryFuture);
                }
            } else {
                reportQueryError(message, ar.cause(), "Query Error!");
            }
        });

        queryFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                List<JsonArray> results = ar.result().getResults();
                if (results.size() == 0) {
                    JsonArray params = new JsonArray().add(buy.getYbUser()).add(buy.getSportSteps()).add(buy.getType());
                    connectionFuture.result().updateWithParams("INSERT INTO public.ybsport_buy (id, yb_user, sport_steps, type, date ,is_enable) " +
                            "VALUES (DEFAULT, ?, ?, ?, DEFAULT, DEFAULT)", params, updateFuture);
                } else {
                    connectionFuture.result().close();
                    message.reply(new JsonObject().put("status", "failed").put("message", "您今日的已经兑换过了~请明天再来吧！"));
                }
            } else {
                connectionFuture.result().close();
                reportQueryError(message, ar.cause(), "Query Result Error!");
            }
        });

        updateFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                int updated = ar.result().getUpdated();
                if (updated > 0) {
                    message.reply(new JsonObject().put("status", "success"));
                }
            } else {
                reportQueryError(message, ar.cause(), "Update Error!");
            }
            connectionFuture.result().close();
        });
    }

    private void getInfo(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject reply = new JsonObject();
        Future<SQLConnection> typeConnection = Future.future();
        Future<SQLConnection> userConnection = Future.future();
        Future<JsonObject> typeResult = Future.future();
        Future<JsonObject> userResult = Future.future();
        postgreSQLClient.getConnection(connection -> {
            if (connection.succeeded()) {
                LOGGER.info("Get Type Connection succeeded!");
                typeConnection.complete(connection.result());
            } else {
                typeConnection.fail(connection.cause());
                reportQueryError(message, connection.cause(), "Get Type Connection failed!");
            }
        });
        postgreSQLClient.getConnection(connection -> {
            if (connection.succeeded()) {
                LOGGER.info("Get User Connection succeeded!");
                userConnection.complete(connection.result());
            } else {
                userConnection.fail(connection.cause());
                reportQueryError(message, connection.cause(), "Get User Connection failed!");
            }
        });
        CompositeFuture.all(typeConnection, userConnection).setHandler(connections -> {
            if (connections.succeeded()) {
                LOGGER.info("Both connections are ready for use!");
                SQLConnection tCon = typeConnection.result();
                SQLConnection uCon = userConnection.result();
                tCon.query("SELECT t.* FROM public.ybsport_type t where is_enable = true ORDER BY t.need_steps",
                        res -> {
                            if (res.succeeded()) {
                                List<JsonObject> list = res.result()
                                        .getResults()
                                        .stream()
                                        .map(json -> StringToPojoJson.toYbSportType(json.toString()))
                                        .collect(Collectors.toList());
                                tCon.close();
                                typeResult.complete(new JsonObject().put("list", new JsonArray(list)));
                            } else {
                                reportQueryError(message, res.cause(), "Result1 failed!");
                            }
                        });
                uCon.queryWithParams("SELECT t.* FROM public.ybsport_buy t WHERE (t.yb_user::json#>>'{userid}')::text = ? AND date = ?",
                        new JsonArray().add(request.getJsonObject("user").getString("userid")).add(LocalDate.now().toString()),
                        res -> {
                            if (res.succeeded()) {
                                List<JsonObject> results = res.result()
                                        .getResults()
                                        .stream()
                                        .map(StringToPojoJson::toYbSportBuy)
                                        .collect(Collectors.toList());
                                uCon.close();
                                userResult.complete(new JsonObject().put("buy", results));
                            } else {
                                reportQueryError(message, res.cause(), "Result2 failed!");
                            }
                        });
            } else {
                reportQueryError(message, connections.cause(), "Both or one connections attempt failed!");
            }
        });
        CompositeFuture.all(typeResult, userResult).setHandler(results -> {
            if (results.succeeded()) {
                LOGGER.info("Both results are ready for use!");
                JsonObject t = results.result().resultAt(0);
                JsonObject u = results.result().resultAt(1);
                reply.mergeIn(t);
                reply.mergeIn(u);
                message.reply(reply);
            } else {
                reportQueryError(message, results.cause(), "Both or one result attempt failed!");
            }
        });
    }

    private void reportQueryError(Message<JsonObject> message, Throwable cause, String show) {
        LOGGER.error("Database query error!", cause);
        message.fail(ReasultBuilder.QUERY_ERROR, cause.getMessage());
    }


}
