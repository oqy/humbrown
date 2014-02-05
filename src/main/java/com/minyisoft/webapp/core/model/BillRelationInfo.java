package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou
 * 表单关系信息
 */
@Getter
@Setter
public class BillRelationInfo extends CoreBaseInfo {
	// 源单/上游单
	private IBillObject sourceBill;
	// 目标单/下游单
	private IBillObject targetBill;
	
	/**
	 * 源单类简码
	 * @return
	 */
	public String getSourceBillClassKey(){
		if(sourceBill!=null){
			return ObjectUuidUtils.getClassShortKey(sourceBill.getClass());
		}else{
			return null;
		}
	}

	/**
	 * 下游单类简码
	 * @return
	 */
	public String getTargetBillClassKey(){
		if(targetBill!=null){
			return ObjectUuidUtils.getClassShortKey(targetBill.getClass());
		}else{
			return null;
		}
	}
}
