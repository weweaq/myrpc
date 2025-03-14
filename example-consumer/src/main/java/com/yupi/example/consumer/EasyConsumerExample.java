package com.yupi.example.consumer;

import com.yupi.example.common.model.User;
import com.yupi.example.common.service.UserService;
import com.yupi.yurpc.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        // 静态代理，UserServiceProxy实现代理
        // UserService userService = new UserServiceProxy();

        // 动态代理，ServiceProxyFactory实现代理
        // 调用顺序，1. 创建ServiceProxy代理对象 2. 调用UserService的所有接口方法
        // 3. 都会走到ServiceProxy的invoke方法里拦截（如果代理对象没调真实的接口，还真调不到实现类的接口）
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("小黄");
        // 调用
        for (int i = 0; i < 3; i++) {
            System.out.println("第" + i + "次调用");
            User newUser = userService.getUser(user);
            if (newUser != null) {
                System.out.println(newUser.getName());
            } else {
                System.out.println("user == null");
            }
        }
    }
}
