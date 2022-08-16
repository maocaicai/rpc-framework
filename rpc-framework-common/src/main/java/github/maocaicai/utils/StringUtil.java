package github.maocaicai.utils;

/**
 * String工具类,判断String是否为空
 * @author maocaicai
 * since 2022/8/16 21:19
 */
public class StringUtil {
    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) return true;
        for (int i = 0; i < s.length(); ++ i) {
            //Character.isWhitespace():如果字符是Java空白字符，则为true;否则错误
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
