package io.piveau.did4dcat.app.handler;

import io.piveau.did4dcat.app.Constants;
import io.piveau.did4dcat.app.models.User;
import io.piveau.did4dcat.app.services.FabricGateway;
import io.piveau.did4dcat.app.services.FabricUserManagement;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

import java.util.Arrays;
import java.util.List;

import org.hyperledger.fabric.gateway.X509Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final FreeMarkerTemplateEngine engine;
    private final FabricUserManagement fabricUserManagement;
    private final FabricGateway fabricGateway;

    public IndexHandler(FreeMarkerTemplateEngine engine, JsonObject config) {
        this.engine = engine;
        this.fabricUserManagement = new FabricUserManagement(config);
        this.fabricGateway = new FabricGateway(config);
    }

    public void handleIndex(RoutingContext context) {
        context.response().putHeader("Content-Type", "text/html");
        context.data().put("page", "home");
        renderTemplate(context);
    }

    public void handleAction(RoutingContext context) {
        String action = context.pathParam("action");
        User user = context.session().get("user");
        switch (action) {
            case "initadmin":
                context.data().put("message", fabricUserManagement.initAdmin().encodePrettily());
                break;
            case "createuser":
                try {
                    context.data().put("message", fabricUserManagement.createUser().encodePrettily());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                break;
            case "code":
                Boolean submit = false;
                String submitTransaction = context.request().getFormAttribute("submit_transaction");
                if(submitTransaction != null) {
                    submit = true;
                }
                String transaction = context.request().getFormAttribute("transaction");
                LOGGER.info(transaction);
                context.data().put("message", invokeCC(user.getIdentity(),
                        new JsonObject(transaction), submit).encodePrettily());
                break;
            case "command":
                String command = context.request().getFormAttribute("command");
                String args = context.request().getFormAttribute("args");
                context.data().put("message", invokeCC(user.getIdentity(), command, args).encodePrettily());
                break;
            default:
                context.data().put("message", "Unknown Action");
                break;
        }
        context.data().put("page", "home");
        renderTemplate(context);
    }

    private JsonObject invokeCC(X509Identity identity) {
        fabricGateway.connect(identity);
        return fabricGateway.executeContract();
    }

    private JsonObject invokeCC(X509Identity identity, JsonObject transaction, Boolean submit) {
        fabricGateway.connect(identity);
        return fabricGateway.executeContract(transaction, submit);
    }

    private JsonObject invokeCC(X509Identity identity, String command, String args) {
        fabricGateway.connect(identity);
        String[] argArray = args.split(" ");
        List<String> argList = Arrays.asList(argArray);
        return fabricGateway.executeContract(command, argList);
    }

    private void renderTemplate(RoutingContext context) {
        context.session().put("page", "home");
        engine.render(context.data(), "templates/index.ftl", res -> {
            if (res.succeeded()) {
                context.response().end(res.result());
            } else {
                context.fail(res.cause());
            }
        });
    }

}
