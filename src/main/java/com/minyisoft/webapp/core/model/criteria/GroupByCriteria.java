package com.minyisoft.webapp.core.model.criteria;

import com.minyisoft.webapp.core.model.criteria.summary.GroupByItem;

public interface GroupByCriteria<E extends Enum<? extends GroupByItem>> {
	public E[] getGroupByItems();
}
