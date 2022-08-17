package github.maocaicai.registry.zk;

import github.maocaicai.enums.RpcErrorMessageEnum;
import github.maocaicai.exception.RpcException;
import github.maocaicai.extention.ExtensionLoader;
import github.maocaicai.loadBalance.LoadBalance;
import github.maocaicai.registry.ServiceDiscovery;
import github.maocaicai.registry.zk.util.CuratorUtils;
import github.maocaicai.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 服务发现实现类
 * @author maocaicai
 * since 2022/8/16 20:32
 */
@Slf4j
public class ZKServiceDiscoveryImpl implements ServiceDiscovery {

    /**
     * 实现查找IP地址策略接口
     */
    private final LoadBalance loadBalance;

    public ZKServiceDiscoveryImpl() {
        /**
         * ExtensionLoader:自适应扩展类，用于自动选择相应接口的实现类
         */
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    /**
     * 输入rpc请求，查找服务方法
     * @param rpcRequest  rpc请求
     * @return
     */
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        //获得服务器名
        String rpcServerName = rpcRequest.getRpcServiceName();
        //获得zk客户端对象
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //根据服务器名利用客户端获取服务器的子节点：即ip地址
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServerName);
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServerName);
        }
        //利用负载均衡策略选择子节点中的某个节点作为目标ip地址及端口
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("成功发现服务IP地址：[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
