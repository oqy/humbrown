package com.minyisoft.webapp.core.utils.cache.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.core.utils.mapper.json.jackson.ModelObjectJavaType;
import com.minyisoft.webapp.core.utils.mapper.json.jackson.ModelObjectModule;

class RedisModelCache implements Cache {
	private final Class<? extends IModelObject> modelClass;

	private static final int PAGE_SIZE = 128;
	private final String name;
	private final RedisTemplate<String, String> template;
	private final byte[] prefix;
	private final byte[] setName;
	private final byte[] hashName;
	private final byte[] cacheLockName;
	private long WAIT_FOR_LOCK = 300;
	private final long expiration;
	
	private final Logger logger=LoggerFactory.getLogger(getClass());
	private final ObjectMapper objectMapper;
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
	RedisModelCache(String name, Class<? extends IModelObject> modelClass, byte[] prefix, RedisTemplate<String, String> template, long expiration) {

		Assert.hasText(name, "non-empty cache name is required");
		this.name = name;
		this.modelClass = modelClass;
		this.template = template;
		this.prefix = prefix;
		this.expiration = expiration;
		
		this.objectMapper=JsonMapper.nonDefaultMapper().getMapper();
		this.objectMapper.registerModule(new ModelObjectModule());
		this.objectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		
		// name of the set holding the keys
		this.setName = stringSerializer.serialize(name + "~keys");
		this.hashName = stringSerializer.serialize(name + "~hashkeys");
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
					logger.debug("读取redis缓存["+modelClass.getName()+"]:"+key);
					waitForLock(connection);
					byte[] bs = null;
					byte[] keyInByte = computeKey(key);
					if (!ObjectUuidUtils.isLegalId(modelClass,(String)key)) {
						byte[] hashKey = connection.hGet(hashName, keyInByte);
						if (hashKey!=null&&ArrayUtils.isNotEmpty(hashKey)) {
							bs=connection.get(hashKey);
						}
					} else {
						bs=connection.get(keyInByte);
					}
					try {
						return (bs == null ? null : new SimpleValueWrapper(objectMapper.readValue(bs, ModelObjectJavaType.construct(modelClass))));
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
		if(key instanceof String&&value instanceof IModelObject) {
			template.execute(new RedisCallback<Object>() {
				public Object doInRedis(RedisConnection connection) throws DataAccessException,ServiceException {
					final IModelObject model = (IModelObject) value;
					byte[] k = computeKey(key);
					
					waitForLock(connection);
					connection.multi();
					if (!StringUtils.equals((String) key, model.getId())) {
						connection.hSet(hashName, k, computeKey(model.getId()));
						k = computeKey(model.getId());
					}
					try {
						connection.set(k, objectMapper.writeValueAsBytes(value));
						logger.debug("写入redis缓存["+modelClass.getName()+"]:"+objectMapper.writeValueAsString(value));
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(),e);
						throw new ServiceException(e);
					}
					connection.zAdd(setName, 0, k);
	
					if (expiration > 0) {
						connection.expire(k, expiration);
						// update the expiration of the set of keys as well
						connection.expire(setName, expiration);
						connection.expire(hashName, expiration);
					}
					connection.exec();
	
					return null;
				}
			}, true);
		}
	}

	public void evict(Object key) {
		if(key instanceof List<?>&&CollectionUtils.isNotEmpty((List<?>)key)){
			for(Object k:(List<?>)key){
				evictSingle(k);
			}
		}else{
			evictSingle(key);
		}
	}
	
	private void evictSingle(Object key){
		final byte[] k = computeKey(key);
		
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
					connection.del(hashName);
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
