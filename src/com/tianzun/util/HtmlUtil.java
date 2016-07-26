package com.tianzun.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


/**
 * Utility class to espace HTML chars and other HTML related stuff can be added in the future.
 * 
 * @author jim
 */
public final class HtmlUtil {

	/**
     * 请求编码
     */
    private static String requestEncoding = "UTF-8";
    
    /**
     * 通过URL得到URL的内容。get方式
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static final String getContent(String path, String encoding) throws Exception {
        StringBuffer sb = new StringBuffer();
        URL url = new URL(path);

        URLConnection connection = url.openConnection();
        //connection.setConnectTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
        //connection.setReadTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
        if (StringUtils.isNotBlank(connection.getContentEncoding())) {
            encoding = connection.getContentEncoding();
        } else if (StringUtils.isNotBlank(connection.getContentType())) {
            String[] contentTypes = connection.getContentType().split(";");
            if (contentTypes.length >= 2) {
                encoding = contentTypes[1].split("=")[1];
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(connection.getInputStream()), encoding));
        String lineText;
        while ((lineText = br.readLine()) != null) {
            sb.append(lineText).append("\n");
        }
        br.close();
        return sb.toString();
    }
    
    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static String postContent(String reqUrl, Map parameters,
            String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter
                    .hasNext();)
            {
                Entry element = (Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),
                        requestEncoding));
                params.append("&");
            }

            if (params.length() > 0)
            {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
//            System.setProperty("sun.net.client.defaultConnectTimeout", String
//                    .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
//            System.setProperty("sun.net.client.defaultReadTimeout", String
//                    .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf=System.getProperty("line.separator");
            while (tempLine != null)
            {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public static final String clearHtml(Object obj) {
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            return StringUtils.EMPTY;
        }
        String text = obj.toString();
        String[] replaces = {"&nbsp;", "\t", "\n"};
        for (String str : replaces) {
            text = text.replaceAll(str, StringUtils.EMPTY);
        }
        return text.trim();
    }

    /**
     * 去掉字符串的html代码
     * 
     * @param htmlStr 字符串
     * @param max_count 
     * @return 结果
     */
    public static String htmlToStr(String htmlStr, int max_count) {
        String result = "";
        boolean flag = true;
        if (htmlStr == null) {
            return null;
        }
        char[] a = htmlStr.toCharArray();
        int length = a.length;
        for (int i = 0; i < length; i++) {
            if (a[i] == '<') {
                flag = false;
                continue;
            }
            if (a[i] == '>') {
                flag = true;
                continue;
            }
            if (flag == true) {
                result += a[i];
            }
        }
        if (result.toString().length() <= max_count * 1.1 && max_count >= 0) {
            return result.toString();
        }
        if (max_count >= 0) {
            return result.toString().substring(0, max_count) + "...";
        } else {
            return result.toString();
        }
    }

    public static String htmlToStr(String htmlStr) {
        return htmlToStr(htmlStr, -1);
    }

    /**
     * Escapes characters that have an HTML entity representation and returns new copy with entities escaped. It uses a
     * quick string to array mapping to avoid creating thousands of temporary objects.
     * 
     * @param nonHTML String containing the text to make HTML-safe
     * @return String containing new copy of string with entities escaped
     */
    public static final String escapeHTMLChars(String nonHTML) {
        String nonHTMLsrc = nonHTML;
        StringBuffer res = new StringBuffer();
        if (nonHTMLsrc == null) {
            nonHTMLsrc = "";
        }
        int l = nonHTMLsrc.length();
        int idx;
        char c;

        for (int i = 0; i < l; i++) {
            c = nonHTMLsrc.charAt(i);
            idx = entityMap.indexOf(c);
            if (idx == -1) {
                res.append(c);
            } else {
                res.append(quickEntities[idx]);
            }
        }
        return res.toString();
    }
    /**
     * These are probably HTML 3.2 level... as it looks like some HTML 4 entities are not present.
     */
    private static final String[][] ENTITIES = {
        /* We probably don't want to filter regular ASCII chars so we leave them out */
        {"&", "amp"}, {"<", "lt"}, {">", "gt"}, {"\"", "quot"},
        {"\u0083", "#131"}, {"\u0084", "#132"}, {"\u0085", "#133"}, {"\u0086", "#134"},
        {"\u0087", "#135"}, {"\u0089", "#137"}, {"\u008A", "#138"}, {"\u008B", "#139"},
        {"\u008C", "#140"}, {"\u0091", "#145"}, {"\u0092", "#146"}, {"\u0093", "#147"},
        {"\u0094", "#148"}, {"\u0095", "#149"}, {"\u0096", "#150"}, {"\u0097", "#151"},
        {"\u0099", "#153"}, {"\u009A", "#154"}, {"\u009B", "#155"}, {"\u009C", "#156"},
        {"\u009F", "#159"},
        {"\u00A0", "nbsp"}, {"\u00A1", "iexcl"}, {"\u00A2", "cent"},
        {"\u00A3", "pound"}, {"\u00A4", "curren"}, {"\u00A5", "yen"},
        {"\u00A6", "brvbar"}, {"\u00A7", "sect"}, {"\u00A8", "uml"},
        {"\u00A9", "copy"}, {"\u00AA", "ordf"}, {"\u00AB", "laquo"}, {"\u00AC", "not"},
        {"\u00AD", "shy"}, {"\u00AE", "reg"}, {"\u00AF", "macr"}, {"\u00B0", "deg"},
        {"\u00B1", "plusmn"}, {"\u00B2", "sup2"}, {"\u00B3", "sup3"},
        {"\u00B4", "acute"}, {"\u00B5", "micro"}, {"\u00B6", "para"},
        {"\u00B7", "middot"}, {"\u00B8", "cedil"}, {"\u00B9", "sup1"},
        {"\u00BA", "ordm"}, {"\u00BB", "raquo"}, {"\u00BC", "frac14"},
        {"\u00BD", "frac12"}, {"\u00BE", "frac34"}, {"\u00BF", "iquest"},
        {"\u00C0", "Agrave"}, {"\u00C1", "Aacute"}, {"\u00C2", "Acirc"},
        {"\u00C3", "Atilde"}, {"\u00C4", "Auml"}, {"\u00C5", "Aring"},
        {"\u00C6", "AElig"}, {"\u00C7", "CCedil"}, {"\u00C8", "Egrave"},
        {"\u00C9", "Eacute"}, {"\u00CA", "Ecirc"}, {"\u00CB", "Euml"},
        {"\u00CC", "Igrave"}, {"\u00CD", "Iacute"}, {"\u00CE", "Icirc"},
        {"\u00CF", "Iuml"},
        {"\u00D0", "ETH"}, {"\u00D1", "Ntilde"}, {"\u00D2", "Ograve"},
        {"\u00D3", "Oacute"}, {"\u00D4", "Ocirc"}, {"\u00D5", "Otilde"},
        {"\u00D6", "Ouml"}, {"\u00D7", "times"}, {"\u00D8", "Oslash"},
        {"\u00D9", "Ugrave"}, {"\u00DA", "Uacute"}, {"\u00DB", "Ucirc"},
        {"\u00DC", "Uuml"}, {"\u00DD", "Yacute"}, {"\u00DE", "THORN"},
        {"\u00DF", "szlig"},
        {"\u00E0", "agrave"}, {"\u00E1", "aacute"}, {"\u00E2", "acirc"},
        {"\u00E3", "atilde"}, {"\u00E4", "auml"}, {"\u00E5", "aring"},
        {"\u00E6", "aelig"}, {"\u00E7", "ccedil"}, {"\u00E8", "egrave"},
        {"\u00E9", "eacute"}, {"\u00EA", "ecirc"}, {"\u00EB", "euml"},
        {"\u00EC", "igrave"}, {"\u00ED", "iacute"}, {"\u00EE", "icirc"},
        {"\u00EF", "iuml"},
        {"\u00F0", "eth"}, {"\u00F1", "ntilde"}, {"\u00F2", "ograve"},
        {"\u00F3", "oacute"}, {"\u00F4", "ocirc"}, {"\u00F5", "otilde"},
        {"\u00F6", "ouml"}, {"\u00F7", "divid"}, {"\u00F8", "oslash"},
        {"\u00F9", "ugrave"}, {"\u00FA", "uacute"}, {"\u00FB", "ucirc"},
        {"\u00FC", "uuml"}, {"\u00FD", "yacute"}, {"\u00FE", "thorn"},
        {"\u00FF", "yuml"}
    };
    private static String entityMap;
    private static String[] quickEntities;
    

    static {
        // Initialize some local mappings to speed it all up
        int l = ENTITIES.length;
        StringBuffer temp = new StringBuffer();

        quickEntities = new String[l];
        for (int i = 0; i < l; i++) {
            temp.append(ENTITIES[i][0]);
            quickEntities[i] = "&" + ENTITIES[i][1] + ";";
        }
        entityMap = temp.toString();
    }
    private final static NodeFilter nfilter = new NodeFilter() {

        public boolean accept(Node arg0) {
            return true;
        }
    };
    


    /**
     * 生成预览内容
     * 
     * @param html
     * @param max_count
     * @return String
     */
    public static String preview(String html, int max_count) {
        if (html.length() <= max_count * 1.1) {
            return html;
        }
        Parser parser = new Parser();
        StringBuffer prvContent = new StringBuffer();
        String ENC_UTF_8 = "UTF-8";
        try {
            parser.setEncoding(ENC_UTF_8);
            parser.setInputHTML(html);
            NodeList nodes = parser.extractAllNodesThatMatch(nfilter);
            Node node = null;
            for (int i = 0; i < nodes.size(); i++) {
                if (prvContent.length() >= max_count) {
                    if (node instanceof TagNode) {
                        TagNode tmp_node = (TagNode) node;
                        boolean isEnd = tmp_node.isEndTag();
                        if (!isEnd) {
                            prvContent.setLength(prvContent.length() - tmp_node.getText().length() - 2);
                        }
                    }
                    // 补齐所有未关闭的标签
                    Node parent = node;
                    // System.out.println("current node is .// "
                    // + parent.getText());
                    do {
                        parent = parent.getParent();
                        // System.out.println("parent = " + parent);
                        if (parent == null) {
                            break;
                        }
                        if (!(parent instanceof TagNode)) {
                            continue;
                        // System.out.println("Parent node is no ended. "
                        // + parent.getText());
                        }
                        prvContent.append(((TagNode) parent).getEndTag().toHtml());
                    } while (true);
                    break;
                }
                node = nodes.elementAt(i);
                if (node instanceof TagNode) {
                    TagNode tag = (TagNode) node;
                    prvContent.append('<');
                    prvContent.append(tag.getText());
                    prvContent.append('>');
                // System.out.println("TAG测试: " + '<' + tag.getText() + '>');
                } else if (node instanceof TextNode) {
                    int space = max_count - prvContent.length();
                    if (space > 10) {
                        TextNode text = (TextNode) node;
                        if (text.getText().length() < 10) {
                            prvContent.append(text.getText());
                        } else {
                            prvContent.append(StringUtils.abbreviate(text.getText(), max_count - prvContent.length()));
                        // System.out.println("TEXT测试: " + text.getText());
                        }
                    }
                }
            }
            return prvContent.toString();
        } catch (ParserException e) {
            e.printStackTrace();
        } finally {
            parser = null;
        }
        return html;
    }
    
    

   
}
