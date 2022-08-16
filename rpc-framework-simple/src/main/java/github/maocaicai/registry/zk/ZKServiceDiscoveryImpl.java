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
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServerName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServerName);
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServerName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("成功发现服务IP地址：[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
