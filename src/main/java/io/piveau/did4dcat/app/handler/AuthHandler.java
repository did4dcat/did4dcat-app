package io.piveau.did4dcat.app.handler;

import io.vertx.ext.web.RoutingContext;

public class AuthHandler {

    public void checkAuth(RoutingContext context) {
        if(context.session().get("user") != null) {
            context.next();
        } else {
            context.redirect("/");
        }
    }

}
