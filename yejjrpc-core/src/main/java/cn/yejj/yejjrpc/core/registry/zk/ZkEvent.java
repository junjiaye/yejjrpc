package cn.yejj.yejjrpc.core.registry.zk;

import cn.yejj.yejjrpc.core.mate.InstanceMata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: yejjrpc
 * @ClassName: ZkEvent
 * @description:
 * @author: yejj
 * @create: 2024-03-20 22:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZkEvent {
    List<InstanceMata> data;
}
