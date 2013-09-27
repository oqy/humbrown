package com.minyisoft.webapp.core.utils.mybatis;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou
 * mybatic ognl扩展表达式
 */
public class Ognl {
	public static boolean isNotEmpty(Object obj){
		if(obj == null) {
			return false;
		}
		if(obj instanceof String) {
			return StringUtils.isNotBlank(obj.toString());
		} else if(obj instanceof IModelObject) {
			return ((IModelObject)obj).isIdPresented();
		} else if(obj instanceof Object[]) {
			return ArrayUtils.isNotEmpty((Object[])obj);
		} else if(obj instanceof Collection<?>){
			return CollectionUtils.isNotEmpty((Collection<?>)obj);
		} else {
			return obj != null;
		}
	}
	
	public static boolean isEmpty(Object obj){
		return !isNotEmpty(obj);
	}

	public static boolean isTrue(Boolean b){
		return BooleanUtils.isTrue(b);
	}

	public static boolean isNotTrue(Boolean b){
		return BooleanUtils.isNotTrue(b);
	}

	public static boolean isFalse(Boolean b){
		return BooleanUtils.isFalse(b);
	}

	public static boolean isNotFalse(Boolean b){
		return BooleanUtils.isNotFalse(b);
	}
}
