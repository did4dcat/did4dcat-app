package io.piveau.did4dcat.app;

import io.piveau.did4dcat.app.handler.*;
import io.piveau.did4dcat.app.services.FabricGateway;
import io.piveau.did4dcat.app.services.TemplateService;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

import java.util.Arrays;

import org.hyperledger.fabric.sdk.helper.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private FreeMarkerTemplateEngine engine;

    public static void main(String[] args) {
        String[] params = Arrays.copyOf(args, args.length + 2);
        params[params.length - 2] = MainVerticle.class.getName();
        params[params.length - 1] = "-worker";
        Launcher.executeCommand("run", params);
    }

     static {
         // https://github.com/hyperledger/fabric-sdk-java/blob/main/src/main/java/org/hyperledger/fabric/sdk/helper/Config.java
         // System.setProperty(Config.SERVICE_DISCOVER_AS_LOCALHOST, "true");
     }


    @Override
    public void start(Promise<Void> promise) {
        loadConfig()
                .compose(this::bootstrapVerticles)
                .compose(this::startServer)
                .onSuccess(v -> {
                    LOGGER.info("DID4DCAT-App successfully launched");
                    promise.complete();
                })
                .onFailure(promise::fail);
    }


    private Future<Void> startServer(JsonObject config) {
        Promise<Void> promise = Promise.promise();

        engine = FreeMarkerTemplateEngine.create(vertx);
        TemplateService templateService = new TemplateService(engine);

        FabricGateway fabricGateway = new FabricGateway(config);

        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        sessionHandler.setCookieSameSite(CookieSameSite.STRICT);
        sessionHandler.setCookieMaxAge(6000000000L);
        sessionHandler.setSessionCookieName("did4dcat_session");

        IndexHandler indexHandler = new IndexHandler(engine, config);
        UserHandler userHandler = new UserHandler(templateService);
        DIDHandler didHandler = new DIDHandler(config, templateService, fabricGateway);
        ContextHandler contextHandler = new ContextHandler();
        AuthHandler authHandler = new AuthHandler();

        Router userRouter = Router.router(vertx);
        userRouter.get("/login").handler(userHandler::getLogin);
        userRouter.post("/login").handler(userHandler::postLogin);
        userRouter.get("/logout").handler(userHandler::getLogout);
        userRouter.get("/did").handler(userHandler::getDID);


        Router didRouter = Router.router(vertx);
        didRouter.get("/create").handler(authHandler::checkAuth).handler(didHandler::getCreateDID);
        didRouter.post("/create").handler(authHandler::checkAuth).handler(didHandler::postCreateDID);
        didRouter.get("/update").handler(authHandler::checkAuth).handler(didHandler::getUpdateDID);
        didRouter.post("/update").handler(authHandler::checkAuth).handler(didHandler::postUpdateDID);
        didRouter.get("/resolve").handler(didHandler::getResolveDID);
        didRouter.post("/resolve").handler(didHandler::postResolveDID);
        didRouter.get("/list").handler(didHandler::getListDIDs);
        didRouter.post("/list").handler(didHandler::postListDIDs);

        Router mainRouter = Router.router(vertx);
        mainRouter.route().handler(sessionHandler);
        mainRouter.route().handler(contextHandler::setContext);
        mainRouter.route().handler(BodyHandler.create());
        mainRouter.route("/static/*").handler(StaticHandler.create("static"));

        mainRouter.get("/console").handler(authHandler::checkAuth).handler(indexHandler::handleIndex);
        mainRouter.get("/").handler(request -> request.redirect("/did/resolve"));
        mainRouter.post("/action/:action").handler(indexHandler::handleAction);

        mainRouter.route("/user/*").subRouter(userRouter);
        mainRouter.route("/did/*").subRouter(didRouter);

        vertx.createHttpServer().requestHandler(mainRouter).listen(config.getInteger(Constants.PORT, 8080), http -> {
            if (http.succeeded()) {
                promise.complete();
                LOGGER.info(("Listening on Port " + http.result().actualPort()));
            } else {
                promise.fail(http.cause());
            }
        });
        return promise.future();
    }


    private Future<JsonObject> bootstrapVerticles(JsonObject config) {
        Promise<JsonObject> promise = Promise.promise();
        vertx.deployVerticle(ShellVerticle.class.getName(), new DeploymentOptions().setConfig(config), ar -> {
            if(ar.succeeded()) {
                promise.complete(config);
            } else {
                promise.fail(ar.cause().getMessage());
            }
        });

        return promise.future();
    }

    private Future<JsonObject> loadConfig() {
        Promise<JsonObject> promise = Promise.promise();
        ConfigRetriever.create(vertx).getConfig(ar -> {
            if (ar.succeeded()) {
                LOGGER.debug("Successfully loaded configuration.");
                promise.complete(ar.result());
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }
}
