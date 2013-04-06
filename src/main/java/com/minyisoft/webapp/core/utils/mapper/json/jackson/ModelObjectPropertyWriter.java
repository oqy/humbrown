package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.minyisoft.webapp.core.model.IModelObject;

public class ModelObjectPropertyWriter extends BeanPropertyWriter {
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	protected ModelObjectPropertyWriter(BeanPropertyWriter base) {
		super(base);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void serializeAsField(Object bean, JsonGenerator jgen,
			SerializerProvider prov) throws Exception{
		logger.debug(bean.getClass().getName()+"\t"+getType().getRawClass().getName());
		// 属性为IModelObject业务对象
		if(IModelObject.class.isAssignableFrom(getType().getRawClass())){
			Object modelObject = get(bean);
	        if (modelObject != null) {
	        	jgen.writeFieldName(getName());
	        	jgen.writeString(((IModelObject)modelObject).getId());
	        }
		}
		// collection对象
		else if(Collection.class.isAssignableFrom(getType().getRawClass())){
			Collection<?> col=(Collection<?>)get(bean);
			if(CollectionUtils.isNotEmpty(col)){
				if(IModelObject.class.isAssignableFrom(getType().getContentType().getRawClass())){
					jgen.writeFieldName(getName());
					jgen.writeStartArray();
					for(IModelObject object:(Collection<IModelObject>)col){
						if(object!=null&&object.isIdPresented()){
							jgen.writeString(object.getId());
						}
					}
					jgen.writeEndArray();
				}else{
					super.serializeAsField(bean, jgen, prov);
				}
			}
		}// array对象
		else if(Object[].class.isAssignableFrom(getType().getRawClass())){
			Object[] array=(Object[])get(bean);
			if(ArrayUtils.isNotEmpty(array)){
				if(IModelObject.class.isAssignableFrom(getType().getContentType().getRawClass())){
					jgen.writeFieldName(getName());
					jgen.writeStartArray();
					for(IModelObject object:(IModelObject[])array){
						if(object!=null&&object.isIdPresented()){
							jgen.writeString(object.getId());
						}
					}
					jgen.writeEndArray();
				}else{
					super.serializeAsField(bean, jgen, prov);
				}
			}
		}else{
			super.serializeAsField(bean, jgen, prov);
		}
	}

}
