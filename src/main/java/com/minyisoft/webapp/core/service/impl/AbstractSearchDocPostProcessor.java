package com.minyisoft.webapp.core.service.impl;

import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.ObjectSearcher;

public abstract class AbstractSearchDocPostProcessor implements CUDPostProcessor<ISearchDocObject> {
	public abstract ObjectSearcher getSearchService();

	@Override
	public void processAferAddNew(ISearchDocObject info) {
		if (info.isIndexable()) {
			getSearchService().index(info);
		}
	}

	@Override
	public void processAfterSave(ISearchDocObject info) {
		if (info.isIndexable()) {
			getSearchService().index(info);
		} else {
			getSearchService().delete(info.getSearchType(), info.getId());
		}
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
