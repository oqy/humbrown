package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 用户角色
 */
@Getter 
@Setter
public abstract class AbstractRoleInfo extends DataBaseInfo {
	private static final long serialVersionUID = -2974386838068962547L;
	// 角色标记值
	private String value;
}
