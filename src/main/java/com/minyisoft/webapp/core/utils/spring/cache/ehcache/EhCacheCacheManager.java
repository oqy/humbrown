package com.minyisoft.webapp.core.utils.spring.cache.ehcache;

import java.io.IOException;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheManager;

/**
 * CacheManager implementation for EhCache
 * 
 * @author qingyong_ou
 */
public class EhCacheCacheManager extends org.springframework.cache.ehcache.EhCacheCacheManager implements
		ModelObjectCacheManager {

	public EhCacheCacheManager() throws CacheException, IOException {
		setCacheManager(CacheManager.create(getClass().getResource("ehcache.xml")));
	}

	@Override
	protected Cache getMissingCache(String name) {
		Cache cache = super.getMissingCache(name);
		if (cache == null) {
			if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType()));
				if (modelClass != null) {
					getCacheManager().addCache(name);
					cache = new EhCacheModelCache(getCacheManager().getCache(name), modelClass);
					addCache(cache);
				}
			} else if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType()));
				if (modelClass != null) {
					getCacheManager().addCache(name);
					cache = new EhCacheModelQueryCache(getCacheManager().getCache(name), modelClass);
					addCache(cache);
				}
			} else {
				getCacheManager().addCache(name);
				cache = new EhCacheCache(getCacheManager().getCache(name));
				addCache(cache);
			}
		}
		return cache;
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
}
