package com.minyisoft.webapp.core.model.assistant.search;

import lombok.Getter;

@Getter
public class CustomScriptField {
	private String name;
	private String script;

	public CustomScriptField(String name, String script) {
		this.name = name;
		this.script = script;
	}
}
