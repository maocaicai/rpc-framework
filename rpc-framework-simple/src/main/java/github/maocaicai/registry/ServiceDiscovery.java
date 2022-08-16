package github.maocaicai.registry;

import github.maocaicai.extention.SPI;
import github.maocaicai.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @author maocaicai
 * since 2022/8/16 20:19
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 根据rpcServiceName获取远程服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcServiceName);
}
