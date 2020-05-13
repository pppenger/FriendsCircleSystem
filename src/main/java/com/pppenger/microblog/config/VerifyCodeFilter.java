package com.pppenger.microblog.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class VerifyCodeFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/login")&&request.getMethod().equalsIgnoreCase("post")){
            try {
                validate(request);
            } catch (VerifyCodeException e) {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("验证码不正确！");
                return;
            }
        }
        // 3. 校验通过，就放行
        filterChain.doFilter(request, response);
    }
    /* 验证保存在session的验证码和表单提交的验证码是否一致 */
    private void validate(HttpServletRequest request) throws ServletRequestBindingException,VerifyCodeException {
        String captcha = ServletRequestUtils.getStringParameter(request, "captcha");
        String code = (String) request.getSession().getAttribute(request.getParameter("uuid"));
        System.out.println("获取提交的code"+captcha);
        System.out.println("获取保存的code"+code);
        if(!code.equalsIgnoreCase(captcha)){
            throw new VerifyCodeException("验证码不正确！");
        }
        request.getSession().removeAttribute(request.getParameter("uuid"));
    }
}