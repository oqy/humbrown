package com.minyisoft.webapp.core.exception;

public class ServiceException extends BaseException {
	private static final long serialVersionUID = 7718994826320581027L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	public  ServiceException(ExceptionMessage exceptionMessage){
		super(exceptionMessage);
	}
	
	public  ServiceException(ExceptionMessage exceptionMessage,Object[] params){
		super(exceptionMessage,params);
	}
}
