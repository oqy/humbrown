package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelObjectDeserializerModifier extends BeanDeserializerModifier {
	@Override
	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		if (IModelObject.class.isAssignableFrom(beanDesc.getType().getRawClass())) {
			return new ModelObjectDeserializer(deserializer);
		}
		return deserializer;
	}
}
