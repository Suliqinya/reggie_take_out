package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.entity.Employee;
import com.gk.reggie.mapper.EmployeeMapper;
import com.gk.reggie.service.EmployyeService;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public class EmployyeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployyeService {
}
