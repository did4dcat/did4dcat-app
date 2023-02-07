package io.piveau.did4dcat.app.handler;

import io.piveau.did4dcat.app.Constants;
import io.piveau.did4dcat.app.models.User;
import io.piveau.did4dcat.app.services.FabricGateway;
import io.piveau.did4dcat.app.services.TemplateService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.X509Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DIDHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TemplateService templateService;
    private final FabricGateway fabricGateway;
    private Wallet wallet;

    public DIDHandler(JsonObject config, TemplateService templateService, FabricGateway fabricGateway) {
        try {
            this.wallet = Wallets.newFileSystemWallet(Paths.get(config.getString(Constants.WALLET_DIR)));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        this.templateService = templateService;
        this.fabricGateway = fabricGateway;
    }

    public void getCreateDID(RoutingContext context) {
        context.data().put("page", "did/create");
        templateService.render(context, "did/create");
    }

    public void postCreateDID(RoutingContext context) {
        String did = context.request().getFormAttribute("did");
        String url = context.request().getFormAttribute("url");
        String hash = context.request().getFormAttribute("hash");
        context.data().put("page", "did/create");
        if(did.isBlank()) {
            context.put("error", "Please enter a DID!");
            templateService.render(context, "did/create");
        } else if(url.isBlank()) {
            context.put("error", "Please enter an URL!");
            templateService.render(context, "did/create");
        } else {
            User user = context.session().get("user");
            fabricGateway.connect(user.getIdentity());
            fabricGateway.createDID(did, url, user, hash).onSuccess(result -> {
                context.put("did", result.getString("did"));
                context.put("didDocument", new JsonObject(result.getString("didDocument")).encodePrettily());
                templateService.render(context, "did/create_success");
            }).onFailure(err -> {
                context.put("error", err.getMessage());
                templateService.render(context, "did/create");
            });
        }
    }

    public void getUpdateDID(RoutingContext context) {
        context.data().put("page", "did/update");
        templateService.render(context, "did/update");
    }

    public void postUpdateDID(RoutingContext context) {
        String did = context.request().getFormAttribute("did");
        String url = context.request().getFormAttribute("url");
        String hash = context.request().getFormAttribute("hash");
        context.data().put("page", "did/update");
        if(did.isBlank()) {
            context.put("error", "Please enter a DID!");
            templateService.render(context, "did/update");
        } else if(url.isBlank()) {
            context.put("error", "Please enter an URL!");
            templateService.render(context, "did/update");
        } else {
            User user = context.session().get("user");
            fabricGateway.connect(user.getIdentity());
            fabricGateway.updateDID(did, url, user, hash).onSuccess(result -> {
                context.put("did", result.getString("did"));
                context.put("didDocument", new JsonObject(result.getString("didDocument")).encodePrettily());
                templateService.render(context, "did/update_success");
            }).onFailure(err -> {
                context.put("error", err.getMessage());
                templateService.render(context, "did/update");
            });
        }
    }

    public void getListDIDs(RoutingContext context) {
        context.data().put("page", "did/list");
        User user = context.session().get("user");
        fabricGateway.connect(user.getIdentity());
        fabricGateway.listDIDs().onSuccess(result -> {
            List<String> didList = new ArrayList<>();
            for (Object obj : result) {
                JsonObject jObj = (JsonObject) obj;
                didList.add(new JsonObject(jObj.getString("didDocument")).encodePrettily());
            }
            context.put("didList", didList);
            templateService.render(context, "did/list_success");
        }).onFailure(err -> {
            context.put("error", err.getMessage());
            templateService.render(context, "did/list");
        });
    }

    public void postListDIDs(RoutingContext context) {
        context.data().put("page", "did/list");
        User user = context.session().get("user");
        fabricGateway.connect(user.getIdentity());
        fabricGateway.listDIDs().onSuccess(result -> {
            List<String> didList = new ArrayList<>();
            for (Object obj : result) {
                JsonObject jObj = (JsonObject) obj;
                StringWriter did = new StringWriter();
                did.append("<p>").append(jObj.getString("did")).append(" - ")
                        .append(jObj.getString("currentURL")).append("</p>");
                didList.add(did.toString());
            }
            context.put("didList", didList);
            templateService.render(context, "did/list_success");
        }).onFailure(err -> {
            context.put("error", err.getMessage());
            templateService.render(context, "did/list");
        });
    }

    public void getResolveDID(RoutingContext context) {
        context.data().put("page", "did/resolve");
        templateService.render(context, "did/resolve");
    }

    public void postResolveDID(RoutingContext context) {
        String did = context.request().getFormAttribute("did");
        context.data().put("page", "did/resolve");
        if(did.isBlank()) {
            context.put("error", "Please enter a DID!");
            templateService.render(context, "did/resolve");
        } else {
            try {
                X509Identity adminIdentity = (X509Identity) wallet.get("admin");
                fabricGateway.connect(adminIdentity);
                fabricGateway.resolveDID(did).onSuccess(result -> {
                    JsonObject didDocument = new JsonObject(result.getString("didDocument"));
                    context.put("url", didDocument.getJsonObject("url").getString("@id"));
                    context.put("didDocument", didDocument.encodePrettily());
                    templateService.render(context, "did/resolve_success");
                }).onFailure(err -> {
                    context.put("error", err.getMessage());
                    templateService.render(context, "did/resolve");
                });
            } catch (IOException e) {
                context.put("error", e.getMessage());
                templateService.render(context, "did/resolve");
            }
        }
    }

}
