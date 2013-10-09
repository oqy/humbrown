package com.minyisoft.webapp.core.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
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
import com.minyisoft.webapp.core.security.shiro.BasePrincipal;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public abstract class BaseServiceImpl<T extends IModelObject,C extends BaseCriteria, D extends IBaseDao<T, C>> implements IBaseService <T,C>{
	protected final Logger logger=LoggerFactory.getLogger(getClass());
	/**
	 * DAO接口
	 */
	private @Getter D baseDao;
	/**
	 * 校验器实例
	 */
	protected @Getter Validator validator;
	/**
	 * 当前服务类对应的Model对象类型
	 */
	private Class<T> modelClass;
	/**
	 * 根据当前业务操作实例（以***Impl形式命名）获取model对象对应的对象别名
	 */
	private String MODEL_CLASS_ALIAS;
	
	@SuppressWarnings("unchecked")
	public BaseServiceImpl(){
		modelClass=(Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		MODEL_CLASS_ALIAS=StringUtils.removeEndIgnoreCase(modelClass.getSimpleName(),"info");
		Class<?>[] interfaces=getClass().getInterfaces();
		if(ArrayUtils.isNotEmpty(interfaces)){
			for(Class<?> i:interfaces){
				if(IBaseService.class.isAssignableFrom(i)){
					IBaseService.MODEL_SERVICE_CACHE.put(modelClass, (Class<IBaseService<?,?>>)i);
					break;
				}
			}
		}
		ObjectUuidUtils.registerModelClass(modelClass);
	}
	
	@Autowired
	public void setDao(D dao,Validator validator){
		this.baseDao=dao;
		this.validator=validator;
	}
	
	@Override
	public void addNew(T info) {
		validateData(info,BaseOprtEnum.ADD);
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_CREATE);
		
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
				((ISeqCodeObject)info).genSeqCode();
			}
		}
		baseDao.insertEntity(info);
	}

	@Override
	public void delete(T info) {
		validateData(info, BaseOprtEnum.DELETE);
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_DELETE);
		if(baseDao.batchDelete(Arrays.asList(info.getId()))<=0){
			throw new ServiceException("无法删除业务对象，请稍后再试");
		}
	}

	@Override
	public void save(T info) {
		validateData(info,BaseOprtEnum.UPDATE);
		checkAuthentication(MODEL_CLASS_ALIAS,PermissionUtils.PERMISSION_UPDATE);
		
		if(info instanceof BaseInfo){
			((BaseInfo)info).setLastUpdateDate(new Date());
			((BaseInfo)info).setLastUpdateUser(getCurrentUser());
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
				if((info=getValue(id))!=null){
					delete(info);
				}
			}
		}
	}

	@Override
	public void submit(T info) {
		Assert.notNull(info,"待操作业务对象不存在");
		
		if (!info.isIdPresented()
				|| getValue(info.getId()) == null) {
			if(!ObjectUuidUtils.isLegalId(info.getClass(), info.getId())){
				info.setId(null);
			}
			addNew(info);
		}else{
			save(info);
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
	 * 编辑信息时是否对对象数据进行检查
	 * @return
	 */
	protected boolean validateBeforeOprt(){
		return true;
	}
	
	private enum BaseOprtEnum{
		ADD,UPDATE,DELETE;
	}
	
	/**
	 * 对象数据检查方法，供子类覆盖实现，检查不通过直接抛出异常
	 */
	private void validateData(T info,BaseOprtEnum oprtType){
		if(!validateBeforeOprt()){
			return;
		}
		
		if(oprtType==BaseOprtEnum.DELETE){
			Assert.isTrue(info!=null&&info.isIdPresented(), "待删除业务对象不能为空");
			_validateDataBeforeDelete(info);
		}else{
			if(oprtType==BaseOprtEnum.ADD){
				Assert.notNull(info,"待新增业务对象不存在");		
				_validateDataBeforeAdd(info);
			}else{
				Assert.isTrue(info!=null&&info.isIdPresented(), "待更新业务对象不能为空");
				_validateDataBeforeSave(info);
			}
			_validateDataBeforeSubmit(info);
			
			if (validator == null
					|| info.getClass().getName().indexOf(ClassUtils.CGLIB_CLASS_SEPARATOR) >= 0) {
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
	}
	
	/**
	 * 删除检查方法，供子类继承实现
	 * @param info
	 */
	protected void _validateDataBeforeDelete(T info) {
		
	}
	
	/**
	 * 新增检查方法，供子类继承实现
	 * @param info
	 */
	protected void _validateDataBeforeAdd(T info) {
			
	}
	
	/**
	 * 更新检查方法，供子类继承实现
	 * @param info
	 */
	protected void _validateDataBeforeSave(T info) {
		
	}
	
	/**
	 * 提交检查方法，供子类继承实现
	 * @param info
	 */
	protected void _validateDataBeforeSubmit(T info) {
		
	}
	
	protected ISystemUserObject getCurrentUser(){
		try{
			return ((BasePrincipal)org.apache.shiro.SecurityUtils.getSubject().getPrincipal()).getSystemUser();
		}catch(Exception e){
			return null;
		}
	}
}
