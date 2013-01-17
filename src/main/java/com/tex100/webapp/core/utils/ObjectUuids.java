package com.tex100.webapp.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.tex100.webapp.core.exception.EntityException;
import com.tex100.webapp.core.model.CoreBaseInfo;
import com.tex100.webapp.core.security.utils.Encodes;

public final class ObjectUuids {
	private static Properties properties=new Properties();
	
	private static Map<String,String> classToKeyMap=new HashMap<String,String>();
	
	private ObjectUuids(){
		
	}
	
	static{	
		try{
			Resource[] resources=new PathMatchingResourcePatternResolver().getResources("**/modelKey.properties");
			if(!ArrayUtils.isEmpty(resources)){
				for (Resource rsc : resources) {
					properties.load(rsc.getInputStream());
				}
			}
			
			Iterator<Entry<Object,Object>> iterator=properties.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<Object,Object> entry=iterator.next();
				classToKeyMap.put(entry.getValue().toString(), entry.getKey().toString());
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String createObjectID(CoreBaseInfo info) {
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(Long.parseLong(classToKeyMap.get(info.getClass().getName()).toString(),16));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new EntityException(EntityException.ENTITY_OBJECT_ID_GENERATE_ERROR,new Object[]{info.getClass().getName()});
		}
		return Encodes.encodeUrlSafeBase64(baos.toByteArray());
	}

	public static Class<?> getObejctClass(String id) {
		try {
			byte[] array = Encodes.decodeBase64(id); 
			DataInput in = new DataInputStream(new ByteArrayInputStream(array));
			Long key = in.readLong();

			return Class.forName(properties.get(Long.toHexString(key).toUpperCase()).toString());
		} catch (Exception ioe) {
			throw new EntityException(EntityException.ENTITY_ID_NOT_EXIST,new Object[]{id});
		}
	}
	
	public static CoreBaseInfo getObjectById(String id){
		try{
			CoreBaseInfo info= (CoreBaseInfo)getObejctClass(id).newInstance();
			info.setId(id);
			return info;
		}catch(Exception ioe){
			throw new EntityException(EntityException.ENTITY_ID_NOT_EXIST,new Object[]{id});
		}
	}
	
	public static String getShortKeyByClassFullName(Class<? extends CoreBaseInfo> clazz){
		return classToKeyMap.get(clazz.getName());
	}
}
