package com.minyisoft.webapp.core.utils.spring.cache.interceptor;

import lombok.Getter;
import lombok.Setter;

import org.springframework.cache.interceptor.CacheableOperation;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou
 * ModelCacheable注解对应实体
 */
public class ModelCacheableOperation extends CacheableOperation {
	private @Getter @Setter Class<? extends IModelObject> modelType;
	
	@Override
	protected StringBuilder getOperationDescription() {
		StringBuilder sb = super.getOperationDescription();
		sb.append(" | modelType='");
		sb.append(this.modelType.getName());
		sb.append("'");
		return sb;
	}
}
