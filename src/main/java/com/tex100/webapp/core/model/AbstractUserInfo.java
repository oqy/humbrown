package com.tex100.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 用户基类
 */
@Getter
@Setter
public abstract class AbstractUserInfo extends DataBaseInfo {
	private static final long serialVersionUID = -4677717777416812185L;
	// 用户登录名
	private String userLoginName;
	// 用户登录密码
	private String userPassword;
	// 用户登录密码附加字符串
	private String userPasswordSalt;
}
