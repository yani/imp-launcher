package src;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HttpUtil {

    public static JSONObject getJsonObjectFromRestAPI(URI uri) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    uri)
                    .header("accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode == 200) {
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(responseBody);
                return data_obj;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
