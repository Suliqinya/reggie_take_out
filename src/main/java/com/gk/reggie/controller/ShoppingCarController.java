package com.gk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gk.reggie.common.BaseContext;
import com.gk.reggie.common.R;
import com.gk.reggie.entity.ShoppingCart;
import com.gk.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 *
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Slf4j
@RequestMapping("/shoppingCart")
@RestController
public class ShoppingCarController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("{}", shoppingCart);
        //设置用户id, 指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前来品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (dishId != null) {
            //当前添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //当前添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        //查询当前菜品或者套餐是否在购物车中
        if (cartServiceOne != null) {
            //如果己经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);

        } else {
            //如果不存在，则添加到购物车，数量默认就是一
             //设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车");
    }

    /**
     * 购物车减少
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public  R<String> delete(@RequestBody ShoppingCart shoppingCart){

        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //判断需要减少的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();

        if (dishId != null){
            //当前类型为菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //当前菜品为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //获取当前菜品或者套餐在购物车中的份数
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        //把份数在原有的基础上-1
        Integer number = cartServiceOne.getNumber();


        if(number-1 == 0){
            //如果number-1=0，则删除这条数据
            shoppingCartService.remove(queryWrapper);
        }else {
            //如果number-1=0，则修改number的数值为number-1
            cartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne);
        }

        return R.success("减少成功");
    }
}
