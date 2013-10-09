package com.minyisoft.webapp.core.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.model.assistant.ISeqCodeObject;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.service.IBillRelationProcessor;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;

@Getter 
@Setter
public abstract class BillBaseInfo extends BaseInfo implements ISeqCodeObject,IBillObject {
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
		return null;
	}
	
	public int getDigitLength() {
		return 4;
	}
	
	@Override
	public void genSeqCode() {
		setBillNumber(getSeqCodeGenStrategy().genSeqCode(this));
	}

	@Override
	public String getSeqCode() {
		return getBillNumber();
	}

	/**
	 * 去掉多余的空格与换行符
	 * @return
	 */
	public String getTrimDescription() {
		if(StringUtils.isNotBlank(getDescription())){
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(this.getDescription().trim());
			return m.replaceAll(" ");
		}else{
			return null;
		}
	}
	
	@Override
	public boolean shouldNotifyObservers(NotifyAction action,IBillObject observer) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IBillRelationProcessor<? extends IBillObject> getBillRelationProcessor() {
		IBaseService<?, ?> service=ServiceUtils.getService(getClass());
		if(service instanceof IBillRelationProcessor){
			return (IBillRelationProcessor<? extends IBillObject>)service;
		}
		return null;
	}
}
