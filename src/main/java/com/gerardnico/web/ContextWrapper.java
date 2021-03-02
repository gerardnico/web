package com.gerardnico.web;

import io.vertx.core.http.impl.Http1xServerResponse;
import io.vertx.core.http.impl.ServerCookie;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextWrapper {

    public static String getHost(RoutingContext ctx) {
        return ctx.request().getHeader("host");
    }

    public static String getShortHost(RoutingContext ctx) {
        return getShortHost(getHost(ctx));
    }

    public static String getShortHost(String host) {
        String shortHost = host;
        if (host.contains(".")) {
            shortHost = host.substring(0, host.indexOf("."));

        } else if (host.contains(":")) {
            shortHost = host.substring(0, host.indexOf(":"));
            return shortHost;
        }
        if (shortHost.equals("example")) {
            shortHost = "apex";
        }
        return shortHost;
    }

    public static String getDomain(RoutingContext ctx) {
        String domain = getHost(ctx);
        if (domain.contains(":")) {
            domain = domain.substring(0, domain.indexOf(":"));
        }
        return domain;
    }

    public static String getPath(RoutingContext ctx) {
        return ctx.request().path();
    }

    /**
     * Headers can be present twice with the same name
     *
     * @param ctx
     * @return
     */
    public static HeadersMultiMap getRequestHeadersMultiMap(RoutingContext ctx) {
        HeadersMultiMap headersResponse = new HeadersMultiMap();
        headersResponse.addAll(ctx.request().headers());
        return headersResponse;

    }

    public static HeadersMultiMap getResponseHeadersAsMultiMap(RoutingContext ctx) {
        HeadersMultiMap headersResponse = new HeadersMultiMap();
        headersResponse.addAll(ctx.response().headers());
        /**
         * Add the cookies
         */
        try {
            Http1xServerResponse response = ((Http1xServerResponse) ctx.response());
            Field cookiesField = response.getClass().getDeclaredField("cookies");
            cookiesField.setAccessible(true);
            Object cookiesFieldValue = cookiesField.get(response);
            if (cookiesFieldValue != null) {
                @SuppressWarnings("unchecked")
                Map<String, ServerCookie> cookies = (Map<String, ServerCookie>) cookiesFieldValue;//IllegalAccessException
                cookies.values().forEach(e -> {
                    headersResponse.add("set-cookie", e.encode());
                });
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return headersResponse;
    }

    public static List<Map.Entry<String,String>> getResponseHeaders(RoutingContext ctx) {
        return MultiMapToList(getResponseHeadersAsMultiMap(ctx));
    }

    /**
     *
     * @param multimap
     * @return a sorted list
     */
    private static List<Map.Entry<String,String>> MultiMapToList(HeadersMultiMap multimap) {
        return multimap.entries()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public static List<Map.Entry<String,String>> getRequestHeaders(RoutingContext ctx) {
        return MultiMapToList(getRequestHeadersMultiMap(ctx));
    }
}
