package cn.echocow.yiban.ybsport.utils;

/**
 * -----------------------------
 *
 * @author EchoCow
 * @program YBSport
 * @description 函数式接口
 * @date 2018-08-23 22:48
 * <p>
 * -----------------------------
 **/
@FunctionalInterface
public interface Function4args<T,R> {
    /**
     * T 作为输入， R作为返回值
     * @param one 参数1
     * @param two 参数2
     * @param three 参数3
     * @param four 参数4
     * @return 返回值R
     */
    R apply(T one, T two, T three, T four);
}
