import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Masterserver {

    public static int port = 5566;

    public static int getLobbyCount(String masterServerHost) {
        try {

            Socket socket = new Socket(masterServerHost, Masterserver.port);

            // Get the output stream to send data to the server
            OutputStream outputStream = socket.getOutputStream();

            // Get the input stream to read the server's response
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Get the server's hello message
            String response = reader.readLine();

            // Ask the server for the lobbies
            String messageToSend = "{\"method\":\"list_lobbies\"}";
            byte[] messageBytes = messageToSend.getBytes("UTF-8");
            outputStream.write(messageBytes);
            outputStream.flush();

            // Get the response
            response = reader.readLine();
            JSONParser parse = new JSONParser();
            JSONObject packetObject = (JSONObject) parse.parse(response);

            // Get the lobby count and return it
            return ((JSONArray) packetObject.get("lobbies")).size();

        } catch (Exception ex) {
            return -1;
        }
    }

}
