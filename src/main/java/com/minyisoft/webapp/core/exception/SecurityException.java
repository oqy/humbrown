package com.minyisoft.webapp.core.exception;

public class SecurityException extends BaseException {
	private static final long serialVersionUID = 8062265912176020985L;
	
	public SecurityException(String message) {
		super(message);
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	public  SecurityException(Enum<? extends ExceptionCode> exception,Object... params){
		super(exception,params);
	}
}
