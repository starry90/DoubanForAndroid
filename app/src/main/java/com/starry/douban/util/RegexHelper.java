package com.starry.douban.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by Starry
 * @since 2021/7/2.
 */

public class RegexHelper {

    public static Matcher matcherBracket(CharSequence content){
        Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
        return pattern.matcher(content);
    }
}
