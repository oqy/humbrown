package com.minyisoft.webapp.core.service.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import lombok.Getter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.BaseInfo;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.model.assistant.ISeqCodeObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.persistence.IBaseDao;
import com.minyisoft.webapp.core.persistence.ICacheableDao;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public abstract class BaseServiceImpl<T extends IModelObject,C extends BaseCriteria, D extends IBaseDao<T, C>> implements IBaseService <T,C>{
	protected final Logger logger=LoggerFactory.getLogger(getClass());
	private @Getter D baseDao;
	@Autowired
	protected @Getter Validator validator;
	@Autowired
	protected @Getter CacheManager cacheManager;
	
	/**
	 * 根据当前业务操作实例（以***Impl形式命名）获取model对象对应的对象别名
	 */
	protected final String MODEL_CLASS_ALIAS=StringUtils.stripEnd(this.getClass().getSimpleName(), "Impl");
	
	@Autowired
	public void setDao(D dao){
		this.baseDao=dao;
	}
	
	@Override
	public void addNew(T info) {
		if(info==null){
			throw new ServiceException("新增业务对象不能为空");
		}
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_CREATE);
		
		if(validateBeforeSubmit()){
			validateData(info);
		}
		
		if(StringUtils.isBlank(info.getId())){
			info.setId(ObjectUuidUtils.createObjectID(info.getClass()));
		}
		if(info instanceof BaseInfo){
			if(((BaseInfo)info).getCreateDate()==null){
				((BaseInfo)info).setCreateDate(new Date());
			}
			if(((BaseInfo)info).getCreateUser()==null){
				((BaseInfo)info).setCreateUser(getCurrentUser());
			}
			if(((BaseInfo)info).getLastUpdateDate()==null){
				((BaseInfo)info).setLastUpdateDate(((BaseInfo)info).getCreateDate());
			}
			if(((BaseInfo)info).getLastUpdateUser()==null){
				((BaseInfo)info).setLastUpdateUser(((BaseInfo)info).getCreateUser());
			}
		}
		if(info instanceof ISeqCodeObject){
			if(((ISeqCodeObject)info).isAutoSeqEnabled()&&StringUtils.isBlank(((ISeqCodeObject)info).getSeqCode())){
				((ISeqCodeObject)info).setSeqCode(((ISeqCodeObject)info).getSeqCodeGenStrategy().genSeqCode((ISeqCodeObject)info));
			}
		}
		baseDao.insertEntity(info);
	}

	@Override
	public void delete(T info) {
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_DELETE);
		validateDataBeforeDelete(info);
		if(baseDao.batchDelete(Arrays.asList(info.getId()))<=0){
			throw new ServiceException("无法删除业务对象，请稍后再试");
		}
	}

	@Override
	public void save(T info) {
		if(info==null||!info.isIdPresented()){
			throw new ServiceException("待更新业务对象不能为空");
		}
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_UPDATE);
		
		if(validateBeforeSubmit()){
			validateData(info);
		}
		
		if(info instanceof BaseInfo){
			((BaseInfo)info).setLastUpdateDate(new Date());
			((BaseInfo)info).setLastUpdateUser(getCurrentUser());
		}
		
		// 暂时手工清理缓存
		if(cacheManager!=null&&baseDao instanceof ICacheableDao<?, ?>){
			cacheManager.getCache("Model:"+ObjectUuidUtils.getClassShortKey(info.getClass())).evict(info.getId());
		}
		if(baseDao.updateEntity(info)<=0){
			throw new ServiceException("无法更新业务对象，请稍后再试");
		}
		// 累计当前版本号
		if(info instanceof CoreBaseInfo){
			((CoreBaseInfo)info).setVersion(((CoreBaseInfo)info).getVersion()+1);
		}
	}

	@Override
	public T getValue(String id) {
		if(StringUtils.isBlank(id)){
			return null;
		}
		return baseDao.getEntity(id);
	}

	@Override
	public void batchDelete(String[] ids) {
		if(!ArrayUtils.isEmpty(ids)){
			T info=null;
			for(String id:ids){
				info=getValue(id);
				if(info!=null){
					delete(getValue(id));
				}
			}
		}
	}

	@Override
	public void submit(T info) {
		if(info==null){
			throw new ServiceException("待操作业务对象不存在");
		}
		if(StringUtils.isBlank(info.getId())){
			addNew(info);
		}else{
			if(getValue(info.getId())==null){
				if(!ObjectUuidUtils.isLegalId(info.getClass(), info.getId())){
					info.setId(null);
				}
				addNew(info);
			}else{
				save(info);
			}
		}
	}

	@Override
	public T find(C criteria) {
		List<T> list=getCollection(criteria);
		if(list==null||list.size()==0){
			return null;
		}else{
			return list.get(0);
		}
	}

	@Override
	public List<T> getCollection() {
		return getCollection(null);
	}

	@Override
	public List<T> getCollection(C criteria) {
		return baseDao.getEntityCollection(criteria);
	}

	@Override
	public int countAll() {
		return count(null);
	}

	@Override
	public int count(C criteria) {
		return baseDao.countEntity(criteria);
	}
	
	/**
	 * 用户授权检查
	 * @param pojoAlias
	 * @param action
	 */
	private void checkAuthentication(String pojoAlias,String action){
		if(ArrayUtils.contains(ignoreAuthenticateActionList(), action))
			return;
		
		String permissionString = pojoAlias + ":" + action;
		if (PermissionUtils.isPermissionDefined(permissionString)) {
			PermissionUtils.checkHasPermission(permissionString);
		}
	}
	
	/**
	 * 设置指定的CRUD类操作不作权限检测，子类可根据具体业务进行覆盖
	 * @return
	 */
	protected String[] ignoreAuthenticateActionList(){
		return null;
	}
	
	/**
	 * 提交信息时是否对对象数据进行检查
	 * @return
	 */
	protected boolean validateBeforeSubmit(){
		return true;
	}
	
	/**
	 * 对象数据检查方法，供子类覆盖实现，检查不通过直接抛出异常
	 */
	protected void validateData(T info) throws ServiceException{
		if(info.getClass().getName().indexOf(ClassUtils.CGLIB_CLASS_SEPARATOR)>=0){
			return;
		}
		
		Set<ConstraintViolation<T>> constraintViolations= validator.validate(info);
		if(!constraintViolations.isEmpty()){
			Iterator<ConstraintViolation<T>> violations=constraintViolations.iterator();
			StringBuffer sb=new StringBuffer();
			int count=0;
			while(violations.hasNext()){
				ConstraintViolation<T> violation=violations.next();
				try{
					Field field=info.getClass().getDeclaredField(violation.getPropertyPath().iterator().next().getName());
					if(field.isAnnotationPresent(Label.class)){
						sb.append(++count).append(".").append(AnnotationUtils.getValue(field.getAnnotation(Label.class), "value")).append(violation.getMessage()).append("\t");
					}
				}catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			throw new ServiceException(sb.toString());
		}
	}
	
	/**
	 * 提交信息时对对象数据进行检查
	 * @return
	 */
	protected void validateDataBeforeDelete(T info){
		if(info==null||!info.isIdPresented()){
			throw new ServiceException("待删除业务对象不能为空");
		}
	}
	
	protected ISystemUserObject getCurrentUser(){
		try{
			return (ISystemUserObject)ObjectUuidUtils.getObjectById((String)org.apache.shiro.SecurityUtils.getSubject().getPrincipal());
		}catch(Exception e){
			return null;
		}
	}
}
