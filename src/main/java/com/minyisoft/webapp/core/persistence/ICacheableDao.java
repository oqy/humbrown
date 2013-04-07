package com.minyisoft.webapp.core.persistence;

import org.springframework.cache.annotation.Cacheable;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
public interface ICacheableDao<T extends IModelObject, C extends BaseCriteria> extends IBaseDao<T, C> {
	public final String CACCHE_NAME_PREFIX="Model:";
	
	public String CACHE_CODE="";
	
	@Override
	@Cacheable(value=CACCHE_NAME_PREFIX+CACHE_CODE)
	public T getEntity(String id);
}
