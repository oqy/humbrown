package com.minyisoft.webapp.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.exception.CoreExceptionType;
import com.minyisoft.webapp.core.exception.EntityException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;

/**
 * ModelObject帮助类
 * @author qingyong_ou
 */
public final class ObjectUuidUtils {
	// 缓存model简码对应的类
	private static final ConcurrentMap<Long, Class<? extends IModelObject>> keyClassMap = new ConcurrentHashMap<Long, Class<? extends IModelObject>>();
	// 缓存model类对应简码
	private static final ConcurrentMap<Class<? extends IModelObject>, Long> classKeyMap = new ConcurrentHashMap<Class<? extends IModelObject>, Long>();
	
	private ObjectUuidUtils(){
		
	}
	
	/**
	 * 注册ModelClass
	 * @param modelClass 类需标注@ModelKey注解
	 */
	@SuppressWarnings("unchecked")
	public static void registerModelClass(Class<? extends IModelObject> modelClass){
		Assert.notNull(modelClass,"待索引ModelClass不能为空");
		Class<? extends IModelObject> userClass=(Class<? extends IModelObject>)ClassUtils.getUserClass(modelClass);
		ModelKey key=userClass.getAnnotation(ModelKey.class);
		Assert.notNull(key,userClass.getName()+"没有实现ModelKey注解");
		
		// 统一转换为大写
		Long classKey=key.value();
		Assert.isTrue(classKey>0,"ModelKey对应Long值需大于0");
		if(!userClass.equals(keyClassMap.get(classKey))){
			Assert.isTrue(!keyClassMap.containsKey(classKey),userClass.getName()+"的ModelKey值已注册，请使用其他键值");
			keyClassMap.put(classKey, userClass);
			classKeyMap.put(userClass, classKey);
		}
	}

	public static String createObjectID(Class<? extends IModelObject> clazz) {
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(classKeyMap.get(ClassUtils.getUserClass(clazz)));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new EntityException(CoreExceptionType.ENTITY_ID_GENERATE_ERROR,new Object[]{ClassUtils.getUserClass(clazz).getName()});
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
	 * @param id
	 * @return
	 */
	public static IModelObject getObject(String id){
		if(StringUtils.isBlank(id)){
			return null;
		}
		try{
			Class<? extends IModelObject> clazz=getObejctClass(id);
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
			else{
				IModelObject info= (IModelObject)clazz.newInstance();
				info.setId(id);
				
				Enhancer enhancer=new Enhancer();  
			    enhancer.setSuperclass(info.getClass());
			    enhancer.setCallback(new ModelLazyLoadMethodInterceptor(info));
			    return (IModelObject)enhancer.create();
			}
		}catch (Exception e) {
			return null;
		}
	}
	
	public static String getClassShortKey(Class<? extends IModelObject> clazz){
		Assert.isTrue(classKeyMap.containsKey(ClassUtils.getUserClass(clazz)),
						clazz == null ? "待查询ModelClass不能为空" : ClassUtils.getUserClass(clazz).getName() + "尚未注册");
		return Long.toHexString(classKeyMap.get(ClassUtils.getUserClass(clazz))).toUpperCase();
	}
	
	public static Class<? extends IModelObject> getClassByObjectKey(String key){
		Assert.hasLength(key,"待查询索引键值不能为空");
		try{
			return keyClassMap.get(Long.parseLong(key,16));
		}catch (Exception e) {
			throw new EntityException("不存在指定key值对应的ModelClass");
		}
	}
	
	/**
	 * 判断指定id是否为指定业务对象合法id
	 * @param modelClazz
	 * @param id
	 * @return
	 */
	public static boolean isLegalId(Class<? extends IModelObject> modelClazz,String id){
		return modelClazz != null && StringUtils.isNotBlank(id)
				&& (id.length() == 32 || id.length() == 24)
				&& (ClassUtils.getUserClass(modelClazz) == getObejctClass(id));
	}
}
