package com.minyisoft.webapp.core.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseInfo extends CoreBaseInfo {
	// 创建人
	public ISystemUserObject createUser;
	// 创建日期
	public Date createDate;
	// 最后更新人
	public ISystemUserObject lastUpdateUser;
	// 最后更新日期
	public Date lastUpdateDate;
}
