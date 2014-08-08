package com.minyisoft.webapp.core.web.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.SortDirection;
import com.minyisoft.webapp.core.model.criteria.SortDirectionEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

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
	// 组件id属性值
	private String id;
	// 组件的对象值
	private Object value;
	// 候选项列表
	private List<?> optionList;
	// autoComplete组件异步请求路径
	private String autoCompleteRequestUrl;

	public SelectModuleUnitInfo(String label, String name, DisplayTypeEnum type, Object value, List<?> optionList) {
		this.label = label;
		this.type = type.getDescription();
		this.name = name;
		this.id = name;
		this.value = value;
		this.optionList = optionList;
	}

	public SelectModuleUnitInfo(String label, String name, DisplayTypeEnum type, Object value) {
		this.label = label;
		this.type = type.getDescription();
		this.name = name;
		this.id = name;
		this.value = value;
	}

	/**
	 * 指定对象是否与组件对象值相匹配
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isValueMatched(Object obj) {
		if (value == null) {
			return false;
		}
		if (value.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return Arrays.equals((Object[]) value, (Object[]) obj);
			} else {
				return ArrayUtils.contains((Object[]) value, obj);
			}
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			if (obj instanceof Collection) {
				return ((Collection<?>) value).containsAll((Collection<?>) obj)
						&& ((Collection<?>) obj).containsAll((Collection<?>) value);
			} else {
				return ((Collection<?>) value).contains(obj);
			}
		} else {
			return value.equals(obj);
		}
	}

	public String getObjectValue(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof IModelObject) {
			return ((IModelObject) obj).getId();
		} else if (obj instanceof DescribableEnum<?>) {
			return String.valueOf(((DescribableEnum<?>) obj).getValue());
		} else if (obj instanceof Date) {
			return DateFormatUtils.format((Date) obj, "yyyy-MM-dd");
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
		if (obj instanceof IModelObject) {
			try {
				return String.valueOf(PropertyUtils.getProperty(obj, "name"));
			} catch (Exception e) {
				return ((IModelObject) obj).getId();
			}
		} else if (obj instanceof DescribableEnum<?>) {
			return ((DescribableEnum<?>) obj).getDescription();
		} else if (obj instanceof Date) {
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
