package com.minyisoft.webapp.core.model.assistant;

/**
 * @author qingyong_ou
 * 顺序编码生成策略
 */
public interface ISeqCodeGenStrategy {
	/**
	 * 生成顺序号
	 * @param seqCodeObject
	 * @return
	 */
	public String genSeqCode(ISeqCodeObject seqCodeObject);
}
