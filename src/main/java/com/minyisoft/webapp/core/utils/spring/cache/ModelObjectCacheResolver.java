package com.minyisoft.webapp.core.utils.spring.cache;

import java.util.Collection;
import java.util.Set;

import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.collect.Sets;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheManager.ModelCacheTypeEnum;

/**
 * 根据方法或类的ModelObjectCacheType注解信息动态获取待操作cacheNames，
 * 无ModelObjectCacheType注解时调用基类方法获取
 * 
 * @author qingyong_ou
 */
public class ModelObjectCacheResolver extends SimpleCacheResolver {

	@Override
	protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
		ModelObjectCacheType modelCache = AnnotationUtils.findAnnotation(context.getMethod(),
				ModelObjectCacheType.class);
		if (modelCache == null) {
			modelCache = AnnotationUtils.findAnnotation(context.getTarget().getClass(), ModelObjectCacheType.class);
		}
		if (modelCache != null) {
			Set<String> cacheNames = Sets.newHashSet();
			for (ModelCacheTypeEnum type : modelCache.cacheType()) {
				for (Class<? extends CoreBaseInfo> clazz : modelCache.modelType()) {
					cacheNames.add(type.getCacheName(clazz));
				}
			}
			return cacheNames;
		}
		return super.getCacheNames(context);
	}
}
