package com.gerardnico.web.handler;

import com.gerardnico.web.ContextWrapper;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

public class Log implements Handler<RoutingContext>  {


    public static final String TAB = "  ";

    public static Handler<RoutingContext> create() {
        return new Log();
    }

    @Override
    public void handle(RoutingContext event) {
        System.out.println("Request received: "+event.request().method()+" "+event.request().absoluteURI());
        List<Map.Entry<String, String>> headers = ContextWrapper.getRequestHeaders(event);
        if (headers.size()==0){
            System.out.println("  No request headers");
        } else {
            System.out.println(TAB +"Request Headers:");
            for (Map.Entry<String,String> header : headers) {
                System.out.println(TAB+TAB+header.getKey()+" : "+header.getValue());
            }
        }
        event.next();
    }

}
