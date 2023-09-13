package com.gk.reggie.dto;


import com.gk.reggie.entity.Dish;
import com.gk.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *这个类继承自Dish类，因此它包含了Dish类中的所有属性和方法。除此之外，DishDto类还有以下属性：
 * flavors - 一个List类型的属性，用于存储DishFlavor对象的集合。这个属性没有在Dish类中定义，因此它是DishDto类自己定义的属性。
 * categoryName - 一个String类型的属性，用于存储Dish所属的category的名字。
 * copies - 一个Integer类型的属性，用于存储Dish的价格
 */
@Data
public class DishDto extends Dish {
    //菜品对应的口味信息
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
