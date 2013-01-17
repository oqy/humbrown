package com.tex100.webapp.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class Springs{
	private static WebApplicationContext context=ContextLoader.getCurrentWebApplicationContext();
	
	public static Object getBean(String name) throws BeansException {
		if(context==null){
			context=ContextLoader.getCurrentWebApplicationContext();
		}
		return context.getBean(name);
	}

}