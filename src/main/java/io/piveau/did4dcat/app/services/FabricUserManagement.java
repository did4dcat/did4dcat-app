package io.piveau.did4dcat.app.services;

import io.piveau.did4dcat.app.Constants;
import io.vertx.core.json.JsonObject;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.identity.IdentityProvider;
import org.hyperledger.fabric.gateway.impl.identity.X509IdentityImpl;
import org.hyperledger.fabric.gateway.impl.identity.X509IdentityProvider;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FabricUserManagement {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final JsonObject config;
    private Properties props;
    private Wallet wallet;
    private HFCAClient hfcaClient;

    public FabricUserManagement(JsonObject config) {
        this.config = config;
        String userDirectory = Paths.get("")
                .toAbsolutePath()
                .toString();

        this.props = new Properties();
        props.put("pemFile", config.getString(Constants.CA_PEM_FILE));
        props.put("allowAllHostNames", "true");

        try {
            this.wallet = Wallets.newFileSystemWallet(Paths.get(config.getString(Constants.WALLET_DIR)));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        try {
            hfcaClient = HFCAClient.createNewInstance(config.getString(Constants.CA_URL), props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            hfcaClient.setCryptoSuite(cryptoSuite);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    public JsonObject readUser(String userId) throws IOException {
        JsonObject result = new JsonObject();
        Identity user = wallet.get(userId);

        result.put("status", "success")
                .put("user", new JsonObject(X509IdentityProvider.INSTANCE.toJson(user).toString()));
        return result;
    }

    public JsonObject listUsers() throws IOException {
        JsonObject result = new JsonObject();
        result.put("status", "success").put("users", wallet.list());
        return result;
    }

    public JsonObject createUser() throws Exception {
        return createUser("user");
    }

    public JsonObject createUser(String userId) throws Exception {
        JsonObject result = new JsonObject();

        X509Identity adminIdentity = (X509Identity) wallet.get("admin");
        FabricUser admin = new FabricUser();
        admin.setName("admin");
        admin.setAffiliation(config.getString(Constants.ORG_AFFILIATION));
        admin.setEnrollment(adminIdentity);
        admin.setMspId(config.getString(Constants.ORG_MSP));

        RegistrationRequest registrationRequest = new RegistrationRequest(userId);
        registrationRequest.setAffiliation(config.getString(Constants.ORG_AFFILIATION));
        registrationRequest.setEnrollmentID(userId);
        String enrollmentSecret = hfcaClient.register(registrationRequest, admin);
        Enrollment enrollment = hfcaClient.enroll(userId, enrollmentSecret);
        Identity user = Identities.newX509Identity(config.getString(Constants.ORG_MSP), enrollment);
        wallet.put(userId, user);
        result.put("status", "success")
                .put("user", new JsonObject(X509IdentityProvider.INSTANCE.toJson(user).toString()));
        return result;
    }

    public JsonObject initAdmin() {
        JsonObject result = new JsonObject();
        try {
            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");
            Enrollment enrollment = hfcaClient.enroll("admin", "adminpw", enrollmentRequestTLS);
            X509Identity user = Identities.newX509Identity(config.getString(Constants.ORG_MSP), enrollment);
            LOGGER.debug("Admin certificate \n" + Identities.toPemString(user.getCertificate()));
            wallet.put("admin", user);
            result.put("status", "success").put("message", "Created admin ID");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result.put("status", "error").put("message", e.getMessage());
            return result;
        }

        return result;
    }

}
