package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class ModelObjectModule extends Module {

	@Override
	public String getModuleName() {
		return "jackson-datatype-webapp-modelobject";
	}

	@Override
	public Version version() {
		return Version.unknownVersion();
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addBeanSerializerModifier(new ModelObjectSerializerModifier());
		context.addBeanDeserializerModifier(new ModelObjectDeserializerModifier());
	}

}
