package com.minyisoft.webapp.core.exception;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.support.ResourceBundleMessageSource;

public abstract class BaseException extends RuntimeException {
	private static final long serialVersionUID = 4961603576232183799L;
	
	private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	static {
		messageSource.setBasename("com.fusung.webapp.core.exception.exceptionMessage");
	}
	
	/**
	 * 设置异常描述资源文件
	 * @param baseNames
	 */
	public static void setBaseNames(String... baseNames){
		if(ArrayUtils.isNotEmpty(baseNames)){
			messageSource.setBasenames((String[])ArrayUtils.add(baseNames, "com.fusung.webapp.core.exception.exceptionMessage"));
		}
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BaseException(Enum<? extends ExceptionCode> exception,Object... params){
		super(messageSource.getMessage(exception.getClass().getName()+"_"+exception.name(), params, "", Locale.getDefault()));
	}
}
