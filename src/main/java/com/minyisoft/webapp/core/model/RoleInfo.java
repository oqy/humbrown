package com.minyisoft.webapp.core.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 用户角色
 */
@Getter 
@Setter
public class RoleInfo extends DataBaseInfo {
	private static final long serialVersionUID = -2974386838068962547L;
	// 角色标记值
	private String value;
	// 权限列表
	private List<PermissionInfo> permissionList = new ArrayList<PermissionInfo>();
}
