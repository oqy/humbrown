package com.minyisoft.webapp.core.utils;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;

/**
 * @author qingong_ou 业务对象后加载拦截器
 */
public class ModelLazyLoadMethodInterceptor implements MethodInterceptor {
	/**
	 * 源业务对象
	 */
	private IModelObject bizModel;
	/**
	 * 是否已加载
	 */
	private boolean lazyLoaded = false;
	/**
	 * 加载排除方法
	 */
	private String[] excludeMethods = { "notify", "wait", "finalize",
			"getClass", "getId", "isIdPresented", "hashCode", "equals" };

	public ModelLazyLoadMethodInterceptor(IModelObject model) {
		super();
		bizModel = model;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		if (!lazyLoaded
				&& !StringUtils.startsWithAny(method.getName(), excludeMethods)) {
			bizModel = ServiceUtils.getModel(bizModel.getId());
			lazyLoaded = true;
		}
		if (bizModel == null) {
			return null;
		} else {
			return method.invoke(bizModel, args);
		}
	}

}
