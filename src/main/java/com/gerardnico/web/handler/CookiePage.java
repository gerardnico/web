package com.gerardnico.web.handler;


import com.gerardnico.web.ContextWrapper;
import com.gerardnico.web.Template;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * An handler that render
 */
public class CookiePage implements Handler<RoutingContext> {

    private final Template template;

    public static final String VISIT_COOKIE_NAME = "visits";

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

        template.render("cookie", ctx);

    }
}
