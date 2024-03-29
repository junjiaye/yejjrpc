package cn.yejj.yejjrpc.core.cluster;

import cn.yejj.yejjrpc.core.api.LoaderBalacer;

import java.util.List;
import java.util.Random;

/**
 * @program: yejjrpc
 * @ClassName: RandomLoadbalancer
 * @description:
 * @author: yejj
 * @create: 2024-03-20 13:51
 */
public class RandomLoadbalancer<T> implements LoaderBalacer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() ==0) return null;
        if (providers.size() == 1) return providers.get(0);
        //随机取一个providers size（）长度内的数字
        return providers.get(random.nextInt(providers.size()));
    }
}
