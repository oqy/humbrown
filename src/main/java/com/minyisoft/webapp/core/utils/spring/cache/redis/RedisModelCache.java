package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.google.common.base.Charsets;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.persistence.CacheableDao;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;

class RedisModelCache extends RedisCache {
	private final Class<? extends IModelObject> modelClass;
	private final byte[] hashName;
	
	private final Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 * Constructs a new <code>RedisModelCache</code> instance.
	 * 
	 * @param modelClass
	 * @param template
	 * @param expiration
	 */
	RedisModelCache(Class<? extends IModelObject> modelClass, JedisTemplate template, int expiration) {
		super(CacheableDao.MODEL_CACHE+ObjectUuidUtils.getClassShortKey(modelClass),template,expiration);
		Assert.notNull(modelClass,"缓存对应业务类不允许为空");
		this.modelClass = modelClass;
		this.hashName = (getName() + "~hashkeys").getBytes(defaultCharset);
	}
	
	@Override
	protected ValueWrapper _get(Jedis jedis, Object key) {
		if(!(key instanceof String)){
			return null;
		}
		byte[] bs = null;
		byte[] keyInByte = computeKey(key);
		if (!ObjectUuidUtils.isLegalId(modelClass,(String)key)) {
			byte[] hashKey = jedis.hget(hashName, keyInByte);
			if (hashKey!=null&&ArrayUtils.isNotEmpty(hashKey)) {
				bs=jedis.get(hashKey);
			}
		} else {
			bs=jedis.get(keyInByte);
		}
		try {
			if(logger.isDebugEnabled()){
				logger.debug("读取redis缓存["+modelClass.getName()+"]:"+keyInByte);
			}
			return (bs == null ? null : new SimpleValueWrapper(JsonMapper.MODEL_OBJECT_MAPPER.getMapper().readValue(bs, modelClass)));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	@Override
	protected boolean isObjectCacheable(Object key, Object value) {
		return key instanceof String&&value instanceof IModelObject;
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
	}
}
