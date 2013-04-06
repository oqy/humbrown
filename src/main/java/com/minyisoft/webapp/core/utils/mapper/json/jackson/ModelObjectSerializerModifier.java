package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class ModelObjectSerializerModifier extends BeanSerializerModifier {
	@Override
	public List<BeanPropertyWriter> changeProperties(
			SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
		if(CollectionUtils.isNotEmpty(beanProperties)){
			BeanPropertyWriter[] writers = beanProperties.toArray(new BeanPropertyWriter[beanProperties.size()]);
			for (int i = 0; i < writers.length; ++i) {
				writers[i] = new ModelObjectPropertyWriter (writers[i]);
			}
			beanProperties=Arrays.asList(writers);
		}
		return beanProperties;
	}
}
