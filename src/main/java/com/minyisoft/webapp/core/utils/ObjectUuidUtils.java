package com.minyisoft.webapp.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.exception.EntityException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;

public final class ObjectUuidUtils {
	// 根据model key检索类全名
	private static final Properties keyClassProperties=new Properties();
	// 根据model key检索类全名
	private static final Properties classKeyProperties=new Properties();
	// 缓存model简码对应的类
	private static final ConcurrentMap<String, Class<? extends IModelObject>> modelClassCaches = new ConcurrentHashMap<String, Class<? extends IModelObject>>();
	
	private ObjectUuidUtils(){
		
	}
	
	static{	
		try{
			Resource[] resources=new PathMatchingResourcePatternResolver().getResources("classpath*:com/fusung/webapp/**/modelKey.properties");
			if(!ArrayUtils.isEmpty(resources)){
				for (Resource rsc : resources) {
					keyClassProperties.load(rsc.getInputStream());
				}
			}
			
			Iterator<Entry<Object,Object>> iterator=keyClassProperties.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<Object,Object> entry=iterator.next();
				classKeyProperties.put(entry.getValue(), entry.getKey());
			}
		}catch (Exception e) {
			throw new EntityException(e);
		}
	}

	public static String createObjectID(Class<? extends IModelObject> clazz) {
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(Long.parseLong((String)classKeyProperties.get(ClassUtils.getUserClass(clazz).getName()),16));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new EntityException(EntityException.ENTITY_OBJECT_ID_GENERATE_ERROR,new Object[]{ClassUtils.getUserClass(clazz).getName()});
		}
		return EncodeUtils.encodeUrlSafeBase64(baos.toByteArray());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends IModelObject> getObejctClass(String id) {
		try {
			byte[] array = EncodeUtils.decodeBase64(id); 
			DataInput in = new DataInputStream(new ByteArrayInputStream(array));
			Long key = in.readLong();

			return (Class<? extends IModelObject>)Class.forName(keyClassProperties.get(Long.toHexString(key).toUpperCase()).toString());
		} catch (Exception ioe) {
			return null;
		}
	}
	
	/**
	 * 根据id获取对象，相关属性后加载处理
	 * @param id
	 * @return
	 */
	public static IModelObject getObjectById(String id){
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
		return (String)classKeyProperties.get(ClassUtils.getUserClass(clazz).getName());
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends IModelObject> getClassByObjectKey(String key){
		Class<? extends IModelObject> modelClass=modelClassCaches.get(key);
		if(modelClass==null){
			try {
				modelClass=(Class<? extends IModelObject>)Class.forName(keyClassProperties.getProperty(key));
				modelClassCaches.put(key, modelClass);
			} catch (ClassNotFoundException e) {
			}
		}
		return modelClass;
	}
	
	/**
	 * 判断指定id是否为指定业务对象合法id
	 * @param modelClazz
	 * @param id
	 * @return
	 */
	public static boolean isLegalId(Class<? extends IModelObject> modelClazz,String id){
		return modelClazz!=null&&StringUtils.isNotBlank(id)&&(id.length()==32||id.length()==24)&&(ClassUtils.getUserClass(modelClazz)==getObejctClass(id));
	}
}
