package com.minyisoft.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.minyisoft.webapp.core.model.enumField.ICoreEnum;

public abstract class StringEnumTypeHandler extends BaseTypeHandler<ICoreEnum<String>> {
	public abstract ICoreEnum<String>[] getEnums();

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			ICoreEnum<String> parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.getValue());
	}

	@Override
	public ICoreEnum<String> getNullableResult(ResultSet rs,
			String columnName) throws SQLException {
		return getEnum(rs.getString(columnName));
	}

	@Override
	public ICoreEnum<String> getNullableResult(ResultSet rs,
			int columnIndex) throws SQLException {
		return getEnum(rs.getString(columnIndex));
	}

	@Override
	public ICoreEnum<String> getNullableResult(CallableStatement cs,
			int columnIndex) throws SQLException {
		return getEnum(cs.getString(columnIndex));
	}
	
	private ICoreEnum<String> getEnum(String value){
		if(ArrayUtils.isNotEmpty(getEnums())){
			for(ICoreEnum<String> e:getEnums()){
				if(StringUtils.equals(e.getValue(), value)){
					return e;
				}
			}
		}
		return null;
	}
}
