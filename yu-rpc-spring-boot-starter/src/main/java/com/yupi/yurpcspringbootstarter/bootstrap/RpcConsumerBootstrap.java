package com.yupi.yurpcspringbootstarter.bootstrap;

import com.yupi.yurpc.proxy.ServiceProxyFactory;
import com.yupi.yurpcspringbootstarter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Rpc 服务消费者启动
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">程序员鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Bean 初始化后执行，注入服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    /**
     * Bean初始化后置处理方法，用于处理带有@RpcReference注解的字段注入代理对象
     *
     * @param bean 当前正在初始化的Spring Bean实例
     * @param beanName 在Spring容器中注册的Bean名称
     * @return 处理后的Bean实例，已注入代理对象的字段
     * @throws BeansException 当字段注入代理对象失败时抛出运行时异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // 扫描目标对象的所有字段，查找需要RPC注入的字段
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 确定接口类型：优先使用注解指定的interfaceClass，默认使用字段类型
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }

                // 生成接口代理对象并注入字段
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    // 处理字段访问异常，转换为非受检异常
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        return bean;
    }

}
