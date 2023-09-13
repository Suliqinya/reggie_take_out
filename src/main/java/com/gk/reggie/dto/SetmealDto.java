package com.gk.reggie.dto;


import com.gk.reggie.entity.Setmeal;
import com.gk.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
