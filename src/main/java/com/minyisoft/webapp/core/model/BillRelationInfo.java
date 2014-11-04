package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou 表单关系信息
 */
@Getter
@Setter
@ModelKey(0x14978EA1AE1L)
public class BillRelationInfo extends CoreBaseInfo {
	// 源单/上游单
	private IBillObject sourceBill;
	// 目标单/下游单
	private IBillObject targetBill;

	/**
	 * 源单类简码
	 * 
	 * @return
	 */
	public String getSourceBillClassKey() {
		return sourceBill != null ? ObjectUuidUtils.getClassShortKey(sourceBill.getClass()) : null;
	}

	/**
	 * 下游单类简码
	 * 
	 * @return
	 */
	public String getTargetBillClassKey() {
		return targetBill != null ? ObjectUuidUtils.getClassShortKey(targetBill.getClass()) : null;
	}
}
