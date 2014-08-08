package com.minyisoft.webapp.core.service.impl;

import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.SearchBaseService;

public abstract class AbstractSearchDocPostProcessor implements CUDPostProcessor<ISearchDocObject> {
	public abstract SearchBaseService getSearchService();

	@Override
	public void processAferAddNew(ISearchDocObject info) {
		getSearchService().index(info);
	}

	@Override
	public void processAfterSave(ISearchDocObject info) {
		getSearchService().index(info);
	}

	@Override
	public void processAfterDelete(ISearchDocObject info) {
		getSearchService().delete(info.getSearchType(), info.getId());
	}

	@Override
	public boolean canProcess(Class<?> targetType) {
		return ISearchDocObject.class.isAssignableFrom(targetType);
	}

}
