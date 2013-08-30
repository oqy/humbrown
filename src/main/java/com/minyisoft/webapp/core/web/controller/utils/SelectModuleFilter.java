package com.minyisoft.webapp.core.web.controller.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.assistant.IAutoCompleteObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.model.criteria.SortDirection;
import com.minyisoft.webapp.core.model.enumField.ICoreEnum;

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
		buildSort();
	}

	/**
	 * 将过滤对象指定字段加入过滤元素列表，按对象默认展示方式进行展示，
	 * Date:TEXT;
	 * String:TEXT;
	 * Integer:TEXT;
	 * Enum:HIDDEN;
	 * Boolean:CHECKBOX;
	 * IModelObject:HIDDEN;
	 * 组件文字说明从指定对象的注解中获取
	 * @param propertyName 过滤对象字段名，对应页面html组件id和name的名字
	 * @throws Exception
	 */
	public void addField(String propertyName) throws Exception{
		_addField(propertyName, getFieldLable(propertyName), null, null);
	}
	
	/**
	 * 将过滤对象指定字段加入过滤元素列表，按对象默认展示方式进行展示
	 * Date:TEXT;
	 * String:TEXT;
	 * Integer:TEXT;
	 * Enum:HIDDEN;
	 * Boolean:CHECKBOX;
	 * IModelObject:HIDDEN;
	 * @param propertyName 过滤对象字段名，对应页面html组件id和name的名字
	 * @param label 组件中的文字说明
	 * @throws Exception
	 */
	public void addField(String propertyName,String label) throws Exception{
		_addField(propertyName, label, null, null);
	}
	
	/**
	 * 将过滤对象指定字段作为隐藏组件加入过滤元素列表
	 * @param propertyName
	 * @throws Exception
	 */
	public void addHiddenField(String propertyName) throws Exception{
		_addField(propertyName, null, DisplayTypeEnum.HIDDEN, null);
	}
	
	/**
	 * 将过滤对象指定字段作为autoComplete组件加入过滤元素列表
	 * @param propertyName
	 * @param autoCompleteRequestUrl
	 * @throws Exception
	 */
	public void addAutoCompleteField(String propertyName,String autoCompleteRequestUrl) throws Exception{
		_addField(propertyName, getFieldLable(propertyName), DisplayTypeEnum.AUTO_COMPLETE,autoCompleteRequestUrl);
	}
	
	/**
	 * 将过滤对象指定字段加入过滤元素列表
	 * @param propertyName 过滤对象字段名，对应页面html组件id和name的名字
	 * @param label 组件中的文字说明
	 * @param displayType 组件展示方式
	 * @param displayPropertyName 过滤对象字段对应为IModelObject对象时作为显示值的属性字段名
	 * @throws Exception
	 */
	private void _addField(String propertyName, String label, DisplayTypeEnum displayType, String autoCompleteRequestUrl) throws Exception{
		Class<?> propertyType=PropertyUtils.getPropertyType(criteria, propertyName);
		displayType=(displayType==null)?getDefaultDisplayType(PropertyUtils.getPropertyType(criteria, propertyName)):displayType;
		
		if(propertyType.isArray()){
			Object[] objs=(Object[])PropertyUtils.getProperty(criteria, propertyName);
			if(ArrayUtils.isEmpty(objs)){
				_addToUnitContentList(propertyName,null,label,propertyType,null,displayType,autoCompleteRequestUrl);
			}else{
				int count=0;
				for(Object obj:objs){
					_addToUnitContentList(propertyName,propertyName+"_"+count,label,propertyType,obj,displayType,autoCompleteRequestUrl);
					count++;
				}
			}
		}else if(Collection.class.isAssignableFrom(propertyType)){
			Collection<?> objs=(Collection<?>)PropertyUtils.getProperty(criteria, propertyName);
			if(CollectionUtils.isEmpty(objs)){
				_addToUnitContentList(propertyName,null,label,propertyType,null,displayType,autoCompleteRequestUrl);
			}else{
				int count=0;
				for(Object obj:objs){
					_addToUnitContentList(propertyName,propertyName+"_"+count,label,propertyType,obj,displayType,autoCompleteRequestUrl);
					count++;
				}
			}
		}else{
			_addToUnitContentList(propertyName,null,label,propertyType,PropertyUtils.getProperty(criteria, propertyName),displayType,autoCompleteRequestUrl);
		}
	}
	
	/**
	 * @param propertyName
	 * @param id
	 * @param label
	 * @param propertyType
	 * @param propertyValue
	 * @param displayType
	 * @param autoCompleteRequestUrl
	 */
	private void _addToUnitContentList(String propertyName, String id, String label, Class<?> propertyType, Object propertyValue, DisplayTypeEnum displayType, String autoCompleteRequestUrl) {
		SelectModuleUnitInfo selectUnit=null;
		if(displayType==DisplayTypeEnum.AUTO_COMPLETE
				&&IAutoCompleteObject.class.isAssignableFrom(propertyType)
				&&!StringUtils.isBlank(autoCompleteRequestUrl)){
			selectUnit=new SelectModuleUnitInfo(label,propertyName,displayType,propertyValue);
			selectUnit.setAutoCompleteRequestUrl(autoCompleteRequestUrl);
		}else{
			if(IAutoCompleteObject.class.isAssignableFrom(propertyType)){
				displayType=getDefaultDisplayType(IModelObject.class);
			}
			selectUnit=new SelectModuleUnitInfo(label,propertyName,displayType,propertyValue);
		}
		if(StringUtils.isNotBlank(id)){
			selectUnit.setId(id);
		}
		unitContentList.add(selectUnit);
	}

	/**
	 * @param name 组件id和name的名字
	 * @param optionList 组件的候选值
	 * @throws Exception
	 */
	public void addField(String name,List<?> optionList) throws Exception{
		addField(name,getFieldLable(name),optionList);
	}

	/**
	 * @param name 组件id和name的名字
	 * @param optionList 组件的候选值
	 * @param label 组件中的文字说明
	 * @throws Exception
	 */
	public void addField(String name,String label,List<?> optionList) throws Exception{
		Class<?> propertyType=PropertyUtils.getPropertyType(criteria, name);

		//设置搜索组件的元素
		if(Boolean.class.isAssignableFrom(propertyType)&&CollectionUtils.isEmpty(optionList)) {
			List<Boolean> bList = new ArrayList<Boolean>();
			bList.add(true);
			bList.add(false);
			unitContentList.add(new SelectModuleUnitInfo(label,name, DisplayTypeEnum.SELECT,PropertyUtils.getProperty(criteria, name),bList));
		} else {
			unitContentList.add(new SelectModuleUnitInfo(label,name, DisplayTypeEnum.SELECT,PropertyUtils.getProperty(criteria, name),optionList));
		}
	}

	/**
	 * 根据排序名获取该排序字段的排序方式
	 * @param name 排序名
	 * @return
	 */
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
	}

	/**
	 * 是否隐藏
	 * @return
	 * @throws Exception
	 */
	public boolean isHideSelectForm() throws Exception{
		if(unitContentList != null && unitContentList.size() > 0) {
			for(SelectModuleUnitInfo info : unitContentList) {
				if(!info.getType().equalsIgnoreCase("hidden")) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 获取过滤组件对应http request的查询字符串
	 * @return
	 */
	public String getRequestQueryString(){
		if(CollectionUtils.isNotEmpty(unitContentList)){
			StringBuffer sb=new StringBuffer();
			for(SelectModuleUnitInfo  unit : unitContentList){
				if(unit.getValue()!=null){
					if(unit.getValue().getClass().isArray()){
						for(Object o :(Object[])unit.getValue()){
							try {
								sb.append(unit.getName()).append("=").append(URLEncoder.encode(unit.getObjectValue(o), "utf-8")).append("&");
							} catch (UnsupportedEncodingException e) {
							}
						}
					}else if(Collection.class.isAssignableFrom(unit.getValue().getClass())){
						for(Object o :(Collection<?>)unit.getValue()){
							try {
								sb.append(unit.getName()).append("=").append(URLEncoder.encode(unit.getObjectValue(o), "utf-8")).append("&");
							} catch (UnsupportedEncodingException e) {
							}
						}
					}else{
						try {
							sb.append(unit.getName()).append("=").append(URLEncoder.encode(unit.getObjectValue(unit.getValue()), "utf-8")).append("&");
						} catch (UnsupportedEncodingException e) {
						}
					}
				}
			}
			return sb.length()>1?sb.deleteCharAt(sb.length()-1).toString():null;
		}
		return null;
	}
	
	/**
	 * 根据指定url构建完整的查询路径
	 * @param url
	 * @return
	 */
	public String getRequestUrl(String url){
		if(StringUtils.isBlank(url)){
			return null;
		}
		String queryString=getRequestQueryString();
		if(StringUtils.isBlank(queryString)){
			return url;
		}else if(url.indexOf('?')<0){
			return url+"?"+queryString;
		}else{
			return url+"&"+queryString;
		}
	}
	
	/**
	 * 对排序进行处理
	 */
	private void buildSort() {
		if(!StringUtils.isBlank(criteria.getSortDirectionString())) {
			try {
				criteria.setSortDirectionByString();
				criteria.setSortDirectionStringByArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
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
	 * IModelObject:HIDDEN;
	 * @param clazz
	 * @return
	 */
	private DisplayTypeEnum getDefaultDisplayType(Class<?> clazz){
		if(Date.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.DATE;
		}else if(IModelObject.class.isAssignableFrom(clazz)
				||ICoreEnum.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.HIDDEN;
		}else if(Boolean.class.isAssignableFrom(clazz)){
			return DisplayTypeEnum.CHECK_BOX;
		}else{
			return DisplayTypeEnum.TEXT;
		}
	} 
}
