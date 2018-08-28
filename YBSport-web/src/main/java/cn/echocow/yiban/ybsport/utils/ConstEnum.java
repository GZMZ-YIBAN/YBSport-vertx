package cn.echocow.yiban.ybsport.utils;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 常量枚举
 * @date 2018-08-24 23:52
 * <p>
 * -----------------------------
 **/
@SuppressWarnings("all")
public enum ConstEnum {
    /**
     * 易班应用授权地址
     */
    YB_AUTH("https://oauth.yiban.cn/code/html"),
    /**
     * 易班app运动计步数据
     */
    YB_STEPS("https://openapi.yiban.cn/extend/sport_steps"),
    /**
     * 易班应用 id
     */
    APP_ID("43116dfd33792348"),
    /**
     * 易班应用密钥
     */
    APP_SECRET("dc1e0c028be8ad8b68138e7cd570203d"),
    /**
     * 登陆成功的回调地址
     */
    REDIRECT_URI("http://f.yiban.cn/iapp260959"),
    /**
     * 易班防跨站伪造参数
     */
    STATE("EchoCow"),
    /**
     * 虎丘参数为空
     */
    STRING_NULL("null"),
    /**
     * 标识符
     */
    FLAG("flag");


    private final String name;

    private ConstEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
