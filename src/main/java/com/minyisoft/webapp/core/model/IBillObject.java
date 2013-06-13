package com.minyisoft.webapp.core.model;

import com.minyisoft.webapp.core.service.IBillRelationProcessor;


/**
 * @author qingyong_ou
 * 表单对象基类接口
 */
public interface IBillObject extends IModelObject {
	/**
	 * 表单编码
	 * @return
	 */
	public String getBillNumber();
	/**
	 * 表单源单
	 * @return
	 */
	public IBillObject getSourceBill();
	/**
	 * 表单关系处理器
	 * @return
	 */
	public IBillRelationProcessor<? extends IBillObject> getBillRelationProcessor();
	/**
	 * 是否需通知关联对象以进行相应操作
	 * @param action
	 * @param observer
	 * @return
	 */
	public boolean shouldNotifyObservers(NotifyAction action,IBillObject observer);
	
	public enum NotifyAction{
		SAVE,DELETE;	
	}
}
