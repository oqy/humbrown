package com.minyisoft.webapp.core.web.controller.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.assistant.IAutoCompleteObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.model.enumField.CoreEnumInterface;

/**
 * @author yongan_cui
 * 搜索组件Filter
 */
public class SelectModuleFilter{
	/**
	 * 过滤组件列表
	 */
	@Getter
	private List<SelectModuleUnitInfo> unitContentList = new ArrayList<SelectModuleUnitInfo>();
	/**
	 * 目标过滤对象
	 */
	@Getter
	private BaseCriteria criteria;
	/**
	 * 是否有排序条件
	 */
	@Getter
	private boolean sortFlag = false;

	/**
	 * 创建搜索组件
	 * @param criteria
	 */
	public SelectModuleFilter(BaseCriteria criteria) {
		this.criteria = criteria;
	}
	public SelectModuleFilter(BaseCriteria criteria,boolean sortFlag) {
		this.criteria = criteria;
		this.sortFlag = sortFlag;
		//buildSort();
	}

	/**
	 * 将过滤对象指定字段加入过滤元素列表，按对象默认展示方式进行展示，
	 * Date:TEXT;
	 * String:TEXT;
	 * Integer:TEXT;
	 * Enum:HIDDEN;
	 * Boolean:CHECKBOX;
	 * CoreBaseInfo:HIDDEN;
	 * 组件文字说明从指定对象的注解中获取
	 * @param name 过滤对象字段名，对应页面html组件id和name的名字
	 * @throws Exception
	 */
	public void addField(String name) throws Exception{
		addField(name, getFieldLable(name), getDefaultDisplayType(PropertyUtils.getPropertyType(criteria, name)), null, null);
	}
	
	/**
	 * 将过滤对象指定字段作为隐藏组件加入过滤元素列表
	 * @param name
	 * @throws Exception
	 */
	public void addHiddenField(String name) throws Exception{
		addField(name, null,DisplayTypeEnum.HIDDEN, null, null);
	}
	
	/**
	 * 将过滤对象指定字段作为autoComplete组件加入过滤元素列表
	 * @param name
	 * @param autoCompleteRequestUrl
	 * @throws Exception
	 */
	public void addAutoCompleteField(String name,String autoCompleteRequestUrl) throws Exception{
		addField(name, getFieldLable(name),DisplayTypeEnum.AUTO_COMPLETE, null,autoCompleteRequestUrl);
	}

	/**
	 * 将过滤对象指定字段加入过滤元素列表，按对象默认展示方式进行展示
	 * Date:TEXT;
	 * String:TEXT;
	 * Integer:TEXT;
	 * Enum:HIDDEN;
	 * Boolean:CHECKBOX;
	 * CoreBaseInfo:HIDDEN;
	 * @param name 过滤对象字段名，对应页面html组件id和name的名字
	 * @param labelVal 组件中的文字说明
	 * @throws Exception
	 */
	public void addField(String name,String labelVal) throws Exception{
		addField(name, labelVal, getDefaultDisplayType(PropertyUtils.getPropertyType(criteria, name)), null, null);
	}
	
	/**
	 * 将过滤对象指定字段加入过滤元素列表
	 * @param name 过滤对象字段名，对应页面html组件id和name的名字
	 * @param labelVal 组件中的文字说明
	 * @param displayType 组件展示方式
	 * @param displayPropertyName 过滤对象字段对应为CoreBaseInfo对象时作为显示值的属性字段名
	 * @throws Exception
	 */
	public void addField(String name,String labelVal,DisplayTypeEnum displayType,String displayPropertyName,String autoCompleteRequestUrl) throws Exception{
		Class<?> propertyType=PropertyUtils.getPropertyType(criteria, name);

		if(CoreBaseInfo.class.isAssignableFrom(propertyType)) {
			if(IAutoCompleteObject.class.isAssignableFrom(propertyType)&&!StringUtils.isBlank(autoCompleteRequestUrl)){
				SelectModuleUnitInfo selectUnit=new SelectModuleUnitInfo(labelVal,name,displayType,PropertyUtils.getProperty(criteria, name),"label");
				selectUnit.setAutoCompleteRequestUrl(autoCompleteRequestUrl);
				unitContentList.add(selectUnit);
			}else{
				unitContentList.add(new SelectModuleUnitInfo(labelVal,name+".id",displayType,PropertyUtils.getProperty(criteria, name),StringUtils.isBlank(displayPropertyName)?"name":displayPropertyName));
			}
		}else{
			unitContentList.add(new SelectModuleUnitInfo(labelVal,name,displayType,PropertyUtils.getProperty(criteria, name),null));
		}
	}

	/**
	 * @param name 组件id和name的名字
	 * @param optionList 组件的候选值
	 * @throws Exception
	 */
	public void addField(String name,List<?> optionList) throws Exception{
		addField(name,getFieldLable(name),optionList,null);
	}
	/**
	 * @param name 组件id和name的名字
	 * @param propertyName 组件为对象时，所取的属性名
	 * @param optionList 组件的候选值
	 * @throws Exception
	 */
	public void addField(String name,List<?> optionList,String displayPropertyName) throws Exception{
		addField(name,getFieldLable(name),optionList,displayPropertyName);
	}
	/**
	 * @param name 组件id和name的名字
	 * @param optionList 组件的候选值
	 * @param labelVal 组件中的文字说明
	 * @throws Exception
	 */
	public void addField(String name,String labelVal,List<?> optionList) throws Exception{
		addField(name,labelVal,optionList,null);
	}

	/**
	 * @param name 组件id和name的名字
	 * @param optionList 组件的候选值
	 * @param label 组件中的文字说明
	 * @param propertyName 组件为对象时，所取的属性名
	 * @throws Exception
	 */
	public void addField(String name,String label,List<?> optionList,String displayPropertyName) throws Exception{
		Class<?> propertyType=PropertyUtils.getPropertyType(criteria, name);

		//设置搜索组件的元素
		if(CoreBaseInfo.class.isAssignableFrom(propertyType)) {
			unitContentList.add(new SelectModuleUnitInfo(label,name+".id", DisplayTypeEnum.SELECT,PropertyUtils.getProperty(criteria, name),optionList,
					StringUtils.isBlank(displayPropertyName)?"name":displayPropertyName));
		} else if(Boolean.class.isAssignableFrom(propertyType)) {
			List<Boolean> bList = new ArrayList<Boolean>();
			bList.add(true);
			bList.add(false);
			unitContentList.add(new SelectModuleUnitInfo(label,name, DisplayTypeEnum.SELECT,PropertyUtils.getProperty(criteria, name),bList,
					StringUtils.isBlank(displayPropertyName)?"name":displayPropertyName));
		} else {
			unitContentList.add(new SelectModuleUnitInfo(label,name, DisplayTypeEnum.SELECT,PropertyUtils.getProperty(criteria, name),optionList,null));
		}
	}

	/**
	 * 根据排序名获取该排序字段的排序方式
	 * @param name 排序名
	 * @return
	 
	public String getSortDirection(String name) {
		String result = "";
		if(this.criteria.getSortDirections() != null && this.criteria.getSortDirections().length != 0) {
			for(SortDirection sort : this.criteria.getSortDirections()) {
				if(sort.getItem().equals(name)) {
					result = sort.getSortDirection().toString().toLowerCase();
					break;
				}
			}
		}
		return result;
	}*/

	/**
	 * 对排序进行处理
	 
	private void buildSort() {
		if(!StringUtils.isBlank(criteria.getSortDirectionString())) {
			try {
				criteria.setSortDirectionByString();
				criteria.setSortDirectionStringByArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/
	/**
	 * 获取过滤对象指定属性的@Label注解值
	 * @param criteria
	 * @param fieldName
	 * @return
	 */
	private String getFieldLable(String fieldName){
		try{
			Field field=null;
			if(StringUtils.indexOf(fieldName, '.')>0){
				String propertyName=StringUtils.substring(fieldName, StringUtils.lastIndexOf(fieldName, '.')+1);
				String classPath=StringUtils.substring(fieldName,0, StringUtils.lastIndexOf(fieldName, '.'));
				field=FieldUtils.getField(PropertyUtils.getPropertyType(criteria, classPath),propertyName, true);
			}else{
				field=FieldUtils.getField(criteria.getClass(), fieldName, true);
			}
			if(field.isAnnotationPresent(Label.class)){
				return (String)AnnotationUtils.getValue(field.getAnnotation(Label.class), "value");
			}else{
				return "";
			}
		}catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 获取对象类型默认显示方式
	 * Date:TEXT;
	 * String:TEXT;
	 * Integer:TEXT;
	 * Enum:HIDDEN;
	 * Boolean:CHECKBOX;
	 * CoreBaseInfo:HIDDEN;
	 * @param clazz
	 * @return
	 */
	private DisplayTypeEnum getDefaultDisplayType(Class<?> clazz){
		if(Date.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.DATE;
		}else if(CoreBaseInfo.class.isAssignableFrom(clazz)
				||CoreEnumInterface.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.HIDDEN;
		}else if(Boolean.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.CHECK_BOX;
		}else{
			return DisplayTypeEnum.TEXT;
		}
	}
}
