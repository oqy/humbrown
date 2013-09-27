package com.minyisoft.webapp.core.web.controller.formatter;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.Formatter;

import com.minyisoft.webapp.core.model.criteria.SortDirection;
import com.minyisoft.webapp.core.model.criteria.SortDirectionEnum;

public class SortDirectionFormatter implements Formatter<SortDirection> {

	@Override
	public String print(SortDirection object, Locale locale) {
		if(object!=null){
			return object.getSortDirection()==SortDirectionEnum.SORT_ASC?"升序":"降序";
		}
		return "";
	}

	public SortDirection parse(String text, Locale locale) throws ParseException {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		SortDirectionEnum direction=SortDirectionEnum.getEnum(text);
		return direction==null?null:new SortDirection(direction);
	}

}
