package cn.echocow.yiban.ybsport.utils;

import io.vertx.core.json.JsonObject;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 返回值结果工具类
 * @date 2018-08-25 00:06
 * <p>
 * -----------------------------
 **/
@SuppressWarnings("all")
public class ReasultBuilder {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_500 = 500;
    public static final int RRDORECT = 302;
    public static final int NO_AUTH = 403;

    public static final String SUCCESS = "success";
    public static final String STATUS = "status";

    public static final int QUERY_ERROR = 1;
    public static final int INSERT_ERROR = 2;
    public static final int UPDATE_ERROR = 3;

    private ReasultBuilder() {

    }

    public static JsonObject buildSuccess(JsonObject data) {
        return new JsonObject().put("code", SUCCESS_CODE).put("data", data).put("msg", "请求成功！");
    }

    public static JsonObject buildSuccess() {
        return new JsonObject().put("code", SUCCESS_CODE).put("data", "null").put("msg", "请求成功！");
    }

    public static JsonObject buildError(JsonObject data, Integer code, String msg) {
        JsonObject result = new JsonObject();
        if (data != null){
            result.put("data", data);
        }else {
            result.put("data","null");
        }
        if (code == null) {
            result.put("code", ERROR_500);
        } else {
            result.put("code", code);
        }
        if (msg == null || msg.isEmpty()) {
            result.put("message", "请求失败，服务器错误!");
        } else {
            result.put("message", msg);
        }
        return result;
    }

    public static JsonObject buildError(Integer code) {
        return buildError(null, code, null);
    }

    public static JsonObject buildError(Integer code, String msg) {
        return buildError(null, code, msg);
    }

    public static JsonObject buildError(String msg) {
        return buildError(null, null, msg);
    }

    public static JsonObject buildError(JsonObject data) {
        return buildError(data, null, null);
    }

    public static JsonObject buildError(JsonObject data, String msg) {
        return buildError(data, null, msg);
    }

    public static JsonObject buildError() {
        return buildError(null, null, null);
    }
}
