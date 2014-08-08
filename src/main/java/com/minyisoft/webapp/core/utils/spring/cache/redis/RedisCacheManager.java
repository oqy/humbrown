package com.minyisoft.webapp.core.utils.spring.cache.redis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisAction;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisActionNoResult;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager;

/**
 * CacheManager implementation for Redis. base on
 * org.springframework.data.redis.cache.RedisCacheManager
 * 
 * @author qingyong_ou
 */
public class RedisCacheManager implements CacheManager, ModelCacheManager {

	// fast lookup by name map
	private final ConcurrentMap<String, Cache> caches = Maps.newConcurrentMap();

	private final JedisTemplate template;
	// 缓存redis cache name
	private final String CACHE_NAMES_KEY = "redisCache:names";

	// defaultExpireTime time in seconds (0 = never expire)
	@Setter
	private int defaultExpiration = 0;
	private Map<String, Integer> expires = null;

	public RedisCacheManager(JedisTemplate template) {
		this.template = template;
	}

	public Cache getCache(final String name) {
		Cache c = caches.get(name);
		if (c == null) {
			int expiration = computeExpiration(name);
			if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_CACHE.getType()));
				if (modelClass != null) {
					c = new RedisModelCache(modelClass, template, expiration);
				}
			} else if (StringUtils.startsWithIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType())) {
				Class<? extends IModelObject> modelClass = ObjectUuidUtils.getClassByObjectKey(StringUtils
						.removeStartIgnoreCase(name, ModelCacheTypeEnum.MODEL_QUERY_CACHE.getType()));
				if (modelClass != null) {
					c = new RedisModelQueryCache(modelClass, template, expiration);
				}
			}
			if (c == null) {
				c = new RedisCache(name, template, expiration);
			}
			caches.put(name, c);
		}
		template.execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) throws Exception {
				jedis.sadd(CACHE_NAMES_KEY, name);
			}
		});
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
		return template.execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) throws Exception {
				return ImmutableSet.copyOf(jedis.smembers(CACHE_NAMES_KEY));
			}
		});
	}

	/**
	 * Sets the expire time (in seconds) for cache regions (by key).
	 * 
	 * @param expires
	 *            time in seconds
	 */
	public void setExpires(Map<String, Integer> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Integer>(expires) : null);
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
		Cache cache;
		for (String cacheName : getCacheNames()) {
			cache = caches.get(cacheName);
			if (cache == null) {
				cache = getCache(cacheName);
			}
			cache.clear();
		}
		template.del(CACHE_NAMES_KEY);
	}
}
