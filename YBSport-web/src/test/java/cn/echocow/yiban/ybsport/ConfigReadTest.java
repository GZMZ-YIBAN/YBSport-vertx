package cn.echocow.yiban.ybsport;

import cn.echocow.yiban.ybsport.utils.VertxSingleton;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static io.vertx.core.parsetools.JsonEventType.VALUE;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 配置文件读取测试
 * @date 2018-08-24 01:13
 * <p>
 * -----------------------------
 **/

public class ConfigReadTest extends AbstractVerticle {
    private static JsonObject result = new JsonObject();
    private static final Vertx VERTX = Vertx.vertx();

    static {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "conf/config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(VERTX, options);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                throw new RuntimeException("Something went wring during getting the config");
            } else {
                result.mergeIn(ar.result());
                System.out.println(result.toString());
            }
        });
    }

    public static void main(String[] args) {
        VERTX.deployVerticle(ConfigReadTest.class.getName());
    }
}
