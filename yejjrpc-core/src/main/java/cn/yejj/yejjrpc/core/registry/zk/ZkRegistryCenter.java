package cn.yejj.yejjrpc.core.registry.zk;

import cn.yejj.yejjrpc.core.api.RegistryCenter;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.mate.ServiceMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: yejjrpc
 * @ClassName: ZkRegistryCenter
 * @description:
 * @author: yejj
 * @create: 2024-03-20 17:32
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;
    private TreeCache cache = null;

    @Value("${yejjrpc.zkServer}")
    private String zkServer;
    @Value("${yejjrpc.zkRoot}")
    private String zkRoot;


    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace(zkRoot)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    @Override
    public void stop() {
        cache.close();
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMata instance) {
        String  servicePath = "/" + service.toPath();
        try{
            //创建服务的持久化节点
            if(client.checkExists().forPath(servicePath) == null){
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            //创建实例的临时性节点
            String instancePath = servicePath + "/" +instance.toPath();
            log.info(" ===> register to zk: " + instancePath);

            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
        }catch (Exception ex){
            throw new RpcException(ex, RpcExceptionEnum.UNKNOWN_EX);
        }
    }

    @Override
    public void unRegister(ServiceMeta service, InstanceMata instance) {
        String  servicePath = "/" + service.toPath();;
        try{
            //判断服务的持久化节点是否存在
            if(client.checkExists().forPath(servicePath) == null){
                return;
            }
            //删除实例的临时性节点
            String instancePath = servicePath + "/" +instance.toPath();
            log.info(" ===> unregister from zk: " + instancePath);
            client.delete().forPath(instancePath);
        }catch (Exception ex){
            throw new RpcException(ex, RpcExceptionEnum.UNKNOWN_EX);
        }
    }

    @Override
    public List<InstanceMata> fetchAll(ServiceMeta service) {
        String servicePath = "/" +service.toPath();;
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            nodes.stream().forEach(System.out::println);
            return mapInstances(nodes);
        }catch (Exception ex){
            throw new RpcException(ex, RpcExceptionEnum.UNKNOWN_EX);
        }
    }

    private List<InstanceMata> mapInstances(List<String> nodes) {
        return nodes.stream().map( str ->{
            String[] strs = str.split("_");
            return InstanceMata.http(strs[0],Integer.valueOf(strs[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ZkChangeListener listener) {
        cache = TreeCache.newBuilder(client,"/"+service.toPath())
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
                (curator,event)->{
                    log.info("zk subscribe event" + event);
                    List<InstanceMata> nodes = fetchAll(service);
                    listener.fire(new ZkEvent(nodes));
                }
        );
        cache.start();
    }


}
