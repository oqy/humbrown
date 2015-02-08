package com.minyisoft.webapp.core.utils.mybatis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatisDAO注解,方便MapperScannerConfigurer扫描，继承自IBaseDao的DAO接口无需标识该注解
 * 
 * @author qingyong_ou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyBatisRepository {
}
