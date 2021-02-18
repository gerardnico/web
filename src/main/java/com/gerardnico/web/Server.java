package com.gerardnico.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

import java.util.Locale;


public class Server extends AbstractVerticle {

    public static final String SEP = ".";
    public static final String TOP_DOMAIN = "example.com";
    public static final String FOO_DOMAIN = "foo.example.com";
    public static final String BAR_DOMAIN = "bar.example.com";

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        //Runner.runExample(Server.class);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
    }

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        // In order to use a Thymeleaf template we first need to create an engine
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);

        // Enable multipart form data parsing
        router.route().handler(BodyHandler.create());

        // on every path
        router.route("/cookie").handler(ctx -> {


            // domain name host information
            String host = ctx.request().getHeader("host");
            String originFromLabel = host.substring(0, host.indexOf("."));
            if (originFromLabel.equals("example")) {
                originFromLabel = "top";
            }


            /*
              Trying to send a cookie from a domain to another
             */
            String action = ctx.request().getParam("action");
            if (action != null) {
                String lastPart = action.substring(action.lastIndexOf(" ") + 1).toLowerCase(Locale.ROOT);
                String cookieName = originFromLabel + "2" + lastPart;
                switch (lastPart) {
                    case "foo":
                        ctx.addCookie(Cookie.cookie(cookieName, cookieName).setDomain(FOO_DOMAIN));
                        System.out.println("The cookie " + cookieName + " was set for the domain " + FOO_DOMAIN);
                        break;
                    case "bar":
                        ctx.addCookie(Cookie.cookie(cookieName, cookieName).setDomain(BAR_DOMAIN));
                        System.out.println("The cookie " + cookieName + " was set for the domain " + BAR_DOMAIN);
                        break;
                    case "top":
                        ctx.addCookie(Cookie.cookie(cookieName, cookieName).setDomain(TOP_DOMAIN));
                        System.out.println("The cookie " + cookieName + " was set for the domain " + TOP_DOMAIN);
                        break;
                    default:
                        ctx.response().setStatusCode(500).setStatusMessage("The action " + lastPart + " is unknown");
                }
            }


            String sharedCookieName = "shared";
            ctx.addCookie(
                    /**
                     * Cookie value should not have any space
                     */
                    Cookie.cookie(sharedCookieName, originFromLabel).setDomain(TOP_DOMAIN)
            );


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
                    Cookie.cookie(visitCookieName, "" + visits)
            );


            /*
             * Template
             */
            JsonObject data = new JsonObject()
                    .put("host", host);
            engine.render(data, "templates/cookie.html", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        // Serve the static resources
        router.route().handler(
                StaticHandler
                        .create()
                        .setCachingEnabled(false)
        );

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Server started on port " + ar.result().actualPort());
                    } else {
                        ar.cause().printStackTrace();
                    }
                });
    }
}
