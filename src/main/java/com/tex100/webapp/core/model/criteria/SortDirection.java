package com.tex100.webapp.core.model.criteria;

import lombok.Getter;
import lombok.Setter;

//排序器
@Getter
@Setter
public class SortDirection {
	// 排序顺序-放在第几位排序
	private int seq;
	// 排序字段
	private String item;
	// 排序方式
	private SortDirectionEnum sortDirection = SortDirectionEnum.SORT_ASC;

	public SortDirection() {
	}

	public SortDirection(SortDirectionEnum sortDirection) {
		this.setSortDirection(sortDirection);
	}

	public SortDirection(SortDirectionEnum sortDirection, String item) {
		this.setSortDirection(sortDirection);
		this.setItem(item);
	}

	public String getMsgString() {
		return this.item + "," + this.sortDirection.toString();
	}

	public SortDirectionEnum getSortDirection() {
		return sortDirection;
	}
}
