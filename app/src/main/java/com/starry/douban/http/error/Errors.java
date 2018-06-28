package com.starry.douban.http.error;

/**
 * @author Starry Jerry
 * @since 2017/10/29.
 */

public class Errors {

    public class Code {
        public final static int UNKNOWN_ERROR = 10001;
        public final static int NETWORK_UNAVAILABLE = 10011;
        public final static int RESPONSE_PARSE_ERROR = 10012;
        public final static int NULL_EXCEPTION = 10013;
    }

    public class Message {
        public final static String UNKNOWN_ERROR = "未知错误";
        public final static String SERVER_ERROR = "服务器错误";
        public final static String NETWORK_UNAVAILABLE = "连接失败，请检查你的网络设置";
        public final static String RESPONSE_PARSE_ERROR = "数据解析错误";
    }
}
