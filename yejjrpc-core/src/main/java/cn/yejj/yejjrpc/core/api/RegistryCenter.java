package cn.yejj.yejjrpc.core.api;

import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.mate.ServiceMeta;
import cn.yejj.yejjrpc.core.registry.zk.ZkChangeListener;

import java.util.List;

/**
 * @program: yejjrpc
 * @ClassName: RegistryCenter
 * @description:
 * @author: yejj
 * @create: 2024-03-20 15:53
 */
public interface RegistryCenter {

    void start();
    void stop();

    //provider端
    void register(ServiceMeta service, InstanceMata instance);
    void unRegister(ServiceMeta service, InstanceMata instance);

    //consumer端
    List<InstanceMata> fetchAll(ServiceMeta service);
    void subscribe(ServiceMeta service, ZkChangeListener listener);//订阅服务上线/下线的变化 void subscribe(String service, ZkChangeListener listener);


    class StaticRegistryCenter implements RegistryCenter{



        List<InstanceMata> providers;

        public StaticRegistryCenter(List<InstanceMata> providers) {
            this.providers = providers;
        }


        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMata instance) {

        }

        @Override
        public void unRegister(ServiceMeta service, InstanceMata instance) {

        }


        @Override
        public List<InstanceMata> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ZkChangeListener listener) {

        }


    }
}
