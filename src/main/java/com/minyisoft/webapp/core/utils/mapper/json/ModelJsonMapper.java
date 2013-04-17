package com.minyisoft.webapp.core.utils.mapper.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minyisoft.webapp.core.utils.mapper.json.jackson.ModelObjectModule;

/**
 * @author qingyong_ou
 * 针对IModelObject对象作转换的JsonMapper
 */
public class ModelJsonMapper {
	//private static Logger logger = LoggerFactory.getLogger(ModelJsonMapper.class);
	
	private static ModelJsonMapper mapper=new ModelJsonMapper();
	
	private ObjectMapper objectMapper;
	
	private ModelJsonMapper(){
		objectMapper=JsonMapper.nonDefaultMapper().getMapper();
		objectMapper.registerModule(new ModelObjectModule());
		// 转换json时只检查变量
		objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
																				.withFieldVisibility(Visibility.ANY)
																				.withGetterVisibility(Visibility.NONE)
																				.withIsGetterVisibility(Visibility.NONE)
																				.withSetterVisibility(Visibility.NONE));
	}
	
	public static ModelJsonMapper getInstance(){
		return mapper;
	}
	
	public ObjectMapper getMapper(){
		return objectMapper;
	}
	
	/*public String toJson(Object object){
		try {
			return objectMapper.writeValueAsString(object);
		}catch (Exception e) {
			logger.warn("write to json string error:" + object, e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}

		try {
			if(IModelObject.class.isAssignableFrom(clazz)){
				return (T)objectMapper.readValue(jsonString, ModelObjectJavaType.construct(ClassUtils.getUserClass(clazz)));
			}else{
				return (T)objectMapper.readValue(jsonString, clazz);
			}
		} catch (IOException e) {
			logger.error("parse json string error:" + jsonString, e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonString, TypeReference<T> reference) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}

		try {
			return (T)objectMapper.readValue(jsonString, reference);
		} catch (IOException e) {
			logger.error("parse json string error:" + jsonString, e);
			return null;
		}
	}*/
}
