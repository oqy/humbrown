package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;

/**
 * @author qingyong_ou
 * 用户基类
 */
@Getter
@Setter
public abstract class AbstractUserInfo extends DataBaseInfo {
	private static final long serialVersionUID = -4677717777416812185L;
	@NotBlank @Label("用户登录账号")
	private String userLoginName;
	@NotBlank @Label("用户登录密码")
	private String userPassword;
	// 用户登录密码附加字符串
	private String userPasswordSalt;
}
