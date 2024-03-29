package cn.yejj.yejjrpc.core.mate;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @program: yejjrpc
 * @ClassName: ProviderMate
 * @description:
 * 用来表示provider提供的接口的信息
 * @author: yejj
 * @create: 2024-03-14 22:01
 */
@Data
public class ProviderMata {
    Method method;
    String methodSign;
    Object serviceImpl;
}
