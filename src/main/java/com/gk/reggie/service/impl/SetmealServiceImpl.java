package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.common.CustomException;
import com.gk.reggie.dto.DishDto;
import com.gk.reggie.dto.SetmealDto;
import com.gk.reggie.entity.Setmeal;
import com.gk.reggie.entity.SetmealDish;
import com.gk.reggie.mapper.SetmealMapper;
import com.gk.reggie.service.SetmealDishService;
import com.gk.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
       //查询套餐状态，是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if (count>1){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

       //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

       //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lqw =new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw);
    }

    /**
     *根据Id获取信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal= this.getById(id);
        SetmealDto setmealDto =new SetmealDto();
        //对象拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    /**
     * 修改套餐信息，同时套餐关联信息
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐表
        this.updateById(setmealDto);
        //更新套餐关系表
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        //更新套餐菜品关系表
        //1.删除对应getSetmealId的setmeal_dish表信息
        setmealDishService.remove(queryWrapper);
        //2.获取提交上来的setmeal_dish表信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //3.遍历集合，给每一个集合中的SetmealId属性赋值
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //4.批量添加setmeal_dish表信息
        setmealDishService.saveBatch(setmealDishes);

    }


}
