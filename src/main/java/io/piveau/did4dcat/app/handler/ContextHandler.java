package io.piveau.did4dcat.app.handler;

import io.piveau.did4dcat.app.models.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

public class ContextHandler {

    public ContextHandler() {
    }

    public void setContext(RoutingContext context) {
        Session session = context.session();
        if(session.get("user") != null) {
            context.put("user", (User) session.get("user"));
        }
        context.next();
    }
}
