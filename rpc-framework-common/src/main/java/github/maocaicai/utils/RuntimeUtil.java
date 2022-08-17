package github.maocaicai.utils;

/**
 * @author maocaicai
 * @createTime 2022/8/17 15:45
 */
public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     *
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
