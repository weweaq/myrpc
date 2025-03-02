package com.yupi.yurpc.proxy;

public class ServiceProxyFactory {

    /*
     * 1. 有什么用？
     * InvocationHandler 是 Java 动态代理的核心接口，用于实现代理对象的方法调用拦截。
     * 当通过代理对象调用方法时，实际会触发 invoke() 方法
     * 开发者可以在此方法中自定义逻辑（例如 RPC 请求、日志记录、权限校验等）。
     *
     * 4. 为什么用？
     * 解耦：将方法调用逻辑与实际业务分离。
     * 灵活性：通过一个 InvocationHandler 处理多个接口的代理，避免为每个接口编写静态代理类。
     * 透明性：调用方无需感知代理存在，直接操作接口。
     *
     * interfaceClass：定义代理对象需要实现的接口。
     * ServiceProxy：实现 InvocationHandler，处理实际方法调用（如 RPC 请求序列化/反序列化）
     *
     */

    public static <T> T getProxy(Class<T> interfaceClass) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new ServiceProxy());
    }
}
