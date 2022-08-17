package github.maocaicai.registry;

import java.net.InetSocketAddress;

/**
 * 注册服务
 * @author maocaicai
 * since 2022/8/17 10:33
 */
public interface ServiceRegistry {

    /**
     *@param rpcServerName, inetSocketAddress]
     *@return
     */
    void registerService(String rpcServerName, InetSocketAddress inetSocketAddress);
}
