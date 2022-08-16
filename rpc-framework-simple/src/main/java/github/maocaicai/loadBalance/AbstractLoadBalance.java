package github.maocaicai.loadBalance;

import github.maocaicai.remoting.dto.RpcRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 加载负载均衡策略的抽象类
 * @author maocaicai
 * since 2022/8/16 20:44
 */
public abstract class AbstractLoadBalance implements LoadBalance{
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest);
}
