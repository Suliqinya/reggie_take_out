package com.gk.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
