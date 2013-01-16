package com.tex100.webapp.core.exception;

import java.io.Serializable;
import java.util.ResourceBundle;

public class ExceptionMessage implements Serializable {
	private static final long serialVersionUID = -1123559445374542058L;
	
	private static final String classPath=ExceptionMessage.class.getName().substring(0, ExceptionMessage.class.getName().lastIndexOf('.')+1);
	private static final ResourceBundle rb=ResourceBundle.getBundle(classPath+"exceptionMessage");

	private String exceptionMsg;

	protected ExceptionMessage(String msgKey) {
		this.exceptionMsg = rb.getString(msgKey);
	}
	
	public String getExceptionMessage(){
		return exceptionMsg;
	}
}
