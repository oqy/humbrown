package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelBeanDeserializerModifier extends BeanDeserializerModifier {
	private ModelBeanDeserializer _modelBeanDeserializer=new ModelBeanDeserializer(IModelObject.class);
	
	@Override
	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		if(beanDesc.getType().getClass()!=ModelBeanJavaType.class
				&&IModelObject.class.isAssignableFrom(beanDesc.getType().getRawClass())){
			return _modelBeanDeserializer;
		}
		return super.modifyDeserializer(config, beanDesc, deserializer);
	}
}
