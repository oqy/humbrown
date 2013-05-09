package com.minyisoft.webapp.core.utils.mapper.json;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.mapper.json.jackson.ModelBeanJavaType;
import com.minyisoft.webapp.core.utils.mapper.json.jackson.ModelObjectModule;


/**
 * @author qingyong_ou
 * 针对IModelObject对象作转换的JsonMapper
 */
public enum ModelJsonMapper {
	INSTANCE;
	
	private ObjectMapper objectMapper;
	
	private final ConcurrentMap<Class<? extends IModelObject>,ModelBeanJavaType> typeMap = new ConcurrentHashMap<Class<? extends IModelObject>,ModelBeanJavaType>();
	
	private ModelJsonMapper(){
		objectMapper=JsonMapper.nonDefaultMapper().getMapper();
		ModelObjectModule module=new ModelObjectModule();
		objectMapper.registerModule(module);
		// 转换json时只检查变量
		objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
																				.withFieldVisibility(Visibility.ANY)
																				.withGetterVisibility(Visibility.NONE)
																				.withIsGetterVisibility(Visibility.NONE)
																				.withSetterVisibility(Visibility.NONE));
	}
	
	public ObjectMapper getMapper(){
		return objectMapper;
	}
	
	/**
	 * 将指定对象转换为json格式字符串
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	public <T extends IModelObject> String toJsonString(T object) throws JsonProcessingException{
		return objectMapper.writerWithType(getType(object.getClass())).writeValueAsString(object);
	}
	
	/**
	 * 将指定对象转换为json格式字节数组
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	public <T extends IModelObject> byte[] toJsonByte(T object) throws JsonProcessingException{
		return objectMapper.writerWithType(getType(object.getClass())).writeValueAsBytes(object);
	}
	
	/**
	 * 将指定json字符串转换为指定类型对象
	 * @param s
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public <T extends IModelObject> T fromJsonString(String s,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		return (T)objectMapper.readValue(s, getType(clazz));
	}
	
	/**
	 * 将指定json字节数组转换为指定类型对象
	 * @param bytes
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public <T extends IModelObject> T fromJsonByte(byte[] bytes,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		return (T)objectMapper.readValue(bytes, getType(clazz));
	}
	
	/**
	 * 将指定对象集合转换为json格式字符串
	 * @param col
	 * @return
	 * @throws JsonProcessingException
	 */
	public <T extends IModelObject> String toJsonString(Collection<T> col) throws JsonProcessingException{
		return objectMapper.writeValueAsString(col);
	}
	
	/**
	 * 将指定对象集合转换为json格式字节数组
	 * @param col
	 * @return
	 * @throws JsonProcessingException
	 */
	public <T extends IModelObject> byte[] toJsonByte(Collection<T> col) throws JsonProcessingException{
		return objectMapper.writeValueAsBytes(col);
	}
	
	public <T extends IModelObject> Collection<T> fromJsonCollectionString(String s,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		JavaType modelCollectionRootType=objectMapper.getTypeFactory().constructCollectionType(Collection.class, clazz);
		return objectMapper.readValue(s, modelCollectionRootType);
	}
	
	public <T extends IModelObject> Collection<T> fromJsonCollectionByte(byte[] s,Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		JavaType modelCollectionRootType=objectMapper.getTypeFactory().constructCollectionType(Collection.class, clazz);
		return objectMapper.readValue(s, modelCollectionRootType);
	}
	
	/**
	 * 缓存IModelObject class对应的ModelBeanJavaType
	 * @param clazz
	 * @return
	 */
	private ModelBeanJavaType getType(Class<? extends IModelObject> clazz){
		ModelBeanJavaType type=typeMap.get(clazz);
		if(type==null){
			type=ModelBeanJavaType.construct(clazz);
			typeMap.put(clazz, type);
		}
		return type;
	}
}
