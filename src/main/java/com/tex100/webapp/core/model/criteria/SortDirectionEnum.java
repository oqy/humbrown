package com.tex100.webapp.core.model.criteria;

//排序类型枚举
public enum SortDirectionEnum {
	SORT_ASC("asc"), SORT_DESC("desc");
	private String sortDirection;

	private SortDirectionEnum(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String toString() {
		return sortDirection;
	}

	public static SortDirectionEnum getEnum(String value) {
		for (SortDirectionEnum stringEnum : SortDirectionEnum.values()) {
			if (stringEnum.toString().equals(value)) {
				return stringEnum;
			}
		}
		return null;
	}
}
