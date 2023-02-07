package io.piveau.did4dcat.app.services;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

public class TemplateService {

    private final FreeMarkerTemplateEngine engine;

    public TemplateService(FreeMarkerTemplateEngine engine) {
        this.engine = engine;
    }

    public void render(RoutingContext context, String template) {
        engine.render(context.data(), getTemplateFile(template), res -> {
            if (res.succeeded()) {
                context.response().end(res.result());
            } else {
                context.fail(res.cause());
            }
        });
    }

    private String getTemplateFile(String template) {
        return "templates/" + template + ".ftl";
    }
}
