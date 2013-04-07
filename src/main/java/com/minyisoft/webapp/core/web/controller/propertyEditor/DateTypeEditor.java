package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.time.DateUtils;

/**
 * 系统整形枚举转换器
 * @author qingyong_ou
 *
 */
public class DateTypeEditor extends PropertyEditorSupport {
	private String[] dateFormats=new String[]{"yyyy-MM-dd"};
	
	public DateTypeEditor(){
		
	}
	
	public DateTypeEditor(String[] dateFormats){
		this.dateFormats=dateFormats;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try{
			setValue(DateUtils.parseDate(text,dateFormats));
		}catch(Exception e){
			setValue(null);
		}
	}
}
