package com.minyisoft.webapp.core.utils.spring.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager.ModelCacheTypeEnum;

/**
 * @author qingyong_ou 清空IModelObject对象的扩展注解
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelCacheEvict {	
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
	 * Whether or not all the entries inside the cache(s) are removed or not. By
	 * default, only the value under the associated key is removed.
	 * <p>Note that setting this parameter to {@code true} and specifying a {@link #key()}
	 * is not allowed.
	 */
	boolean allEntries() default false;

	/**
	 * Whether the eviction should occur after the method is successfully invoked (default)
	 * or before. The latter causes the eviction to occur irrespective of the method outcome (whether
	 * it threw an exception or not) while the former does not.
	 */
	boolean beforeInvocation() default false;

	/**
	 * Model对象类，配合CacheEvict的value值组装真正的cacheName
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
