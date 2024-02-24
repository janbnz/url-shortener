package de.janbnz.url.rest.endpoint;

import io.javalin.http.NotAcceptableResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class Endpoint {

    /**
     * Returns the body of a NanoHTTPD session
     *
     * @param body The request body
     * @return the body as json object
     */
    public JSONObject getBody(String body) {
        try {
            return new JSONObject(body);
        } catch (JSONException ex) {
            throw new NotAcceptableResponse("Body is not a json string");
        }
    }

    /**
     * Returns the value of a specific key in a json object or throws an exception if not existing
     *
     * @param json The json object
     * @param key  The key
     * @return The value
     */
    public String getJsonString(JSONObject json, String key) {
        if (!json.has(key)) throw new NotAcceptableResponse("Please specify \"" + key + "\"");
        return json.getString(key);
    }
}