package github.maocaicai.remoting.transport.netty.client;

import github.maocaicai.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于存放未被服务端处理的请求，一个map集合，key是请求id，value是请求对象，即CompletableFuture
 * @author maocaicai
 * since 2022/8/17 10:43
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURE = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURE.put(requestId, future);
    }
    /**
     *在服务器处理完后，会返回RpcResponse，所以参数为rpcResponse
     * 利用rpcResponse的requestId将存储未准备好的map中存储的相应对象删除
     *@param rpcResponse
     */
    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURE.remove(rpcResponse.getRequestId());
        if (future != null) {
            //利用complete()可以将response结束，并可用get()获得
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }

    }
}
