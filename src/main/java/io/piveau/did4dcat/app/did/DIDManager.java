package io.piveau.did4dcat.app.did;

import io.piveau.did4dcat.app.models.User;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

public class DIDManager {

    public JsonObject buildDatasetDIDDocument(String did, String user, String url, String hash) {
        JsonObject didDoc = new JsonObject();

        didDoc.put("@context", new JsonArray().add("https://www.w3.org/ns/did/v1").add("https://did4dcat.org/context/v1"));
        didDoc.put("id", "did:dcat:dataset:" + did);
        didDoc.put("controller", "did:dcat:provider:" + user);
        didDoc.put("url", new JsonObject().put("@id", url));
        didDoc.put("issued", LocalDateTime.now().toString());
        didDoc.put("modified", LocalDateTime.now().toString());

        if(hash != null && !hash.isEmpty()) {
            didDoc.put("hash", new JsonObject().put("value", hash).put("alg", "URDNA2015"));
        }
        return didDoc;
    }

    public JsonObject buildDataProviderDIDDocument(User user) {
        JsonObject didDoc = new JsonObject();

        String did = "did:dcat:provider:" + user.getName();

        didDoc.put("@context", new JsonArray().add("https://www.w3.org/ns/did/v1"));
        didDoc.put("id", did);

        JsonObject jwkJSON = new JsonObject(user.getJSONWebKey().toPublicJWK().toJSONString());

        JsonObject verificationMethodObject = new JsonObject();
        String keyDID = did + "#key";
        verificationMethodObject.put("@context", "https://w3c-ccg.github.io/lds-jws2020/contexts/v1/");
        verificationMethodObject.put("id", keyDID);
        verificationMethodObject.put("type", "JsonWebKey2020");
        verificationMethodObject.put("controller", did);
        verificationMethodObject.put("publicKeyJwk", jwkJSON);

        didDoc.put("verificationMethod", new JsonArray().add(verificationMethodObject));
        didDoc.put("assertionMethod", new JsonArray().add(keyDID));
        didDoc.put("authentication", new JsonArray().add(keyDID));

        return didDoc;
    }


}
