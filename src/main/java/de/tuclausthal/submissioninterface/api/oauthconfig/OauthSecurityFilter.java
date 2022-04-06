package de.tuclausthal.submissioninterface.api.oauthconfig;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.util.Configuration;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Provider
@SecureOauth
@Priority(Priorities.AUTHORIZATION)
public class OauthSecurityFilter implements ContainerRequestFilter {

    public static final String BEARER = "Bearer";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        String bearerToken = containerRequestContext.getHeaderString(AUTHORIZATION);
        String message = "";

        if (bearerToken == null || !bearerToken.startsWith(BEARER)) {
            message = "No valid bearer token provided";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorMessage).build());
        }

        String jwt = bearerToken.substring(BEARER.length()).trim();
        try {
            validateJWT(jwt);
        } catch (JWTVerificationException exception) {
            if (findRootCause(exception) instanceof TokenExpiredException) {
                message = exception.getMessage();
            } else {
                message = "Invalid JWT token";
            }

            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorMessage).build());
        }
    }

    public void validateJWT(String token) throws JWTVerificationException {
        RSAPublicKey publicKey = Configuration.getInstance().getPublicKey();
        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(Configuration.getInstance().getOauth2RealmUrl())
                .build();
        verifier.verify(token);
    }

    public static Throwable findRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);

        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
