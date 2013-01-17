package com.minyisoft.webapp.core.security.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.minyisoft.webapp.core.model.PermissionInfo;

public final class Permissions {
	private static Logger logger = LoggerFactory.getLogger(Permissions.class);

	private static Map<String, PermissionInfo> permissionMap = new HashMap<String, PermissionInfo>();

	private static final String permissionFilePattern="**/permission/*.permission";
	
	private static ThreadLocal<Boolean> suspendPermissionCheck = new ThreadLocal<Boolean>();
	
	static {
		loadPermissions();
	}
	
	private Permissions(){
		
	}

	/**
	 * 从权限文件读入所有权限并存储到内存中，加快访问
	 */
	private static void loadPermissions() {
		permissionMap.clear();
		
		try {
			Resource[] permissionFiles=new PathMatchingResourcePatternResolver().getResources(permissionFilePattern);
			for(Resource rs : permissionFiles){
				List<String> lines=FileUtils.readLines(rs.getFile());
				if(CollectionUtils.isNotEmpty(lines)){
					String[] permissionSet = null;
					String[] permissionPropertySet = null;
					for(String line:lines){
						if (StringUtils.isBlank(line)|| line.startsWith("//")) {
							continue;
						}
						permissionSet = StringUtils.split(line,'=');
						if (permissionSet != null&& permissionSet.length == 2) {
							permissionPropertySet = StringUtils.split(permissionSet[1], ',');
							if (permissionPropertySet != null&& permissionPropertySet.length == 3) {
								PermissionInfo permissionInfo = new PermissionInfo();
								permissionInfo.setId(permissionSet[0]);
								permissionInfo.setName(permissionPropertySet[1]);
								permissionInfo.setGroupLabel(permissionPropertySet[2]);
								permissionInfo.setValue(permissionPropertySet[0]);
								permissionInfo.setModuleCode(StringUtils.substringBetween(rs.getURL().toString(),"tex100/","/security"));
								permissionMap.put(permissionInfo.getValue(),permissionInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 检测系统是否定义了指定的权限字符串
	 * 
	 * @param permissionString
	 * @return
	 */
	public static boolean isPermissionDefined(String permissionString) {
		if (permissionMap.size() == 0) {
			loadPermissions();
		}
		return permissionMap.containsKey(permissionString);
	}

	/**
	 * 返回系统所有权限列表
	 * 
	 * @return
	 */
	public static List<PermissionInfo> getSystemPermissionList() {
		if (permissionMap.size() == 0) {
			loadPermissions();
		}
		List<PermissionInfo> systemPermissionList = new ArrayList<PermissionInfo>();
		Iterator<String> iterator = permissionMap.keySet().iterator();
		while (iterator.hasNext()) {
			systemPermissionList.add(permissionMap.get(iterator.next()));
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
				&& (currentUser.isPermitted(permissionString));
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
