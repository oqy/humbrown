package com.minyisoft.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

public class StringEnumTypeHandler<E extends Enum<? extends DescribableEnum<String>>> extends
		BaseTypeHandler<E> {
	private Class<E> type;

	public StringEnumTypeHandler(Class<E> type) {
		if (type == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter,
			JdbcType jdbcType) throws SQLException {
		ps.setString(i, ((DescribableEnum<String>)parameter).getValue());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return getEnum(rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return getEnum(rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return getEnum(cs.getString(columnIndex));
	}

	private E getEnum(String value) {
		return DescribableEnumHelper.getEnum(type, value);
	}
}
