package cn.yejj.yejjrpc.core.cluster;

import cn.yejj.yejjrpc.core.api.Router;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: yejjrpc
 * @ClassName: GrayRouter
 * @description:
 * 还可以对用户、请求 增加灰度标识，在进行灰度路由时，将用户标识和请求信息传进来，直接走到灰度节点
 * 目前做 是单个服务的灰度，采用的方式是流量染色（给服务配置灰度标识）+调拨（灰度百分比）
 * 全链路灰度/压测： 应用服务-redis-mysql-mq....是更复杂的一套东西
 * @author: yejj
 * @create: 2024-04-06 08:51
 */
@Slf4j
public class GrayRouter implements Router<InstanceMata> {

    private int grayRatio;

    public GrayRouter(int ratio) {
        grayRatio = ratio;
    }

    @Override
    public List route(List<InstanceMata> providers) {
        if (providers == null || providers.size() <=1){
            return providers;
        }
        List<InstanceMata> normalNode = new ArrayList<>();
        List<InstanceMata> grayNode = new ArrayList<>();
        providers.forEach(node->{
            if ("true".equals(node.getParameters().get("gray"))){
                normalNode.add(node);
            }else {
                grayNode.add(node);
            }
        });
        log.debug(" grayRouter grayNodes/normalNodes,grayRatio ===> {}/{},{}",
                grayNode.size(), normalNode.size(), grayRatio);
        if (normalNode.isEmpty() || grayNode.isEmpty()){
            return providers;
        }
        if(grayRatio <= 0){
            return normalNode;
        }else if(grayRatio >= 100){
            return grayNode;
        }
        Random random = new Random();
        int i = random.nextInt(100);
        if(i < grayRatio){
            log.debug(" grayRouter grayNodes ===> {}", grayNode);

            return grayNode;
        }else {
            log.debug(" grayRouter normalNodes ===> {}", normalNode);
            return normalNode;
        }
    }
}
