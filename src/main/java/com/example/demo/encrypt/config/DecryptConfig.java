package com.example.demo.encrypt.config;

import com.example.demo.encrypt.core.DecryptFilter;
import com.example.demo.encrypt.init.ApiDecryptDataInit;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 解密自动配置
 *
 * @author 马亮
 * @date 2021/3/8 17:20
 */
@Configuration
public class DecryptConfig {

    /**
     * 注册Filter
     *
     * @return 过滤器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DecryptFilter());
        registration.addUrlPatterns("/*");
        registration.setName("DecryptFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * Api解密数据初始化
     *
     * @return
     */
    @Bean
    public ApiDecryptDataInit apiEncryptDataInit() {
        return new ApiDecryptDataInit();
    }
}
