package com.minyisoft.webapp.core.security.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public final class PermissionUtils {
	private static Logger logger = LoggerFactory.getLogger(PermissionUtils.class);

	private static Map<String, PermissionInfo> permissionMap = new HashMap<String, PermissionInfo>();

	private static final String permissionFilePattern="classpath*:com/fusung/webapp/**/permission/*.permission";
	
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
	
	static {
		loadPermissions();
	}
	
	private PermissionUtils(){
		
	}

	/**
	 * 从权限文件读入所有权限并存储到内存中，加快访问
	 */
	private static void loadPermissions() {
		permissionMap.clear();
		
		try {
			Resource[] permissionFiles=new PathMatchingResourcePatternResolver().getResources(permissionFilePattern);
			for(Resource rs : permissionFiles){
				BufferedReader br=new BufferedReader(new InputStreamReader(rs.getInputStream()));
				String line="";
				String[] permissionSet = null;
				String[] permissionPropertySet = null;
				while(StringUtils.isNotBlank(line=br.readLine())){
					if (line.startsWith("//")) {
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
							permissionInfo.setModuleCode(StringUtils.substringAfterLast(
									StringUtils.substringBefore(ObjectUuidUtils.getClassNameByObjectKey(permissionInfo.getGroupLabel()), ".model"), 
									"."));
							permissionMap.put(permissionInfo.getValue(),permissionInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
