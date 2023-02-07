package io.piveau.did4dcat.app;


import io.piveau.did4dcat.app.services.FabricUserManagement;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.HttpTermOptions;
import io.vertx.ext.shell.term.SSHTermOptions;
import io.vertx.ext.shell.term.TelnetTermOptions;
import org.apache.http.auth.AuthOption;
import io.vertx.ext.shell.impl.auth.PropertiesShellAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShellVerticle extends AbstractVerticle {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private FabricUserManagement fabricUserManagement;

    @Override
    public void start(Promise<Void> startPromise) {

        this.fabricUserManagement = new FabricUserManagement(config());

        /*ShellServiceOptions shellServiceOptions =   new ShellServiceOptions().setSSHOptions(
                new SSHTermOptions().
                        setHost("localhost").
                        setPort(5000).
                        setKeyPairOptions(new JksOptions().
                                setPath("D:\\Tmp\\openssl\\keystore.jks").
                                setPassword("123456")
                        ).
                        setAuthOptions(
                                new JsonObject()
                                        .put("provider", "shiro")
                                        .put("type", "PROPERTIES")
                                        .put("config", new JsonObject()
                                        .put("properties_path", "file:D:\\Tmp\\openssl\\auth.properties"))
                        )
        );*/

        Integer cliPort = config().getInteger("CLI_PORT");

        ShellServiceOptions shellServiceOptions = new ShellServiceOptions().setHttpOptions(
                new HttpTermOptions()
                        .setHost("0.0.0.0")
                        .setPort(cliPort)
        );

        ShellService shellService = ShellService.create(vertx, shellServiceOptions);
        shellService.start(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Loaded CLI");
                startPromise.complete();
            } else {
                LOGGER.error("Failed to load CLI " + ar.cause().getMessage());
                startPromise.fail(ar.cause().getMessage());
            }
        });

        CommandBuilder readUser = CommandBuilder.command("read_user");
        readUser.processHandler(process -> {
            List<String> args = process.args();
            if (args.size() != 1) {
                process.write("Usage: read_user $user_id'\n");
                process.end();
            } else {
                try {
                    String result = fabricUserManagement.readUser(args.get(0)).encodePrettily();
                    process.write(result);
                    process.write("\n");
                    process.end();
                } catch (Exception e) {
                    process.write(e.getMessage());
                    process.write("\n");
                    process.end();
                }
            }
        });

        CommandBuilder listUsers = CommandBuilder.command("list_users");
        listUsers.processHandler(process -> {
            try {
                String result = fabricUserManagement.listUsers().encodePrettily();
                process.write(result);
                process.write("\n");
                process.end();
            } catch (Exception e) {
                process.write(e.getMessage());
                process.write("\n");
                process.end();
            }
        });

        CommandBuilder initAdmin = CommandBuilder.command("init_admin");
        initAdmin.processHandler(process -> {
            String result = fabricUserManagement.initAdmin().encodePrettily();
            process.write(result);
            process.write("\n");
            process.end();
        });

        CommandBuilder createUser = CommandBuilder.command("create_user");
        createUser.processHandler(process -> {
            List<String> args = process.args();
            if (args.size() != 1) {
                process.write("Usage: create_user $user_id'\n");
                process.end();
            } else {
                try {
                    String result = fabricUserManagement.createUser(args.get(0)).encodePrettily();
                    process.write(result);
                    process.write("\n");
                    process.end();
                } catch (Exception e) {
                    process.write(e.getMessage());
                    process.write("\n");
                    process.end();
                }
            }
        });

        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(listUsers.build(vertx));
        registry.registerCommand(readUser.build(vertx));
        registry.registerCommand(initAdmin.build(vertx));
        registry.registerCommand(createUser.build(vertx));
    }

}
