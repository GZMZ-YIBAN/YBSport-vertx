package cn.echocow.yiban.ybsport.utils;

import io.vertx.core.Vertx;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 单例 vertx
 * @date 2018-08-23 22:48
 * <p>
 * -----------------------------
 **/
public class VertxSingleton {
    public static final Vertx VERTX = Vertx.vertx();
}
