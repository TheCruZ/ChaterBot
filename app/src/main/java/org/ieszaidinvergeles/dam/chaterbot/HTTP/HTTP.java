package org.ieszaidinvergeles.dam.chaterbot.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTTP {

    public static final int METHOD_TYPE_GET = 0;
    public static final int METHOD_TYPE_POST = 1;
    public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded; ";
    public static final String CONTENT_TYPE_JSON = "application/json; ";

    public static final String USERAGENT_CHROME = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";


    private static void writeCrash(Exception ex) {
        try {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] errorStack = ex.getStackTrace();
            for (StackTraceElement s : errorStack) {
                sb.append(s.toString()).append("\r\n");
            }
          //  Files.write(new File("ErrorHTTPV2_" + System.nanoTime() + ".log").toPath(), sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
        }
    }

    public static HTTPR request(int requestType, String UA, String url, String data, String cType,
                                List<Cookie> cookies, List<Header> headers, boolean follow, Proxy proxy) {

        try {
            HttpURLConnection connection;
            StringBuilder response = new StringBuilder();
            int statusCode = -1;
            URL web;
            try {
                web = new URL(url);
                if (proxy != null) {
                    connection = (HttpURLConnection) web.openConnection(proxy);
                } else {
                    connection = (HttpURLConnection) web.openConnection();
                }
            } catch (IOException ex) {
                writeCrash(ex);
                return new HTTPR(-2);
            }

            try {
                String method;
                switch (requestType) {
                    case METHOD_TYPE_GET:
                        method = "GET";
                        break;
                    case METHOD_TYPE_POST:
                        method = "POST";
                        break;
                    default:
                        return new HTTPR(-3);
                }

                connection.setRequestMethod(method);
            } catch (ProtocolException ex) {
                writeCrash(ex);
                return new HTTPR(-4);
            }
            if (requestType == METHOD_TYPE_POST && data != null) {
                connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
            }

            if (cType != null) {
                connection.setRequestProperty("Content-Type", cType + " charset=UTF-8");
            }
            //connection.setRequestProperty("Connection", "close");
            if (UA != null) {
                connection.setRequestProperty("User-Agent", UA);
            }
            connection.setInstanceFollowRedirects(follow);

            if (cookies != null && cookies.size() > 0) {
                String value = "";
                for (Cookie c : cookies) {
                    value += c.toString();
                }
                connection.setRequestProperty("Cookie", value);
            }
            if (headers != null && headers.size() > 0) {
                for (Header h : headers) {
                    connection.setRequestProperty(h.getHeader(), h.getValue());
                }
            }

//            ((URLConnection) connection).setReadTimeout(60000);
//            ((URLConnection) connection).setConnectTimeout(60000);
            try {
                connection.setUseCaches(false);
                if (requestType == METHOD_TYPE_POST) {
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    wr.write(data);
                    wr.flush();
                }

                statusCode = connection.getResponseCode();
                List<Header> returnHeaders = null;
                try {
                    Map<String, List<String>> headerFields = connection.getHeaderFields();
                    returnHeaders = new ArrayList<>();

                    for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                        StringBuilder str = new StringBuilder();
                        for (String p : entry.getValue()) {
                            str.append(p).append(" ");
                        }
                        returnHeaders.add(new Header(entry.getKey()==null?"":entry.getKey(), str.toString()));
                    }

                    if (returnHeaders.size() <= 0) {
                        returnHeaders = null;
                    }
                } catch (Exception ex) {
                    writeCrash(ex);
                    returnHeaders = null;
                }

                InputStream is = null;
                try {
                    is = connection.getInputStream();
                } catch (IOException ex) {
                    try {
                        is = connection.getErrorStream();
                    } catch (Exception ex2) {
                    }
                }
                if (is != null) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));//iso-8859-1
                    int c;
                    while ((c = rd.read()) != -1) {
                        response.append((char) c);
                    }
                    rd.close();
                }
                return new HTTPR(statusCode, response.toString(), returnHeaders);
            } catch (IOException ex) {
                writeCrash(ex);
                return new HTTPR(-5);
            } finally {
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
            writeCrash(ex);
            return new HTTPR(-15);
        }
    }

    public static HTTPR getHtml(String UserAgent, String url, List<Cookie> cookies, List<Header> headers, boolean follow, Proxy proxy) {
        return request(METHOD_TYPE_GET, UserAgent, url, null, null, cookies, headers, follow, proxy);
    }

    public static HTTPR getHtml(String url, List<Cookie> cookies, List<Header> headers, boolean follow, Proxy proxy) {
        return getHtml(USERAGENT_CHROME, url, cookies, headers, follow, proxy);
    }

    public static HTTPR getHtml(String url, List<Cookie> cookies, List<Header> headers, boolean follow) {
        return getHtml(url, cookies, headers, follow, null);
    }

    public static HTTPR getHtml(String url, List<Cookie> cookies, List<Header> headers) {
        return getHtml(url, cookies, headers, true);
    }

    public static HTTPR getHtml(String url, List<Cookie> cookies) {
        return getHtml(url, cookies, null);
    }

    public static HTTPR getHtml(String url) {
        return getHtml(url, null);
    }

    public static HTTPR postHtml(String UserAgent, String url, String data, String cType, List<Cookie> cookies, List<Header> headers, boolean follow, Proxy proxy) {
        return request(METHOD_TYPE_POST, UserAgent, url, data, cType, cookies, headers, follow, proxy);
    }

    public static HTTPR postHtml(String url, String data, String cType, List<Cookie> cookies, List<Header> headers, boolean follow, Proxy proxy) {
        return postHtml(USERAGENT_CHROME, url, data, cType, cookies, headers, follow, proxy);
    }

    public static HTTPR postHtml(String url, String data, String cType, List<Cookie> cookies, List<Header> headers, boolean follow) {
        return postHtml(url, data, cType, cookies, headers, follow, null);
    }

    public static HTTPR postHtml(String url, String data, String cType, List<Cookie> cookies, List<Header> headers) {
        return postHtml(url, data, cType, cookies, headers, true);
    }

    public static HTTPR postHtml(String url, String data, String cType, List<Cookie> cookies) {
        return postHtml(url, data, cType, cookies, null);
    }

    public static HTTPR postHtml(String url, String data, String cType) {
        return postHtml(url, data, cType, null);
    }
}

