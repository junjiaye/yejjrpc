package cn.yejj.yejjrpc.core.filter;

import cn.yejj.yejjrpc.core.api.Filter;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import cn.yejj.yejjrpc.core.utils.MockUtils;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @program: yejjrpc
 * @ClassName: MockFilter
 * @description:
 * @author: yejj
 * @create: 2024-03-25 15:39
 */
public class MockFilter implements Filter {
    @Override
    @SneakyThrows
    public Object  prefilter(RpcRequest rpcRequest) {
        Class service = Class.forName(rpcRequest.getService());
        Method method = getMethod(rpcRequest.getMethodSign(), service);
        Class clazz =  method.getReturnType();
        return MockUtils.mock(clazz);
    }

    @Nullable
    private static Method getMethod(String methodSign, Class service) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> MethodUtils.methodSign(method).equals(methodSign))
                .findFirst().orElse(null);
    }

    @Override
    public Object postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse,Object result) {
        return null;
    }
}
