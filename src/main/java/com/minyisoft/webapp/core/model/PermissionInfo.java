package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.ModelKey;

/**
 * @author qingyong_ou
 * 权限信息
 */
@Getter
@Setter
@ModelKey(0x588A18A0)
public class PermissionInfo extends DataBaseInfo {
	// 权限值
	private String value;
	// 权限组编码
	private String groupLabel;
	// 模块代码
	private String moduleCode;
}
