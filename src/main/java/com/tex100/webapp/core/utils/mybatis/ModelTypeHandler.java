package com.tex100.webapp.core.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.cglib.proxy.Enhancer;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.tex100.webapp.core.model.CoreBaseInfo;
import com.tex100.webapp.core.utils.ModelLazyLoadMethodInterceptor;
import com.tex100.webapp.core.utils.ObjectUuids;

public class ModelTypeHandler extends BaseTypeHandler<CoreBaseInfo> {
	
	private Object getEnhancedBizModel(String id) throws SQLException {
		CoreBaseInfo bizModel=ObjectUuids.getObjectById(id);
		Enhancer enhancer=new Enhancer();  
        enhancer.setSuperclass(bizModel.getClass());  
        enhancer.setCallback(new ModelLazyLoadMethodInterceptor(bizModel));
        return enhancer.create();
	}
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			CoreBaseInfo parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getId());
	}

	@Override
	public CoreBaseInfo getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return (CoreBaseInfo)getEnhancedBizModel(rs.getString(columnName));
	}

	@Override
	public CoreBaseInfo getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return (CoreBaseInfo)getEnhancedBizModel(rs.getString(columnIndex));
	}

	@Override
	public CoreBaseInfo getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return (CoreBaseInfo)getEnhancedBizModel(cs.getString(columnIndex));
	}
}
