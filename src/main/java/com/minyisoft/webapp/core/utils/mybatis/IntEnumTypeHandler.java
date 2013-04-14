package com.minyisoft.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.minyisoft.webapp.core.model.enumField.ICoreEnum;

public abstract class IntEnumTypeHandler extends BaseTypeHandler<ICoreEnum<Integer>> {
	public abstract ICoreEnum<Integer>[] getEnums();

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			ICoreEnum<Integer> parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setInt(i, parameter.getValue());
	}

	@Override
	public ICoreEnum<Integer> getNullableResult(ResultSet rs,
			String columnName) throws SQLException {
		return getEnum(rs.getInt(columnName));
	}

	@Override
	public ICoreEnum<Integer> getNullableResult(ResultSet rs,
			int columnIndex) throws SQLException {
		return getEnum(rs.getInt(columnIndex));
	}

	@Override
	public ICoreEnum<Integer> getNullableResult(CallableStatement cs,
			int columnIndex) throws SQLException {
		return getEnum(cs.getInt(columnIndex));
	}
	
	private ICoreEnum<Integer> getEnum(int value){
		if(ArrayUtils.isNotEmpty(getEnums())){
			for(ICoreEnum<Integer> e:getEnums()){
				if(e.getValue()==value){
					return e;
				}
			}
		}
		return null;
	}
}
