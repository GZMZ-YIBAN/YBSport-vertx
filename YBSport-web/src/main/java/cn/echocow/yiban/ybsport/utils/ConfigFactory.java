package cn.echocow.yiban.ybsport.utils;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;


/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 参数配置工厂
 * @date 2018-08-24 11:11
 * <p>
 * -----------------------------
 **/
public class ConfigFactory {
    /**
     * 可以获取到配置文件的配置信息
     */
    public static ConfigRetriever retriever;

    static {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "conf/config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
        retriever = ConfigRetriever.create(VertxSingleton.VERTX, options);
    }
}
