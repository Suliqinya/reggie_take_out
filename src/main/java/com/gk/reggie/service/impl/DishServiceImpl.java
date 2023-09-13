package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.dto.DishDto;
import com.gk.reggie.entity.Dish;
import com.gk.reggie.entity.DishFlavor;
import com.gk.reggie.mapper.DishMapper;
import com.gk.reggie.service.DishFlavorService;
import com.gk.reggie.service.DishService;
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
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
   @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * 操作多张表
     * @param dishDto
     */
    @Override
    @Transactional //添加事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //对flavors集合进行流式操作，使用map方法对每个元素进行处理，将每个元素的dishId属性设置为dishId变量的值。
        //最后，使用collect方法将处理后的DishFlavor集合进行收集，并返回一个新的List集合。
        flavors.stream().map((itme)->{
            itme.setDishId(dishId);
            return itme;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
       //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        //将Dish对象(dish)的属性值拷贝到DishDto对象(dishDto)中。
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    /**
     *更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据--dish flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        /* 添加当前提交过来的口味数据--dish_flavor表的nserti操作 */
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.stream().map((itme)->{
            itme.setDishId(dishDto.getId());
            return itme;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


}
