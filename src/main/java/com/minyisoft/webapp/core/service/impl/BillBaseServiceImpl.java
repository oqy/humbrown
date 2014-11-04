package com.minyisoft.webapp.core.service.impl;

import java.util.List;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.minyisoft.webapp.core.model.BillBaseInfo;
import com.minyisoft.webapp.core.model.BillRelationInfo;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.core.model.IBillObject.NotifyAction;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.persistence.AbstractBillRelationDao;
import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.core.service.BillRelationProcessor;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public abstract class BillBaseServiceImpl<T extends BillBaseInfo, C extends BaseCriteria, D extends BaseDao<T, C>>
		extends BaseServiceImpl<T, C, D> implements BillRelationProcessor<T> {
	@Autowired
	@Getter
	private AbstractBillRelationDao billRelationDao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addNew(T info) {
		super.addNew(info);

		if (info.getSourceBill() != null) {
			BillRelationInfo relation = new BillRelationInfo();
			relation.setId(ObjectUuidUtils.createObjectID(BillRelationInfo.class));
			relation.setSourceBill(info.getSourceBill());
			relation.setTargetBill(info);
			billRelationDao.insertRelation(relation);

			// 源单执行相应操作
			BillRelationProcessor processor = info.getSourceBill().getBillRelationProcessor();
			if (processor != null) {
				processor.processAfterTargetBillAdded(info.getSourceBill(), info);
			}
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void delete(T info) {
		super.delete(info);

		// 若指定bill存在源单
		if (info.getSourceBill() != null) {
			// 擦除表单关联关系
			BillRelationInfo relation = billRelationDao.getRelation(info);
			if (relation != null) {
				billRelationDao.deleteRelation(relation);

				// 源单执行相应操作
				if (info.shouldNotifyObservers(NotifyAction.DELETE, info.getSourceBill())) {
					BillRelationProcessor processor = info.getSourceBill().getBillRelationProcessor();
					if (processor != null) {
						processor.processAfterTargetBillDeleted(info.getSourceBill(), info);
					}
				}
			}
		}
		// 若指定bill存在下游单
		List<BillRelationInfo> relations = billRelationDao.getRelations(info);
		if (!CollectionUtils.isEmpty(relations)) {
			for (BillRelationInfo relation : relations) {
				// 擦除表单关联关系
				billRelationDao.deleteRelation(relation);
				// 下游单执行相应操作
				if (info.shouldNotifyObservers(NotifyAction.DELETE, relation.getTargetBill())) {
					BillRelationProcessor processor = relation.getTargetBill().getBillRelationProcessor();
					if (processor != null) {
						processor.processAfterSourceBillDeleted(info, relation.getTargetBill());
					}
				}
			}
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void save(T info) {
		super.save(info);

		// 若指定bill存在源单
		if (info.getSourceBill() != null) {
			// 源单执行相应操作
			if (info.shouldNotifyObservers(NotifyAction.SAVE, info.getSourceBill())) {
				BillRelationProcessor processor = info.getSourceBill().getBillRelationProcessor();
				if (processor != null) {
					processor.processAfterTargetBillUpdated(info.getSourceBill(), info);
				}
			}
		}
		BillRelationInfo relation = billRelationDao.getRelation(info);
		// 若未设定表单关联关系，新增相应信息
		if (relation == null && info.getSourceBill() != null) {
			relation = new BillRelationInfo();
			relation.setId(ObjectUuidUtils.createObjectID(BillRelationInfo.class));
			relation.setSourceBill(info.getSourceBill());
			relation.setTargetBill(info);
			billRelationDao.insertRelation(relation);
		}
		// 若已设定表单关联关系，但bill取消了上游关联，擦除表单关联关系
		else if (relation != null && info.getSourceBill() == null) {
			billRelationDao.deleteRelation(relation);
		}
		// 若已设定表单关联关系，但bill更改了上游关联，更新表单关联关系
		else if (relation != null && !relation.getSourceBill().equals(info.getSourceBill())) {
			relation.setSourceBill(info.getSourceBill());
			billRelationDao.updateRelation(relation);
		}

		// 若指定bill存在下游单
		List<BillRelationInfo> relations = billRelationDao.getRelations(info);
		if (!CollectionUtils.isEmpty(relations)) {
			for (BillRelationInfo r : relations) {
				// 下游单执行相应操作
				if (info.shouldNotifyObservers(NotifyAction.SAVE, r.getTargetBill())) {
					BillRelationProcessor processor = r.getTargetBill().getBillRelationProcessor();
					if (processor != null) {
						processor.processAfterSourceBillUpdated(info, r.getTargetBill());
					}
				}
			}
		}
	};

	@Override
	public void processAfterSourceBillDeleted(IBillObject sourceBill, T targetBill) {
	}

	@Override
	public void processAfterSourceBillUpdated(IBillObject sourceBill, T targetBill) {
	}

	@Override
	public void processAfterTargetBillAdded(T sourceBill, IBillObject targetBill) {
	}

	@Override
	public void processAfterTargetBillUpdated(T sourceBill, IBillObject targetBill) {
	}

	@Override
	public void processAfterTargetBillDeleted(T sourceBill, IBillObject targetBill) {
	}
}
