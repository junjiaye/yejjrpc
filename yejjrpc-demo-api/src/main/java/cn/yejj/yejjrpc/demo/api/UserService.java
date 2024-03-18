package cn.yejj.yejjrpc.demo.api;

import java.util.List;

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

    int getId(User user,int i);
    int getId(int[] ins);

    int getId(List<Integer> list);


    int[] getId();

    String getName();

    String getName(int i);

}
