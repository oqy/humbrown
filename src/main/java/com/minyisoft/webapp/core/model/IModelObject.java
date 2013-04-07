package com.minyisoft.webapp.core.model;

/**
 * @author qingyong_ou
 * 业务对象基类接口，所有业务对象均需继承
 */
public interface IModelObject {
	/**
	 * 获取对象主键
	 * @return
	 */
	public String getId();
	
	/**
	 * 设置对象主键
	 * @param id
	 */
	public void setId(String id);
	
	/**
	 * 判断id是否非空
	 * @return
	 */
	public boolean isIdPresented();
}
