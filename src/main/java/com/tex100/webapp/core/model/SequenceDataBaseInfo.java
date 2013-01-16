package com.tex100.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 可排序数据类基类对象
 */
@Getter
@Setter
public abstract class SequenceDataBaseInfo extends DataBaseInfo {
	private static final long serialVersionUID = -5757214154505335332L;
	// 序号
	private int seq=99;
}
