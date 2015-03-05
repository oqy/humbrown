package com.minyisoft.webapp.core.utils.spring.cache.ehcache;

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
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

public class EhCacheModelCache implements Cache {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final Ehcache cache;
	private final Class<? extends IModelObject> modelClass;

	public EhCacheModelCache(Ehcache ehcache, Class<? extends IModelObject> modelClass) {
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
		if (key instanceof String && cache.get(key) != null) {
			String cacheValue = null;
			if (!ObjectUuidUtils.isLegalId(modelClass, (String) key)) {
				String objectId = (String) cache.get(key).getObjectValue();
				if (StringUtils.isNotBlank(objectId) && cache.get(objectId) != null) {
					cacheValue = (String) cache.get(objectId).getObjectValue();
				}
			} else {
				cacheValue = (String) cache.get(key).getObjectValue();
			}
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("读取EhCache缓存[" + modelClass.getName() + "]:" + cacheValue);
				}
				return StringUtils.isBlank(cacheValue) ? null : new SimpleValueWrapper(
						JsonMapper.MODEL_OBJECT_MAPPER.fromJson(cacheValue, modelClass));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

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

	@Override
	public void put(Object key, Object value) {
		IModelObject model = (IModelObject) value;
		if (!StringUtils.equals((String) key, model.getId())) {
			cache.put(new Element(key, model.getId()));
		}
		try {
			cache.put(new Element(model.getId(), JsonMapper.MODEL_OBJECT_MAPPER.toJson(model)));
			if (logger.isDebugEnabled()) {
				logger.debug("写入EhCache缓存[" + modelClass.getName() + "]:" + model.getId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void evict(Object key) {
		this.cache.remove(key);
		if (logger.isDebugEnabled()) {
			logger.debug("删除EhCache缓存[" + modelClass.getName() + "]:" + key);
		}
	}

	@Override
	public void clear() {
		this.cache.removeAll();
		if (logger.isDebugEnabled()) {
			logger.debug("清空EhCache缓存[" + modelClass.getName() + "]");
		}
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
}
