package com.minyisoft.webapp.core.web.controller.formatter;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;

public class ModelObjectFormatter<T extends IModelObject> implements Formatter<T> {

	@Override
	public String print(T object, Locale locale) {
		if(object!=null&&object.isIdPresented()){
			return object.getId();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public T parse(String text, Locale locale) throws ParseException {
		try{
			return (T)ServiceUtils.getModel(text);
		}catch (Exception e) {
			return null;
		}
	}

}
