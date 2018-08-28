package cn.echocow.yiban.ybsport.pojo;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.sql.Date;
import java.time.LocalDate;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 易运动兑换记录 pojo
 * @date 2018-08-24 23:01
 * <p>
 * -----------------------------
 **/
public class YbSportBuy {

    private long id;
    private String ybUser;
    private long sportSteps;
    private long type;
    private Date date;
    private boolean isEnable;

    public YbSportBuy(long id, String ybUser, long sportSteps, long type, String data, boolean isEnable) {
        this.id = id;
        this.ybUser = ybUser;
        this.sportSteps = sportSteps;
        this.type = type;
        this.date = Date.valueOf(LocalDate.now());
        this.isEnable = false;
    }

    public YbSportBuy(String ybUser, long sportSteps, long type, String date) {
        this.ybUser = ybUser;
        this.sportSteps = sportSteps;
        this.type = type;
        this.date = Date.valueOf(LocalDate.now());
        this.isEnable = false;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public static YbSportBuy fromJsonObject(JsonObject object) {
        boolean flag = false;
        if (object.getBoolean("isEnable") != null) {
            flag = object.getBoolean("isEnabled");
        }
        return new YbSportBuy(object.getLong("id"), object.getJsonObject("ybUser").toString(),
                object.getLong("sportSteps"), object.getLong("type"), object.getString("date")
                , flag);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getYbUser() {
        return ybUser;
    }

    public void setYbUser(String ybUser) {
        this.ybUser = ybUser;
    }


    public long getSportSteps() {
        return sportSteps;
    }

    public void setSportSteps(long sportSteps) {
        this.sportSteps = sportSteps;
    }


    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }

    /**
     * 记录！！！！
     */
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("id", id).put("ybUser", new JsonObject(ybUser)).put("sportSteps", sportSteps)
                .put("type", type).put("date", date.toString());
    }

}
