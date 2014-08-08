package com.minyisoft.webapp.core.model.assistant;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 需要实现顺序编码的对象接口
 */
public interface ISeqCodeObject extends IModelObject {
	/**
	 * 自动编码开关
	 * 
	 * @return
	 */
	boolean isAutoSeqEnabled();

	/**
	 * 获取初始顺序号
	 * 
	 * @return
	 */
	int getInitValue();

	/**
	 * 获取顺序号递增步长
	 * 
	 * @return
	 */
	int getAutoIncreaseStep();

	/**
	 * 获取顺序号前缀
	 * 
	 * @return
	 */
	String getSeqCodePrefix();

	/**
	 * 获取顺序号阿拉伯数字位数
	 * 
	 * @return
	 */
	int getDigitLength();
	
	/**
	 * 获取顺序号
	 * 
	 * @return
	 */
	String getSeqCode();
	
	/**
	 * 生成顺序号
	 */
	void genSeqCode();

	/**
	 * 获取顺序号生成策略接口
	 * 
	 * @return
	 */
	ISeqCodeGenStrategy getSeqCodeGenStrategy();
}
