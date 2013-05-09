package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.assistant.ISeqCodeObject;

@Getter 
@Setter
public abstract class BillBaseInfo extends BaseInfo implements ISeqCodeObject {
	// 单据号码
	private String billNumber;
	// 备注描述
	private String description;
	
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
		return null;
	}
	
	public int getDigitLength() {
		return 4;
	}
	
	@Override
	public void setSeqCode(String seqCode) {
		setBillNumber(getSeqCodeGenStrategy().genSeqCode(this));
	}

	@Override
	public String getSeqCode() {
		return getBillNumber();
	}
}
