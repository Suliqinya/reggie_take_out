package com.gk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.reggie.entity.Orders;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public interface OrdersService  extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
