package com.minyisoft.webapp.core.utils.mybatis;

import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.util.Set;

import lombok.Setter;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import com.google.common.base.Charsets;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

/**
 * 自定义SqlSessionFactoryBean，自动注册整形及字符型DescribableEnum枚举类的typeHandler，但会忽略已设定的config配置文件信息
 * @author qingyong_ou
 */
public class CustomSqlSessionFactoryBean extends SqlSessionFactoryBean {
	// DescribableEnum枚举类型所在包名
	private @Setter String describableEnumPackage;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 注册整形及字符型DescribableEnum枚举类的typeHandler
		if (hasLength(this.describableEnumPackage)) {
			StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\"><configuration><typeHandlers>");
			
			String[] describableEnumPackages = tokenizeToStringArray(this.describableEnumPackage,ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ResolverUtil<DescribableEnum<?>> resolverUtil = new ResolverUtil<DescribableEnum<?>>();
			for (String packageToScan : describableEnumPackages) {
				resolverUtil.findImplementations(DescribableEnum.class, packageToScan);
				Set<Class<? extends DescribableEnum<?>>> typeSet = resolverUtil.getClasses();
				for (Class<? extends DescribableEnum<?>> clazz : typeSet) {
					sb.append("<typeHandler handler=\""+DescribableEnumTypeHandler.class.getName()+"\" javaType=\""+clazz.getName()+"\"/>");
				}
			}
			sb.append("</typeHandlers></configuration>");
			setConfigLocation(new ByteArrayResource(sb.toString().getBytes(Charsets.UTF_8)));
		}
		// 注册ModelTypeHandler和PermissionInfo别名
		setTypeAliases(new Class<?>[] { ModelTypeHandler.class,
				PermissionInfo.class, DescribableEnumArrayTypeHandler.class });
		// 注册自定义ObjectFactory
		setObjectFactory(new CustomObjectFactory());
		super.afterPropertiesSet();
		
		Configuration configuration = getObject().getConfiguration();
		// 插入null值时jdbcType默认类型
		configuration.setJdbcTypeForNull(JdbcType.NULL);
	}
}