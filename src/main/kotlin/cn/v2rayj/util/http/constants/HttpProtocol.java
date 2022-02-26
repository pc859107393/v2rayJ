package cn.v2rayj.util.http.constants;

public interface HttpProtocol {

    /**
     * http
     */
    String HTTP = "http://";

    /**
     * https
     */
    String HTTPS = "https://";

    /**
     * ws
     */
    String WS = "ws://";

    /**
     * wss
     */
    String WSS = "wss://";

    /**
     * 获取前缀
     *
     * @param url 地址
     * @return
     */
    static String getProtocol(String url) {
        if (url.startsWith(HTTP)) {
            return HTTP;
        }
        if (url.startsWith(HTTPS)) {
            return HTTPS;
        }
        if (url.startsWith(WS)) {
            return WS;
        }
        if (url.startsWith(WSS)) {
            return WSS;
        }
        return null;
    }

}
