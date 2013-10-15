package com.minyisoft.webapp.core.security.shiro;

import java.io.Serializable;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

/**
 * @author qingyong_ou
 * Shiro基础Principal
 */
public class BasePrincipal implements Serializable {
	private static final long serialVersionUID = 971522020039284173L;
	
	private String systemUserId;
	private @Getter String systemOrgId;
	
	public BasePrincipal(ISystemUserObject user){
		Assert.isTrue(user!=null&&user.isIdPresented(),"系统登录用户对象不能为空");
		systemUserId=user.getId();
	}
	
	public void setSystemOrg(ISystemOrgObject org){
		if(org==null||!org.isIdPresented()){
			systemOrgId=null;
		}else{
			systemOrgId=org.getId();
		}
	}
	
	public ISystemUserObject getSystemUser(){
		return (ISystemUserObject)ObjectUuidUtils.getObject(systemUserId);
	}
	
	@Override
	public String toString() {
		return StringUtils.isBlank(systemOrgId)?systemUserId:systemUserId+":"+systemOrgId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj){
			return true;
		}
		if(!(obj instanceof BasePrincipal)){
			return false;
		}
		BasePrincipal p=(BasePrincipal)obj;
		return StringUtils.equals(systemUserId, p.getSystemOrgId())&&StringUtils.equals(systemOrgId, p.systemOrgId);
	}
	
	@Override
	public int hashCode() {
		if (StringUtils.isBlank(systemOrgId)) {
			return systemUserId.hashCode();
		} else {
			return 31 * systemUserId.hashCode() + systemOrgId.hashCode();
		}
	}
}
