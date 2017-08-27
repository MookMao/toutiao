package com.mook.toutiao.configuration;

import com.mook.toutiao.interceptor.LoginRequiredInterceptor;
import com.mook.toutiao.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    /**
     * 启动服务时就会进行拦截器的注册
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //处理顺序跟注册顺序有关
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
        super.addInterceptors(registry);
    }
}
