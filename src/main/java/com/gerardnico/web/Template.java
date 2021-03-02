package com.gerardnico.web;


import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.impl.Http1xServerRequest;
import io.vertx.core.http.impl.Http1xServerResponse;
import io.vertx.core.http.impl.ServerCookie;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Template {


    public static final String SHARED_MAP_NAME = "templateSharedMapName";
    private final ThymeleafTemplateEngine engine;

    public void render(JsonObject data, RoutingContext ctx) {
        engine.render(data, "templates/page.html", res -> {
            if (res.succeeded()) {
                ctx.response().end(res.result());
            } else {
                ctx.fail(res.cause());
            }
        });
    }

    public void render(String page, RoutingContext ctx) {
        JsonObject data = new JsonObject()
                .put("host", ContextWrapper.getHost(ctx))
                .put("domain", ContextWrapper.getDomain(ctx))
                .put("path", ContextWrapper.getPath(ctx))
                .put("shortHost", ContextWrapper.getShortHost(ctx))
                .put("title", page + " (on " + ContextWrapper.getShortHost(ctx) + ")")
                .put("subtitle", "From " + ContextWrapper.getHost(ctx))
                .put("page", page)
                .put("cross_ref_1", "http://foo.example.com:8080/" + page)
                .put("cross_ref_2", "http://bar.example.com:8080/" + page)
                .put("cross_ref_3", "http://example.com:8080/" + page);
        ctx.vertx().sharedData().getLocalMap(SHARED_MAP_NAME).forEach((key, value) -> data.put(key.toString(), value.toString()));

        /**
         * Headers
         */
        List<Map.Entry<String, String>> headers = ctx.request().headers().entries()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        data.put("request_headers", headers);


        /**
         * Headers response
         */
        MultiMap headersResponse = new HeadersMultiMap();
        headersResponse.addAll(ctx.response().headers());
        /**
         * Add the cookies
         */
        try {
            Http1xServerResponse response = ((Http1xServerResponse) ctx.response());
            Field cookiesField = response.getClass().getDeclaredField("cookies");
            cookiesField.setAccessible(true);
            Object cookiesFieldValue = cookiesField.get(response);
            if (cookiesFieldValue!=null) {
                @SuppressWarnings("unchecked")
                Map<String, ServerCookie> cookies = (Map<String, ServerCookie>) cookiesFieldValue;//IllegalAccessException
                cookies.values().forEach(e -> {
                    headersResponse.add("set-cookie", e.encode());
                });
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        data.put("response_headers", headersResponse);

        render(data, ctx);

    }

    public Template(Vertx vertx) {
        // In order to use a Thymeleaf template we first need to create an engine
        engine = ThymeleafTemplateEngine.create(vertx);
//        TemplateEngine thymeleafEngine = engine.unwrap();
//        thymeleafEngine.addTemplateResolver(new ResourceResolver());


    }

    public static Template create(Vertx vertx) {
        return new Template(vertx);
    }
}
