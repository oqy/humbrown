package com.minyisoft.webapp.core.security.shiro;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemRoleObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;

/**
 * @author qingyong_ou shiro登录对象
 */
public abstract class AbstractShiroDbRealm<U extends ISystemUserObject, R extends ISystemRoleObject> extends
		AuthorizingRealm {
	/**
	 * 认证回调函数,登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		ISystemUserObject user = getUserByLoginName(token.getUsername());
		if (user != null) {
			if (StringUtils.isBlank(user.getUserPasswordSalt()) || !isCredentialsSaltEnabled()) {
				return new SimpleAuthenticationInfo(createPrincipal(user), user.getUserPassword(), getName());
			} else {
				return new SimpleAuthenticationInfo(createPrincipal(user), user.getUserPassword(),
						ByteSource.Util.bytes(user.getUserPasswordSalt()), getName());
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
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Object principal = principals.getPrimaryPrincipal();
		if (!(principal instanceof BasePrincipal)) {
			return null;
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// 获取用户角色
		U user = (U) ((BasePrincipal) principal).getSystemUser();
		ISystemOrgObject org = getSystemOrg((BasePrincipal) principals.getPrimaryPrincipal());
		List<R> userRoles = getUserRoles(user, org);
		boolean hasAdministratorRole = false;
		if (CollectionUtils.isNotEmpty(userRoles)) {
			for (R role : userRoles) {
				info.addRole(role.getValue());
				hasAdministratorRole = hasAdministratorRole
						|| PermissionUtils.ADMINISTRATOR_ROLE.equals(role.getValue());
			}
		}
		// 系统管理员默认拥有全部权限
		if (hasAdministratorRole) {
			info.addStringPermission("*");
		}
		// 获取用户权限
		else {
			List<PermissionInfo> userPermissions = getUserPermissions(user, org);
			if (CollectionUtils.isNotEmpty(userPermissions)) {
				for (PermissionInfo permission : userPermissions) {
					info.addStringPermission(permission.getValue());
				}
			}
		}
		return info;
	}

	@Override
	protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
		Object cacheKey = super.getAuthenticationCacheKey(principals);
		if (cacheKey instanceof BasePrincipal) {
			return ((BasePrincipal) cacheKey).toString();
		}
		return cacheKey;
	}

	/**
	 * 是否启用密码加盐检查
	 * 
	 * @return
	 */
	protected boolean isCredentialsSaltEnabled() {
		return true;
	}

	/**
	 * 设定Password校验的Hash算法与迭代次数.
	 */
	@PostConstruct
	protected void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(getHashAlgorithm());
		matcher.setHashIterations(getHashInterations() <= 0 ? 1024 : getHashInterations());

		setCredentialsMatcher(matcher);
	}

	/**
	 * 创建Principal，业务系统可根据实际需求返回继承BasePrincipal的Principal
	 * 
	 * @param user
	 * @return
	 */
	protected abstract BasePrincipal createPrincipal(ISystemUserObject user);

	/**
	 * 获取哈希算法
	 * 
	 * @return
	 */
	protected abstract String getHashAlgorithm();

	/**
	 * 获取哈希迭代次数
	 * 
	 * @return
	 */
	protected abstract int getHashInterations();

	/**
	 * 获取登录用户所在组织架构
	 * 
	 * @param basePrincipal
	 * @return
	 */
	protected abstract ISystemOrgObject getSystemOrg(BasePrincipal basePrincipal);

	/**
	 * 根据登录名获取用户信息
	 * 
	 * @param userLoginName
	 * @return
	 */
	protected abstract U getUserByLoginName(String userLoginName);

	/**
	 * 获取授予指定用户于指定组织结构的角色列表
	 * 
	 * @param user
	 * @param systemOrg
	 *            可为空
	 * @return
	 */
	protected abstract List<R> getUserRoles(U user, ISystemOrgObject systemOrg);

	/**
	 * 获取授予指定用户于指定组织结构的权限列表
	 * 
	 * @param user
	 * @param systemOrg
	 *            可为空
	 * @return
	 */
	protected abstract List<PermissionInfo> getUserPermissions(U user, ISystemOrgObject systemOrg);
}
