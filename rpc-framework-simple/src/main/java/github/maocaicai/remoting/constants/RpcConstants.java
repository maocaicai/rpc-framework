package github.maocaicai.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author maocaicai
 * 私有协议的字段常量
 * since 2022/8/16 10:46
 */
public class RpcConstants {
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

    public static final byte[] MAGIC_NUMBER = {(byte) 'c', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final byte VERSION = 1;
    public static final byte total_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
