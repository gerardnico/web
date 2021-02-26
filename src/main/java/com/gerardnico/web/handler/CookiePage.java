package com.gerardnico.web.handler;


import com.gerardnico.web.ContextWrapper;
import com.gerardnico.web.Template;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * An handler that puts a cookie for all visits
 */
public class CookiePage implements Handler<RoutingContext> {

    private final Template template;

    public CookiePage(Template template) {
        this.template = template;
    }

    public static Handler<RoutingContext> create(Template template) {

        return new CookiePage(template);
    }

    @Override
    public void handle(RoutingContext ctx) {


        /**
         * Trying to send a cookie from a domain to another
         */
        String action = ctx.request().getParam("action");
        if (action != null) {
            String targetDomain = ctx.request().getParam("domain");
            String shortTargetDomain = ContextWrapper.getShortHost(targetDomain);
            String sourceShortHost = ContextWrapper.getShortHost(ctx);
            String cookieName = sourceShortHost + "2" + shortTargetDomain;
            ctx.addCookie(Cookie.cookie(cookieName, cookieName).setDomain(targetDomain));
        }

        /**
         * Cookie value should not have any space
         */
//        String sharedCookieName = "shared";
//        ctx.addCookie(
//
//                Cookie.cookie(sharedCookieName, shortHost).setDomain(TOP_DOMAIN)
//        );


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
        ctx.addCookie(
                Cookie.cookie(visitCookieName, "" + visits).setPath("/cookie")
        );

        template.render("cookie", ctx);

    }
}
