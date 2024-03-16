package cn.yejj.yejjrpc.demo.api;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
public interface UserService {
    User findById(int id);

    String findStrById();

    Integer findIntgerById(Integer i);

    long getId(long id);

    int getId(User user);

    String getName();

    String getName(int i);

}
