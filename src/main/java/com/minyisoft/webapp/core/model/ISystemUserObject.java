package com.minyisoft.webapp.core.model;

/**
 * @author qingyong_ou 系统用户对象接口
 */
public interface ISystemUserObject extends IModelObject {
	/**
	 * 用户名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 用户登录账号
	 * 
	 * @return
	 */
	public String getUserLoginName();

	/**
	 * 用户登录密码
	 * 
	 * @return
	 */
	public String getUserPassword();

	/**
	 * 用户登录密码附加字符串
	 * 
	 * @return
	 */
	public String getUserPasswordSalt();
}
