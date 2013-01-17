package com.tex100.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.tex100.webapp.core.model.enumField.CoreEnumInterface;

public abstract class IntEnumTypeHandler extends BaseTypeHandler<CoreEnumInterface<Integer>> {
	public abstract CoreEnumInterface<Integer>[] getEnums();

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			CoreEnumInterface<Integer> parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setInt(i, parameter.getValue());
	}

	@Override
	public CoreEnumInterface<Integer> getNullableResult(ResultSet rs,
			String columnName) throws SQLException {
		return getEnum(rs.getInt(columnName));
	}

	@Override
	public CoreEnumInterface<Integer> getNullableResult(ResultSet rs,
			int columnIndex) throws SQLException {
		return getEnum(rs.getInt(columnIndex));
	}

	@Override
	public CoreEnumInterface<Integer> getNullableResult(CallableStatement cs,
			int columnIndex) throws SQLException {
		return getEnum(cs.getInt(columnIndex));
	}
	
	private CoreEnumInterface<Integer> getEnum(int value){
		if(ArrayUtils.isNotEmpty(getEnums())){
			for(CoreEnumInterface<Integer> e:getEnums()){
				if(e.getValue()==value){
					return e;
				}
			}
		}
		return null;
	}
}
