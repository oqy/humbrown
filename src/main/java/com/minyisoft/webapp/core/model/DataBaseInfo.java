package com.minyisoft.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public abstract class DataBaseInfo extends BaseInfo {
	// 名称
	private String name;
	// 备注
	private String description;
}
