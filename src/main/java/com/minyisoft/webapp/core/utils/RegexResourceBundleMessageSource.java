package com.minyisoft.webapp.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class RegexResourceBundleMessageSource extends ResourceBundleMessageSource {
	private static final Logger logger=LoggerFactory.getLogger(RegexResourceBundleMessageSource.class);
	private final String PROPERTY_POSTFIX = ".properties";
	private final String PACKAGE_PREFIX="com.minyisoft.webapp.";
	
	public RegexResourceBundleMessageSource(String[] regexBaseNames){
		List<String> baseNameList = new ArrayList<String>();
		if (ArrayUtils.isNotEmpty(regexBaseNames)) {
			try {
				PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
				for (String baseName : regexBaseNames) {
					Resource[] resources = patternResolver.getResources(baseName);
					for (Resource resource : resources) {
						String fileName=resource.toString();
						if(fileName.indexOf(StringUtils.replace(PACKAGE_PREFIX, ".", "\\"))>0){
							fileName=StringUtils.substring(fileName, fileName.lastIndexOf(StringUtils.replace(PACKAGE_PREFIX, ".", "\\")));
							fileName=StringUtils.substringBefore(fileName, PROPERTY_POSTFIX).replace("\\", ".");
						}else if(fileName.indexOf(StringUtils.replace(PACKAGE_PREFIX, ".", "/"))>0){
							fileName=StringUtils.substring(fileName, fileName.lastIndexOf(StringUtils.replace(PACKAGE_PREFIX, ".", "/")));
							fileName=StringUtils.substringBefore(fileName, PROPERTY_POSTFIX).replace("/", ".");
						}
						baseNameList.add(fileName);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		if (CollectionUtils.isNotEmpty(baseNameList)) {
			setBasenames(baseNameList.toArray(new String[baseNameList.size()]));
		}
	}
	
	/**
	 * 系统默认资源文件
	 */
	private static final RegexResourceBundleMessageSource systemDefaultMessageSource=new RegexResourceBundleMessageSource(new String[]{"classpath*:com/minyisoft/webapp/**/exceptionMessage.properties","classpath*:com/minyisoft/webapp/**/enumField/enumDescription.properties"});
	
	/**
	 * 获取系统默认资源文件
	 * @return
	 */
	public static final RegexResourceBundleMessageSource getSystemDefaultMessageSource(){
		return systemDefaultMessageSource;
	}
}
