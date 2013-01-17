package com.minyisoft.webapp.core.exception;


public class EntityException extends BaseException {
	private static final long serialVersionUID = 7322667667588541195L;
	
	public static final ExceptionMessage ENTITY_ID_NOT_EXIST = new ExceptionMessage("Entity_ID_Not_Exist");
	public static final ExceptionMessage ENTITY_OBJECT_ID_GENERATE_ERROR = new ExceptionMessage("Entity_Object_ID_Generate_Error");
	public static final ExceptionMessage ENTITY_DATA_ACCESS_ERROR = new ExceptionMessage("Entity_Data_Access_Error");

	public EntityException() {
		super();
	}

	public EntityException(String message) {
		super(message);
	}

	public EntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityException(Throwable cause) {
		super(cause);
	}
	
	public  EntityException(ExceptionMessage exceptionMessage){
		super(exceptionMessage);
	}
	
	public  EntityException(ExceptionMessage exceptionMessage,Object[] params){
		super(exceptionMessage,params);
	}
}
