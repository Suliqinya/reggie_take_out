package com.gk.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置MP的分页插件
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Configuration
public class MybatisConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor intercept =new MybatisPlusInterceptor();
        intercept.addInnerInterceptor(new PaginationInnerInterceptor());
        return intercept;
    }
}
