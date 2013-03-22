package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.model.criteria.SortDirection;
import com.minyisoft.webapp.core.model.criteria.SortDirectionEnum;

/**
 * 排序器对象类型编辑器
 * @author qingyong_ou
 *
 */
public class SortDirectionTypeEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		SortDirectionEnum direction=SortDirectionEnum.getEnum(text);
		setValue(direction==null?null:new SortDirection(direction));
	}
}
