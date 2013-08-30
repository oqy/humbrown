package com.minyisoft.webapp.core.security.shiro.cache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.utils.redis.JedisTemplate;

/**
 * @author qingyong_ou
 * Shiro缓存集群管理器
 */
public class ShiroClusterCacheManager<K,V> implements CacheManager, Initializable, Destroyable {
	private static final Logger log = LoggerFactory.getLogger(ShiroClusterCacheManager.class);
	protected JedisTemplate jedisTemplate;
	/**
	 * 本地缓存管理器
	 */
	protected net.sf.ehcache.CacheManager ehCacheManager;
	/**
	 * 本地缓存配置文件路径
	 */
	private final String cacheManagerConfigFile = "classpath:com/fusung/webapp/core/security/cache/ehcache.xml";
	
	// fast lookup by name map
	private final ConcurrentMap<String, Cache<K,V>> caches = new ConcurrentHashMap<String, Cache<K,V>>();
	/**
	 * 默认缓存名
	 */
	public static final String SESSION_CLUSTER_DEFAULT_CACHE_NAME="Shiro:Default";
	/**
	 * session缓存名
	 */
	public static final String SESSION_CLUSTER_SESSION_CACHE_NAME="Shiro:Session";
	/**
	 * 用户授权信息（角色、权限等）缓存名
	 */
	public static final String SESSION_CLUSTER_AUTHORIZATION_CACHE_NAME="Shiro:Authorization";
	
	/**
	 * 是否启动本地缓存，帮助提升读取效率
	 */
	private @Getter @Setter boolean localCacheEnabled=false;

	@SuppressWarnings("unchecked")
	@Override
	public Cache<K, V> getCache(String name) throws CacheException {
		Assert.hasText(name, "缓存名不允许为空");
		if (StringUtils.equalsIgnoreCase(name,SESSION_CLUSTER_AUTHORIZATION_CACHE_NAME)) {
			name = SESSION_CLUSTER_AUTHORIZATION_CACHE_NAME;
		} else if (StringUtils.equalsIgnoreCase(name,SESSION_CLUSTER_SESSION_CACHE_NAME)) {
			name = SESSION_CLUSTER_SESSION_CACHE_NAME;
		} else {
			name = SESSION_CLUSTER_DEFAULT_CACHE_NAME;
		}
		Cache<K, V> cache=caches.get(name);
		if(cache==null){
			if (localCacheEnabled && ehCacheManager != null) {
				if (ehCacheManager.getEhcache(name) == null) {
					ehCacheManager.addCache(name);
				}
				cache=new ShiroClusterCache<K, V>(name,jedisTemplate,ehCacheManager.getEhcache(name));
			}else{
				cache=new ShiroClusterCache<K, V>(name,jedisTemplate,null);
			}
			caches.put(name, cache);
		}
		return cache;
	}
	
	public void setJedisTemplate(JedisTemplate template){
		jedisTemplate=template;
	}

	@Override
	public void destroy() throws Exception {
		try {
			if (localCacheEnabled && ehCacheManager != null) {
				ehCacheManager.shutdown();
			}
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to cleanly shutdown implicitly created CacheManager instance.  " + "Ignoring (shutting down)...");
            }
        }
	}

	@Override
	public void init() throws ShiroException {
		try {
			if(localCacheEnabled){
				ehCacheManager = new net.sf.ehcache.CacheManager(ResourceUtils.getInputStreamForPath(cacheManagerConfigFile));
			}
        } catch (IOException e) {
            throw new ConfigurationException("Unable to obtain input stream for cacheManagerConfigFile [" + cacheManagerConfigFile + "]", e);
        }
	}
}
