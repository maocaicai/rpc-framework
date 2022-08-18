package github.maocaicai.loadBalance;

import github.maocaicai.extension.SPI;
import github.maocaicai.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 选择IP地址负载均衡策略
 * @author maocaicai
 * since 2022/8/16 20:38
 */
@SPI
public interface LoadBalance {
    /**
     * 从存在的服务ip地址列表中选择一个ip地址
     * @param serviceUrlList
     * @param rpcRequest
     * @return
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
