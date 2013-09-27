package com.minyisoft.webapp.core.utils.mapper.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBase;
import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou
 * IModelObject类专属JavaType，用于区别IModelObject根对象和属性对象
 */
public final class ModelBeanJavaType extends TypeBase {
	private static final long serialVersionUID = -800374828948534376L;

	private ModelBeanJavaType(Class<?> raw, int additionalHash,
			Object valueHandler, Object typeHandler) {
		super(raw, additionalHash, valueHandler, typeHandler);
	}
	
	public static ModelBeanJavaType construct(Class<? extends IModelObject> cls)
    {
        return new ModelBeanJavaType(cls,0,null,null);
    }

	@Override
	protected String buildCanonicalName() {
		return _class.getName();
	}

	@Override
	public StringBuilder getGenericSignature(StringBuilder sb) {
		_classSignature(_class, sb, false);
		return sb;
	}

	@Override
	public StringBuilder getErasedSignature(StringBuilder sb) {
		return _classSignature(_class, sb, true);
	}

	@Override
	public JavaType withTypeHandler(Object h) {
		return new ModelBeanJavaType(_class, 0, _valueHandler, h);
	}

	@Override
	public JavaType withContentTypeHandler(Object h) {
		throw new IllegalArgumentException("ModelBean types have no content types; can not call withContenTypeHandler()");
	}

	@Override
	public JavaType withValueHandler(Object h) {
		if (h == _valueHandler) {
			return this;
		}
		return new ModelBeanJavaType(_class, 0, h, _typeHandler);
	}

	@Override
	public JavaType withContentValueHandler(Object h) {
		throw new IllegalArgumentException("ModelBean types have no content types; can not call withContenValueHandler()");
	}

	@Override
	protected JavaType _narrow(Class<?> subclass) {
		return null;
	}

	@Override
	public JavaType narrowContentsBy(Class<?> contentClass) {
		throw new IllegalArgumentException("Internal error: ModelBeanJavaType.narrowContentsBy() should never be called");
	}

	@Override
	public JavaType widenContentsBy(Class<?> contentClass) {
		throw new IllegalArgumentException("Internal error: ModelBeanJavaType.widenContentsBy() should never be called");
	}

	@Override
	public boolean isContainerType() {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(40);
        sb.append("[ModelBean type, class ").append(buildCanonicalName()).append(']');
        return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;

        ModelBeanJavaType other = (ModelBeanJavaType) o;

        // Classes must be identical... 
        if (other._class != this._class) return false;
        return true;
	}
}
