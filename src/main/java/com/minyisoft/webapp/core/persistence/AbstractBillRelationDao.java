package com.minyisoft.webapp.core.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.model.BillRelationInfo;
import com.minyisoft.webapp.core.model.IBillObject;

public abstract interface AbstractBillRelationDao {
	/**
	 * 根据目标单查找表单关系信息
	 * 
	 * @param targetBill
	 * @return
	 */
	BillRelationInfo getRelation(@Param("targetBill")IBillObject targetBill);

	/**
	 * 根据源单查找表单关系信息
	 * 
	 * @param sourceBill
	 * @return
	 */
	List<BillRelationInfo> getRelations(@Param("sourceBill")IBillObject sourceBill);
	
	/**
	 * 新增记录
	 * 
	 * @param relation
	 */
	void insertRelation(BillRelationInfo relation);

	/**
	 * 更新（保存）记录
	 * 
	 * @param info
	 * @return
	 */
	int updateRelation(BillRelationInfo relation);

	/**
	 * 根据id集合批量删除记录
	 * 
	 * @param ids
	 * @return
	 */
	int deleteRelation(BillRelationInfo relation);
}
