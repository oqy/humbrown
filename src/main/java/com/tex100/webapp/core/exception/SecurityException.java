package com.tex100.webapp.core.exception;

public class SecurityException extends BaseException {
	private static final long serialVersionUID = 8062265912176020985L;
	
	public static final ExceptionMessage SECURITY_NOT_HAS_ROLE = new ExceptionMessage("Security_Not_Has_Role");
	public static final ExceptionMessage SECURITY_NOT_HAS_PERMISSION = new ExceptionMessage("Security_Not_Has_Permission");
	public static final ExceptionMessage SECURITY_NOT_LOGIN=new ExceptionMessage("Security_Not_Login");

	public SecurityException() {
		super();
	}

	public SecurityException(String message) {
		super(message);
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityException(Throwable cause) {
		super(cause);
	}
	
	public  SecurityException(ExceptionMessage exceptionMessage){
		super(exceptionMessage);
	}
	
	public  SecurityException(ExceptionMessage exceptionMessage,Object[] params){
		super(exceptionMessage,params);
	}
}
