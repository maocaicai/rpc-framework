package github.maocaicai.loadBalance.loadBalancer;

import github.maocaicai.loadBalance.AbstractLoadBalance;
import github.maocaicai.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡实现类，随机选择一个地址的策略
 * @author maocaicai
 * since 2022/8/16 20:52
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
