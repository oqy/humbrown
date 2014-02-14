package com.minyisoft.webapp.core.persistence;

import java.util.List;

import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.core.model.criteria.summary.SummaryResult;

public interface SummaryDao<C extends BaseCriteria, S extends SummaryResult>{
	/**
	 * 按指定过滤条件获取汇总信息
	 * @param criteria
	 * @return
	 */
	public List<S> summaryEntity(C criteria);
}
