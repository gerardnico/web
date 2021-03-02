package com.gerardnico.web.handler;


import com.gerardnico.web.Template;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An handler that puts a cookie visits for all visits
 */
public class CookieVisits implements Handler<RoutingContext> {
    public static Handler<RoutingContext> create() {
        return new CookieVisits();
    }

    @Override
    public void handle(RoutingContext ctx) {


        String visitCookieName = "visits";
        String cookieHeaders = ctx.request().getHeader("Cookie");
        List<Integer> cookiesValues = new ArrayList<>();
        if (cookieHeaders != null) {

            cookiesValues = Arrays.stream(cookieHeaders.split(";"))
                    .map(e -> e.split("="))
                    .filter(e -> e[0].trim().equals(visitCookieName))
                    .map(e -> Integer.valueOf(e[1]))
                    .collect(Collectors.toList());

        }

        Integer cookieValue;
        Integer cookieScopedValue = null;
        switch (cookiesValues.size()) {
            case 0:
                cookieValue = 0;
                break;
            case 1: // Should never happens but ...
                cookieValue = cookiesValues.get(0);
                break;
            case 2:
            default:
                cookieScopedValue = cookiesValues.get(0);
                cookieValue = cookiesValues.get(1);
                break;

        }

        /**
         * Default
         */
        cookieValue++;
        Cookie cookieDomain = Cookie.cookie(visitCookieName, String.valueOf(cookieValue));
        ctx.addCookie(cookieDomain);


        /**
         * Scoped
         */
        // Init
        if (ctx.request().path().equals("/cookie")) {
            if (cookieScopedValue == null) {
                cookieScopedValue = 0;
            }
        }
        // Processing
        if (cookieScopedValue != null) {
            cookieScopedValue++;
            Cookie cookie = Cookie.cookie(visitCookieName, String.valueOf(cookieScopedValue)).setPath(ctx.request().path());
            ctx.addCookie(cookie);
        }

        /**
         * Next
         */
        ctx.next();

    }
}
