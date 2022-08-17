package github.maocaicai.provider.impl;


import github.maocaicai.config.RpcServiceConfig;
import github.maocaicai.enums.RpcErrorMessageEnum;
import github.maocaicai.exception.RpcException;
import github.maocaicai.extention.ExtensionLoader;
import github.maocaicai.provider.ServiceProvider;
import github.maocaicai.registry.ServiceRegistry;
import github.maocaicai.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maocaicai
 * since 2022/8/17 16:40
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    /**
     * 添加服务
     * @param rpcServiceConfig rpc 服务相关属性
     */
    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        /**
         * 先获取当前配置的rpc服务名，如果已经注册的服务集合registeredService中存在了，那么直接退出
         * 如果不存在，则向registeredService集合中添加当前rpcServiceName
         * 然后放入serviceMap中
         */
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        /**
         * 根据serviceMap.get获取此rpc服务名对应服务，直接返回该服务
         */
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        /**
         * 发布服务，获取当前的host地址，然后addService添加到当前registeredService集合
         * 利用serviceRegistry注册类注册当前的服务配置名，并将对应host、port作为名字一部分
         */
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
