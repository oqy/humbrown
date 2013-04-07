package com.minyisoft.webapp.core.exception;

import java.text.MessageFormat;

public abstract class BaseException extends RuntimeException {
	private static final long serialVersionUID = 4961603576232183799L;

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}
	
	public  BaseException(ExceptionMessage exceptionMessage){
		super(exceptionMessage.getExceptionMessage());
	}
	
	public  BaseException(ExceptionMessage exceptionMessage,Object[] params){
		super(MessageFormat.format(exceptionMessage.getExceptionMessage(),params));
	}
}
