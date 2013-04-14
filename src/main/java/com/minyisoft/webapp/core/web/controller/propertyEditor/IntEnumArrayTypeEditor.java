package com.minyisoft.webapp.core.web.controller.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.exception.WebException;
import com.minyisoft.webapp.core.model.enumField.ICoreEnum;

/**
 * 系统整形枚举转换器
 * @author qingyong_ou
 *
 */
public class IntEnumArrayTypeEditor extends PropertyEditorSupport {
	private ICoreEnum<Integer>[] intEnums;

	public IntEnumArrayTypeEditor(ICoreEnum<Integer>[] intEnums) {
		this.intEnums = intEnums;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		if(StringUtils.indexOf(text, '_')>0){
			String[] values=StringUtils.split(text, '_');
			ICoreEnum<?>[] intEnums=new ICoreEnum<?>[values.length];
			for(int i=0;i<values.length;i++){
				intEnums[i]=getIntEnum(values[i]);
			}
			setValue(intEnums);
		}else{
			setValue(getIntEnum(text));
		}
	}
	
	private ICoreEnum<Integer> getIntEnum(String valueString){
		if (!StringUtils.isNumeric(valueString)) {
			throw new WebException();
		}
		int currentInt = Integer.parseInt(valueString);
		
		for (ICoreEnum<Integer> intEnum : this.intEnums) {
			if (intEnum.getValue() == currentInt) {
				return(intEnum);
			}
		}
		
		if(intEnums!=null&&intEnums.length>0){
			return intEnums[0];
		}else{
			return null;
		}
	}
}
