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

@Alias("modelClassHandler")
public class ModelClassTypeHandler extends BaseTypeHandler<Class<? extends IModelObject>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Class<? extends IModelObject> parameter,
			JdbcType jdbcType) throws SQLException {
		ps.setString(i, ObjectUuidUtils.getClassShortKey(parameter));
	}

	@Override
	public Class<? extends IModelObject> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return ObjectUuidUtils.getClassByObjectKey(rs.getString(columnName));
	}

	@Override
	public Class<? extends IModelObject> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return ObjectUuidUtils.getClassByObjectKey(rs.getString(columnIndex));
	}

	@Override
	public Class<? extends IModelObject> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return ObjectUuidUtils.getClassByObjectKey(cs.getString(columnIndex));
	}
}
