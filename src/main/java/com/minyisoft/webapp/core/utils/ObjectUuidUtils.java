package com.minyisoft.webapp.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.exception.CoreExceptionType;
import com.minyisoft.webapp.core.exception.EntityException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;

/**
 * ModelObject帮助类
 * 
 * @author qingyong_ou
 */
public final class ObjectUuidUtils {
	// 缓存model简码与类的对应关系
	private static final BiMap<Long, Class<? extends IModelObject>> keyClassMap = HashBiMap.create();

	private ObjectUuidUtils() {

	}

	/**
	 * 获取指定modelClass的@ModelKey值
	 * 
	 * @param modelClass
	 * @return
	 */
	private static long _getModelKey(Class<? extends IModelObject> modelClass) {
		Assert.notNull(modelClass);
		Class<?> userClass = ClassUtils.getUserClass(modelClass);
		ModelKey key = userClass.getAnnotation(ModelKey.class);
		Assert.notNull(key, userClass.getName() + "没有实现ModelKey注解");
		return key.value();
	}

	/**
	 * 注册ModelClass
	 * 
	 * @param modelClass
	 *            类需标注@ModelKey注解
	 */
	@SuppressWarnings("unchecked")
	public static void registerModelClass(Class<? extends IModelObject> modelClass) {
		long classKey = _getModelKey(modelClass);
		Class<? extends IModelObject> userClass = (Class<? extends IModelObject>) ClassUtils.getUserClass(modelClass);
		Assert.isTrue(classKey > 0 && !keyClassMap.containsKey(classKey), "[" + userClass.getName()
				+ "]ModelKey对应Long值需大于0且不能与已注册的键值相同");
		keyClassMap.put(classKey, userClass);
	}

	public static String createObjectID(Class<? extends IModelObject> clazz) {
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(keyClassMap.inverse().get(ClassUtils.getUserClass(clazz)));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new EntityException(CoreExceptionType.ENTITY_ID_GENERATE_ERROR, new Object[] { ClassUtils
					.getUserClass(clazz).getName() });
		}
		return EncodeUtils.encodeUrlSafeBase64(baos.toByteArray());
	}

	public static Class<? extends IModelObject> getObejctClass(String id) {
		try {
			byte[] array = EncodeUtils.decodeBase64(id);
			DataInput in = new DataInputStream(new ByteArrayInputStream(array));
			return keyClassMap.get(in.readLong());
		} catch (Exception ioe) {
			return null;
		}
	}

	/**
	 * 根据id获取对象，相关属性后加载处理
	 * 
	 * @param id
	 * @return
	 */
	public static IModelObject getObject(String id) {
		Class<? extends IModelObject> clazz;
		if (!StringUtils.isBlank(id) && (clazz = getObejctClass(id)) != null) {
			// 目标对象为枚举类型
			if (clazz.isEnum()) {
				for (IModelObject e : clazz.getEnumConstants()) {
					if (e.getId().equals(id)) {
						return e;
					}
				}
				return null;
			}
			// 目标对象为CoreBaseInfo对象类型
			else {
				try {
					IModelObject info = (IModelObject) clazz.newInstance();
					info.setId(id);

					Enhancer enhancer = new Enhancer();
					enhancer.setSuperclass(info.getClass());
					enhancer.setCallback(new ModelLazyLoadMethodInterceptor(info));
					return (IModelObject) enhancer.create();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	public static String getClassShortKey(Class<? extends IModelObject> clazz) {
		return Long.toHexString(_getModelKey(clazz)).toUpperCase();
	}

	public static Class<? extends IModelObject> getClassByObjectKey(String key) {
		Assert.hasLength(key, "待查询索引键值不能为空");
		try {
			return keyClassMap.get(Long.parseLong(key, 16));
		} catch (Exception e) {
			throw new EntityException("不存在指定key值对应的ModelClass");
		}
	}

	/**
	 * 判断指定id是否为指定业务对象合法id
	 * 
	 * @param modelClazz
	 * @param id
	 * @return
	 */
	public static boolean isLegalId(Class<? extends IModelObject> modelClazz, String id) {
		return modelClazz != null && id != null && Base64.isBase64(id) && (id.length() == 32 || id.length() == 24)
				&& (ClassUtils.getUserClass(modelClazz) == getObejctClass(id));
	}

	/**
	 * 判断指定id是否合法
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isLegalId(String id) {
		return id != null && Base64.isBase64(id) && (id.length() == 32 || id.length() == 24)
				&& keyClassMap.containsValue(getObejctClass(id));
	}

	/**
	 * 获取指定父类对象/接口的所有已注册ModelObject
	 * 
	 * @param superclass
	 * @return
	 */
	public static Set<Class<? extends IModelObject>> getSubclasses(Class<?> superclass) {
		Assert.notNull(superclass);
		Set<Class<? extends IModelObject>> classSet = Sets.newHashSet();
		for (Class<? extends IModelObject> mapClass : keyClassMap.values()) {
			if (superclass.isAssignableFrom(mapClass)) {
				classSet.add(mapClass);
			}
		}
		return ImmutableSet.copyOf(classSet);
	}

	/**
	 * 获取指定类型标识
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getClassLabel(Class<? extends IModelObject> clazz) {
		Assert.notNull(clazz);
		return (String) AnnotationUtils.getValue(clazz.getAnnotation(Label.class), "value");
	}
}
