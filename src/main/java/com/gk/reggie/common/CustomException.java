package com.gk.reggie.common;

/**
 * 自定义异常类
 * 自定义了删除分类是关联到菜品或者套餐时的异常
 * @author 你是海蜇吗？
 * @version 1.0
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
