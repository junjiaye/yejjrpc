package cn.yejj.yejjrpc.core.cluster;

import cn.yejj.yejjrpc.core.api.LoaderBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: yejjrpc
 * @ClassName: RoundLoadbalancer
 * @description:
 * @author: yejj
 * @create: 2024-03-20 14:08
 */
public class RoundLoadbalancer<T> implements LoaderBalancer<T> {
    AtomicInteger index = new AtomicInteger(0);
    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() ==0) return null;
        if (providers.size() == 1) return providers.get(0);
        //index+1然后根据provider个数取模，依次进行轮询.，使用&0x7fffffff保证永远是正数
        return providers.get((index.getAndIncrement()&0x7fffffff) % providers.size());
    }
}
