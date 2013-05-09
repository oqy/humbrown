package com.minyisoft.webapp.core.utils.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class RedisModelCacheManager implements CacheManager{
	private Logger logger=LoggerFactory.getLogger(getClass());
	// 缓存model简码对应的类
	private final ConcurrentMap<String, Class<? extends IModelObject>> modelClassCaches = new ConcurrentHashMap<String, Class<? extends IModelObject>>();
	// model对象缓存名称前缀
	private final String modelCacehNamePrefix="Model:";
	// model集合缓存名称前缀
	private final String modelQueryCacehNamePrefix="ModelCollection:";

	// fast lookup by name map
	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	private final Collection<String> names = Collections.unmodifiableSet(caches.keySet());
	private final StringRedisTemplate template;

	private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix(":");

	// 0 - never expire
	private long defaultExpiration = 0;
	private Map<String, Long> expires = null;

	public RedisModelCacheManager(StringRedisTemplate template) {
		this.template = template;
	}

	@SuppressWarnings("unchecked")
	public Cache getCache(String name) {
		Cache c = caches.get(name);
		if (c == null) {
			long expiration = computeExpiration(name);
			// 实例缓存
			if(StringUtils.startsWithIgnoreCase(name, modelCacehNamePrefix)){
				String modelKey=StringUtils.stripStart(name, modelCacehNamePrefix);
				Class<? extends IModelObject> modelClass=modelClassCaches.get(modelKey);
				if(modelClass==null){
					try {
						modelClass=(Class<? extends IModelObject>)Class.forName(ObjectUuidUtils.getClassNameByObjectKey(modelKey));
						modelClassCaches.put(modelKey, modelClass);
					} catch (ClassNotFoundException e) {
						logger.error(e.getMessage(),e);
					}
				}
				if(modelClass!=null){
					c = new RedisModelCache(name, modelClass, cachePrefix.prefix(name), template, expiration);
				}
			}
			// 查询 缓存
			else if(StringUtils.startsWithIgnoreCase(name, modelQueryCacehNamePrefix)){
				String modelKey=StringUtils.stripStart(name, modelQueryCacehNamePrefix);
				Class<? extends IModelObject> modelClass=modelClassCaches.get(modelKey);
				if(modelClass==null){
					try {
						modelClass=(Class<? extends IModelObject>)Class.forName(ObjectUuidUtils.getClassNameByObjectKey(modelKey));
						modelClassCaches.put(modelKey, modelClass);
					} catch (ClassNotFoundException e) {
						logger.error(e.getMessage(),e);
					}
				}
				if(modelClass!=null){
					c = new RedisModelQueryCache(name, modelClass, cachePrefix.prefix(name), template, expiration);
				}
			}
			caches.put(name, c);
		}

		return c;
	}

	private long computeExpiration(String name) {
		Long expiration = null;
		if (expires != null) {
			expiration = expires.get(name);
		}
		return (expiration != null ? expiration.longValue() : defaultExpiration);
	}

	public Collection<String> getCacheNames() {
		return names;
	}

	/**
	 * Sets the cachePrefix. Defaults to 'DefaultRedisCachePrefix').
	 * 
	 * @param cachePrefix
	 *            the cachePrefix to set
	 */
	public void setCachePrefix(RedisCachePrefix cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	/**
	 * Sets the default expire time (in seconds).
	 * 
	 * @param defaultExpireTime
	 *            time in seconds.
	 */
	public void setDefaultExpiration(long defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}

	/**
	 * Sets the expire time (in seconds) for cache regions (by key).
	 * 
	 * @param expires
	 *            time in seconds
	 */
	public void setExpires(Map<String, Long> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
	}
}