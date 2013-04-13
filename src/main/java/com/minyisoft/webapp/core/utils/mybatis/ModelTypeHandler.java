package com.minyisoft.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class ModelTypeHandler<E extends IModelObject> extends BaseTypeHandler<E> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			IModelObject parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getId());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return getModel(rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return getModel(rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return getModel(cs.getString(columnIndex));
	}
	
	@SuppressWarnings("unchecked")
	public E getModel(String id){
		return (E) ObjectUuidUtils.getEnhancedObjectById(id);
	}
}
