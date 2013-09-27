package com.minyisoft.webapp.core.exception;

public class WebException extends BaseException {
	private static final long serialVersionUID = 8062265912176020985L;
	
	public static final ExceptionMessage WEB_VERIFY_ERROR = new ExceptionMessage("Web_Verify_Error");
	public static final ExceptionMessage WEB_VERIFY_CODE_ERROR = new ExceptionMessage("Web_Verify_Code_Error");
	public static final ExceptionMessage WEB_VERIFY_CODE_EXPIRED = new ExceptionMessage("Web_Verify_Code_Expired");
	public static final ExceptionMessage WEB_MESSAGE_NOT_SAME_COMPANY = new ExceptionMessage("Web_Message_Not_Same_Company");

	public WebException() {
		super();
	}

	public WebException(String message) {
		super(message);
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebException(Throwable cause) {
		super(cause);
	}
	
	public  WebException(ExceptionMessage exceptionMessage){
		super(exceptionMessage);
	}
	
	public  WebException(ExceptionMessage exceptionMessage,Object[] params){
		super(exceptionMessage,params);
	}
}
