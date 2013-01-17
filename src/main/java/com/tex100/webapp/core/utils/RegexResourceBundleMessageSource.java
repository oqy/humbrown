package com.tex100.webapp.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class RegexResourceBundleMessageSource extends
		ResourceBundleMessageSource {
	private final String PROPERTY_POSTFIX = ".properties";
	
	public RegexResourceBundleMessageSource(String[] regexBaseNames){
		List<String> baseNameList = new ArrayList<String>();
		if (ArrayUtils.isNotEmpty(regexBaseNames)) {
			try {
				PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
				for (String baseName : regexBaseNames) {
					Resource[] resources = patternResolver.getResources(baseName);
					for (Resource resource : resources) {
						String fileName = resource.toString();
						fileName = fileName.substring(fileName.indexOf("com")).replace('\\', '.').replace('/', '.');
						baseNameList.add(fileName.substring(0,fileName.indexOf(PROPERTY_POSTFIX)));
					}
				}
			} catch (Exception e) {
			}
		}
		if (CollectionUtils.isNotEmpty(baseNameList)) {
			setBasenames(baseNameList.toArray(new String[baseNameList.size()]));
		}
	}
}
