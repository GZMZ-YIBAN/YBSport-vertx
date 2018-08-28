package cn.echocow.yiban.ybsport.pojo;


import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 易运动可兑换类型
 * @date 2018-08-24 23:01
 * <p>
 * -----------------------------
 **/
public class YbSportType {

    private long id;
    private long needSteps;
    private long getMoney;
    private String isEnable;

    public YbSportType(long id, long needSteps, long getMoney, String isEnable) {
        this.id = id;
        this.needSteps = needSteps;
        this.getMoney = getMoney;
        this.isEnable = isEnable;
    }

    public YbSportType(String id, String needSteps, String getMoney, String isEnable) {
        this.id = Long.parseLong(id);
        this.needSteps = Long.parseLong(needSteps);
        this.getMoney = Long.parseLong(getMoney);
        this.isEnable = isEnable;
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }

    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("id", id).put("needSteps", needSteps).put("getMoney", getMoney)
                .put("isEnable", isEnable);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getNeedSteps() {
        return needSteps;
    }

    public void setNeedSteps(long needSteps) {
        this.needSteps = needSteps;
    }


    public long getGetMoney() {
        return getMoney;
    }

    public void setGetMoney(long getMoney) {
        this.getMoney = getMoney;
    }


    public String getisEnable() {
        return isEnable;
    }

    public void setisEnable(String isEnable) {
        this.isEnable = isEnable;
    }

}
