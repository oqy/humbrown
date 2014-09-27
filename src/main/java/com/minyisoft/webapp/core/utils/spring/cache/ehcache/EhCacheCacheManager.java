package com.minyisoft.webapp.core.utils.spring.cache.ehcache;

import java.io.IOException;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager;

/**
 * CacheManager implementation for EhCache
 * 
 * @author qingyong_ou
 */
public class EhCacheCacheManager extends org.springframework.cache.ehcache.EhCacheCacheManager implements
		ModelCacheManager, DisposableBean {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	// 是否隐式创建cacheManager
	private boolean cacheManagerImplicitlyCreated = false;

	public EhCacheCacheManager() throws CacheException, IOException {
		setCacheManager(CacheManager.create(getClass().getResource("ehcache.xml")));
		cacheManagerImplicitlyCreated = true;
	}

	public Cache getCache(final String name) {
		if (!getCacheNames().contains(name)) {
			if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType()));
				if (modelClass != null) {
					getCacheManager().addCache(name);
					Cache cache = new EhCacheModelCache(getCacheManager().getCache(name), modelClass);
					addCache(cache);
					return cache;
				}
			} else if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType()));
				if (modelClass != null) {
					getCacheManager().addCache(name);
					Cache cache = new EhCacheModelQueryCache(getCacheManager().getCache(name), modelClass);
					addCache(cache);
					return cache;
				}
			}
		}
		return super.getCache(name);
	}

	@Override
	public Cache getModelCache(Class<? extends IModelObject> modelClazz) {
		Assert.notNull(modelClazz);
		return getCache(ModelCacheTypeEnum.MODEL_CACHE.getCacheName(modelClazz));
	}

	@Override
	public Cache getModelQueryCache(Class<? extends IModelObject> modelClazz) {
		Assert.notNull(modelClazz);
		return getCache(ModelCacheTypeEnum.MODEL_QUERY_CACHE.getCacheName(modelClazz));
	}

	@Override
	public void clearAllCache() {
		getCacheManager().clearAll();
	}

	@Override
	public void destroy() throws Exception {
		if (cacheManagerImplicitlyCreated) {
			try {
				logger.warn("关闭EhCache CacheManager实例");
				getCacheManager().shutdown();
			} catch (Exception e) {
				logger.warn("无法关闭EhCache CacheManager实例，忽略关闭操作");
			}
		}
	}
}
