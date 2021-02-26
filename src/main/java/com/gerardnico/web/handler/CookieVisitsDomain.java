package com.gerardnico.web.handler;


import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * An handler that puts a cookie for all visits
 */
public class CookieVisitsDomain implements Handler<RoutingContext> {
    public static Handler<RoutingContext> create() {
        return new CookieVisitsDomain();
    }

    @Override
    public void handle(RoutingContext ctx) {

        String visitCookieName = "visitsDomain";
        Cookie someCookie = ctx.getCookie(visitCookieName);

        long visits = 0;
        if (someCookie != null) {
            String cookieValue = someCookie.getValue();
            try {
                visits = Long.parseLong(cookieValue);
            } catch (NumberFormatException e) {
                visits = 0;
            }
        }

        // increment the tracking
        visits++;

        // Add a cookie - this will get written back in the response automatically
        ctx.addCookie(
                Cookie.cookie(visitCookieName, "" + visits)
        );

        ctx.next();

    }
}
