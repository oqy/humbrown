package com.minyisoft.webapp.core.utils.spring;

import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;

/**
 * @author qingyong_ou
 *         搜索已注册的CUDPostProcessor，并注入到已在spring容器中注册的BaseServiceImpl实现类里
 * 
 */
@Component
public class ServiceCUDPostProcessorInjector implements ApplicationListener<ContextRefreshedEvent> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			Map<String, CUDPostProcessor> cudPostProcessorMap = event.getApplicationContext().getBeansOfType(
					CUDPostProcessor.class);
			if (cudPostProcessorMap != null && !cudPostProcessorMap.isEmpty()) {
				List<CUDPostProcessor> cudPostProcessors = Lists.newArrayList();
				for (String cudPostProcessorBeanName : cudPostProcessorMap.keySet()) {
					cudPostProcessors.add(cudPostProcessorMap.get(cudPostProcessorBeanName));
				}

				Map<String, BaseService> serviceBeanMap = event.getApplicationContext().getBeansOfType(
						BaseService.class);
				if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
					for (String serviceBeanName : serviceBeanMap.keySet()) {
						try {
							if (serviceBeanMap.get(serviceBeanName) instanceof Advised
									&& ((Advised) serviceBeanMap.get(serviceBeanName)).getTargetSource().getTarget() instanceof BaseServiceImpl) {
								((BaseServiceImpl) ((Advised) serviceBeanMap.get(serviceBeanName)).getTargetSource()
										.getTarget()).setPostProcessors(cudPostProcessors);
							} else if (serviceBeanMap.get(serviceBeanName) instanceof BaseServiceImpl) {
								((BaseServiceImpl) serviceBeanMap.get(serviceBeanName))
										.setPostProcessors(cudPostProcessors);
							} else {
								throw new ServiceException("无法为" + serviceBeanName + "设置CUD后处理器，请检查");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
