package com.gerardnico.web.handler;


import com.gerardnico.web.Template;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

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
        Cookie cookie = Cookie.cookie(visitCookieName, "" + visits);
        if (ctx.request().path().equals("/cookie")) {
            cookie.setPath(ctx.request().path());
        } else {
            /**
             * Because Javascript cannot see this cookie
             */
            LocalMap<Object, Object> map = ctx.vertx().sharedData().getLocalMap(Template.SHARED_MAP_NAME);
            map.put("visitsCookieDomainScoped",visits);
        }
        ctx.addCookie(cookie);


        ctx.next();

    }
}
