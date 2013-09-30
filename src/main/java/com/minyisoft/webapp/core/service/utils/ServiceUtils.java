package com.minyisoft.webapp.core.service.utils;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.spring.SpringUtils;

/**
 * @author qingyong_ou 业务接口工具类
 */
public final class ServiceUtils {
	private ServiceUtils() {

	}

	/**
	 * 根据IModelObject对象返回业务service，不存在对应的service则抛出异常
	 * 
	 * @param info
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static IBaseService<IModelObject, BaseCriteria> getService(Class<? extends IModelObject> clazz){
		Assert.isTrue(clazz!=null&&CoreBaseInfo.class.isAssignableFrom(clazz),"无效的业务实体类型，无法获取对应业务接口");
		Class<? extends IBaseService<? extends IModelObject,? extends BaseCriteria>> serviceClass=IBaseService.MODEL_SERVICE_CACHE.get(ClassUtils.getUserClass(clazz));
		if(serviceClass==null){
			throw new ServiceException("你所请求的业务接口不存在");
		}
		return (IBaseService<IModelObject, BaseCriteria>)SpringUtils.getApplicationContext().getBean(serviceClass);
	}

	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static IModelObject getModel(String id){
		if (StringUtils.isBlank(id)) {
			return null;
		}
		Class<? extends IModelObject> clazz=ObjectUuidUtils.getObejctClass(id);
		if(clazz!=null){
			// 目标对象为枚举类型
			if(clazz.isEnum()){
				for(IModelObject e:clazz.getEnumConstants()){
					if(e.getId().equals(id)){
						return e;
					}
				}
				return null;
			}
			// 目标对象为CoreBaseInfo对象类型
			return getService(clazz).getValue(id);
		}
		return null;
	}

	/**
	 * 根据id集合获取对象集合
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public static List<? extends IModelObject> getModelCollection(String[] ids){
		if (ArrayUtils.isEmpty(ids)) {
			return null;
		}
		Class<?> clazz = ClassUtils.getUserClass(ObjectUuidUtils.getObjectById(ids[0]).getClass());
		
		String className = StringUtils.substring(clazz.getName(), 0, clazz.getName().lastIndexOf("."))+ ".criteria.";
		if (StringUtils.endsWithIgnoreCase(clazz.getSimpleName(), "info")) {
			className += StringUtils.substring(clazz.getSimpleName(), 0,
					StringUtils.lastIndexOfIgnoreCase(clazz.getSimpleName(),"Info"));
		}
		try{
			BaseCriteria criteria = (BaseCriteria) Class.forName(className + "Criteria").newInstance();
			criteria.setIds(ids);
			return getService(ObjectUuidUtils.getObejctClass(ids[0])).getCollection(criteria);
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}

	/**
	 * 根据id删除对象
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static <T extends IModelObject> void deleteModel(T object){
		if(!object.getClass().isEnum()){
			getService(object.getClass()).delete(object);
		}
	}
}
