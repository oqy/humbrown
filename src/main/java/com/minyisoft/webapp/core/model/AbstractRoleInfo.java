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
	// 角色标记值
	private String value;
}
