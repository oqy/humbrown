package com.minyisoft.webapp.core.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.utils.ObjectUuids;

@Getter 
@Setter
public abstract class CoreBaseInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = -790025641619656774L;
	// 记录id，主键
	private String id;
	
	/**
	 * 获取类简码
	 * @return
	 */
	public String getClassLabel(){
		return ObjectUuids.getShortKeyByClassFullName(getClass());
	}

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
		if (StringUtils.isBlank(this.getId())) {
			return super.hashCode();
		}else{
			return this.getId().hashCode();
		}
	}
}
