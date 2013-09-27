package com.minyisoft.webapp.core.exception;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class ExceptionMessage {
	private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	static {
		messageSource.setBasename("com.fusung.webapp.core.exception.exceptionMessage");
	}

	private String exceptionMsg;

	protected ExceptionMessage(String msgKey) {
		this.exceptionMsg = messageSource.getMessage(msgKey, null, Locale.getDefault());
	}

	public String getExceptionMessage() {
		return exceptionMsg;
	}
}
