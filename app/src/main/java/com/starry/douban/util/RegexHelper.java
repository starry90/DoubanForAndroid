package com.starry.douban.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by Starry
 * @since 2021/7/2.
 */

public class RegexHelper {

    public static Matcher matcherBracket(CharSequence content) {
        Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
        return pattern.matcher(content);
    }

    /**
     * (?<=exp) 零宽度向后匹配
     * <p>
     * 例如只要求输出2后面的abc，那么就可以用(?<=2)来进行锚点标定，匹配出其后面的字符串。
     *
     * @param content 匹配内容
     * @param start   开始字符
     * @param end     结束字符
     * @return Matcher
     */
    public static Matcher matcher(CharSequence content, String start, String end) {
        Pattern pattern = Pattern.compile(StringUtils.format("(?<=%s)[^%s]+", start, end));
        return pattern.matcher(content);
    }
}
