package com.minyisoft.webapp.core.model.enumField;

/**
 * @author qingyong_ou 枚举对象接口
 */
public interface CoreEnumInterface<T> {
	/**
	 * 获取枚举值
	 * 
	 * @return
	 */
	public T getValue();

	/**
	 * 获取枚举值描述
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 枚举变量名
	 * 
	 * @return
	 */
	public String name();
}
