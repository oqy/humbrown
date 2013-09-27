package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class ModelBeanDeserializer extends StdDeserializer<IModelObject> {
	private static final long serialVersionUID = -7882247910895109366L;

	protected ModelBeanDeserializer() {
		super(IModelObject.class);
	}

	@Override
	public IModelObject deserialize(JsonParser jp,DeserializationContext ctxt) throws IOException,JsonProcessingException {
		if (StringUtils.isNotBlank(jp.getValueAsString())) {
			return ObjectUuidUtils.getObjectById(jp.getValueAsString());
		}
		return null;
	}
}
