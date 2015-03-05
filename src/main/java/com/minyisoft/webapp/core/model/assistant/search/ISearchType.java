package com.minyisoft.webapp.core.model.assistant.search;

import java.util.Map;

import lombok.Getter;

/**
 * @author qingyong_ou
 * 搜索引擎索引对象类别（等同数据库的table）接口
 */
public interface ISearchType {
	/**
	 * 获取名称
	 * @return
	 */
	String getTypeName();
	
	/**
	 * 获取用于关键字搜索的字段集合
	 * @return
	 */
	String[] getKeywordFields();
	
	/**
	 * 获取用于辅助搜索（如排序、聚合查询）的字段集合
	 * @return
	 */
	String[] getAssistiveFields();
	
	/**
	 * 获取索引字段属性集合，Map中key为字段名，value为字段对应属性集
	 * @return
	 */
	Map<String,ISearchType.FieldProperties> getFieldsProperties();
	
	/**
	 * 从指定待索引对象获取索引字段集合
	 * @param indexObject
	 * @return
	 */
	Map<String, Object> getIndexFields(ISearchDocObject indexObject);
	
	
	/**
	 * @author Administrator
	 * 索引字段属性集
	 */
	@Getter
	public class FieldProperties{
		private FieldTypeEnum fieldType;
		private IndexOprtTypeEnum indexOprtType;
		
		public FieldProperties(FieldTypeEnum type){
			fieldType=type;
		}
		
		public FieldProperties setIndexOprtType(IndexOprtTypeEnum type){
			indexOprtType=type;
			return this;
		}
	}
	
	/**
	 * @author qingyong_ou
	 * 索引字段类型
	 */
	public static enum FieldTypeEnum {
		STRING("string"), FLOAT("float"), DOUBLE("double"), BYTE("byte"), SHORT(
				"short"), INTEGER("integer"), LONG("long"), DATE("date"), BOOLEAN(
				"boolean"), BINARY("binary"), GEO_POINT("geo_point");

		private String type;

		private FieldTypeEnum(String t) {
			type = t;
		}

		@Override
		public String toString() {
			return type;
		}
	}
	
	/**
	 * @author qingyong_ou
	 * 索引字段分词操作类型
	 */
	public static enum IndexOprtTypeEnum{
		ANALYZED("analyzed"),NOT_ANALYZED("not_analyzed"),NO("no");
		
		private String type;

		private IndexOprtTypeEnum(String t) {
			type = t;
		}

		@Override
		public String toString() {
			return type;
		}
	}
}
