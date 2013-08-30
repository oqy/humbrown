package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelBeanSerializerModifier extends BeanSerializerModifier {
	private ModelBeanSerializer _modelBeanSerializer=new ModelBeanSerializer();
	
	@Override
	public JsonSerializer<?> modifySerializer(SerializationConfig config,
			BeanDescription beanDesc, JsonSerializer<?> serializer) {
		if(beanDesc.getType().getClass()!=ModelBeanJavaType.class
				&&IModelObject.class.isAssignableFrom(beanDesc.getType().getRawClass())){
			return _modelBeanSerializer;
		}
		return super.modifySerializer(config, beanDesc, serializer);
	}
}
