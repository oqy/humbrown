package com.minyisoft.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

@Alias("bizModelHandler")
public class ModelTypeHandler extends BaseTypeHandler<IModelObject> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			IModelObject parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getId());
	}

	@Override
	public IModelObject getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return ObjectUuidUtils.getObjectById(rs.getString(columnName));
	}

	@Override
	public IModelObject getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return ObjectUuidUtils.getObjectById(rs.getString(columnIndex));
	}

	@Override
	public IModelObject getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return ObjectUuidUtils.getObjectById(cs.getString(columnIndex));
	}
}
