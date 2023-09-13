package com.gk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.reggie.entity.Category;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
