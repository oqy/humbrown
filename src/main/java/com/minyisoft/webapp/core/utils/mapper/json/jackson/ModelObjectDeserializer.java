package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class ModelObjectDeserializer extends StdDeserializer<IModelObject> implements ResolvableDeserializer {
	private static final long serialVersionUID = -7882247910895109366L;

	private final JsonDeserializer<?> defaultDeserializer;

	protected ModelObjectDeserializer(JsonDeserializer<?> defaultDeserializer) {
		super(IModelObject.class);
		this.defaultDeserializer = defaultDeserializer;
	}

	@Override
	public IModelObject deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		if (StringUtils.isNotBlank(jp.getValueAsString())) {
			return ObjectUuidUtils.getObject(jp.getValueAsString());
		}
		return (IModelObject) defaultDeserializer.deserialize(jp, ctxt);
	}

	@Override
	public void resolve(DeserializationContext ctxt) throws JsonMappingException {
		if (defaultDeserializer instanceof ResolvableDeserializer) {
			((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
		}
	}
}
