package com.minyisoft.webapp.core.utils.spring.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheManager.ModelCacheTypeEnum;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelObjectCacheType {
	/**
	 * Model对象类
	 * 
	 * @return
	 */
	Class<? extends CoreBaseInfo>[] modelType();

	/**
	 * 缓存类型 实体缓存 or 查询缓存
	 * 
	 * @return
	 */
	ModelCacheTypeEnum[] cacheType() default ModelCacheTypeEnum.MODEL_CACHE;
}
