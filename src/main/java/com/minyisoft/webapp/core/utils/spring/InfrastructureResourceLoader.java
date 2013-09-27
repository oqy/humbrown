package com.minyisoft.webapp.core.utils.spring;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import lombok.Setter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.model.enumField.CoreEnumHelper;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou
 * 加载CoreEunm枚举描述文件、Permission权限描述文件，注册IModelObject接口实现类，主要于Spring配置文件注册后使用
 * IBaseService接口的实现类初始化时将会同步注册其对应的IModelObject接口实现类，因此无需通过此loader注册
 */
public class InfrastructureResourceLoader implements InitializingBean {
	/**
	 * 权限描述文件
	 */
	private @Setter Resource[] permissionDefinitions;
	/**
	 * CoreEunm枚举描述文件
	 */
	private @Setter String[] coreEnumDescriptionBaseNames;
	/**
	 * IModelObject接口实现类
	 */
	private @Setter Class<? extends IModelObject>[] modelObjectClasses;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// 读取权限定义文件（需符合指定格式），注册系统权限
		if(ArrayUtils.isNotEmpty(permissionDefinitions)){
			String line="";
			String[] permissionSet = null;
			String[] permissionPropertySet = null;
			PermissionInfo permission;
			for(Resource rs : permissionDefinitions){
				BufferedReader br=new BufferedReader(new InputStreamReader(rs.getInputStream(),"UTF-8"));
				while(StringUtils.isNotBlank(line=br.readLine())){
					if (line.startsWith("//")) {
						continue;
					}
					permissionSet = StringUtils.split(line,'=');
					if (permissionSet != null && permissionSet.length == 2) {
						permissionPropertySet = StringUtils.split(permissionSet[1], ',');
						if (permissionPropertySet != null
								&& permissionPropertySet.length == 3) {
							permission = new PermissionInfo();
							permission.setId(permissionSet[0]);
							permission.setName(permissionPropertySet[1]);
							permission.setGroupLabel(permissionPropertySet[2]);
							permission.setValue(permissionPropertySet[0]);
							permission.setModuleCode(StringUtils.substringAfterLast(
									StringUtils.substringBefore(ObjectUuidUtils.getClassByObjectKey(permission.getGroupLabel()).getName(), ".model"), 
									"."));
							PermissionUtils.registerPermission(permission);
						}
					}
				}
			}
		}
		
		// 加载CoreEunm枚举描述文件
		if(ArrayUtils.isNotEmpty(coreEnumDescriptionBaseNames)){
			CoreEnumHelper.setDescriptionBaseNames(coreEnumDescriptionBaseNames);
		}
		
		// 注册IModelObject接口实现类，以用于生成ModelClass实例id，及根据id获取ModelClass实例
		if(ArrayUtils.isNotEmpty(modelObjectClasses)){
			for(Class<? extends IModelObject> modelClass:modelObjectClasses){
				ObjectUuidUtils.registerModelClass(modelClass);
			}
		}
	}
}
