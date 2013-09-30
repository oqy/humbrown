package com.minyisoft.webapp.core.exception;

public class WebException extends BaseException {
	private static final long serialVersionUID = 8062265912176020985L;
	
	public WebException(String message) {
		super(message);
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
	}

	public  WebException(Enum<? extends ExceptionCode> exception,Object... params){
		super(exception,params);
	}
}
