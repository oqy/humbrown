package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.persistence.CacheableDao;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;

/**
 * CacheManager implementation for Redis.
 * base on org.springframework.data.redis.cache.RedisCacheManager
 * 
 * @author qingyong_ou
 */
public class RedisCacheManager implements CacheManager {

	// fast lookup by name map
	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	private final Collection<String> names = Collections.unmodifiableSet(caches.keySet());
	private final JedisTemplate template;
	
	// 0 - never expire
	private int defaultExpiration = 0;
	private Map<String, Integer> expires = null;

	public RedisCacheManager(JedisTemplate template) {
		this.template = template;
	}

	public Cache getCache(String name) {
		Cache c = caches.get(name);
		if (c == null) {
			int expiration = computeExpiration(name);
			if(StringUtils.startsWithIgnoreCase(name, CacheableDao.MODEL_CACHE)){
				Class<? extends IModelObject> modelClass=ObjectUuidUtils.getClassByObjectKey(StringUtils.removeStartIgnoreCase(name,CacheableDao.MODEL_CACHE));
				if(modelClass!=null){
					c = new RedisModelCache(modelClass, template, expiration);
				}
			}else if(StringUtils.startsWithIgnoreCase(name, CacheableDao.MODEL_QUERY_CACHE)){
				Class<? extends IModelObject> modelClass=ObjectUuidUtils.getClassByObjectKey(StringUtils.removeStartIgnoreCase(name,CacheableDao.MODEL_QUERY_CACHE));
				if(modelClass!=null){
					c = new RedisModelQueryCache(modelClass, template, expiration);
				}
			}
			if(c==null){
				c = new RedisCache(name, template, expiration);
			}
			caches.put(name, c);
		}

		return c;
	}

	private int computeExpiration(String name) {
		Integer expiration = null;
		if (expires != null) {
			expiration = expires.get(name);
		}
		return (expiration != null ? expiration : defaultExpiration);
	}

	public Collection<String> getCacheNames() {
		return names;
	}

	/**
	 * Sets the default expire time (in seconds).
	 *
	 * @param defaultExpireTime time in seconds.
	 */
	public void setDefaultExpiration(int defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}

	/**
	 * Sets the expire time (in seconds) for cache regions (by key).
	 *
	 * @param expires time in seconds
	 */
	public void setExpires(Map<String, Integer> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Integer>(expires) : null);
	}
}
