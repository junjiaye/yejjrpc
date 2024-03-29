package cn.yejj.yejjrpc.core.api;

import java.util.List;

/**
 * @program: yejjrpc
 * @ClassName: LoaderBalacer
 *
 * 随机 权重 轮询  自适应（AAWR）
 * @author: yejj
 * @create: 2024-03-19 20:44
 */
public interface LoaderBalacer<T> {

    T choose(List<T> providers);

    LoaderBalacer Defaut = p -> (p == null || p.size() == 0) ? null : p.get(0);

}
