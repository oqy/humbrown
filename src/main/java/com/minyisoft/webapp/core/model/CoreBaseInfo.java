package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

@Getter
@Setter
public abstract class CoreBaseInfo implements IModelObject {
	// 记录id，主键
	private String id;
	// 对象版本号，乐观锁应用
	@Setter
	private int version = 1;

	public void setId(String id) {
		if (ObjectUuidUtils.isLegalId(getClass(), id)) {
			this.id = id;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
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
		} else {
			return this.getId().hashCode();
		}
	}

	@Override
	public boolean isIdPresented() {
		return ObjectUuidUtils.isLegalId(getClass(), id);
	}

	public String getClassLabel() {
		return ObjectUuidUtils.getClassLabel(getClass());
	}
}
