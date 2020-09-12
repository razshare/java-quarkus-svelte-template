package io.github.tncrazvan.quarkus.tools.security;


import static io.github.tncrazvan.quarkus.tools.encoding.Base64.btoa;
import static io.github.tncrazvan.quarkus.tools.encoding.Hashing.getSha512String;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author Razvan Tanase
 */
public class JwtMessage {
    private static String KEY = "";

    public static void setKey(String key){
        KEY = key;
    }
    public static String getKey(){
        return KEY;
    }
    
    private final JsonObject header = new JsonObject();
    private final JsonObject body;
    private String contents = "";
    
    /**
     * @param account account to encode
     * @param charset character set to use while encoding.
     */
    public JwtMessage(JsonObject account, final String charset) {
        header.put("alg", "HS512");
        header.put("typ", "JWT");
        this.body = account;
        final String header64 = btoa(this.header.toString(), charset);
        final String body64 = btoa(this.body.toString(), charset);
        final String token = btoa(getSha512String(header64 + "." + body64, KEY, charset),charset);
        this.contents = header64 + "." + body64 + "." + token;
    }

    @Override
    public final String toString() {
        return contents;
    }

    public final JsonObject getHeader() {
        return header;
    }

    public final JsonObject getBody() {
        return body;
    }

    public static boolean ok(String jwt){
        return ok(jwt,"UTF-8");
    }
    public static boolean ok(String jwt, String charset){
        String[] pieces = jwt.split("\\.");
        if(pieces.length < 3) 
            return false;
        final String token = btoa(getSha512String(pieces[0] + "." + pieces[1], KEY, charset),charset);
        return token.equals(pieces[2]);
    }
}