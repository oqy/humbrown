package com.minyisoft.webapp.core.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.model.assistant.ISeqCodeGenStrategy;
import com.minyisoft.webapp.core.model.assistant.ISeqCodeObject;
import com.minyisoft.webapp.core.persistence.AbstractBillRelationDao;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.core.service.BillRelationProcessor;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.spring.SpringUtils;

@Getter
@Setter
public abstract class BillBaseInfo extends BaseInfo implements ISeqCodeObject, IBillObject {
	// 单据号码
	private String billNumber;
	// 备注描述
	private String description;
	// 源单
	private IBillObject sourceBill;

	public boolean isAutoSeqEnabled() {
		return false;
	}

	public int getAutoIncreaseStep() {
		return 1;
	}

	public int getInitValue() {
		return 1;
	}

	public String getSeqCodePrefix() {
		return "";
	}

	public int getDigitLength() {
		return 4;
	}

	@Override
	public void genSeqCode() {
		if (StringUtils.isBlank(billNumber) && isAutoSeqEnabled() && getSeqCodeGenStrategy() != null) {
			setBillNumber(getSeqCodeGenStrategy().genSeqCode(this));
		}
	}

	@Override
	public ISeqCodeGenStrategy getSeqCodeGenStrategy() {
		return null;
	}

	/**
	 * 去掉多余的空格与换行符
	 * 
	 * @return
	 */
	public String getTrimDescription() {
		if (StringUtils.isNotBlank(getDescription())) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(this.getDescription().trim());
			return m.replaceAll(" ");
		} else {
			return null;
		}
	}

	@Override
	public boolean shouldNotifyObservers(NotifyAction action, IBillObject observer) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BillRelationProcessor<? extends IBillObject> getBillRelationProcessor() {
		BaseService<?, ?> service = ServiceUtils.getService(getClass());
		if (service instanceof BillRelationProcessor) {
			return (BillRelationProcessor<? extends IBillObject>) service;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IBillObject> Optional<T> getChildBill(Class<T> childBillClass) {
		AbstractBillRelationDao billRelationDao = SpringUtils.getApplicationContext().getBean(
				AbstractBillRelationDao.class);
		if (billRelationDao != null) {
			List<BillRelationInfo> billRelations = billRelationDao.getRelations(this);
			for (BillRelationInfo relation : billRelations) {
				if (childBillClass.isAssignableFrom(relation.getTargetBill().getClass())) {
					return (Optional<T>) Optional.of(relation.getTargetBill());
				}
			}
		}
		return Optional.absent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IBillObject> Optional<List<T>> getChildBills(Class<T> childBillClass) {
		AbstractBillRelationDao billRelationDao = SpringUtils.getApplicationContext().getBean(
				AbstractBillRelationDao.class);
		if (billRelationDao != null) {
			List<BillRelationInfo> billRelations = billRelationDao.getRelations(this);
			List<T> childBills = Lists.newArrayList();
			for (BillRelationInfo relation : billRelations) {
				if (childBillClass.isAssignableFrom(relation.getTargetBill().getClass())) {
					childBills.add((T) relation.getTargetBill());
				}
			}
			if (!childBills.isEmpty()) {
				return Optional.of(childBills);
			}
		}
		return Optional.absent();
	}
}
