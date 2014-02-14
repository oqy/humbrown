package com.minyisoft.webapp.core.utils.spring.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qingyong_ou 批量清空IModelObject对象缓存的扩展注解
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelCachesEvict {
	ModelCacheEvict[] value();
}
