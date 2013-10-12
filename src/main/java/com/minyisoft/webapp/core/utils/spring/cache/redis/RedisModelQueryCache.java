package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.persistence.CacheableDao;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.mapper.json.ModelJsonMapper;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;

class RedisModelQueryCache extends RedisCache {
	private final Class<? extends IModelObject> modelClass;

	private final Logger logger=LoggerFactory.getLogger(getClass());

	/**
	 * 
	 * Constructs a new <code>RedisModelCache</code> instance.
	 * 
	 * @param modelClass
	 * @param template
	 * @param expiration
	 */
	RedisModelQueryCache(Class<? extends IModelObject> modelClass, JedisTemplate template, int expiration) {
		super(CacheableDao.MODEL_QUERY_CACHE+ObjectUuidUtils.getClassShortKey(modelClass),template,expiration);
		Assert.notNull(modelClass,"缓存对应业务类不允许为空");
		this.modelClass = modelClass;
	}
	
	@Override
	protected ValueWrapper _get(Jedis jedis, Object key) {
		if(!(key instanceof String)){
			return null;
		}
		byte[] keyBytes=computeKey(key);
		byte[] bs = jedis.get(keyBytes);
		try {
			logger.debug("读取redis集合缓存["+modelClass.getName()+"]:"+new String(keyBytes,"utf-8"));
			return (bs == null ? null : new SimpleValueWrapper(ModelJsonMapper.INSTANCE.fromJsonCollectionByte(bs,modelClass)));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	@Override
	protected boolean isObjectCacheable(Object key, Object value) {
		return key instanceof String && value instanceof Collection;
	}
	
	@Override
	protected byte[] _put(Transaction transaction, Object key, Object value) {
		@SuppressWarnings("unchecked")
		final Collection<? extends IModelObject> col = (Collection<? extends IModelObject>) value;
		byte[] cacheKey = computeKey(key);
		try {
			byte[] cacheByte=ModelJsonMapper.INSTANCE.toJsonByte(col);
			transaction.set(cacheKey, cacheByte);
			logger.debug("写入redis集合缓存["+modelClass.getName()+"]:"+new String(cacheByte,defaultCharset));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return cacheKey;
	}
}
