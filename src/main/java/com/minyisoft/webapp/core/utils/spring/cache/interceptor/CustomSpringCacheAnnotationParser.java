package com.minyisoft.webapp.core.utils.spring.cache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheOperation;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.spring.cache.annotation.ModelCacheEvict;
import com.minyisoft.webapp.core.utils.spring.cache.annotation.ModelCacheable;

/**
 * @author qingyong_ou
 * 自定义缓存注解parser
 */
@SuppressWarnings("serial")
public class CustomSpringCacheAnnotationParser extends
		SpringCacheAnnotationParser {
	@Override
	public Collection<CacheOperation> parseCacheAnnotations(AnnotatedElement ae) {
		Collection<CacheOperation> ops=super.parseCacheAnnotations(ae);
		Collection<ModelCacheable> caching = getAnnotations(ae, ModelCacheable.class);
		if (caching != null) {
			ops = lazyInit(ops);
			for (ModelCacheable c : caching) {
				ops.addAll(parseModelCacheableAnnotation(ae, c));
			}
		}
		Collection<ModelCacheEvict> evicts = getAnnotations(ae, ModelCacheEvict.class);
		if (evicts != null) {
			ops = lazyInit(ops);
			for (ModelCacheEvict e : evicts) {
				ops.addAll(parseModelCacheEvictAnnotation(ae, e));
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
	Collection<CacheOperation> parseModelCacheableAnnotation(AnnotatedElement ae, ModelCacheable modelCacheable) {
		Collection<CacheOperation> ops = null;

		Class<? extends IModelObject> modelType=modelCacheable.modelType();
		Cacheable cacheables = modelCacheable.cacheable();
		if (modelType!=null&&cacheables!=null) {
			ops = lazyInit(ops);
			
			ModelCacheableOperation cuo = new ModelCacheableOperation();
			cuo.setCacheNames(modelCacheable.cacheable().value());
			cuo.setCondition(modelCacheable.cacheable().condition());
			cuo.setUnless(modelCacheable.cacheable().unless());
			cuo.setKey(modelCacheable.cacheable().key());
			cuo.setName(ae.toString());
			cuo.setModelType(modelCacheable.modelType());
			ops.add(cuo);
		}

		return ops;
	}
	
	/**
	 * 解释@ModelCacheEvict注解
	 * @param ae
	 * @param modelCacheEvict
	 * @return
	 */
	Collection<CacheOperation> parseModelCacheEvictAnnotation(AnnotatedElement ae, ModelCacheEvict modelCacheEvict) {
		Collection<CacheOperation> ops = null;

		Class<? extends IModelObject> modelType=modelCacheEvict.modelType();
		CacheEvict cacheables = modelCacheEvict.evict();
		if (modelType!=null&&cacheables!=null) {
			ops = lazyInit(ops);
			
			ModelCacheEvictOperation ceo = new ModelCacheEvictOperation();
			ceo.setCacheNames(modelCacheEvict.evict().value());
			ceo.setCondition(modelCacheEvict.evict().condition());
			ceo.setKey(modelCacheEvict.evict().key());
			ceo.setCacheWide(modelCacheEvict.evict().allEntries());
			ceo.setBeforeInvocation(modelCacheEvict.evict().beforeInvocation());
			ceo.setName(ae.toString());
			ceo.setModelType(modelCacheEvict.modelType());
			ops.add(ceo);
		}

		return ops;
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
