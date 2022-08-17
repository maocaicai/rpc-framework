package github.maocaicai.provider;

import github.maocaicai.config.RpcServiceConfig;

/**
 * 存储并提供服务对象
 * @author maocaicai
 * since 2022/8/17 16:40
 */
public interface ServiceProvider {

    /**
     * 添加服务
     * @param rpcServiceConfig rpc 服务相关属性
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获得服务
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * 发布服务
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(RpcServiceConfig rpcServiceConfig);
}
