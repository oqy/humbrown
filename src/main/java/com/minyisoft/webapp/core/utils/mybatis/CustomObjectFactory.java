package com.minyisoft.webapp.core.utils.mybatis;

import java.util.List;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou 自定义对象创建工厂
 */
public class CustomObjectFactory extends DefaultObjectFactory {

	private static final long serialVersionUID = 1524725713463288862L;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes,
			List<Object> constructorArgs) {
		// 若类型为IModelObject且<constructor>中只包含一个1个字符型参数，返回一个增强后的IModelObject对象
		if (IModelObject.class.isAssignableFrom(type)
				&& constructorArgTypes != null && constructorArgs != null
				&& constructorArgs.size() == 1
				&& constructorArgs.get(0) instanceof String) {
			return (T) ObjectUuidUtils.getObject((String) constructorArgs
					.get(0));
		}
		// 否则调用父类方法
		return super.create(type, constructorArgTypes, constructorArgs);
	}
}
