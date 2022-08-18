package github.maocaicai.remoting.transport;


import github.maocaicai.extension.SPI;
import github.maocaicai.remoting.dto.RpcRequest;

/**
 * 发送请求接口
 */
@SPI
public interface RpcRequestTransport {

    /**
     * send rpc request to server and get result
     * @param rpcRequest 消息请求
     * @return 服务器响应
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
