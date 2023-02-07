package io.piveau.did4dcat.app.services;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.security.PrivateKey;
import java.util.Set;

public class FabricUser implements User {

    private String name;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return null;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(X509Identity identity) {
        this.enrollment = new Enrollment() {
            @Override
            public PrivateKey getKey() {
                return identity.getPrivateKey();
            }

            @Override
            public String getCert() {
                return Identities.toPemString(identity.getCertificate());
            }
        };
    }

    @Override
    public String getMspId() {
        return null;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }


}
