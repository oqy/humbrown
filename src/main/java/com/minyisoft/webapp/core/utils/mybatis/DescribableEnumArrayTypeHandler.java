package com.minyisoft.webapp.core.utils.mybatis;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

@Alias("describableEnumArrayHandler")
public class DescribableEnumArrayTypeHandler<E extends Enum<? extends DescribableEnum<?>>> extends BaseTypeHandler<E[]> {
	private Class<E> classType;
	private Class<?> describableDetailType;

	@SuppressWarnings("unchecked")
	public DescribableEnumArrayTypeHandler(Class<E> type) {
		Assert.isTrue(type != null && type.isArray(), "Type argument cannot be null");
		this.classType = (Class<E>) type.getComponentType();
		for (Type interfaceType : classType.getGenericInterfaces()) {
			if (interfaceType instanceof ParameterizedType
					&& ((ParameterizedType) interfaceType).getRawType() == DescribableEnum.class) {
				describableDetailType = (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
			}
			continue;
		}
		Assert.notNull(describableDetailType, "DescribableDetailType argument cannot be null");
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E[] parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, Joiner.on("|").join(parameter));
	}

	@Override
	public E[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getEnum(rs.getString(columnName));
	}

	@Override
	public E[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getEnum(rs.getString(columnIndex));
	}

	@Override
	public E[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getEnum(cs.getString(columnIndex));
	}

	@SuppressWarnings("unchecked")
	private E[] getEnum(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		List<String> values = Splitter.on("|").trimResults().omitEmptyStrings().splitToList(value);
		List<E> returnEnums = Lists.newArrayList();
		for (String s : values) {
			returnEnums.add(DescribableEnumHelper.getEnum(classType, describableDetailType == Integer.class
					&& StringUtils.isNumeric(s) ? Integer.parseInt(s) : s));
		}

		return returnEnums.toArray((E[]) Array.newInstance(classType, returnEnums.size()));
	}
}
