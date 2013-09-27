package com.minyisoft.webapp.core.utils.spring.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.Cacheable;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou
 * 缓存IModelObject对象的扩展注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModelCacheable {
	Cacheable cacheable();
	/**
	 * Model对象类，配合Cacheable的value值组装真正的cacheName
	 * @return
	 */
	Class<? extends IModelObject> modelType();
}
