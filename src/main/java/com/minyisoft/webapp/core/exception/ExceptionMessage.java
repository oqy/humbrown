package com.minyisoft.webapp.core.exception;

import java.io.Serializable;
import java.util.Locale;

import com.minyisoft.webapp.core.utils.RegexResourceBundleMessageSource;

public class ExceptionMessage implements Serializable {
	private static final long serialVersionUID = -1123559445374542058L;
	
	private String exceptionMsg;

	protected ExceptionMessage(String msgKey) {
		this.exceptionMsg = RegexResourceBundleMessageSource.getSystemDefaultMessageSource().getMessage(msgKey, null, Locale.getDefault());
	}
	
	public String getExceptionMessage(){
		return exceptionMsg;
	}
}
