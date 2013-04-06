package com.minyisoft.webapp.core.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author qingyong_ou
 * 业务接口切面基类
 */
public class BaseServiceAspect extends BaseAspect{
	/**
	 * 删除Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.delete(java.lang.String))")
	public void deletePointcut() {}
	
	/**
	 * 批量删除Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.batchDelete(java.lang.String[]))")
	public void batchDeletePointcut() {}
	
	/**
	 * 新增Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.addNew(com.minyisoft.webapp.core.model.IModelObject))")
	public void addNewPointcut() {}
	
	/**
	 * 更新Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.save(com.minyisoft.webapp.core.model.IModelObject))")
	public void savePointcut() {}
	
	/**
	 * 提交Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.submit(com.minyisoft.webapp.core.model.IModelObject))")
	public void submitPointcut() {}
	
	/**
	 * 取值Pointcut
	 */
	@Pointcut("execution(* com.minyisoft.webapp.*.service.impl.*.getValue(java.lang.String))")
	public void getValuePointcut(){}
}
