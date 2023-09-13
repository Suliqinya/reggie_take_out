package com.gk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.reggie.common.R;
import com.gk.reggie.entity.Employee;
import com.gk.reggie.service.EmployyeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployyeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        //3.如果没有查询到则返回登陆失败结果
        if ( emp == null){
            return R.error("登陆失败");
        }

        //4.密码比对，如果不一致则返回登陆失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看员工状态，如果未以禁用状态，则返回员工以禁用结果

        if( emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6.登陆成功，将员工id存入Session并返回登陆结果

        request.getSession().setAttribute("employee",emp.getId());


        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        log.info("添加员工,员工信息{}",employee.toString());

        //设置初始密码123456，并且需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());

        //设置创建人
        Long empId=(Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        //设置更新人
       // employee.setUpdateUser(empId);

        //添加到数据库
        employeeService.save(employee);
        return R.success("添加员工成功！");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
  public R<Page> page(int page,int pageSize,String name){

        log.info("page = {} , pageSize = {} name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw= new LambdaQueryWrapper();

        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
  }

    /**
     * 根据ID修改员工信息
     * @param employee
     * @return
     */
  @PutMapping
  public R<String> update(@RequestBody Employee employee){

//      Long empId = (Long)request.getSession().getAttribute("employee");

       // employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
       //根据ID获取用户对象
      employeeService.updateById(employee);

      //前端如果是点击禁用，传过来的status就是0,反之亦然



        return R.success("员工信息修改成功");
  }

  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable long id){

      Employee emp = employeeService.getById(id);
      if(emp != null){
         return R.success(emp);
     }
        return R.error("没有查询到对应员工信息！");
  }


}
