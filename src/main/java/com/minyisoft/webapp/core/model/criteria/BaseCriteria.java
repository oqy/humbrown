package com.minyisoft.webapp.core.model.criteria;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;

//查询条件基类
@Getter
@Setter
public abstract class BaseCriteria {
	// 主键集合
	private String[] ids;
	@Label(value = "查询时间起")
	private Date queryBeginDate;
	@Label(value="查询时间止")
	private Date queryEndDate;
	@Label(value="按创建时间排序")
	private SortDirection createDateOrder;
	// 按最后更新日期排序
	private SortDirection lastUpdateDateOrder;
	// 分页器
	private PageDevice pageDevice;
}
