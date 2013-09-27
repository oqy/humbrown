package com.minyisoft.webapp.core.security.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.minyisoft.webapp.core.model.PermissionInfo;

public final class PermissionUtils {
	private static Map<String, PermissionInfo> permissionMap = new ConcurrentHashMap<String, PermissionInfo>();

	private static ThreadLocal<Boolean> suspendPermissionCheck = new ThreadLocal<Boolean>();
	
	/**
	 * 新增权限键值
	 */
	public static final String PERMISSION_CREATE="create";
	
	/**
	 * 查看权限键值
	 */
	public static final String PERMISSION_READ="read";
	
	/**
	 * 更新权限键值
	 */
	public static final String PERMISSION_UPDATE="update";
	
	/**
	 * 删除权限键值
	 */
	public static final String PERMISSION_DELETE="delete";
	
	/**
	 * 默认角色【系统管理员】角色键值
	 */
	public static final String ADMINISTRATOR_ROLE="ADMINISTRATOR";
	
	private PermissionUtils(){
		
	}
	
	/**
	 * 注册权限
	 * @param permission
	 */
	public static void registerPermission(PermissionInfo permission){
		permissionMap.put(permission.getValue(), permission);
	}

	/**
	 * 检测系统是否定义了指定的权限字符串
	 * 
	 * @param permissionString
	 * @return
	 */
	public static boolean isPermissionDefined(String permissionString) {
		if (permissionMap.size() == 0) {
			return false;
		}
		return permissionMap.containsKey(permissionString);
	}

	/**
	 * 返回系统所有权限列表
	 * 
	 * @return
	 */
	public static List<PermissionInfo> getSystemPermissionList() {
		List<PermissionInfo> systemPermissionList = new ArrayList<PermissionInfo>();
		if (permissionMap.size() >0) {
			Iterator<String> iterator = permissionMap.keySet().iterator();
			while (iterator.hasNext()) {
				systemPermissionList.add(permissionMap.get(iterator.next()));
			}
		}
		return systemPermissionList;
	}

	/**
	 * 判断当前用户是否包含指定权限
	 * 
	 * @param permissionString
	 */
	public static void checkHasPermission(String permissionString) {
		if (!hasPermission(permissionString)) {
			throw new com.minyisoft.webapp.core.exception.SecurityException(
					com.minyisoft.webapp.core.exception.SecurityException.SECURITY_NOT_HAS_PERMISSION);
		}
	}
	
	/**
	 * 检查当前用户是否包含指定权限（系统管理用户默认拥有所有权限）
	 * @param permissionString
	 * @return
	 */
	public static boolean hasPermission(String permissionString) {
		// 若当前线程暂停权限检查，直接返回true
		if(suspendPermissionCheck.get()!=null&&suspendPermissionCheck.get()){
			return true;
		}
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser != null && currentUser.isAuthenticated()
				&& (currentUser.isPermitted(permissionString)||currentUser.hasRole(ADMINISTRATOR_ROLE));
	}

	/**
	 * 判断当前用户是否包含指定用户角色
	 * 
	 * @param permissionString
	 */
	public static void checkHasRole(String RoleKey) {
		if (!hasRole(RoleKey)) {
			throw new com.minyisoft.webapp.core.exception.SecurityException(
					com.minyisoft.webapp.core.exception.SecurityException.SECURITY_NOT_HAS_ROLE);
		}
	}

	/**
	 * 检测当前用户是否包含指定用户角色
	 * 
	 * @param permissionString
	 */
	public static boolean hasRole(String RoleKey) {
		// 若当前线程暂停权限检查，直接返回true
		if(suspendPermissionCheck.get()!=null&&suspendPermissionCheck.get()){
			return true;
		}
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser != null && currentUser.isAuthenticated()
				&& currentUser.hasRole(RoleKey);
	}
	
	/**
	 * 在当前线程暂停权限检查
	 */
	public static void stopPermissionCheck(){
		if(suspendPermissionCheck.get()==null){
			suspendPermissionCheck.set(true);
		}
	}
	
	/**
	 * 在当前线程启动权限检查
	 */
	public static void startPermissionCheck(){
		if(suspendPermissionCheck.get()!=null){
			suspendPermissionCheck.remove();
		}
	}
}
