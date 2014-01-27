package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelObjectSerializerModifier extends BeanSerializerModifier {
	private ModelObjectSerializer _modelBeanSerializer = new ModelObjectSerializer();

	@Override
	public JsonSerializer<?> modifySerializer(SerializationConfig config,
			BeanDescription beanDesc, JsonSerializer<?> serializer) {
		Class<?> rowClass = beanDesc.getType().getRawClass();
		if (IModelObject.class.isAssignableFrom(rowClass)
				&& ClassUtils.isCglibProxyClass(rowClass)) {
			return _modelBeanSerializer;
		}
		return serializer;
	}
}
