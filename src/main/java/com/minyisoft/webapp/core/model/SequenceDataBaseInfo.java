package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 可排序数据类基类对象
 */
@Getter
@Setter
public abstract class SequenceDataBaseInfo extends DataBaseInfo {
	// 序号
	private int seq=99;
}
