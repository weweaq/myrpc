package com.yupi.yurpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yupi.yurpc.RpcApplication;
import com.yupi.yurpc.config.RpcConfig;
import com.yupi.yurpc.model.RpcRequest;
import com.yupi.yurpc.model.RpcResponse;
import com.yupi.yurpc.serializer.JdkSerializer;
import com.yupi.yurpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Serializer serializer = new JdkSerializer();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            // 这里的地址为硬编码，需要通过注册中心和服务发现机制解决
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            try (HttpResponse httpResponse = HttpRequest.post("http://" + rpcConfig.getServerHost() +":"+ rpcConfig.getServerPort()).body(bytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

            // 测试不调用实际远程服务
//            System.out.println("调用远程服务: " + rpcRequest.getServiceName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 当 try 块中的代码抛出 IOException 或其子类异常时，进入 catch 块。由于 catch 块中没有显式的 return 语句，程序会继续执行 catch 块之后的代码，最终返回 null。
        return null;
    }
}
