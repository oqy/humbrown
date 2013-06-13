package com.minyisoft.webapp.core.service.utils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.utils.SpringUtils;

/**
 * @author qingyong_ou 业务接口工具类
 */
public final class ServiceUtils {
	// 缓存IModelObject类对应IBaseService接口
	private static final ConcurrentMap<Class<? extends IModelObject>,IBaseService<IModelObject, BaseCriteria>> modelServiceCaches = new ConcurrentHashMap<Class<? extends IModelObject>,IBaseService<IModelObject, BaseCriteria>>();
	
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
		if(clazz==null){
			throw new ServiceException("你所请求的业务接口不存在");
		}
		IBaseService<IModelObject,BaseCriteria> bizInterface=modelServiceCaches.get(clazz);
		if(bizInterface==null){
			String className = ClassUtils.getUserClass(clazz).getSimpleName();
			if (StringUtils.endsWithIgnoreCase(className, "info")) {
				className = StringUtils.removeEndIgnoreCase(className, "Info");
			}
			bizInterface = (IBaseService<IModelObject, BaseCriteria>) SpringUtils.getBean(StringUtils.uncapitalize(className) + "Service");
			if (bizInterface == null) {
				throw new ServiceException("你所请求的业务接口不存在");
			}
			modelServiceCaches.put(clazz, bizInterface);
		}
		return bizInterface;
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
		return getService(ObjectUuidUtils.getObejctClass(id)).getValue(id);
	}

	/**
	 * 根据对象获取对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static IModelObject getModel(IModelObject object)
			throws Exception {
		if (object == null || !object.isIdPresented()) {
			return null;
		}
		return getService(object.getClass()).getValue(object.getId());
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
		Class<?> clazz = ObjectUuidUtils.getObjectById(ids[0]).getClass();
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
			throw new ServiceException(e);
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
		getService(object.getClass()).delete(object);
	}
}
