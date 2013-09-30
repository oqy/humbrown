package com.minyisoft.webapp.core.exception;


public class EntityException extends BaseException {
	private static final long serialVersionUID = 7322667667588541195L;
	
	public EntityException(String message) {
		super(message);
	}

	public EntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityException(Enum<? extends ExceptionCode> exception,Object... params){
		super(exception,params);
	}
}
