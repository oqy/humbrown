package com.minyisoft.webapp.core.utils.mybatis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

public class DescribableEnumTypeHandler<E extends Enum<? extends DescribableEnum<?>>> extends BaseTypeHandler<E> {
	private Class<E> classType;
	// DescribableEnum参数类型
	private Class<?> describableDetailType = String.class;

	public DescribableEnumTypeHandler(Class<E> type) {
		Assert.notNull(type, "Type argument cannot be null");
		this.classType = type;
		this.describableDetailType = findDetailType(type);
		Assert.notNull(describableDetailType, "DescribableDetailType argument cannot be null");
	}

	/**
	 * 递归获取DescribableEnum泛型接口的参数类型
	 * 
	 * @param targetClass
	 * @return
	 */
	private Class<?> findDetailType(Class<?> targetClass) {
		for (Type interfaceType : targetClass.getGenericInterfaces()) {
			if (interfaceType instanceof ParameterizedType
					&& ((ParameterizedType) interfaceType).getRawType() == DescribableEnum.class) {
				return (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
			} else if (interfaceType instanceof Class) {
				Class<?> clazz = findDetailType((Class<?>) interfaceType);
				if (clazz != null) {
					return clazz;
				}
			}
		}
		return null;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		if (describableDetailType == Integer.class) {
			ps.setInt(i, (Integer) ((DescribableEnum<?>) parameter).getValue());
		} else {
			ps.setString(i, (String) ((DescribableEnum<?>) parameter).getValue());
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return DescribableEnumHelper.getEnum(classType, describableDetailType == Integer.class ? rs.getInt(columnName)
				: rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return DescribableEnumHelper.getEnum(classType, describableDetailType == Integer.class ? rs.getInt(columnIndex)
				: rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return DescribableEnumHelper.getEnum(classType, describableDetailType == Integer.class ? cs.getInt(columnIndex)
				: cs.getString(columnIndex));
	}
}
