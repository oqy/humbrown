package com.minyisoft.webapp.core.utils.spring.cache;

import lombok.Getter;

import org.springframework.cache.Cache;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou 支持对IModelObject对象进行缓存操作的CacheManager
 * 
 */
public interface ModelCacheManager {
	Cache getModelCache(Class<? extends IModelObject> modelClazz);

	Cache getModelQueryCache(Class<? extends IModelObject> modelClazz);

	public enum ModelCacheTypeEnum {
		MODEL_CACHE("Model:"), MODEL_QUERY_CACHE("ModelQuery:");

		@Getter
		private String type;

		private ModelCacheTypeEnum(String type) {
			this.type = type;
		}

		public String getCacheName(Class<? extends IModelObject> modelClazz) {
			return type + ObjectUuidUtils.getClassShortKey(modelClazz);
		}
	}
}
