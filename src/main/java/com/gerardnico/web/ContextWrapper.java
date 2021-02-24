package com.gerardnico.web;

import io.vertx.ext.web.RoutingContext;

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
}
