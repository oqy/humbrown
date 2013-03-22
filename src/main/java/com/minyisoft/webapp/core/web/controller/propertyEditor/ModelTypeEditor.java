package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.service.utils.ServiceUtils;

/**
 * IModelObject类对象类型编辑器，根据id字段转换
 * @author qingyong_ou
 *
 */
public class ModelTypeEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		try{
			setValue(ServiceUtils.getModel(text));
		}catch (Exception e) {
			setValue(null);
		}
	}
}
