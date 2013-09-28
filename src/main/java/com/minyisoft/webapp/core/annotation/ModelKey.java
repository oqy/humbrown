package com.minyisoft.webapp.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qingyong_ou 
 * ICoreModel子类索引键注解，用于生成ModelClass实例id，及根据id获取ModelClass实例
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ModelKey {
	String value();
}
