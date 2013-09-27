package com.minyisoft.webapp.core.web.controller.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.minyisoft.webapp.core.model.enumField.CoreEnumHelper;
import com.minyisoft.webapp.core.model.enumField.ICoreEnum;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomStringToEnumConverterFactory implements
		ConverterFactory<String, Enum> {
	/**
	 * 缓存转换器
	 */
	private final ConcurrentMap<Class<? extends Enum>, Converter<String, Enum>> converterCache = new ConcurrentHashMap<Class<? extends Enum>, Converter<String, Enum>>(); 

	@Override
	public <T extends Enum> Converter<String, T> getConverter(
			Class<T> targetType) {
		Converter<String, Enum> converter=converterCache.get(targetType);
		if(converter!=null){
			return (Converter<String, T>)converter;
		}
		
		if(ICoreEnum.class.isAssignableFrom(targetType)
				&& ArrayUtils.isNotEmpty(targetType.getGenericInterfaces())) {
			Type type = targetType.getGenericInterfaces()[0];
			// 如果该泛型类型是参数化类型
			if (type instanceof ParameterizedType) {
				// 获取泛型类型的实际类型参数集
				Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
				// 取出第一个(下标为0)参数的值
				if (parameterizedType[0] == String.class) {
					converter=new StringToStringCoreEnum(targetType);
				} else if (parameterizedType[0] == Integer.class) {
					converter=new StringToIntCoreEnum(targetType);
				}
			}
		}
		if(converter==null){
			converter=new StringToEnum(targetType);
		}
		converterCache.put(targetType, converter);
		return (Converter<String, T>)converter;
	}
	
	private static final class StringToStringCoreEnum<T extends ICoreEnum<String>> implements Converter<String, T> {

		private final Class<T> targetType;

		public StringToStringCoreEnum(Class<T> targetType) {
			this.targetType = targetType;
		}

		public T convert(String source) {
			if (StringUtils.isBlank(source)) {
				return null;
			}
			return CoreEnumHelper.getEnum(targetType, source);
		}
	}
	
	private static final class StringToIntCoreEnum<T extends ICoreEnum<Integer>> implements Converter<String, T> {

		private final Class<T> targetType;

		public StringToIntCoreEnum(Class<T> targetType) {
			this.targetType = targetType;
		}

		public T convert(String source) {
			if (StringUtils.isBlank(source) || !StringUtils.isNumeric(source)) {
				return null;
			}
			return CoreEnumHelper.getEnum(targetType, Integer.parseInt(source));
		}
	}
	
	private class StringToEnum<T extends Enum> implements Converter<String, T> {

		private final Class<T> enumType;

		public StringToEnum(Class<T> enumType) {
			this.enumType = enumType;
		}

		public T convert(String source) {
			if (source.length() == 0) {
				// It's an empty enum identifier: reset the enum value to null.
				return null;
			}
			return (T) Enum.valueOf(this.enumType, source.trim());
		}
	}
}
