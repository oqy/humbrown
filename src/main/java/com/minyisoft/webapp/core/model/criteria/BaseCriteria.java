package com.minyisoft.webapp.core.model.criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.annotation.Label;

//查询条件基类
@Getter
@Setter
public abstract class BaseCriteria {
	// 主键集合
	private String[] ids;
	// 需排除主键集合
	private String[] excludeIds;
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
	// 排序数组--SQL用
	private SortDirection[] sortDirections;

	// 排序数组字符串--页面信息传递用
	private String sortDirectionString;
	
	public void setSortDirectionStringByArray() {
		StringBuffer result = new StringBuffer();
		for(SortDirection s : sortDirections) {
			result.append("-" + s.getMsgString());
		}
		if(result.length() != 0) {
			this.setSortDirectionString(result.toString().substring(1));
		} else {
			this.setSortDirectionString("");
		}
	}
	
	public void setSortDirectionByString() throws Exception {
		if(!StringUtils.isBlank(sortDirectionString)) {
			String[] sortSrcStrs = sortDirectionString.split(":");
			String fireSortItem = sortSrcStrs[0];
			if(sortSrcStrs.length==1) {
				if(fireSortItem.indexOf(",") == -1) {
					SortDirection fireSort = new SortDirection();
					fireSort.setItem(fireSortItem);
					fireSort.setSortDirection(SortDirectionEnum.SORT_ASC);
					this.setSortDirections(new SortDirection[]{fireSort});
					return;
				}
				String[] sortStaStrs = fireSortItem.split("-");
				SortDirection[] staSortDirection = new SortDirection[sortStaStrs.length];
				for(int i = 0; i < sortStaStrs.length; i++) {
					SortDirection temp = new SortDirection();
					String[] msg = sortStaStrs[i].split(",");
					if(StringUtils.isBlank(msg[0]) || StringUtils.isBlank(msg[1])) {
						throw new Exception("排序信息为空");
					}
					temp.setItem(msg[0]);
					temp.setSortDirection(SortDirectionEnum.getEnum(msg[1]));
					staSortDirection[i] = temp;
				}
				this.setSortDirections(staSortDirection);
				return;
			}
			String[] sortStrs = sortSrcStrs[1].split("-");
			int index = -1;
			List<SortDirection> sortList = new ArrayList<SortDirection>();
			for(int i = 0; i < sortStrs.length; i++) {
				SortDirection temp = new SortDirection();
				String[] msg = sortStrs[i].split(",");
				if(StringUtils.isBlank(msg[0]) || StringUtils.isBlank(msg[1])) {
					throw new Exception("排序信息为空");
				}
				temp.setItem(msg[0]);	
				if(temp.getItem().equalsIgnoreCase(fireSortItem)) {
					index = i;
				}
				temp.setSortDirection(SortDirectionEnum.getEnum(msg[1]));	
				sortList.add(temp);
			}
			SortDirection fireSortUnit = new SortDirection();
			if(index != -1) {
				fireSortUnit.setItem(sortList.get(index).getItem());
				fireSortUnit.setSortDirection(getDefaultSort(sortList.get(index).getSortDirection().toString()));
				sortList.remove(index);
			} else {
				fireSortUnit.setItem(fireSortItem);
				fireSortUnit.setSortDirection(SortDirectionEnum.SORT_ASC);
			}
			sortList.add(0, fireSortUnit);
			this.setSortDirections(getSortFromList(sortList));
		}
	}
	
	private SortDirectionEnum getDefaultSort(String sort) {
		SortDirectionEnum result = SortDirectionEnum.SORT_ASC;
		if(!StringUtils.isBlank(sort)) {
			if(sort.equalsIgnoreCase(SortDirectionEnum.SORT_ASC.toString())) {
				result = SortDirectionEnum.SORT_DESC;
			} else {
				result = SortDirectionEnum.SORT_ASC;
			}
		}
		return result;
	}
	
	private SortDirection[] getSortFromList(List<SortDirection> sortList) {
		int len = sortList.size();
		if(sortList == null || len == 0) {
			return null;
		}
		SortDirection[] result = new SortDirection[len];
		for(int i = 0; i < len; i++) {
			result[i] = sortList.get(i);
		}
		return result;
	}
}
