package com.minyisoft.webapp.core.utils.cache.redis;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.mapper.json.ModelJsonMapper;

class RedisModelQueryCache implements Cache {
	private final Class<? extends IModelObject> modelClass;

	private static final int PAGE_SIZE = 128;
	private final String name;
	private final StringRedisTemplate template;
	private final byte[] prefix;
	private final byte[] setName;
	private final byte[] cacheLockName;
	private long WAIT_FOR_LOCK = 300;
	private final long expiration;
	
	private final Logger logger=LoggerFactory.getLogger(getClass());
	private final RedisSerializer<Object> keySerializer=new JdkSerializationRedisSerializer();

	/**
	 * 
	 * Constructs a new <code>RedisModelCache</code> instance.
	 * 
	 * @param name
	 * @param modelClass
	 * @param prefix
	 * @param template
	 * @param expiration
	 */
	RedisModelQueryCache(String name, Class<? extends IModelObject> modelClass, byte[] prefix, StringRedisTemplate template, long expiration) {

		Assert.hasText(name, "non-empty cache name is required");
		this.name = name;
		this.modelClass = modelClass;
		this.template = template;
		this.prefix = prefix;
		this.expiration = expiration;
		
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		
		// name of the set holding the keys
		this.setName = stringSerializer.serialize(name + "~keys");
		this.cacheLockName = stringSerializer.serialize(name + "~lock");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return template;
	}

	public ValueWrapper get(final Object key) {
		if (key instanceof String) {
			return (ValueWrapper) template.execute(new RedisCallback<ValueWrapper>() {
				public ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
					waitForLock(connection);
					byte[] keyBytes=computeKey(key);
					byte[] bs = connection.get(keyBytes);
					try {
						logger.debug("读取redis集合缓存["+modelClass.getName()+"]:"+new String(keyBytes,"utf-8"));
						return (bs == null ? null : new SimpleValueWrapper(ModelJsonMapper.INSTANCE.fromJsonCollectionByte(bs,modelClass)));
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						return null;
					}
				}
			}, true);
		}
		return null;
	}

	public void put(final Object key, final Object value) {
		if (key instanceof String
				&& value instanceof Collection) {
			template.execute(new RedisCallback<Object>() {
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					@SuppressWarnings("unchecked")
					final Collection<? extends IModelObject> col = (Collection<? extends IModelObject>) value;
					byte[] k = computeKey(key);
					
					waitForLock(connection);
					connection.multi();
					try {
						byte[] cacheByte=ModelJsonMapper.INSTANCE.toJsonByte(col);
						connection.set(k, cacheByte);
						logger.debug("写入redis集合缓存["+modelClass.getName()+"]:"+new String(cacheByte,"utf-8"));
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					connection.zAdd(setName, 0, k);
	
					if (expiration > 0) {
						connection.expire(k, expiration);
						// update the expiration of the set of keys as well
						connection.expire(setName, expiration);
					}
					connection.exec();
	
					return null;
				}
			}, true);
		}
	}

	public void evict(Object key) {
		final byte[] k = computeKey(key);
		try {
			logger.debug("擦除redis查询缓存["+modelClass.getName()+"]:"+new String(k,"utf-8"));
		} catch (UnsupportedEncodingException e) {
		}
		
		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.del(k);
				// remove key from set
				connection.zRem(setName, k);
				return null;
			}
		}, true);
	}

	public void clear() {
		logger.debug("清空redis查询缓存["+modelClass.getName()+"]");
		// need to del each key individually
		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// another clear is on-going
				if (connection.exists(cacheLockName)) {
					return null;
				}

				try {
					connection.set(cacheLockName, cacheLockName);

					int offset = 0;
					boolean finished = false;

					do {
						// need to paginate the keys
						Set<byte[]> keys = connection.zRange(setName, (offset)* PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
						finished = keys.size() < PAGE_SIZE;
						offset++;
						if (!keys.isEmpty()) {
							connection.del(keys.toArray(new byte[keys.size()][]));
						}
					} while (!finished);

					connection.del(setName);
					return null;

				} finally {
					connection.del(cacheLockName);
				}
			}
		}, true);
	}

	private byte[] computeKey(Object key) {
		byte[] k = keySerializer.serialize(key);

		if (prefix == null || prefix.length == 0)
			return k;

		byte[] result = Arrays.copyOf(prefix, prefix.length + k.length);
		System.arraycopy(k, 0, result, prefix.length, k.length);
		return result;
	}

	private boolean waitForLock(RedisConnection connection) {
		boolean retry;
		boolean foundLock = false;
		do {
			retry = false;
			if (connection.exists(cacheLockName)) {
				foundLock = true;
				try {
					Thread.currentThread().wait(WAIT_FOR_LOCK);
				} catch (InterruptedException ex) {
					// ignore
				}
				retry = true;
			}
		} while (retry);
		return foundLock;
	}

}
