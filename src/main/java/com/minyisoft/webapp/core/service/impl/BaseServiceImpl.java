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
import org.springframework.cache.Cache.ValueWrapper;
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
import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.core.security.BasePermissionTypeEnum;
import com.minyisoft.webapp.core.security.shiro.BasePrincipal;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager;

public abstract class BaseServiceImpl<T extends IModelObject,C extends BaseCriteria, D extends BaseDao<T, C>> implements BaseService <T,C>{
	protected final Logger logger=LoggerFactory.getLogger(getClass());
	/**
	 * DAO接口
	 */
	private @Getter D baseDao;
	/**
	 * 校验器实例
	 */
	private @Getter Validator validator;
	/**
	 * 缓存管理器实例
	 */
	private @Getter ModelCacheManager cacheManager;
	/**
	 * 当前服务类对应的Model对象类型
	 */
	private Class<T> modelClass;
	/**
	 * 根据当前业务操作实例（以***Impl形式命名）获取model对象对应的对象别名
	 */
	private final String MODEL_CLASS_ALIAS;
	
	@SuppressWarnings("unchecked")
	public BaseServiceImpl(){
		modelClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		MODEL_CLASS_ALIAS=StringUtils.removeEndIgnoreCase(modelClass.getSimpleName(),"info");
		Class<?>[] interfaces = getClass().getInterfaces();
		if (ArrayUtils.isNotEmpty(interfaces)) {
			for (Class<?> i : interfaces) {
				if (BaseService.class.isAssignableFrom(i)) {
					BaseService.MODEL_SERVICE_CACHE.put(modelClass,
							(Class<BaseService<?, ?>>) i);
					break;
				}
			}
		}
		ObjectUuidUtils.registerModelClass(modelClass);
	}
	
	@Autowired
	public void setDao(D dao) {
		this.baseDao = dao;
	}
	
	@Autowired(required = false)
	public void setOptionalComponent(Validator validator,
			ModelCacheManager cacheManager) {
		this.validator = validator;
		this.cacheManager = cacheManager;
	}
	
	@Override
	public void addNew(T info) {
		_validateData(info, BasePermissionTypeEnum.CREATE);
		_checkAuthentication(BasePermissionTypeEnum.CREATE);

		if (StringUtils.isBlank(info.getId())) {
			info.setId(ObjectUuidUtils.createObjectID(info.getClass()));
		}
		if (info instanceof BaseInfo) {
			if (((BaseInfo) info).getCreateDate() == null) {
				((BaseInfo) info).setCreateDate(new Date());
			}
			if (((BaseInfo) info).getCreateUser() == null) {
				((BaseInfo) info).setCreateUser(getCurrentUser());
			}
			if (((BaseInfo) info).getLastUpdateDate() == null) {
				((BaseInfo) info).setLastUpdateDate(((BaseInfo) info).getCreateDate());
			}
			if (((BaseInfo) info).getLastUpdateUser() == null) {
				((BaseInfo) info).setLastUpdateUser(((BaseInfo) info).getCreateUser());
			}
		}
		if (info instanceof ISeqCodeObject
				&& ((ISeqCodeObject) info).isAutoSeqEnabled()
				&& StringUtils.isBlank(((ISeqCodeObject) info).getSeqCode())) {
			((ISeqCodeObject) info).genSeqCode();
		}
		baseDao.insertEntity(info);
		
		clearQueryCache();
	}

	@Override
	public void delete(T info) {
		_validateData(info, BasePermissionTypeEnum.DELETE);
		_checkAuthentication(BasePermissionTypeEnum.DELETE);
		if (baseDao.batchDelete(Arrays.asList(info.getId())) <= 0) {
			throw new ServiceException("无法删除业务对象，请稍后再试");
		}
		
		evictModelCache(info);
	}

	@Override
	public void save(T info) {
		_validateData(info, BasePermissionTypeEnum.UPDATE);
		_checkAuthentication(BasePermissionTypeEnum.UPDATE);

		if (info instanceof BaseInfo) {
			((BaseInfo) info).setLastUpdateDate(new Date());
			((BaseInfo) info).setLastUpdateUser(getCurrentUser());
		}
		if (baseDao.updateEntity(info) <= 0) {
			throw new ServiceException("无法更新业务对象，请稍后再试");
		}
		// 累计当前版本号
		if (info instanceof CoreBaseInfo) {
			((CoreBaseInfo) info).setVersion(((CoreBaseInfo) info).getVersion() + 1);
		}
		
		evictModelCache(info);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		if (_modelCacheEnabled()) {
			ValueWrapper wrapper = cacheManager.getModelCache(modelClass)
					.get(id);
			if (wrapper != null) {
				return (T) wrapper.get();
			}
		}
		T info = baseDao.getEntity(id);
		if (_modelCacheEnabled() && info != null) {
			cacheManager.getModelCache(modelClass).put(id, info);
		}
		return info;
	}

	@Override
	public void batchDelete(String[] ids) {
		if (!ArrayUtils.isEmpty(ids)) {
			T info = null;
			for (String id : ids) {
				if ((info = getValue(id)) != null) {
					delete(info);
				}
			}
		}
	}

	@Override
	public void submit(T info) {
		Assert.notNull(info,"待操作业务对象不存在");
		
		if (!info.isIdPresented()) {
			info.setId(null);
			addNew(info);
		}else if(getValue(info.getId()) == null){
			addNew(info);
		}else{
			save(info);
		}
	}

	@Override
	public T find(C criteria) {
		List<T> list = getCollection(criteria);
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<T> getCollection() {
		return getCollection(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getCollection(C criteria) {
		final Object cacheKey = criteria != null ? criteria : "all";

		if (_queryCacheEnabled()) {
			ValueWrapper wrapper = cacheManager.getModelQueryCache(modelClass).get(cacheKey);
			if (wrapper != null) {
				return (List<T>) wrapper.get();
			}
		}
		List<T> col = baseDao.getEntityCollection(criteria);
		if (_queryCacheEnabled()) {
			cacheManager.getModelQueryCache(modelClass).put(cacheKey, col);
		}
		return col;
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
	 * 单体对象缓存开关
	 * @return
	 */
	protected boolean useModelCache() {
		return false;
	}
	
	/**
	 * 查询缓存开关
	 * @return
	 */
	protected boolean useQueryCache() {
		return false;
	}
	
	private boolean _modelCacheEnabled() {
		return cacheManager != null && useModelCache();
	}
	
	private boolean _queryCacheEnabled() {
		return cacheManager != null && useQueryCache();
	}
	
	/**
	 * 清除指定对象缓存信息
	 * @param info
	 */
	protected void evictModelCache(T info){
		if (_modelCacheEnabled()) {
			cacheManager.getModelCache(modelClass).evict(info.getId());
		}
		clearQueryCache();
	}
	
	protected void clearModelCache(){
		if (_modelCacheEnabled()) {
			cacheManager.getModelCache(modelClass).clear();
		}
		clearQueryCache();
	}
	
	/**
	 * 清除全部查询缓存信息
	 */
	protected void clearQueryCache(){
		if (_queryCacheEnabled()) {
			cacheManager.getModelQueryCache(modelClass).clear();
		}
	}
	
	/**
	 * 用户授权检查
	 * @param pojoAlias
	 * @param action
	 */
	private void _checkAuthentication(BasePermissionTypeEnum oprtType){
		String permissionString = MODEL_CLASS_ALIAS + ":" + oprtType;
		if (PermissionUtils.isPermissionDefined(permissionString)) {
			PermissionUtils.checkHasPermission(permissionString);
		}
	}
	
	/**
	 * 对象数据检查方法，供子类覆盖实现，检查不通过直接抛出异常
	 */
	private void _validateData(T info, BasePermissionTypeEnum oprtType){
		if (oprtType == BasePermissionTypeEnum.DELETE) {
			Assert.isTrue(info != null && info.isIdPresented(), "待删除业务对象不能为空");
			_validateDataBeforeDelete(info);
		} else {
			if (oprtType == BasePermissionTypeEnum.CREATE) {
				Assert.notNull(info, "待新增业务对象不存在");
				_validateDataBeforeAdd(info);
			} else {
				Assert.isTrue(info != null && info.isIdPresented(), "待更新业务对象不能为空");
				_validateDataBeforeSave(info);
			}
			_validateDataBeforeSubmit(info);
			
			if (validator == null
					|| ClassUtils.isCglibProxyClass(info.getClass())) {
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
