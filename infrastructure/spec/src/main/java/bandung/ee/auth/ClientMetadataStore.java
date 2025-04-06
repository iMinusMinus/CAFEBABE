package bandung.ee.auth;

import std.ietf.http.oauth.Registration;

public interface ClientMetadataStore<ID> {

    ID rememberClientMetadata(Registration.InformationResponse clientMetadata);

    Registration.InformationResponse loadClientMetadata(String clientId);

    Registration.InformationResponse loadClientMetadata(String op, String clientId);

    boolean refreshClientMetadata(Registration.InformationResponse clientMetadata);

    boolean removeClientMetadata(String clientId);

    boolean removeClientMetadata(String op, String clientId);

}
