package com.tex100.webapp.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public abstract class DataBaseInfo extends BaseInfo {
	private static final long serialVersionUID = -2321845103243170960L;
	// 名称
	private String name;
	// 备注
	private String description;
}
