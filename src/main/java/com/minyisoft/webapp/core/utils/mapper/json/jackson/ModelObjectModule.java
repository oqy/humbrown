package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class ModelObjectModule extends SimpleModule {
	private static final long serialVersionUID = 5309608946736458160L;

	@Override
	public void setupModule(SetupContext context) {
		super.setupModule(context);
		context.addBeanSerializerModifier(new ModelBeanSerializerModifier());
		context.addBeanDeserializerModifier(new ModelBeanDeserializerModifier());
	}

}
