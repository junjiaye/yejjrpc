package cn.yejj.yejjrpc.demo.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import cn.yejj.yejjrpc.demo.api.User;
import cn.yejj.yejjrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Component
@YejjProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        User user = new User(id, "yejj"+System.currentTimeMillis());
        return user;
    }
}
