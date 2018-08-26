package cn.echocow.yiban.ybsport.utils;

import cn.echocow.yiban.ybsport.pojo.YbSportBuy;
import cn.echocow.yiban.ybsport.pojo.YbSportType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 字符串转化 pojo json
 * @date 2018-08-24 21:33
 * <p>
 * -----------------------------
 **/
public class StringToPojoJson {
    public static JsonObject toYbSportType(String str) {
        String[] split = str.substring(1, str.length() - 1).split(",");
        return new YbSportType(split[0], split[1], split[2], split[3]).toJsonObject();
    }

    public static JsonObject toYbSportBuy(JsonArray arr) {
        return new YbSportBuy(arr.getLong(0), arr.getString(1),
                arr.getLong(2), arr.getLong(3), arr.getString(4),arr.getBoolean(5)).toJsonObject();
    }

    public static JsonObject toBuyJson(JsonArray arr){
        JsonObject buy = new JsonObject();
        buy.put("date",arr.getString(0));
        buy.put("get_money",arr.getLong(1));
        buy.put("is_enable",arr.getBoolean(2));
        return buy;
    }
}
