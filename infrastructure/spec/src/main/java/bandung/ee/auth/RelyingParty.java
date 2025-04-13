package bandung.ee.auth;

import std.ietf.http.oauth.Authorization;
import std.ietf.http.oauth.AuthorizationServerMetadataResponse;
import std.ietf.http.oauth.ClientMetadata;
import std.ietf.http.oauth.ErrorResponse;
import std.ietf.http.oauth.ErrorValue;
import std.ietf.http.oauth.Introspection;
import std.ietf.http.oauth.Registration;
import std.ietf.http.oauth.Revocation;
import std.ietf.http.oauth.ServerResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Optional;

/**
 * REST RP
 * @author iMinusMinus
 * @date 2025-04-04
 */
public class RelyingParty implements Registration, Authorization, Introspection, Revocation {

    public static final String OP_METADATA_CONFIG_FMT = "oauth2.client.provider.%s";

    private final ClientMetadataStore clientRepository;

    private final String op;

    private final Client client;

    private final AuthorizationServerMetadataResponse serverProperties;

    private Registration.InformationResponse clientProperties;

    public RelyingParty(ClientMetadataStore clientRepository, String op, Configuration configuration) {
        this.clientRepository = clientRepository;
        this.op = op;
        this.client = ClientBuilder.newBuilder().withConfig(configuration).build();
        this.serverProperties = (AuthorizationServerMetadataResponse) configuration.getProperty(String.format(OP_METADATA_CONFIG_FMT, op));
    }

    private synchronized URI loadRegistrationMgmtUrl(String clientId) {
        if (clientProperties == null) {
            clientProperties = Optional.ofNullable(clientRepository.loadClientMetadata(op, clientId))
                    .orElse(null);
        }
        if (clientProperties == null) {
            throw new IllegalStateException("not registered");
        }
        return clientProperties.getRegistration_client_uri();
    }

    @Override
    public ServerResponse authorize(Authorization.Request request) {
        javax.ws.rs.core.Response response = client.target(serverProperties.getAuthorization_endpoint())
                .resolveTemplates(request.toMap()).request().get();
        if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT) {
            return new Authorization.RedirectResponse(response.getHeaderString(HttpHeaders.LOCATION));
        }
        if (MediaType.APPLICATION_JSON_TYPE.isCompatible(MediaType.valueOf(response.getHeaderString(HttpHeaders.CONTENT_TYPE)))) {
            return response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK ?
                    response.readEntity(Authorization.DeviceAuthorizationResponse.class) : response.readEntity(ErrorResponse.class);
        }
        throw new IllegalStateException();
    }

    @Override
    public ServerResponse grant(String credentials, Authorization.TokenRequest request) {
        javax.ws.rs.core.Response response = client.target(serverProperties.getToken_endpoint()).request() // xxx 如果授权服务器
                .header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request.toMap(), MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        return response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK ?
                response.readEntity(Authorization.TokenResponse.class) : response.readEntity(ErrorResponse.class);
    }

    @Override
    public ServerResponse introspect(String credentials, Introspection.Request request) {
        javax.ws.rs.core.Response response = client.target(serverProperties.getIntrospection_endpoint()).request()
                .header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request.toMap(), MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        return response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK ? // 200, 401
                response.readEntity(Introspection.Response.class) : response.readEntity(ErrorResponse.class);
    }

    @Override
    public ServerResponse registering(String credentials, ClientMetadata request) {
        Invocation.Builder requestBuilder = client.target(serverProperties.getRegistration_endpoint()).request();
        if (credentials != null) {
            requestBuilder = requestBuilder.header(HttpHeaders.AUTHORIZATION, credentials);
        }
        javax.ws.rs.core.Response response = requestBuilder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        ServerResponse serverResponse;
        if ( response.getStatus() == javax.ws.rs.core.Response.Status.CREATED.getStatusCode()) {
            clientProperties = response.readEntity(Registration.InformationResponse.class);
            serverResponse = clientProperties;
            clientProperties.setOpId(op);
            clientRepository.rememberClientMetadata(clientProperties);
        } else {
            serverResponse = response.readEntity(ErrorResponse.class);
        }
        return serverResponse;
    }

    @Override
    public ServerResponse read(String credentials, String clientId) {
        javax.ws.rs.core.Response response = client.target(loadRegistrationMgmtUrl(clientId)) // clientId已经属于registrationClientUri一部分（path或query parameter）
                .request().header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .get();
        ServerResponse serverResponse; // 200, 401(clientId不存在，access_token不匹配), 403(无权限)
        if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK) {
            clientProperties = response.readEntity(Registration.InformationResponse.class);
            serverResponse = clientProperties;
        } else {
            serverResponse = response.readEntity(ErrorResponse.class);
        }
        return serverResponse;
    }

    @Override
    public ServerResponse update(String credentials, UpdateRequest request) {
        javax.ws.rs.core.Response response = client.target(loadRegistrationMgmtUrl(request.getClient_id()))
                .request().header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
        ServerResponse serverResponse;
        if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK) {
            clientProperties = response.readEntity(Registration.InformationResponse.class);
            serverResponse = clientProperties;
            clientProperties.setOpId(op);
            clientRepository.refreshClientMetadata(clientProperties);
        } else {
            serverResponse = response.readEntity(ErrorResponse.class);
        }
        return serverResponse;
    }

    @Override
    public ErrorResponse deprovision(String credentials, String clientId) {
        javax.ws.rs.core.Response response = client.target(loadRegistrationMgmtUrl(clientId))
                .request().header(HttpHeaders.AUTHORIZATION, credentials)
                .delete();
        if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.NO_CONTENT) { // 204, 401, 403, 405
            clientRepository.removeClientMetadata(op, clientId);
            clientProperties = null;
            return null;
        }
        ErrorValue error = null;
        if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.UNAUTHORIZED) {
            error = ErrorValue.invalid_token;
        } else if (response.getStatusInfo() == javax.ws.rs.core.Response.Status.FORBIDDEN) {
            error = ErrorValue.insufficient_scope;
        } else if  (response.getStatusInfo() == javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED) {
            error = ErrorValue.invalid_request;
        }
        return new ErrorResponse(error.name());
    }

    @Override
    public ServerResponse revoke(String credentials, Revocation.Request request) {
        javax.ws.rs.core.Response response = client.target(serverProperties.getRevocation_endpoint()).request()
                .header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request.toMap(), MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        return response.hasEntity() ?
                response.readEntity(ErrorResponse.class) : null; // 200(No Content), 400, 503
    }

    public ServerResponse obtainUserInfo(String credentials) {
        javax.ws.rs.core.Response response = client.target(serverProperties.getUserinfo_endpoint())
                .request()
                .header(HttpHeaders.AUTHORIZATION, credentials).accept(MediaType.APPLICATION_JSON_TYPE)
                .get(); // recommended get, not post
        return response.getStatusInfo() == javax.ws.rs.core.Response.Status.OK ?
                response.readEntity(OpenIDConnectProvider.UserInfoResponse.class) :
                response.readEntity(ErrorResponse.class);
    }
}
