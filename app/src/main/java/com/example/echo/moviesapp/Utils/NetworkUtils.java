package com.example.echo.moviesapp.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    //Getting the response for a URL
    public static String getResponse(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = con.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            con.disconnect();
        }

    }
}
