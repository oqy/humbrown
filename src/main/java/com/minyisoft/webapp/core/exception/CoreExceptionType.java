package com.minyisoft.webapp.core.exception;

/**
 * @author qingyong_ou
 * 核心包异常类型
 */
public enum CoreExceptionType implements ExceptionCode {
	ENTITY_NOT_EXIST("CE001"), // 不存在id为[{0}]对应的记录
	ENTITY_ID_GENERATE_ERROR("CE002"), // 无法为对象{0}生成ID信息
	NOT_HAS_ROLE("CE003"), // 抱歉，但当前您具备进行此操作的用户角色，请您联系客服，开通相关功能
	NOT_HAS_PERMISSION("CE004"), // 抱歉，但当前您没有进行此操作的权限，请您联系客服，开通相关功能
	USER_NOT_LOGIN("CE005"); // 您当前请求的操作需要登录后方能使用
	
	private String code;
	
	private CoreExceptionType(String code) {
		this.code = code;
	}

	@Override
	public String getExceptionCode() {
		return code;
	}
}
