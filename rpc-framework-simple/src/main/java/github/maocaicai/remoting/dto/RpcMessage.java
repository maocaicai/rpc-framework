package github.maocaicai.remoting.dto;

import lombok.*;

/**
 * @author maocaicai
 * 私有协议定义
 * since 2022/8/16 10:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    /**
     *  *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
     *  *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
     *  *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
     *  *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
     *  *   |                                                                                                       |
     *  *   |                                         body                                                          |
     *  *   |                                                                                                       |
     *  *   |                                        ... ...                                                        |
     *  *   +-------------------------------------------------------------------------------------------------------+
     *  * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
     *  * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
     */
    private byte messageType;
    private byte codec;
    private byte compress;
    private int requestId;
    private Object data;
}
