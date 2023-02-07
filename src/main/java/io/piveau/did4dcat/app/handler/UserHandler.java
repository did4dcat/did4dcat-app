package io.piveau.did4dcat.app.handler;

import io.piveau.did4dcat.app.did.DIDManager;
import io.piveau.did4dcat.app.services.TemplateService;
import io.piveau.did4dcat.app.models.User;
import io.vertx.ext.web.RoutingContext;
import org.hyperledger.fabric.gateway.Identities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TemplateService templateService;

    public UserHandler(TemplateService templateService) {
        this.templateService = templateService;
    }

    public void getLogin(RoutingContext context) {
        templateService.render(context, "user/login");
    }

    public void postLogin(RoutingContext context) {
        String userIdentity = context.request().getFormAttribute("userIdentity");
        if(userIdentity.isEmpty()) {
            context.put("error", "Please enter your identity!");
            templateService.render(context, "user/login");
        } else {
            User user;
            try {
                user = new User(userIdentity);
                // LOGGER.info("User Private Key " + Identities.toPemString(user.getIdentity().getPrivateKey()));
                context.session().put("user", user);
                context.redirect("/");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                context.put("error", "The identity is not correct");
                templateService.render(context, "user/login");
            }
        }
    }

    public void getLogout(RoutingContext context) {
        context.session().destroy();
        context.redirect("/");
    }

    public void getDID(RoutingContext context) {
        User user = context.session().get("user");
        DIDManager didManager =  new DIDManager();
        context.put("did", didManager.buildDataProviderDIDDocument(user).encodePrettily());
        templateService.render(context, "user/did");
    }
}
