package com.minyisoft.webapp.core.security.shiro.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

import com.minyisoft.webapp.core.utils.redis.JedisTemplate;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisAction;
import com.minyisoft.webapp.core.utils.redis.JedisTemplate.JedisActionNoResult;

/**
 * @author qingyong_ou
 *
 * shiro集群缓存实现
 * @param <K>
 * @param <V>
 */
public class ShiroClusterCache<K,V> implements Cache<K, V> {
	private static final Logger logger = LoggerFactory.getLogger(ShiroClusterCache.class);
	/**
	 * 是否启动本地缓存，若启动本地缓存，在集群缓存查询到的数据将同时存储到本地缓存，
	 * 本地缓存TTL较短，以秒为单位，用于程序在短时间内多次重复查询时减少与集群缓存的网络请求次数
	 */
	private boolean localCacheEnabled;
	/**
	 * 本地缓存，使用ehCache
	 */
	private net.sf.ehcache.Ehcache localCache;
	/**
	 * 集群缓存，使用redis
	 */
	private JedisTemplate jedisTemplate;
	/**
	 * redis键前缀
	 */
	private String keyPrefix;
	/**
	 * redis键前缀字节数组
	 */
	private byte[] keyPrefixBytes;
	/**
	 * redis键集合
	 */
	private byte[] cacheKeySetName;
	/**
	 * shiro源键集合
	 */
	private byte[] originalKeySetName;
	private final Converter<Object, byte[]> serializer = new SerializingConverter();
	private final Converter<byte[], Object> deserializer = new DeserializingConverter();
	private final Charset defaultCharset=Charset.forName("UTF8");
	
	public ShiroClusterCache(String prefix,JedisTemplate template,Ehcache ehCache){
		Assert.hasText(prefix, "缓存前缀不允许为空");
		Assert.notNull(template, "集群缓存不允许为空");
		jedisTemplate = template;
		localCacheEnabled = (ehCache != null);
		localCache = ehCache;
		keyPrefix = prefix.concat(":");
		keyPrefixBytes = keyPrefix.getBytes(defaultCharset);
		cacheKeySetName = (prefix + "~cacheKeys").getBytes(defaultCharset);
		originalKeySetName = (prefix + "~originalKeys").getBytes(defaultCharset);
	}
	
	private byte[] computeKey(Object key){
		if (key instanceof String) {
			return (keyPrefix.concat((String) key)).getBytes(defaultCharset);
		}
		byte[] k = serializer.convert(key);
		byte[] result = Arrays.copyOf(keyPrefixBytes, keyPrefixBytes.length + k.length);
		System.arraycopy(k, 0, result, keyPrefixBytes.length, k.length);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(final K key) throws CacheException {
		if (key == null) {
			return null;
		} else {
			if(localCacheEnabled) {
				// 先从本地缓存获取信息
				logger.debug("从本地缓存查找shiro缓存，key="+key);
				Element element = localCache.get(key);
				if (element != null) {
					return (V) element.getObjectValue();
				}
			}
			// 若在本地缓存没有相应信息，从redis缓存中中查找
			logger.debug("从集群缓存查找shiro缓存，key="+key);
			return jedisTemplate.execute(new JedisAction<V>() {

				@Override
				public V action(Jedis jedis) throws Exception {
					byte[] bs = jedis.get(computeKey(key));
					if (bs == null) {
						return null;
					} else {
						V val = (V) deserializer.convert(bs);
						if(localCacheEnabled){
							// 设置本地缓存
							localCache.put(new Element(key, val));
						}
						return val;
					}
				}
			});
		}
	}

	@Override
	public V put(final K key, final V value) throws CacheException {
		V previous = get(key);
		if (localCacheEnabled) {
			// 清除本地缓存
			localCache.remove(key);
		}
		// 设置集群缓存
		jedisTemplate.execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) throws Exception {
				byte[] cacheKey = computeKey(key);
				jedis.set(cacheKey, serializer.convert(value));
				jedis.zadd(cacheKeySetName, 0, cacheKey);
				jedis.zadd(originalKeySetName, 0, serializer.convert(key));
			}
		});
		return previous;
	}

	@Override
	public V remove(final K key) throws CacheException {
		V previous = get(key);
		if (localCacheEnabled) {
			// 删除本地缓存
			localCache.remove(key);
		}
		// 删除集群缓存
		jedisTemplate.execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) throws Exception {
				byte[] cacheKey = computeKey(key);
				jedis.del(cacheKey);
				jedis.zrem(cacheKeySetName, cacheKey);
				jedis.zrem(originalKeySetName, serializer.convert(key));
			}
		});
		return previous;

	}
	
	private static final int PAGE_SIZE = 128;

	@Override
	public void clear() throws CacheException {
		if (localCacheEnabled) {
			localCache.removeAll();
		}
		jedisTemplate.execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) throws Exception {
				int offset = 0;
				boolean finished = false;

				do {
					// need to paginate the keys
					Set<byte[]> keys = jedis.zrange(cacheKeySetName, (offset)
							* PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
					finished = keys.size() < PAGE_SIZE;
					offset++;
					if (!keys.isEmpty()) {
						jedis.del(keys.toArray(new byte[keys.size()][]));
					}
				} while (!finished);

				jedis.del(cacheKeySetName);
				jedis.del(originalKeySetName);
			}
		});
	}

	@Override
	public int size() {
		return jedisTemplate.execute(new JedisAction<Integer>() {

			@Override
			public Integer action(Jedis jedis) throws Exception {
				return jedis.zcount(cacheKeySetName, 0, 0).intValue();
			}
		});
	}

	@Override
	public Set<K> keys() {
		return jedisTemplate.execute(new JedisAction<Set<K>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Set<K> action(Jedis jedis) throws Exception {
				int offset = 0;
				boolean finished = false;

				Set<K> keySet = new HashSet<K>();
				do {
					// need to paginate the keys
					Set<byte[]> originalKeys = jedis.zrange(originalKeySetName,
							(offset) * PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
					finished = originalKeys.size() < PAGE_SIZE;
					offset++;
					if (!originalKeys.isEmpty()) {
						for (byte[] originalKey : originalKeys) {
							keySet.add((K) serializer.convert(originalKey));
						}
					}
				} while (!finished);
				return keySet;
			}
		});
	}

	@Override
	public Collection<V> values() {
		Set<K> keys = keys();
		if (keys.isEmpty()) {
			return null;
		} else {
			Collection<V> valueList = new ArrayList<V>();
			for (K key : keys) {
				V value = get(key);
				if (value != null) {
					valueList.add(value);
				}
			}
			return valueList;
		}
	}
}
