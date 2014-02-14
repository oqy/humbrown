package com.minyisoft.webapp.core.utils.spring.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager.ModelCacheTypeEnum;

/**
 * @author qingyong_ou 缓存IModelObject对象的扩展注解
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelCacheable {
	/**
	 * Spring Expression Language (SpEL) attribute for computing the key dynamically.
	 * <p>Default is "", meaning all method parameters are considered as a key.
	 */
	String key() default "";

	/**
	 * Spring Expression Language (SpEL) attribute used for conditioning the method caching.
	 * <p>Default is "", meaning the method is always cached.
	 */
	String condition() default "";

	/**
	 * Spring Expression Language (SpEL) attribute used to veto method caching.
	 * <p>Unlike {@link #condition()}, this expression is evaluated after the method
	 * has been called and can therefore refer to the {@code result}. Default is "",
	 * meaning that caching is never vetoed.
	 * @since 3.2
	 */
	String unless() default "";

	/**
	 * Model对象类，配合Cacheable的value值组装真正的cacheName
	 * 
	 * @return
	 */
	Class<? extends IModelObject> modelType();

	/**
	 * 缓存类型 实体缓存 or 查询缓存
	 * 
	 * @return
	 */
	ModelCacheTypeEnum cacheType() default ModelCacheTypeEnum.MODEL_CACHE;
}
