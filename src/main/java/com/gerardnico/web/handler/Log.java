package com.gerardnico.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class Log implements Handler<RoutingContext>  {


    public static Handler<RoutingContext> create() {
        return new Log();
    }

    @Override
    public void handle(RoutingContext event) {
        System.out.println("Request received: "+event.request().method()+" "+event.request().absoluteURI());
        event.next();
    }

}
