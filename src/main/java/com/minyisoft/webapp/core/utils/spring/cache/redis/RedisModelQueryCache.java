package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.Collection;

import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.google.common.base.Charsets;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager.ModelCacheTypeEnum;

class RedisModelQueryCache extends RedisCache {
	private final Class<? extends IModelObject> modelClass;

	/**
	 * 
	 * Constructs a new <code>RedisModelCache</code> instance.
	 * 
	 * @param modelClass
	 * @param template
	 * @param expiration
	 */
	RedisModelQueryCache(Class<? extends IModelObject> modelClass, JedisTemplate template, int expiration) {
		super(ModelCacheTypeEnum.MODEL_QUERY_CACHE.getCacheName(modelClass),
				template, expiration);
		this.modelClass = modelClass;
	}
	
	@Override
	protected ValueWrapper _get(Jedis jedis, Object key) {
		if (!(key instanceof String || key instanceof BaseCriteria)) {
			return null;
		}
		if (key instanceof BaseCriteria) {
			key = BaseCriteria.getKey((BaseCriteria) key);
		}
		byte[] keyBytes = computeKey(key);
		byte[] bs = jedis.get(keyBytes);
		try {
			logger.debug("读取redis集合缓存[" + modelClass.getName() + "]:"
					+ new String(keyBytes, Charsets.UTF_8));
			return (bs == null ? null : new SimpleValueWrapper(
					JsonMapper.MODEL_OBJECT_MAPPER.getMapper().readValue(
							bs,
							JsonMapper.MODEL_OBJECT_MAPPER
									.createCollectionType(Collection.class,
											modelClass))));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	protected boolean isObjectCacheable(Object key, Object value) {
		return (key instanceof String || key instanceof BaseCriteria)
				&& value instanceof Collection;
	}

	@Override
	protected byte[] _put(Transaction transaction, Object key, Object value) {
		@SuppressWarnings("unchecked")
		final Collection<? extends IModelObject> col = (Collection<? extends IModelObject>) value;
		if (key instanceof BaseCriteria) {
			key = BaseCriteria.getKey((BaseCriteria) key);
		}
		byte[] cacheKey = computeKey(key);
		try {
			byte[] cacheByte = JsonMapper.MODEL_OBJECT_MAPPER.getMapper().writeValueAsBytes(col);
			transaction.set(cacheKey, cacheByte);
			logger.debug("写入redis集合缓存[" + modelClass.getName() + "]:"
					+ new String(cacheByte, defaultCharset));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return cacheKey;
	}
	
	@Override
	protected void _furtherClear(Jedis jedis) {
		if (logger.isDebugEnabled()) {
			logger.debug("清空redis缓存[" + modelClass.getName() + "]:" + getName());
		}
	}
}
