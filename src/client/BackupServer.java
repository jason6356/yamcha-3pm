package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackupServer {
    public static String GET_REQUEST_FOOD_TEXT() throws IOException {
        URL url = new URL("https://script.google.com/macros/s/AKfycbxaBXkGpKanvLpO7fdFlTzkw2HyujnJ33BUVcW_n5I8TZpkPT88dc2-Zq5wqOjmJ2RMJw/exec");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine + "\n");
        }
        in.close();
        con.disconnect();
        return content.toString();
    }


}
