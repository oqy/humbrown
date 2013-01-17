package com.minyisoft.webapp.core.service.utils;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.service.IBaseService;
import com.minyisoft.webapp.core.utils.ObjectUuids;
import com.minyisoft.webapp.core.utils.Springs;

/**
 * @author qingyong_ou 业务接口工具类
 */
public final class Services {
	private Services() {

	}

	/**
	 * 根据id返回业务service，不存在对应的service则抛出异常
	 * 
	 * @param id
	 * @return
	 */
	public static IBaseService<CoreBaseInfo, BaseCriteria> getServiceById(
			String id) throws Exception {
		return getServiceByObject(ObjectUuids.getObjectById(id));
	}

	/**
	 * 根据CoreBaseInfo对象返回业务service，不存在对应的service则抛出异常
	 * 
	 * @param info
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static IBaseService<CoreBaseInfo, BaseCriteria> getServiceByObject(CoreBaseInfo info) throws Exception {
		String className = info.getClass().getSimpleName();
		if (StringUtils.endsWithIgnoreCase(className, "info")) {
			className = StringUtils.substring(className, 0,
					StringUtils.lastIndexOf(className, "Info"));
		}
		IBaseService<CoreBaseInfo, BaseCriteria> bizInterface = (IBaseService<CoreBaseInfo, BaseCriteria>) Springs.getBean(StringUtils.uncapitalize(className) + "Service");
		if (bizInterface == null) {
			throw new ServiceException("你所请求的业务接口不存在");
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
	public static CoreBaseInfo getModel(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return getServiceById(id).getValue(id);
	}

	/**
	 * 根据对象获取对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static CoreBaseInfo getModel(CoreBaseInfo object)
			throws Exception {
		if (object == null || StringUtils.isBlank(object.getId())) {
			return null;
		}
		return getServiceByObject(object).getValue(object.getId());
	}

	/**
	 * 根据id集合获取对象集合
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public static List<CoreBaseInfo> getModelCollection(String[] ids)
			throws Exception {
		if (ArrayUtils.isEmpty(ids)) {
			return null;
		}
		Class<?> clazz = ObjectUuids.getObjectById(ids[0]).getClass();
		String className = StringUtils.substring(clazz.getName(), 0, clazz.getName().lastIndexOf("."))+ ".criteria.";
		if (StringUtils.endsWithIgnoreCase(clazz.getSimpleName(), "info")) {
			className += StringUtils.substring(clazz.getSimpleName(), 0,
					StringUtils.lastIndexOfIgnoreCase(clazz.getSimpleName(),"Info"));
		}
		BaseCriteria criteria = (BaseCriteria) Class.forName(className + "Criteria").newInstance();
		criteria.setIds(ids);
		return getServiceById(ids[0]).getCollection(criteria);
	}

	/**
	 * 根据id删除对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static int deleteModel(String id) throws Exception {
		return getServiceById(id).delete(id);
	}
}
