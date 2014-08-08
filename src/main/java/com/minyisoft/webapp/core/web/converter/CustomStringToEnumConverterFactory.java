package com.minyisoft.webapp.core.web.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomStringToEnumConverterFactory implements ConverterFactory<String, Enum> {
	/**
	 * 缓存转换器
	 */
	private final ConcurrentMap<Class<? extends Enum>, Converter<String, Enum>> converterCache = Maps
			.newConcurrentMap();

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		Converter<String, Enum> converter = converterCache.get(targetType);
		if (converter != null) {
			return (Converter<String, T>) converter;
		}

		if (DescribableEnum.class.isAssignableFrom(targetType)) {
			converter = new StringToDescribableEnum(targetType);
		} else {
			converter = new StringToEnum(targetType);
		}
		converterCache.put(targetType, converter);
		return (Converter<String, T>) converter;
	}

	private static final class StringToDescribableEnum<T extends Enum<? extends DescribableEnum<String>>> implements
			Converter<String, T> {

		private final Class<T> targetType;

		public StringToDescribableEnum(Class<T> targetType) {
			this.targetType = targetType;
		}

		public T convert(String source) {
			if (StringUtils.isNotBlank(source)) {
				for (Type interfaceType : targetType.getGenericInterfaces()) {
					if (interfaceType instanceof ParameterizedType
							&& ((ParameterizedType) interfaceType).getRawType() == DescribableEnum.class) {
						if (((ParameterizedType) interfaceType).getActualTypeArguments()[0] == Integer.class
								&& StringUtils.isNumeric(source)) {
							return DescribableEnumHelper.getEnum(targetType, Integer.parseInt(source));
						} else {
							return DescribableEnumHelper.getEnum(targetType, source);
						}
					}
					continue;
				}
			}
			return null;
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
