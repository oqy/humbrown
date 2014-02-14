package com.minyisoft.webapp.core.model.criteria;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

//查询条件基类
@Getter
@Setter
public abstract class BaseCriteria{
	// 主键集合
	private String[] ids;
	// 需排除主键集合
	private String[] excludeIds;
	@Label(value = "查询时间起")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date queryBeginDate;
	@Label(value = "查询时间止")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date queryEndDate;
	@Label(value = "按创建时间排序")
	private SortDirection createDateOrder;
	// 按最后更新日期排序
	private SortDirection lastUpdateDateOrder;
	// 分页器
	private PageDevice pageDevice;
	
	/**
	 * 通过转json格式后计算md5值，获取指定查询对象的key值，用于缓存等用途
	 * 
	 * @param criteria
	 * @return
	 */
	public final static String getKey(BaseCriteria criteria) {
		Assert.notNull(criteria);
		return DigestUtils.md5Hex(JsonMapper.MODEL_OBJECT_MAPPER
				.toJson(criteria));
	}
	
	public void setExcludeIds(String... ids){
		this.excludeIds=ids;
	}
	
	public void setIds(String... ids){
		this.ids=ids;
	}
}
