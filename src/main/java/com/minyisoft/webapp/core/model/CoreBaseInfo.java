package com.minyisoft.webapp.core.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

@Getter 
@Setter
public abstract class CoreBaseInfo implements IModelObject,Serializable, Cloneable {
	private static final long serialVersionUID = -790025641619656774L;
	
	// 记录id，主键
	private String id;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CoreBaseInfo)) {
			return false;
		}
		if (StringUtils.isBlank(this.getId())) {
			return false;
		}
		CoreBaseInfo compareObj = (CoreBaseInfo) obj;
		return this.getId().equals(compareObj.getId());
	}

	@Override
	public Object clone() throws SerializationException {
		return SerializationUtils.clone(this);
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
