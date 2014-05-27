package com.minyisoft.webapp.core.utils.spring.cache.redis;

/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.minyisoft.webapp.core.utils.redis.JedisTemplate;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisAction;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisActionNoResult;

/**
 * Cache implementation on top of Redis.
 * base on org.springframework.data.redis.cache.RedisCache
 * @author qingyong_ou
 */
class RedisCache implements Cache {
	protected final Logger logger=LoggerFactory.getLogger(getClass());

	private static final int PAGE_SIZE = 128;
	private final String name;
	private final @Getter JedisTemplate template;
	private final byte[] prefix;
	private final byte[] setName;
	private final byte[] cacheLockName;
	private long WAIT_FOR_LOCK = 300;
	private final @Getter int expiration;
	protected final Charset defaultCharset=Charset.forName("UTF8");
	protected final Converter<Object, byte[]> serializer = new SerializingConverter();
	private final Converter<byte[], Object> deserializer = new DeserializingConverter();
	
	/**
	 * 
	 * Constructs a new <code>RedisCache</code> instance.
	 * 
	 * @param name cache name
	 * @param template
	 * @param expiration
	 */
	RedisCache(String name, JedisTemplate template, int expiration) {

		Assert.hasText(name, "non-empty cache name is required");
		this.name = name;
		this.template = template;
		this.expiration = expiration;
		this.prefix = (name + ":").getBytes(defaultCharset);

		// name of the set holding the keys
		this.setName = (name + "~keys").getBytes(defaultCharset);
		this.cacheLockName = (name + "~lock").getBytes(defaultCharset);
	}

	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation simply returns the RedisTemplate used for configuring
	 * the cache, giving access to the underlying Redis store.
	 */
	public Object getNativeCache() {
		return template;
	}

	public ValueWrapper get(final Object key) {
		return template.execute(new JedisAction<ValueWrapper>() {

			@Override
			public ValueWrapper action(Jedis jedis) throws Exception {
				waitForLock(jedis);
				return _get(jedis,key);
			}
		});
	}
	
	protected ValueWrapper _get(Jedis jedis, Object key){
		byte[] bs = jedis.get(computeKey(key));
		return bs == null ? null : new SimpleValueWrapper(deserializer.convert(bs));
	}

	public void put(final Object key, final Object value) {
		if (!isObjectCacheable(key, value)) {
			return;
		}
		
		template.execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) throws Exception {
				waitForLock(jedis);
				Transaction t = jedis.multi();
				byte[] cacheKey = _put(t, key, value);
				t.zadd(setName, 0, cacheKey);

				if (expiration > 0) {
					t.expire(cacheKey, expiration);
					// update the expiration of the set of keys as well
					t.expire(setName, expiration);
				}
				t.exec();
			}
		});
	}
	
	protected boolean isObjectCacheable(Object key,Object value){
		return true;
	}
	
	protected byte[] _put(Transaction transaction, Object key,Object value) {
		byte[] cacheKey = computeKey(key);
		transaction.set(cacheKey, serializer.convert(value));
		return cacheKey;
	}

	public void evict(Object key) {
		final byte[] k = computeKey(key);

		template.execute(new JedisActionNoResult() {
			
			@Override
			public void action(Jedis jedis) throws Exception {
				jedis.del(k);
				// remove key from set
				jedis.zrem(setName, k);
			}
		});
	}
	
	public final void clear() {
		// need to del each key individually
		template.execute(new JedisActionNoResult() {
			
			@Override
			public void action(Jedis jedis) throws Exception {
				// another clear is on-going
				if (jedis.exists(cacheLockName)) {
					return;
				}

				try {
					jedis.set(cacheLockName, cacheLockName);

					int offset = 0;
					boolean finished = false;

					do {
						// need to paginate the keys
						Set<byte[]> keys = jedis.zrange(setName, (offset)
								* PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
						finished = keys.size() < PAGE_SIZE;
						offset++;
						if (!keys.isEmpty()) {
							jedis.del(keys.toArray(new byte[keys.size()][]));
						}
					} while (!finished);

					jedis.del(setName);
					_furtherClear(jedis);
				} finally {
					jedis.del(cacheLockName);
				}
			}
		});
	}
	
	/**
	 * 清空缓存的扩展操作，供子类实现
	 * @param jedis
	 */
	protected void _furtherClear(Jedis jedis) {
		
	}

	protected byte[] computeKey(Object key) {
		if(key instanceof String){
			return (name + ":" + key).getBytes(defaultCharset);
		}
		
		byte[] k = serializer.convert(key);
		byte[] result = Arrays.copyOf(prefix, prefix.length + k.length);
		System.arraycopy(k, 0, result, prefix.length, k.length);
		return result;
	}

	private boolean waitForLock(Jedis connection) {
		boolean retry;
		boolean foundLock = false;
		do {
			retry = false;
			if (connection.exists(cacheLockName)) {
				foundLock = true;
				try {
					Thread.sleep(WAIT_FOR_LOCK);
				} catch (InterruptedException ex) {
					// ignore
				}
				retry = true;
			}
		} while (retry);
		return foundLock;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Class<T> arg1) {
		ValueWrapper value = get(key);
		return value == null ? null : (T) value.get();
	}
}
