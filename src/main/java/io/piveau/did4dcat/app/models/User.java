package io.piveau.did4dcat.app.models;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.gateway.impl.identity.X509IdentityProvider;

import javax.json.Json;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

public class User {

    private X509Identity identity;
    private String name;

    public User(String identityString) throws CertificateException, IOException, InvalidKeyException {
        javax.json.JsonObject json = Json.createReader(new StringReader(identityString)).readObject();
        this.identity = X509IdentityProvider.INSTANCE.fromJson(json);
        X500Name x500Name = new JcaX509CertificateHolder(this.identity.getCertificate()).getSubject();
        RDN cn = x500Name.getRDNs(BCStyle.CN)[0];
        this.name = IETFUtils.valueToString(cn.getFirst().getValue());
    }

    public String getName() {
        return name;
    }

    public X509Identity getIdentity() {
        return identity;
    }

    public JWK getJSONWebKey() {
        JWK jwk = null;
        try {
            jwk = JWK.parse(identity.getCertificate());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwk;
    }



}
