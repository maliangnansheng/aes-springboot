package com.example.demo.encrypt.annotation;

import java.lang.annotation.*;

/**
 * 数据加密/解密
 *
 * @author 马亮
 * @date 2021/3/5 10:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Encrypt {
    /**
     * 接口uri，比如 /xxxController/getList（基本用不上）
     * @return 加密请求地址
     */
    String value() default "";
}
