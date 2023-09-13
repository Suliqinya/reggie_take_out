package com.gk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.reggie.dto.SetmealDto;
import com.gk.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询套餐信息和套餐菜品关系信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    /**
     * 修改套餐信息，同时套餐关联信息
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
}
