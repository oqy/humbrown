package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * 分录基类
 */
@Getter
@Setter
public abstract class EntryBaseInfo extends CoreBaseInfo {
	private static final long serialVersionUID = -8773675981896205308L;
	// 分录记录顺序
	private int seq;
}
