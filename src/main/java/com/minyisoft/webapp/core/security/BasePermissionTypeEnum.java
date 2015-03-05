package com.minyisoft.webapp.core.security;

/**
 * @author Administrator CRUD 基本操作权限类型枚举
 */
public enum BasePermissionTypeEnum {
	CREATE("create"), READ("read"), UPDATE("update"), DELETE("delete");

	private String oprt;

	private BasePermissionTypeEnum(String oprt) {
		this.oprt = oprt;
	}

	@Override
	public String toString() {
		return oprt;
	}
}
