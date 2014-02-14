package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.google.common.base.Charsets;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager.ModelCacheTypeEnum;

class RedisModelCache extends RedisCache {
	private final Class<? extends IModelObject> modelClass;
	private final byte[] hashName;
	
	/**
	 * 
	 * Constructs a new <code>RedisModelCache</code> instance.
	 * 
	 * @param modelClass
	 * @param template
	 * @param expiration
	 */
	RedisModelCache(Class<? extends IModelObject> modelClass,
			JedisTemplate template, int expiration) {
		super(ModelCacheTypeEnum.MODEL_CACHE.getCacheName(modelClass),
				template, expiration);
		this.modelClass = modelClass;
		this.hashName = (getName() + "~hashkeys").getBytes(defaultCharset);
	}
	
	@Override
	protected ValueWrapper _get(Jedis jedis, Object key) {
		if (!(key instanceof String)) {
			return null;
		}
		byte[] bs = null;
		byte[] keyBytes = computeKey(key);
		if (!ObjectUuidUtils.isLegalId(modelClass, (String) key)) {
			byte[] hashKey = jedis.hget(hashName, keyBytes);
			if (hashKey != null && ArrayUtils.isNotEmpty(hashKey)) {
				bs = jedis.get(hashKey);
			}
		} else {
			bs = jedis.get(keyBytes);
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("读取redis缓存[" + modelClass.getName() + "]:"
						+ new String(keyBytes, Charsets.UTF_8));
			}
			return (bs == null ? null : new SimpleValueWrapper(
					JsonMapper.MODEL_OBJECT_MAPPER.getMapper().readValue(bs, modelClass)));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	protected boolean isObjectCacheable(Object key, Object value) {
		return key instanceof String && value instanceof IModelObject;
	}
	
	@Override
	protected byte[] _put(Transaction transaction, Object key,Object value) {
		IModelObject model = (IModelObject) value;
		byte[] cacheKey=computeKey(key);
		if (!StringUtils.equals((String) key, model.getId())) {
			transaction.hset(hashName, cacheKey, computeKey(model.getId()));
			if(getExpiration()>0){
				transaction.expire(hashName, getExpiration());
			}
			cacheKey = computeKey(model.getId());
		}
		try {
			byte[] cacheByte=JsonMapper.MODEL_OBJECT_MAPPER.getMapper().writeValueAsBytes(model);
			transaction.set(cacheKey, cacheByte);
			if(logger.isDebugEnabled()){
				logger.debug("写入redis缓存["+modelClass.getName()+"]:"+new String(cacheByte, Charsets.UTF_8));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return cacheKey;
	}

	public void evict(Object key) {
		if (logger.isDebugEnabled()) {
			logger.debug("删除redis缓存[" + modelClass.getName() + "]:" + key);
		}
		if (key instanceof List<?> && !((List<?>) key).isEmpty()) {
			for (Object k : (List<?>) key) {
				super.evict(k);
			}
		} else {
			super.evict(key);
		}
	}
	
	@Override
	protected void _furtherClear(Jedis jedis) {
		jedis.del(hashName);
		if (logger.isDebugEnabled()) {
			logger.debug("清空redis缓存[" + modelClass.getName() + "]:" + getName());
		}
	}
}
