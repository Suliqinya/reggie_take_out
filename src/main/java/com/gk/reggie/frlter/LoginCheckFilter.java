package com.gk.reggie.frlter;

import com.alibaba.fastjson.JSON;
import com.gk.reggie.common.BaseContext;
import com.gk.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Slf4j
@WebFilter(filterName = "loginCheFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的uri
        String[] urls=new String[]{
                "/employee/logout",
                "/employee/login",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否要处理
        if (check(requestURI,urls)){
            //3.如果不需要处理则放行
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断登录员工状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") !=null){
            log.info("员工用户已登录,用户id为：{}",request.getSession().getAttribute("employee"));

            //把用户id存到当前http请求的线程中去
            Long empId=(long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }


        //4-2 .判断用户登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") !=null){
            log.info("用户已登录,用户id为：{}",request.getSession().getAttribute("user"));

            //把用户id存到当前http请求的线程中去
            Long userId=(long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
       //5.如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));


    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI,String[] urls){

        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
