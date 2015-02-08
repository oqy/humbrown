package com.minyisoft.webapp.core.utils;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;

/**
 * ModelObject帮助类
 * 
 * @author qingyong_ou
 */
public final class ObjectProxyUtils {

	private ObjectProxyUtils() {

	}

	/**
	 * 获取cglib代理对象
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CoreBaseInfo> T getCglibProxy(T model) {
		Assert.notNull(model);
		if (!ClassUtils.isCglibProxy(model)) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(model.getClass());
			enhancer.setCallback(new ModelLazyLoadMethodInterceptor(model));
			return (T) enhancer.create();
		}
		return model;
	}

	/**
	 * 获取被代理的原对象
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CoreBaseInfo> T deProxy(T model) {
		Assert.notNull(model);
		if (ClassUtils.isCglibProxy(model)) {
			return (T) ServiceUtils.getService(model.getClass()).getValue(model.getId());
		}
		return model;
	}
}
