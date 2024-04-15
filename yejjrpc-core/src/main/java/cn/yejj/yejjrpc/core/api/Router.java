package cn.yejj.yejjrpc.core.api;

import java.util.List;

public interface Router<T> {
    List<T> route(List<T> providers);

    Router Default = providers -> providers;
}
