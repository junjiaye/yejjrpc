package cn.yejj.yejjrpc.demo.consumer.controller;

import cn.yejj.yejjrpc.core.annotation.YejjConsumer;
import cn.yejj.yejjrpc.core.api.RpcContext;
import cn.yejj.yejjrpc.demo.api.User;
import cn.yejj.yejjrpc.demo.api.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: yejjrpc
 * @ClassName: TestController
 * @description:
 * @author: yejj
 * @create: 2024-03-20 13:59
 */
@RestController
public class TestController {
    @YejjConsumer
    UserService userService;

    @RequestMapping("/case1")
    public void case1() {
        System.out.println("Case 1. >>===[常规int类型，返回User对象]===");
        User user = userService.findById(1);
        System.out.println("RPC result userService.findById(1) = " + user);
    }

    @RequestMapping("/case2")
    public void case2() {
        System.out.println("Case 2. >>===[测试方法重载，同名方法，参数不同===");
        User user1 = userService.findById(1, "hubao");
        System.out.println("RPC result userService.findById(1, \"hubao\") = " + user1);
    }

    @RequestMapping("/case3")
    public void case3() {
        // 测试返回字符串
        System.out.println("Case 3. >>===[测试返回字符串]===");
        System.out.println("userService.getName() = " + userService.getName());
    }

    @RequestMapping("/case4")
    public void case4() {
        // 测试重载方法返回字符串
        System.out.println("Case 4. >>===[测试重载方法返回字符串]===");
        System.out.println("userService.getName(123) = " + userService.getName(123));
    }

    @RequestMapping("/case5")
    public void case5() {
        System.out.println("Case 5. >>===[测试local toString方法]===");
        System.out.println("userService.toString() = " + userService.toString());
    }

    @RequestMapping("/case6")
    public void case6() {
        System.out.println("Case 6. >>===[常规int类型，返回User对象]===");
        System.out.println("userService.getId(10) = " + userService.getId(10));
    }

    @RequestMapping("/case7")
    public void case7() {
        System.out.println("Case 7. >>===[测试long+float类型]===");
        System.out.println("userService.getId(10f) = " + userService.getId(10f));
    }

    @RequestMapping("/case8")
    public void case8() {
        System.out.println("Case 8. >>===[测试参数是User类型]===");
        System.out.println("userService.getId(new User(100,\"KK\")) = " +
                userService.getId(new User(100, "KK")));
    }
    @RequestMapping("/case9")
    public void case9() {
        System.out.println("Case 9. >>===[测试返回参数是int[]类型]===");
        for (long id : userService.getIds()) {
            System.out.println(id);
        }
    }
    @RequestMapping("/case10")
    public void case10() {
        System.out.println("Case 10. >>===[测试返回long[]]===");
        System.out.println(" ===> userService.getLongIds(): ");
        for (long id : userService.getLongIds()) {
            System.out.println(id);
        }
    }

    @RequestMapping("/case11")
    public void case11() {
        System.out.println(" Case 11. >>===[测试参数和返回值都是int[]类型]=== ");
        for (long id : userService.getIds(new int[]{4, 5, 6})) {
            System.out.println(id);
        }
    }

    @RequestMapping("/case12")
    public void case12() {
        System.out.println("Case 12. >>===[测试参数和返回值都是User[]类型]===");
        User[] users = new User[]{
                new User(100, "KK100"),
                new User(101, "KK101")};
        Arrays.stream(userService.findUsers(users)).forEach(System.out::println);
    }


    @RequestMapping("/case13")
    public void case13() {
        System.out.println("Case 13. >>===[测试参数和返回值都是List类型]===");
        List<User> list = userService.getList(List.of(
                new User(100, "KK100"),
                new User(101, "KK101")));
        list.forEach(System.out::println);
    }

    @RequestMapping("/case14")
    public void case14() {
        System.out.println("Case 14. >>===[测试参数和返回值都是Map类型]===");
        Map<String, User> map = new HashMap<>();
        map.put("A200", new User(200, "KK200"));
        map.put("A201", new User(201, "KK201"));
        userService.getMap(map).forEach(
                (k, v) -> System.out.println(k + " -> " + v)
        );
    }
    @RequestMapping("/case15")
    public void case15() {
        System.out.println("Case 15. >>===[测试参数和返回值都是Boolean/boolean类型]===");
        System.out.println("userService.getFlag(false) = " + userService.getFlag(false));
    }
    @RequestMapping("/case16")
    public void case16() {
        System.out.println("Case 16. >>===[测试参数为long，返回值都是User类型]===");
        userService.findById(100L);
    }
    @RequestMapping("/case17")
    public void case17() {
        System.out.println("Case 17. >>===[模拟测试异常情况]===");
        userService.ex(true);
    }

    @RequestMapping("/case18")
    public void case18(@RequestParam("timeout") Integer timeout) {
        System.out.println("Case 18. >>===[模拟测试调用超时的情况]===");
        System.out.println(userService.find(timeout));
    }
    @RequestMapping("/case19")
    public void case19(@RequestParam("timeout") Integer timeout) {
        System.out.println("Case 19. >>===[测试通过Context跨消费者和提供者进行传参]===");
        String Key_Version = "rpc.version";
        String Key_Message = "rpc.message";
        RpcContext.setContextParameter(Key_Version, "v8");
        RpcContext.setContextParameter(Key_Message, "this is a test message");
        String version = userService.echoParameter(Key_Version);
        String message = userService.echoParameter(Key_Message);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Version + " -> " + version);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Message + " -> " + message);
        RpcContext.ContextParameters.get().clear();
    }
}
