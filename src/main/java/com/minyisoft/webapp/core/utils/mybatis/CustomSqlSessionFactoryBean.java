package com.minyisoft.webapp.core.utils.mybatis;

import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Set;

import lombok.Setter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.InputStreamResource;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

/**
 * 自定义SqlSessionFactoryBean，自动注册整形及字符型ICoreEnum枚举类的typeHandler，但会忽略已设定的config配置文件信息
 * @author qingyong_ou
 */
public class CustomSqlSessionFactoryBean extends SqlSessionFactoryBean {
	// ICoreEnum枚举类型所在包名
	private @Setter String describableEnumPackage;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 注册整形及字符型ICoreEnum枚举类的typeHandler
		if (hasLength(this.describableEnumPackage)) {
			StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\"><configuration><typeHandlers>");
			
			String[] coreEnumPackageArray = tokenizeToStringArray(this.describableEnumPackage,ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ResolverUtil<DescribableEnum<?>> resolverUtil = new ResolverUtil<DescribableEnum<?>>();
			for (String packageToScan : coreEnumPackageArray) {
				resolverUtil.findImplementations(DescribableEnum.class, packageToScan);
				Set<Class<? extends DescribableEnum<?>>> typeSet = resolverUtil.getClasses();
				for (Class<? extends DescribableEnum<?>> clazz : typeSet) {
					if (DescribableEnum.class.isAssignableFrom(clazz)
							&& ArrayUtils.isNotEmpty(clazz.getGenericInterfaces())) {
						Type type = clazz.getGenericInterfaces()[0];
						// 如果该泛型类型是参数化类型
						if (type instanceof ParameterizedType) {
							// 获取泛型类型的实际类型参数集
							Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
							// 取出第一个(下标为0)参数的值
							if (parameterizedType[0] == String.class) {
								sb.append("<typeHandler handler=\""+StringEnumTypeHandler.class.getName()+"\" javaType=\""+clazz.getName()+"\"/>");
							} else if (parameterizedType[0] == Integer.class) {
								sb.append("<typeHandler handler=\""+IntEnumTypeHandler.class.getName()+"\" javaType=\""+clazz.getName()+"\"/>");
							}
						}
					}
				}
			}
			sb.append("</typeHandlers></configuration>");
			setConfigLocation(new InputStreamResource(IOUtils.toInputStream(sb.toString(), Charset.forName("UTF-8"))));
		}
		// 注册ModelTypeHandler和PermissionInfo别名
		setTypeAliases(new Class<?>[]{ModelTypeHandler.class,PermissionInfo.class});
		super.afterPropertiesSet();
		
		Configuration configuration=getObject().getConfiguration();
		// 插入null值时jdbcType默认类型
		configuration.setJdbcTypeForNull(JdbcType.NULL);
	}
}