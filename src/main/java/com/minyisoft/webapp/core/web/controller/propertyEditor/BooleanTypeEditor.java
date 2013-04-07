package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.exception.WebException;

/**
 * 系统整形枚举转换器
 * @author yongan_cui
 *
 */
public class BooleanTypeEditor extends PropertyEditorSupport {

	public BooleanTypeEditor() {
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		if (isTure(text)) {
			setValue(true);
			return;
		}
		if (isFlase(text)) {
			setValue(false);
			return;
		}
		throw new WebException("BooleanTypeEditor出错！");
		
	}
	private boolean isTure(String text) {
		if("1".equals(text) || "true".equals(text)) {
			return true;
		} else {
			return false;
		}
	}
	private boolean isFlase(String text) {
		if("0".equals(text) || "false".equals(text)) {
			return true;
		} else {
			return false;
		}
	}
}
