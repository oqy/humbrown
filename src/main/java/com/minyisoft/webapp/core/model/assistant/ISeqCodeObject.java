package com.minyisoft.webapp.core.model.assistant;

/**
 * @author qingyong_ou 需要实现顺序编码的对象接口
 */
public interface ISeqCodeObject {
	/**
	 * 获取类简码
	 * 
	 * @return
	 */
	public String getClassLabel();

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
	 * 设置顺序号
	 * 
	 * @param seqCode
	 */
	public void setSeqCode(String seqCode);

	/**
	 * 获取顺序号生成策略接口
	 * 
	 * @return
	 */
	public ISeqCodeGenStrategy getSeqCodeGenStrategy();
}
