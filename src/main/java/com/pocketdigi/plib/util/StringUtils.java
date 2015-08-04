package com.pocketdigi.plib.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 *
 * @author fhp
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 检测字符串中是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * ascii转中文
     *
     * @param ascii 5位数字
     * @return
     */
    private static String ascii2chinese(String ascii) {
        return String.valueOf((char) Integer.parseInt(ascii));
    }

    /**
     * 把Html.toHtml转换成的html中的ascii码转换回中文
     *
     * @return
     */
    public static String html2chinese(String string) {
        String patternStr = "&#([\\d]{5});";
        Pattern p = Pattern.compile(patternStr);
        Matcher matcher = p.matcher(string);
        while (matcher.find()) {
            string = string.replace(matcher.group(0), ascii2chinese(matcher.group(1)));
        }
        return string;
    }

    /**
     * 从文件路径或URL获取文件名，带扩展名
     *
     * @param pathOrUrl
     * @return 文件名
     */
    public static String getFileName(String pathOrUrl) {
        Pattern p = Pattern.compile("/([^/]*?\\.[^/]*?)$");
        Matcher matcher = p.matcher(pathOrUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 从文件路径或URL获取扩展名,不带.
     *
     * @return
     */
    public static String getExtension(String pathOrUrl) {
        Pattern p = Pattern.compile("\\.([^/]*?)[$|\\?]");
        Matcher matcher = p.matcher(pathOrUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 获取不带扩展名的文件名,支持URL或本地路径
     *
     * @param pathOrUrl
     * @return
     */
    public static String getFileNameWithoutExt(String pathOrUrl) {
        Pattern p = Pattern.compile("/([^/]*?)\\.[^/]*?$");
        Matcher matcher = p.matcher(pathOrUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 是否是手机号码
     *
     * @param str 1开头，后跟10位数字
     * @return
     */
    public static boolean isCellPhoneNumber(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8][\\d]{9}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验是否是中国车牌,字母 必须大写
     *
     * @param plateNumber
     * @return
     */
    public static boolean isChinesePlateNumber(String plateNumber) {
        if (TextUtils.isEmpty(plateNumber))
            return false;
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
        Matcher matcher = pattern.matcher(plateNumber);
        return matcher.find();
    }

    /**
     * 添加省略号
     *
     * @param src       原字符串
     * @param maxLength 最大长度，超过此长度截掉
     * @return
     */
    public static String addEllipsis(String src, int maxLength) {
        if (src.length() <= maxLength)
            return src;
        return src.substring(0, maxLength) + "...";
    }

    public static String hidePhone(String phone) {
        return phone.substring(0,3)+"****"+phone.substring(7,11);
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}
