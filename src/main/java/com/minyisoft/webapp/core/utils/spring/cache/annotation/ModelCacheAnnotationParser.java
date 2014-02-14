package com.minyisoft.webapp.core.utils.spring.cache.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.util.Assert;


/**
 * @author qingyong_ou
 * 自定义缓存注解parser，用于解释自定义的@ModelCacheable、@ModelCacheEvict及@ModelCachesEvict注解
 * <cache:annotation-driven/>不会使用该parser，需启用parser时，spring配置文件可按以下方式进行配置
 * <bean id="annotationCacheOperationSource"
		class="org.springframework.cache.annotation.AnnotationCacheOperationSource">
		<constructor-arg name="annotationParser">
			<bean class="com.fusung.webapp.core.utils.spring.cache.annotation.ModelCacheAnnotationParser" />
		</constructor-arg>
	</bean>
	<bean id="cacheInterceptor" class="org.springframework.cache.interceptor.CacheInterceptor">
		<property name="cacheOperationSources" ref="annotationCacheOperationSource" />
		<property name="cacheManager" ref="cacheManager" />
	</bean>
	<bean id="beanFactoryCacheOperationSourceAdvisor"
		class="org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor">
		<property name="cacheOperationSource" ref="annotationCacheOperationSource" />
		<property name="adviceBeanName" value="cacheInterceptor" />
	</bean>
 */
@SuppressWarnings("serial")
public class ModelCacheAnnotationParser extends
		SpringCacheAnnotationParser {
	@Override
	public Collection<CacheOperation> parseCacheAnnotations(AnnotatedElement ae) {
		Collection<CacheOperation> ops = super.parseCacheAnnotations(ae);
		Collection<ModelCacheable> caching = getAnnotations(ae, ModelCacheable.class);
		if (caching != null) {
			ops = lazyInit(ops);
			for (ModelCacheable c : caching) {
				ops.add(parseModelCacheableAnnotation(ae, c));
			}
		}
		Collection<ModelCacheEvict> cacheEvicts = getAnnotations(ae, ModelCacheEvict.class);
		if (cacheEvicts != null) {
			ops = lazyInit(ops);
			for (ModelCacheEvict e : cacheEvicts) {
				ops.add(parseModelCacheEvictAnnotation(ae, e));
			}
		}
		Collection<ModelCachesEvict> cachesEvicts = getAnnotations(ae, ModelCachesEvict.class);
		if (cachesEvicts != null) {
			ops = lazyInit(ops);
			for (ModelCachesEvict e : cachesEvicts) {
				for(ModelCacheEvict ce : e.value()){
					ops.add(parseModelCacheEvictAnnotation(ae, ce));
				}
			}
		}
		return ops;
	}
	
	/**
	 * 解释@modelCacheable注解
	 * @param ae
	 * @param modelCacheable
	 * @return
	 */
	CacheableOperation parseModelCacheableAnnotation(AnnotatedElement ae, ModelCacheable modelCacheable) {
		Assert.notNull(modelCacheable.modelType());
		Assert.notNull(modelCacheable.cacheType());
		
		CacheableOperation cuo = new CacheableOperation();
		cuo.setCacheNames(new String[] { modelCacheable.cacheType().getCacheName(modelCacheable.modelType()) });
		cuo.setCondition(modelCacheable.condition());
		cuo.setUnless(modelCacheable.unless());
		cuo.setKey(modelCacheable.key());
		cuo.setName(ae.toString());
		return cuo;
	}
	
	/**
	 * 解释@ModelCacheEvict注解
	 * @param ae
	 * @param modelCacheEvict
	 * @return
	 */
	CacheEvictOperation parseModelCacheEvictAnnotation(AnnotatedElement ae, ModelCacheEvict modelCacheEvict) {
		Assert.notNull(modelCacheEvict.modelType());
		Assert.notNull(modelCacheEvict.cacheType());

		CacheEvictOperation ceo = new CacheEvictOperation();
		ceo.setCacheNames(new String[] { modelCacheEvict.cacheType().getCacheName(modelCacheEvict.modelType()) });
		ceo.setCondition(modelCacheEvict.condition());
		ceo.setKey(modelCacheEvict.key());
		ceo.setCacheWide(modelCacheEvict.allEntries());
		ceo.setBeforeInvocation(modelCacheEvict.beforeInvocation());
		ceo.setName(ae.toString());
		return ceo;
	}
	
	private <T extends Annotation> Collection<CacheOperation> lazyInit(Collection<CacheOperation> ops) {
		return (ops != null ? ops : new ArrayList<CacheOperation>(1));
	}
	
	private <T extends Annotation> Collection<T> getAnnotations(AnnotatedElement ae, Class<T> annotationType) {
		Collection<T> anns = new ArrayList<T>(2);

		// look at raw annotation
		T ann = ae.getAnnotation(annotationType);
		if (ann != null) {
			anns.add(ann);
		}

		// scan meta-annotations
		for (Annotation metaAnn : ae.getAnnotations()) {
			ann = metaAnn.annotationType().getAnnotation(annotationType);
			if (ann != null) {
				anns.add(ann);
			}
		}

		return (anns.isEmpty() ? null : anns);
	}
}
