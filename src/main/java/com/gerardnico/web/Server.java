package com.gerardnico.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
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

        Template template = Template.create(vertx);

        // Enable multipart form data parsing
        router.route().handler(BodyHandler.create());

        // Add a counter on all pages
        router.route().handler(CookieVisitsDomain.create());

        // Cookie Page
        router.route("/cookie").handler(CookiePage.create(template));

        // Serve the index
        router.route("/").handler(ctx->{
            template.render("index",ctx);
        });

        router.errorHandler(500, rc -> {
            System.err.println("Handling failure");
            Throwable failure = rc.failure();
            if (failure != null) {
                failure.printStackTrace();
            }
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
