package de.janbnz.url.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.janbnz.url.auth.user.User;
import io.javalin.http.Context;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;

public class Authentication {

    private final Algorithm algorithm;
    private final String secretKey;
    private final JWTProvider<User> provider;

    public Authentication(String secretKey) {
        this.secretKey = secretKey;
        this.algorithm = Algorithm.HMAC256(secretKey);

        final JWTGenerator<User> generator = (client, algorithm) -> {
            final JWTCreator.Builder token = JWT.create().withClaim("name", client.name()).withClaim("role", client.role().toString());
            return token.sign(this.algorithm);
        };

        final JWTVerifier verifier = JWT.require(this.algorithm).build();
        this.provider = new JWTProvider<>(algorithm, generator, verifier);
    }

    public String generate(User client) {
        return this.provider.generateToken(client);
    }

    public DecodedJWT validate(Context context) {
        return JavalinJWT.getTokenFromHeader(context).flatMap(provider::validateToken).orElse(null);
    }

    public DecodedJWT validate(String token) {
        return this.provider.validateToken(token).orElse(null);
    }

    public String getSecretKey() {
        return secretKey;
    }
}