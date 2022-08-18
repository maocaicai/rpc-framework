package github.maocaicai.registry;

import github.maocaicai.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 注册服务
 * @author maocaicai
 * since 2022/8/17 10:33
 */
@SPI
public interface ServiceRegistry {

    /**
     *@param rpcServerName, inetSocketAddress]
     *@return
     */
    void registerService(String rpcServerName, InetSocketAddress inetSocketAddress);
}
