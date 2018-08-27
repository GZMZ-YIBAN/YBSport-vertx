package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.utils.ConfigFactory;
import cn.echocow.yiban.ybsport.utils.VertxSingleton;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description db
 * @date 2018-08-28 00:36
 * <p>
 * -----------------------------
 **/
public class DbTest extends AbstractVerticle {
//    public static void main(String[] args) {
//        Vertx vertx = Vertx.vertx();
//        vertx.deployVerticle(DbTest.class.getName());
//    }
    private AsyncSQLClient postgreSQLClient;
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ConfigFactory.retriever.getConfig(res -> {
            if (res.succeeded()) {
                postgreSQLClient = PostgreSQLClient.createShared(VertxSingleton.VERTX, res.result().getJsonObject("database"));
                date();
                startFuture.complete();
            } else {
                System.out.println("errrrrrrror");
            }

        });

    }


    private void date(){
        LocalDateTime now = LocalDateTime.now();
        postgreSQLClient.getConnection(ar -> {
            if (ar.succeeded()) {
                SQLConnection connection = ar.result();
                connection.query("SELECT * FROM public.ybsport_time WHERE is_enable = TRUE ORDER BY id LIMIT 1",res -> {
                    if (res.succeeded()) {
                        List<JsonArray> results = res.result().getResults();
                        if (results.size() == 0){
                            LOGGER.error("none");
                        }
                        JsonArray objects = results.get(0);
                        LocalDateTime start = LocalDateTime.parse(objects.getString(1));
                        LocalDateTime end = LocalDateTime.parse(objects.getString(2));
                        LOGGER.info("start:" + start);
                        LOGGER.info("end:" + end);
                        LOGGER.info("now:" + now);
                        if (now.isBefore(end) && now.isAfter(start)){
                            LOGGER.info("ok");
                        } else {
                            LOGGER.error("no");
                        }
                    } else {
                        LOGGER.error("error");
                    }
                    connection.close();
                });
            } else {
                System.out.println("as");
            }
        });
    }
}
