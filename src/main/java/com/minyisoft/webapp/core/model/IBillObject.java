package com.minyisoft.webapp.core.model;

import java.util.List;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.service.BillRelationProcessor;

/**
 * @author qingyong_ou 表单对象基类接口
 */
public interface IBillObject extends IModelObject {
	/**
	 * 表单编码
	 * 
	 * @return
	 */
	String getBillNumber();

	/**
	 * 表单源单
	 * 
	 * @return
	 */
	IBillObject getSourceBill();

	/**
	 * 获取指定类型的子单
	 * 
	 * @param childBillClass
	 * @return
	 */
	<T extends IBillObject> Optional<T> getChildBill(Class<T> childBillClass);

	/**
	 * 获取指定类型的所有子单
	 * 
	 * @param childBillClass
	 * @return
	 */
	<T extends IBillObject> Optional<List<T>> getChildBills(Class<T> childBillClass);

	/**
	 * 表单关系处理器
	 * 
	 * @return
	 */
	BillRelationProcessor<? extends IBillObject> getBillRelationProcessor();

	/**
	 * 是否需通知关联对象以进行相应操作
	 * 
	 * @param action
	 * @param observer
	 * @return
	 */
	boolean shouldNotifyObservers(NotifyAction action, IBillObject observer);

	public enum NotifyAction {
		SAVE, DELETE;
	}
}
