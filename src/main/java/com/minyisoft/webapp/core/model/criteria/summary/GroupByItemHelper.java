package com.minyisoft.webapp.core.model.criteria.summary;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class GroupByItemHelper {
	private GroupByItemHelper() {

	}

	public static final boolean containsGroupByItem(GroupByItem[] groupByItems,
			String name) {
		if (ArrayUtils.isEmpty(groupByItems) || StringUtils.isBlank(name)) {
			return false;
		}
		for (GroupByItem item : groupByItems) {
			if (StringUtils.equalsIgnoreCase(item.getName(), name)) {
				return true;
			}
		}
		return false;
	}
}
