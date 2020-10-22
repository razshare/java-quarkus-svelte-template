package com.github.tncrazvan.quarkus.tools.security;


import static com.github.tncrazvan.quarkus.tools.encoding.Base64.btoa;
import static com.github.tncrazvan.quarkus.tools.encoding.Hashing.getSha512String;

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

    /**
     * Get the header part of this jwt.
     * @return header of the jwt.
     */
    public final JsonObject getHeader() {
        return header;
    }

    /**
     * Get the body part of this jwt.<br />
     * The body of the jwt usually is supposed to contain some user metadata.
     * @return body of the jwt.
     */
    public final JsonObject getBody() {
        return body;
    }

    /**
     * Check if a given jwt string is valid.<br />
     * A valid jwt must contain a header and a body that when both glued together in a base64 encoding 
     * and hashed together they match exactly the 3rd part of the jwt: the token encoded in base64.<br /><br />
     * <b>Example: </b><br />
     * Given the jwt string: "AAA.BBB.TTTTTT", where<br />
     * <ul>
     *  <li>"AAA" = header</li>
     *  <li>"BBB" = body</li>
     *  <li>"TTTTTT" = token</li>
     * </ul>
     * then in order for this token to be valid, the following must be true: <br />
     * <u>base64( hash512( "AAA" + "." + "BBB" , __KEY__) ) == "TTTTTT"</u>,<br />
     * where <b>___KEY___</b> is JwtMessage::KEY.
     * @param jwt
     * @return true if the given jwt string is intact and has not been changed, otherwise false.
     */
    public static boolean ok(String jwt){
        return ok(jwt,"UTF-8");
    }

    /**
     * Check if a given jwt string is valid.<br />
     * A valid jwt must contain a header and a body that when both glued together in a base64 encoding 
     * and hashed together they match exactly the 3rd part of the jwt: the token encoded in base64.<br /><br />
     * <b>Example: </b><br />
     * Given the jwt string: "AAA.BBB.TTTTTT", where<br />
     * <ul>
     *  <li>"AAA" = header</li>
     *  <li>"BBB" = body</li>
     *  <li>"TTTTTT" = token</li>
     * </ul>
     * then in order for this token to be valid, the following must be true: <br />
     * <u>base64( hash512( "AAA" + "." + "BBB" , __KEY__) ) == "TTTTTT"</u>,<br />
     * where <b>___KEY___</b> is JwtMessage::KEY.
     * @param jwt
     * @param charset charset to use while decoding the jwt.
     * @return true if the given jwt string is intact and has not been changed, otherwise false.
     */
    public static boolean ok(String jwt, String charset){
        String[] pieces = jwt.split("\\.");
        if(pieces.length < 3) 
            return false;
        final String token = btoa(getSha512String(pieces[0] + "." + pieces[1], KEY, charset),charset);
        return token.equals(pieces[2]);
    }
}