package com.minyisoft.webapp.core.model.assistant;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 供需实现jQuery AutoComplete组件异步查询功能的对象实现
 */
public interface IAutoCompleteObject extends IModelObject {
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
