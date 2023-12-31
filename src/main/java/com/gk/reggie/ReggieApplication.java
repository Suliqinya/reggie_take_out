package com.gk.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement //开启事务支持
@EnableCaching              //开启缓存注解功能
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功！");
    }
}
