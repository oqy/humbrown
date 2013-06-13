package com.minyisoft.webapp.core.service.impl;

import java.util.List;

import lombok.Getter;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.minyisoft.webapp.core.model.BillRelationInfo;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.core.model.IBillObject.NotifyAction;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.persistence.IAbstractBillRelationDao;
import com.minyisoft.webapp.core.persistence.IBaseDao;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.service.IBillRelationProcessor;

public abstract class BillBaseImpl<T extends IBillObject, C extends BaseCriteria, D extends IBaseDao<T,C>> extends BaseServiceImpl<T,C,D> implements IBaseService<T, C>,IBillRelationProcessor<T> {
	@Autowired
	private @Getter IAbstractBillRelationDao billReationDao;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addNew(T info) {
		super.addNew(info);
		
		if(info.getSourceBill()!=null){
			BillRelationInfo relation=new BillRelationInfo();
			relation.setSourceBill(info.getSourceBill());
			relation.setTargetBill(info);
			billReationDao.insertRelation(relation);
			
			// 源单执行相应操作
			IBillRelationProcessor processor=info.getSourceBill().getBillRelationProcessor();
			if(processor!=null){
				processor.processAfterTargetBillAdded(info.getSourceBill(),info);
			}
		}
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void delete(T info) {
		super.delete(info);
		
		// 若指定bill存在源单
		if(info.getSourceBill()!=null){
			// 擦除表单关联关系
			BillRelationInfo relation=billReationDao.getRelation(info);
			if(relation!=null){
				billReationDao.deleteRelation(relation);
				
				// 源单执行相应操作
				if(info.shouldNotifyObservers(NotifyAction.DELETE, info.getSourceBill())){
					IBillRelationProcessor processor=info.getSourceBill().getBillRelationProcessor();
					if(processor!=null){
						processor.processAfterTargetBillDeleted(info.getSourceBill(),info);
					}
				}
			}
		}
		// 若指定bill存在下游单
		List<BillRelationInfo> relations=billReationDao.getRelations(info);
		if(CollectionUtils.isNotEmpty(relations)){
			for(BillRelationInfo relation : relations){
				// 擦除表单关联关系
				billReationDao.deleteRelation(relation);
				// 下游单执行相应操作
				if(info.shouldNotifyObservers(NotifyAction.DELETE, relation.getTargetBill())){
					IBillRelationProcessor processor=relation.getTargetBill().getBillRelationProcessor();
					if(processor!=null){
						processor.processAfterSourceBillDeleted(info,relation.getTargetBill());
					}
				}
			}
		}
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void save(T info) {
		super.save(info);
		
		// 若指定bill存在源单
		if(info.getSourceBill()!=null){
			// 源单执行相应操作
			if(info.shouldNotifyObservers(NotifyAction.SAVE, info.getSourceBill())){
				IBillRelationProcessor processor=info.getSourceBill().getBillRelationProcessor();
				if(processor!=null){
					processor.processAfterTargetBillUpdated(info.getSourceBill(),info);
				}
			}
		}
		BillRelationInfo relation=billReationDao.getRelation(info);
		// 若未设定表单关联关系，新增相应信息
		if(relation==null&&info.getSourceBill()!=null){
			relation=new BillRelationInfo();
			relation.setSourceBill(info.getSourceBill());
			relation.setTargetBill(info);
			billReationDao.insertRelation(relation);
		}
		// 若已设定表单关联关系，但bill取消了上游关联，擦除表单关联关系
		else if(relation!=null&&info.getSourceBill()==null){
			billReationDao.deleteRelation(relation);
		}
		// 若已设定表单关联关系，但bill更改了上游关联，更新表单关联关系
		else if(relation!=null&&!relation.getSourceBill().equals(info.getSourceBill())){
			relation.setSourceBill(info.getSourceBill());
			billReationDao.updateRelation(relation);
		}
				
		// 若指定bill存在下游单
		List<BillRelationInfo> relations=billReationDao.getRelations(info);
		if(CollectionUtils.isNotEmpty(relations)){
			for(BillRelationInfo r : relations){
				// 下游单执行相应操作
				if(info.shouldNotifyObservers(NotifyAction.SAVE, r.getTargetBill())){
					IBillRelationProcessor processor=r.getTargetBill().getBillRelationProcessor();
					if(processor!=null){
						processor.processAfterSourceBillUpdated(info,r.getTargetBill());
					}
				}
			}
		}
	};

	@Override
	public void processAfterSourceBillDeleted(IBillObject sourceBill,T targetBill) {
	}

	@Override
	public void processAfterSourceBillUpdated(IBillObject sourceBill,T targetBill) {
	}

	@Override
	public void processAfterTargetBillAdded(T sourceBill, IBillObject targetBill) {
	}

	@Override
	public void processAfterTargetBillUpdated(T sourceBill,IBillObject targetBill) {
	}

	@Override
	public void processAfterTargetBillDeleted(T sourceBill,IBillObject targetBill) {
	}
}
