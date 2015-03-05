package com.minyisoft.webapp.core.utils.spring.cache.ehcache;

import java.util.Collection;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

public class EhCacheModelQueryCache implements Cache {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final Ehcache cache;
	private final Class<? extends IModelObject> modelClass;

	public EhCacheModelQueryCache(Ehcache ehcache, Class<? extends IModelObject> modelClass) {
		Assert.notNull(ehcache, "Ehcache must not be null");
		Assert.notNull(modelClass, "Ehcache must not be null");
		Status status = ehcache.getStatus();
		Assert.isTrue(Status.STATUS_ALIVE.equals(status),
				"An 'alive' Ehcache is required - current cache is " + status.toString());
		this.cache = ehcache;
		this.modelClass = modelClass;
	}

	@Override
	public final String getName() {
		return this.cache.getName();
	}

	@Override
	public final Ehcache getNativeCache() {
		return this.cache;
	}

	@Override
	public ValueWrapper get(Object key) {
		if ((key instanceof String || key instanceof BaseCriteria)) {
			String cacheKey = (key instanceof String) ? (String) key : BaseCriteria.getKey((BaseCriteria) key);
			if (cache.get(cacheKey) != null) {
				try {
					String cacheValue = (String) cache.get(cacheKey).getObjectValue();
					logger.debug("读取EhCache集合缓存[" + modelClass.getName() + "]:" + cacheKey);
					return StringUtils.isBlank(cacheValue) ? null : new SimpleValueWrapper(
							JsonMapper.MODEL_OBJECT_MAPPER.fromJson(cacheValue,
									JsonMapper.MODEL_OBJECT_MAPPER.createCollectionType(Collection.class, modelClass)));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		ValueWrapper valueWrapper = get(key);
		return valueWrapper != null && type.isInstance(valueWrapper.get()) ? (T) valueWrapper.get() : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void put(Object key, Object value) {
		if ((key instanceof String || key instanceof BaseCriteria) && value instanceof Collection) {
			final Collection<? extends IModelObject> col = (Collection<? extends IModelObject>) value;
			String cacheKey = (key instanceof String) ? (String) key : BaseCriteria.getKey((BaseCriteria) key);
			try {
				cache.put(new Element(cacheKey, JsonMapper.MODEL_OBJECT_MAPPER.toJson(col)));
				logger.debug("写入EhCache集合缓存[" + modelClass.getName() + "]:" + cacheKey);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void evict(Object key) {
		this.cache.remove(key);
		if (logger.isDebugEnabled()) {
			logger.debug("删除ehcache缓存[" + modelClass.getName() + "]:" + key);
		}
	}

	@Override
	public void clear() {
		this.cache.removeAll();
		if (logger.isDebugEnabled()) {
			logger.debug("清空ehcache缓存[" + modelClass.getName() + "]");
		}
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
}
