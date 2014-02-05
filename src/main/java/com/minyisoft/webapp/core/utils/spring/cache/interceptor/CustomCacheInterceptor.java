package com.minyisoft.webapp.core.utils.spring.cache.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou
 * 自定义的缓存解释器，遇到@ModelCacheable或@ModelCacheEvict注解时，组装真正的cacheName以获取相应的cache
 * 
 * 使用自定义缓存注解时，spring配置文件需注释掉<cache:annotation-driven/>，可使用如下定义进行替换：
 * <bean id="annotationCacheOperationSource" class="org.springframework.cache.annotation.AnnotationCacheOperationSource">
 * 		<constructor-arg name="annotationParser">
 * 			<bean class="com.minyisoft.webapp.core.utils.spring.cache.interceptor.CustomSpringCacheAnnotationParser"/>
 * 		</constructor-arg>
 * </bean>
 * <bean id="cacheInterceptor" class="com.minyisoft.webapp.core.utils.spring.cache.interceptor.CustomCacheInterceptor">
 * 		<property name="cacheOperationSources" ref="annotationCacheOperationSource"/>
 * 		<property name="cacheManager" ref="cacheManager"/>
 * </bean>
 * <bean id="beanFactoryCacheOperationSourceAdvisor" class="org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor">
 * 		<property name="cacheOperationSource" ref="annotationCacheOperationSource"/>
 * 		<property name="adviceBeanName" value="cacheInterceptor"/>
 * </bean>
 */
@SuppressWarnings("serial")
public class CustomCacheInterceptor extends CacheInterceptor {
	@Override
	protected Collection<? extends Cache> getCaches(CacheOperation operation) {
		if (operation instanceof ModelCacheableOperation
				|| operation instanceof ModelCacheEvictOperation) {
			Set<String> cacheNames = operation.getCacheNames();
			Class<? extends IModelObject> modelType = (operation instanceof ModelCacheableOperation) ? ((ModelCacheableOperation) operation).getModelType() 
															: ((ModelCacheEvictOperation) operation).getModelType();
			Collection<Cache> caches = new ArrayList<Cache>(cacheNames.size());
			for (String cacheName : cacheNames) {
				Cache cache = getCacheManager().getCache(cacheName+ObjectUuidUtils.getClassShortKey(modelType));
				if (cache == null) {
					throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for " + operation);
				}
				caches.add(cache);
			}
			return caches;
		}
		return super.getCaches(operation);
	}
}
