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
			// TODO: handle exception
		}
	}

	public static String createObjectID(IModelObject info) {
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(Long.parseLong((String)classKeyProperties.get(info.getClass().getName()),16));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new EntityException(EntityException.ENTITY_OBJECT_ID_GENERATE_ERROR,new Object[]{info.getClass().getName()});
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
			throw new EntityException(EntityException.ENTITY_ID_NOT_EXIST,new Object[]{id});
		}
	}
	
	public static IModelObject getObjectById(String id){
		if(StringUtils.isBlank(id)){
			return null;
		}
		try{
			IModelObject info= (IModelObject)getObejctClass(id).newInstance();
			info.setId(id);
			return info;
		}catch(Exception ioe){
			throw new EntityException(EntityException.ENTITY_ID_NOT_EXIST,new Object[]{id});
		}
	}
	
	public static IModelObject getEnhancedObjectById(String id){
		IModelObject bizModel=ObjectUuidUtils.getObjectById(id);
		if(bizModel!=null){
			Enhancer enhancer=new Enhancer();  
	        enhancer.setSuperclass(bizModel.getClass());  
	        enhancer.setCallback(new ModelLazyLoadMethodInterceptor(bizModel));
	        return (IModelObject)enhancer.create();
        }else{
        	return null;
        }
	}
	
	public static String getClassShortKey(Class<? extends IModelObject> clazz){
		return (String)classKeyProperties.get(ClassUtils.getUserClass(clazz).getName());
	}
	
	public static String getClassNameByObjectKey(String key){
		return keyClassProperties.getProperty(key);
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
