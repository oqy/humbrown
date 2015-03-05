package com.minyisoft.webapp.core.service;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * 增删改操作后处理接口
 * 
 * @author qingyong_ou
 * 
 * @param <T>
 * 
 */
public interface CUDPostProcessor<T extends IModelObject> {
	void processAferAddNew(T info);

	void processAfterSave(T info);

	void processAfterDelete(T info);

	boolean canProcess(Class<?> targetType);
}
