package com.minyisoft.webapp.core.service;

import com.minyisoft.webapp.core.model.IBillObject;

/**
 * @author qingyong_ou
 * 表单对象业务处理接口基类
 * @param <T>
 */
public interface BillRelationProcessor<T extends IBillObject>{
	/**
	 * 源单删除后的业务处理
	 * @param sourceBill
	 */
	public void processAfterSourceBillDeleted(IBillObject sourceBill,T targetBill);
	
	/**
	 * 源单更新后的业务处理
	 * @param sourceBill
	 */
	public void processAfterSourceBillUpdated(IBillObject sourceBill,T targetBill);
	
	/**
	 * 目标单新增后的业务处理
	 * @param targetBill
	 */
	public void processAfterTargetBillAdded(T sourceBill,IBillObject targetBill);
	
	/**
	 * 目标单更新后的业务处理
	 * @param targetBill
	 */
	public void processAfterTargetBillUpdated(T sourceBill,IBillObject targetBill);
	
	/**
	 * 目标单删除后的业务处理
	 * @param targetBill
	 */
	public void processAfterTargetBillDeleted(T sourceBill,IBillObject targetBill);
}
