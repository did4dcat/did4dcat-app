package io.piveau.did4dcat.app.services;

import io.piveau.did4dcat.app.Constants;
import io.piveau.did4dcat.app.did.DIDManager;
import io.piveau.did4dcat.app.models.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.identity.IdentityConstants;
import org.hyperledger.fabric.gateway.impl.identity.IdentityProvider;
import org.hyperledger.fabric.gateway.impl.identity.X509IdentityImpl;
import org.hyperledger.fabric.gateway.impl.identity.X509IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class FabricGateway {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Wallet wallet;
    private Gateway gateway = null;
    private Path networkPath;
    private DIDManager didManager;

    private final JsonObject config;

    public FabricGateway(JsonObject config) {
        this.config = config;
        try {
            this.wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        networkPath = Paths.get(config.getString(Constants.PEER_CONFIG));
        didManager = new DIDManager();
    }

    public void connect(X509Identity identity) {
        Gateway.Builder builder = Gateway.createBuilder();
        try {
            builder.identity(identity).networkConfig(networkPath).discovery(true);
            this.gateway = builder.connect();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public JsonObject executeContract() {
        JsonObject result =  new JsonObject();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        try {
            byte[] response = contract.evaluateTransaction("GetAllDatasets");
            result.put("status", "success").put("message", new JsonArray(new String(response)));
        } catch (ContractException e) {
            LOGGER.error(e.getMessage());
            result.put("status", "error").put("message", e.getMessage());
        }
        return result;
    }

    public JsonObject executeContract(JsonObject transaction, Boolean submit) {
        JsonObject result =  new JsonObject();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        String function = transaction.getString("function");
        JsonArray args = transaction.getJsonArray("Args");

        List<String> argsList = new ArrayList<>();
        args.forEach(object -> {
            argsList.add((String) object);
        });

        try {
            byte[] response;
            if(submit) {
                response = contract.submitTransaction(function, argsList.toArray(String[]::new));
            } else {
                response = contract.evaluateTransaction(function, argsList.toArray(String[]::new));
            }
            result.put("status", "success").put("message", new String(response));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result.put("status", "error").put("message", e.getMessage());
        }
        return result;
    }

    public JsonObject executeContract(String command, List<String> args) {
        JsonObject result =  new JsonObject();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        try {
            byte[] response = contract.evaluateTransaction(command, args.toArray(String[]::new));
            //byte[] response = contract.submitTransaction(command, args.toArray(String[]::new));
            result.put("status", "success");
            if (response.length > 0) {
                result.put("message", new JsonObject(new String(response)));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result.put("status", "error").put("message", e.getMessage());
        }

        return result;
    }

    public Future<JsonArray> listDIDs() {
        Promise<JsonArray> promise = Promise.promise();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        try {
            byte[] response = contract.evaluateTransaction("GetMyDatasets");
            promise.complete(new JsonArray(new String(response)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            promise.fail(e.getMessage());
        }
        return promise.future();
    }

    public Future<JsonObject> createDID(String did, String url, User user, String hash) {
        Promise<JsonObject> promise = Promise.promise();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        JsonObject didDocument = didManager.buildDatasetDIDDocument(did, user.getName(), url, hash);
        LOGGER.info(didDocument.encodePrettily());

        try {
            byte[] response = contract.submitTransaction("CreateDataset", didDocument.toString());
            promise.complete(new JsonObject(new String(response)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            promise.fail(e.getMessage());
        }
        return promise.future();
    }

    public Future<JsonObject> updateDID(String did, String url, User user, String hash) {
        Promise<JsonObject> promise = Promise.promise();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        JsonObject didDocument = didManager.buildDatasetDIDDocument(did, user.getName(), url, hash);
        try {
            byte[] response = contract.submitTransaction("UpdateDataset", didDocument.toString());
            promise.complete(new JsonObject(new String(response)));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            promise.fail(e.getMessage());
        }
        return promise.future();
    }

    public Future<JsonObject> resolveDID(String did) {
        Promise<JsonObject> promise = Promise.promise();
        Network network = gateway.getNetwork(config.getString(Constants.CHANNEL));
        Contract contract = network.getContract(config.getString(Constants.CHAINCODE));

        try {
            byte[] response = contract.submitTransaction("ReadDataset", did);
            promise.complete(new JsonObject(new String(response)));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            promise.fail(e.getMessage());
        }
        return promise.future();
    }

}
