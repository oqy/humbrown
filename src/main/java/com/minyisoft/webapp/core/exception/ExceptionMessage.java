package com.minyisoft.webapp.core.exception;

import java.util.Locale;

import com.minyisoft.webapp.core.utils.RegexResourceBundleMessageSource;

public class ExceptionMessage{
	private String exceptionMsg;

	protected ExceptionMessage(String msgKey) {
		this.exceptionMsg = RegexResourceBundleMessageSource.getSystemDefaultMessageSource().getMessage(msgKey, null, Locale.getDefault());
	}
	
	public String getExceptionMessage(){
		return exceptionMsg;
	}
}
