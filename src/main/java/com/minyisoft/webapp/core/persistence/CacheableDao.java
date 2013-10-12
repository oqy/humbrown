package com.minyisoft.webapp.core.persistence;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;

public interface CacheableDao<T extends IModelObject, C extends BaseCriteria> extends BaseDao<T, C> {
	public String MODEL_CACHE="Model:";
	
	public String MODEL_QUERY_CACHE="ModelCollection:";
}
