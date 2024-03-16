package cn.yejj.yejjrpc.demo.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import cn.yejj.yejjrpc.demo.api.Parent;
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
public class UserServiceImpl extends Parent implements UserService {
    @Override
    public User findById(int id) {
        if (id == 404){
            throw new RuntimeException("模拟错误：404");
        }
        User user = new User(id, "yejj"+System.currentTimeMillis());
        return user;
    }

    @Override
    public String findStrById() {
        return "======== null =======";
    }

    @Override
    public Integer findIntgerById(Integer i) {
        return i;
    }

    @Override
    public long getId(long id) {
        return 203000L;
    }

    @Override
    public int getId(User user) {
        return 202;
    }

    @Override
    public String getName() {
        return "yejj gogogo";
    }

    @Override
    public String getName(int i) {
        return "yejj gogogogo" +i;
    }
}
