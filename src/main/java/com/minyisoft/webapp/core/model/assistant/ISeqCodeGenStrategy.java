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
	String genSeqCode(ISeqCodeObject seqCodeObject);
}
