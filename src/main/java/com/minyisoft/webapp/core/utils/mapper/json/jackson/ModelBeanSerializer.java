package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelBeanSerializer extends StdSerializer<IModelObject> {

	protected ModelBeanSerializer(Class<IModelObject> t) {
		super(t);
	}

	@Override
	public void serialize(IModelObject value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		if(value!=null&&value.isIdPresented()){
			jgen.writeString(value.getId());
		}
	}

}
