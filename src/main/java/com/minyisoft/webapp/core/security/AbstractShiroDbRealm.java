package com.minyisoft.webapp.core.security;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.minyisoft.webapp.core.model.ISystemRoleObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou shiro登录对象
 */
public abstract class AbstractShiroDbRealm<U extends ISystemUserObject,R extends ISystemRoleObject> extends AuthorizingRealm {
	/**
	 * 认证回调函数,登录时调用.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		ISystemUserObject user = getUserByLoginName(token.getUsername());
		if (user != null) {
			if(StringUtils.isBlank(user.getUserPasswordSalt())){
				return new SimpleAuthenticationInfo(user.getId(), user.getUserPassword(),null, getName());
			}else{
				byte[] salt = EncodeUtils.decodeHex(user.getUserPasswordSalt());
				return new SimpleAuthenticationInfo(user.getId(), user.getUserPassword(),ByteSource.Util.bytes(salt), getName());
			}
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		U user = (U) ObjectUuidUtils.getObjectById((String)principals.getPrimaryPrincipal());
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		List<R> userRoles = getUserRoles(user);
		if (CollectionUtils.isNotEmpty(userRoles)) {
			for (R role : userRoles) {
				info.addRole(role.getValue());
			}
		}
		List<PermissionInfo> userPermissions = getUserPermissions(user);
		if (CollectionUtils.isNotEmpty(userPermissions)) {
			for (PermissionInfo permission : userPermissions) {
				info.addStringPermission(permission.getValue());
			}
		}
		return info;
	}

	/**
	 * 设定Password校验的Hash算法与迭代次数.
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(getHashAlgorithm());
		matcher.setHashIterations(getHashInterations()<=0?1024:getHashInterations());

		setCredentialsMatcher(matcher);
	}

	/**
	 * 获取哈希算法
	 * 
	 * @return
	 */
	public abstract String getHashAlgorithm();

	/**
	 * 获取哈希迭代次数
	 * 
	 * @return
	 */
	public abstract int getHashInterations();

	/**
	 * 根据登录名获取用户信息
	 * 
	 * @param userLoginName
	 * @return
	 */
	public abstract U getUserByLoginName(String userLoginName);

	/**
	 * 获取授予指定用户的角色列表
	 * 
	 * @param user
	 * @return
	 */
	public abstract List<R> getUserRoles(U user);

	/**
	 * 获取授予指定用户的权限列表
	 * 
	 * @param user
	 * @return
	 */
	public abstract List<PermissionInfo> getUserPermissions(U user);
}
