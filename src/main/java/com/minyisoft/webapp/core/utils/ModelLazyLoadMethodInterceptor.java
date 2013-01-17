package com.minyisoft.webapp.core.utils;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.service.utils.Services;

/**
 * @author qingong_ou
 * 业务对象后加载拦截器
 */
public class ModelLazyLoadMethodInterceptor implements MethodInterceptor,Serializable {
	private static final long serialVersionUID = -8191145475189976338L;
	/**
	 * 源业务对象
	 */
	private CoreBaseInfo bizModel;
	/**
	 * 是否已加载
	 */
	private boolean lazyLoaded=false;
	
	public ModelLazyLoadMethodInterceptor(CoreBaseInfo model){
		super();
		bizModel=model;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (!lazyLoaded && method.getName().indexOf("getId") < 0) {
			bizModel=Services.getModel(bizModel);
			lazyLoaded=true;
        }
		if(bizModel==null){
			return null;
		}else{
			return method.invoke(bizModel, args);
		}
	}

}
