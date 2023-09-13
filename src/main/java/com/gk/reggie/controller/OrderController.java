package com.gk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.reggie.common.BaseContext;
import com.gk.reggie.common.R;
import com.gk.reggie.entity.Orders;
import com.gk.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据 {}",orders.toString());
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 分页查询订单信息
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime){


        log.info("page: " + page + ", pageSize: " + pageSize+" beginTime "+beginTime+" endTime "+endTime);

        // 格式化时间参数
        if (beginTime!=null || endTime!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String beginTimeStr = beginTime.format(formatter);
            String endTimeStr = endTime.format(formatter);
        }

        //获取用户id
        Long userId = BaseContext.getCurrentId();

        //构造分页构造器
        Page<Orders> pageInfo =new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        queryWrapper.eq(beginTime!=null,Orders::getOrderTime,beginTime);
        queryWrapper.eq(endTime!=null,Orders::getCheckoutTime,endTime);

        Page<Orders> ordersPage = ordersService.page(pageInfo, queryWrapper);

        return R.success(ordersPage);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("orders {} ",orders);
        Orders ordersById = ordersService.getById(orders);
        ordersById.setStatus(orders.getStatus());
        ordersService.updateById(ordersById);
        return R.success("修改订单状态成功");
    }

    @GetMapping("/userPage")
    public R<Page> checkOrder(int page,int pageSize){
        log.info("page {}  pageSize {} ",page,pageSize);
            //获取用户id
        Long userId = BaseContext.getCurrentId();

        //根据用户id获取订单信息
        Page<Orders> pagheInfo= new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId!=null,Orders::getUserId,userId);

        Page<Orders> ordersPage = ordersService.page(pagheInfo, queryWrapper);

        return R.success(ordersPage);
    }
}
