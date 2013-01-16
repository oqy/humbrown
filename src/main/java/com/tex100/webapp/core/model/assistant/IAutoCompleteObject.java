package com.tex100.webapp.core.model.assistant;

/**
 * @author qingyong_ou 供需实现jQuery AutoComplete组件异步查询功能的对象实现
 */
public interface IAutoCompleteObject {
	/**
	 * 获取对象id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * AutoComplete组件value属性
	 * 
	 * @return
	 */
	public String getValue();

	/**
	 * AutoComplete组件label属性
	 * 
	 * @return
	 */
	public String getLabel();
}
