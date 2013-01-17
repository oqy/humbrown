package com.tex100.webapp.core.security.utils;

import com.tex100.webapp.core.model.AbstractUserInfo;

/**
 * @author qingyong_ou 安全工具类
 */
public final class Securities {
	private Securities() {

	}
	
	public static final String PERMISSION_CREATE="create";
	
	public static final String PERMISSION_READ="read";
	
	public static final String PERMISSION_UPDATE="update";
	
	public static final String PERMISSION_DELETE="delete";

	/**
	 * 获取当前系统登录用户信息
	 * 
	 * @return
	 */
	public static AbstractUserInfo getCurrentUser() {
		try{
			return (AbstractUserInfo) org.apache.shiro.SecurityUtils.getSubject().getPrincipal();
		}catch(Exception e){
			return null;
		}
	}
}
