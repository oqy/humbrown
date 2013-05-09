package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

@Getter 
@Setter
public abstract class CoreBaseInfo implements IModelObject{
	// 记录id，主键
	private String id;

	@Override
	public boolean equals(Object obj) {
		if(this==obj){
			return true;
		}
		if (!(obj instanceof CoreBaseInfo)) {
			return false;
		}
		if (StringUtils.isBlank(this.getId())) {
			return false;
		}
		CoreBaseInfo compareObj = (CoreBaseInfo) obj;
		return this.getId().equals(compareObj.getId());
	}
	
	@Override
	public int hashCode() {
		if (!isIdPresented()) {
			return super.hashCode();
		}else{
			return this.getId().hashCode();
		}
	}
	
	@Override
	public boolean isIdPresented() {
		return StringUtils.isNotBlank(getId());
	}
}
