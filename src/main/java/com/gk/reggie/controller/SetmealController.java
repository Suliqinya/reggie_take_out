package com.gk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.reggie.common.R;
import com.gk.reggie.dto.SetmealDto;
import com.gk.reggie.entity.Category;
import com.gk.reggie.entity.Setmeal;
import com.gk.reggie.service.CategoryService;
import com.gk.reggie.service.SetmealDishService;
import com.gk.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 * 为什么分页查询不使用缓存呢？
 * 因为使用缓存技术主要是解决在高并发的情况下，频繁查询数据库会导致系统性能下降
 * 分页查询的方法一般都是使用在后台管理，很少存在高并发的情况，使用可以不使用缓存技术
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * 并且删除套餐分类的缓存数据
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log .info("套餐信息{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return  R.success("新增套餐成功");
    }

    /**
     * 按照条件分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name) {
        log.info("page{}     pageSize{}   name{}",page,pageSize,name);
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);
        
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtoList = records.stream().map((itme) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(itme, setmealDto);

            //分类id
            Long categoryId = itme.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * 删除套餐分类的缓存数据
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public R<String> delete(List<Long> ids){
        log.info("ids = []",ids);
        setmealService.removeWithDish(ids);
        return null;
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     * 删除套餐分类的缓存数据
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 停售或者起售套餐
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,Long[] ids){
        log.info("Status = {} ids={}",status,ids);
        //根据id获取套餐对象
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        //修改套餐对象的Status
        setmealList.stream().map((itme)->{
            itme.setStatus(status);
            return itme;
        }).collect(Collectors.toList());

        boolean success= setmealService.updateBatchById(setmealList);
        if (success) {
            return R.success("操作成功");
        }else {
            return R.error("操作失败");
        }
    }

    /**
     * 根据条件查询套餐信息
     * 使用SpringCache框架和redis缓存套餐数据
     * @param setmeal
     * @return 返回结果的类需要实现序列化接口Serializable，否则前端芜湖获取返回结果
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //构造查询添加
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //排除停售的菜品
        queryWrapper.eq(Setmeal::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        return R.success(setmealList);
    }
}
