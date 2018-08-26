package cn.echocow.yiban.ybsport.pojo;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 易班用户
 * @date 2018-08-26 18:12
 * <p>
 * -----------------------------
 **/
public class YbUser {
    private String userId;
    private String userName;
    private String userNick;
    private String userSex;

    public YbUser(JsonObject object){
        this.userId = object.getString("userid");
        this.userName = object.getString("username");
        this.userNick = object.getString("usernick");
        if ("M".equals(object.getString("usersex"))){
            this.userSex = "小哥哥";
        } else {
            this.userSex = "小姐姐";
        }
    }

    @Override
    public String toString(){
        return Json.encode(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }
}
