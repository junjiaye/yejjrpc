package cn.yejj.yejjrpc.core.mate;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @program: yejjrpc
 * @ClassName: ProviderMate
 * @description:
 * @author: yejj
 * @create: 2024-03-14 22:01
 */
@Data
public class ProviderMate {
    Method method;
    String methodSign;
    Object serviceImpl;
}
