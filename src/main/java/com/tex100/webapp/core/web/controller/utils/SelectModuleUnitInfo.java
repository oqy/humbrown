package com.tex100.webapp.core.web.controller.utils;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.tex100.webapp.core.model.CoreBaseInfo;
import com.tex100.webapp.core.model.criteria.SortDirection;
import com.tex100.webapp.core.model.criteria.SortDirectionEnum;
import com.tex100.webapp.core.model.enumField.CoreEnumInterface;

@Getter
@Setter
// 搜索条件辅助组件对象
public class SelectModuleUnitInfo {
	// 组件label
	private String label;
	// 组件类型
	private String type;
	// 组件name属性值
	private String name;
	// 组件的对象值
	private Object value;
	// 候选项列表
	private List<?> optionList;
	// 对象值和候选项显示属性
	private String displayPropertyName;
	// autoComplete组件异步请求路径
	private String autoCompleteRequestUrl;

	public SelectModuleUnitInfo(String label, String name,
			DisplayTypeEnum type, Object value, List<?> optionList,
			String displayPropertyName) {
		this.label = label;
		this.type = type.getDescription();
		this.name = name;
		this.value = value;
		this.optionList = optionList;
		this.displayPropertyName = displayPropertyName;
	}

	public SelectModuleUnitInfo(String label, String name,
			DisplayTypeEnum type, Object value, String displayPropertyName) {
		this.label = label;
		this.type = type.getDescription();
		this.name = name;
		this.value = value;
		this.displayPropertyName = displayPropertyName;
	}

	/**
	 * 获取html组件id属性值
	 * 
	 * @return
	 */
	public String getComponentId() {
		if (StringUtils.endsWithIgnoreCase(name, ".id")) {
			return StringUtils.substringBefore(name, ".id");
		} else {
			return name;
		}
	}

	public String getObjectValue(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof CoreBaseInfo) {
			return ((CoreBaseInfo) obj).getId();
		} else if (obj instanceof CoreEnumInterface<?>) {
			return String.valueOf(((CoreEnumInterface<?>) obj).getValue());
		} else if (obj instanceof Date) {
			return DateFormatUtils.format((Date) obj, "yyyy-MM-dd");
		} else if (obj instanceof CoreEnumInterface<?>[]) {
			return StringUtils.join((CoreEnumInterface<?>[]) obj, "_");
		} else if (obj instanceof String[]) {
			return StringUtils.join((String[]) obj, "_");
		} else if (obj instanceof SortDirection) {
			return ((SortDirection) obj).getSortDirection().toString();
		} else {
			return String.valueOf(obj);
		}

	}

	public String getDisplayLabel(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof CoreBaseInfo) {
			try {
				return (String) PropertyUtils.getProperty(obj,
						displayPropertyName);
			} catch (Exception e) {
				return ((CoreBaseInfo) obj).getId();
			}
		} else if (obj instanceof CoreEnumInterface<?>) {
			return String.valueOf(((CoreEnumInterface<?>) obj).getDescription());
		}else if (obj instanceof Date) {
			return DateFormatUtils.format((Date) obj, "yyyy-MM-dd");
		} else if (obj instanceof Boolean) {
			if ((Boolean) obj) {
				return "是";
			}
			return "否";
		} else if (obj instanceof SortDirection) {
			if (((SortDirection) obj).getSortDirection() == SortDirectionEnum.SORT_ASC) {
				return "升序";
			} else {
				return "降序";
			}
		} else {
			return String.valueOf(obj);
		}
	}
}
