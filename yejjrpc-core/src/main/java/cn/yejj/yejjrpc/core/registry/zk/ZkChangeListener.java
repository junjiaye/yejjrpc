package cn.yejj.yejjrpc.core.registry.zk;

/**
 * @program: yejjrpc
 * @ClassName: ZkChangeListener
 * @description:
 * @author: yejj
 * @create: 2024-03-20 22:50
 */
public interface ZkChangeListener {

    void fire(ZkEvent event);
}
