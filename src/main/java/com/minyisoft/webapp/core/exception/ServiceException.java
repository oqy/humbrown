package com.minyisoft.webapp.core.exception;

public class ServiceException extends BaseException {
	private static final long serialVersionUID = 7718994826320581027L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public  ServiceException(Enum<? extends ExceptionCode> exception,Object... params){
		super(exception,params);
	}
}
