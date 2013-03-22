package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.exception.WebException;
import com.minyisoft.webapp.core.model.enumField.CoreEnumInterface;

/**
 * 系统整形枚举转换器
 * @author qingyong_ou
 *
 */
public class IntEnumTypeEditor extends PropertyEditorSupport {
	private CoreEnumInterface<Integer>[] intEnums;

	public IntEnumTypeEditor(CoreEnumInterface<Integer>[] intEnums) {
		this.intEnums = intEnums;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		if (!StringUtils.isNumeric(text)) {
			throw new WebException();
		}
		int currentInt = Integer.parseInt(text);
		for (CoreEnumInterface<Integer> intEnum : this.intEnums) {
			if (intEnum.getValue() == currentInt) {
				setValue(intEnum);
				return;
			}
		}
		setValue(null);
	}
}
