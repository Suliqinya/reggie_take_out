package com.gk.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前用户登陆的id
 * @author 你是海蜇吗？
 * @version 1.0
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static  Long getCurrentId(){
        return threadLocal.get();
    }
}
