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
	public boolean isAutoSeqEnabled();

	/**
	 * 获取初始顺序号
	 * 
	 * @return
	 */
	public int getInitValue();

	/**
	 * 获取顺序号递增步长
	 * 
	 * @return
	 */
	public int getAutoIncreaseStep();

	/**
	 * 获取顺序号前缀
	 * 
	 * @return
	 */
	public String getSeqCodePrefix();

	/**
	 * 获取顺序号阿拉伯数字位数
	 * 
	 * @return
	 */
	public int getDigitLength();
	
	/**
	 * 获取顺序号
	 * 
	 * @return
	 */
	public String getSeqCode();
	
	/**
	 * 生成顺序号
	 */
	public void genSeqCode();

	/**
	 * 获取顺序号生成策略接口
	 * 
	 * @return
	 */
	public ISeqCodeGenStrategy getSeqCodeGenStrategy();
}
