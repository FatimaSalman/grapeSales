package com.example.fatima.grapeapplication.manager;

import java.io.File;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

public class MailgunService {
    public final static String MAILGUN_API_USERNAME = "api";

    private final HttpUrl mailgunApiUrl;
    private final String authHeaderValue;

    public MailgunService(HttpUrl mailgunbaseUrl, String mailgunDomain, String mailgunApiKey) {
        this.mailgunApiUrl = HttpUrl.parse(mailgunbaseUrl.toString() + "/" + mailgunDomain);
        this.authHeaderValue = buildAuthHeader(mailgunApiKey);
    }

    public boolean sendMail(MailgunEmail email) throws IOException {
        Request request = new Request.Builder()
                .url(mailgunApiUrl.toString() + "/messages")
                .post(email.toMultipartBody())
                .addHeader("Authentication", authHeaderValue)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        return response.isSuccessful();
    }

    private static String buildAuthHeader(String mailgunApiKey) {
        String authString = String.format("%s:%s", MAILGUN_API_USERNAME, mailgunApiKey);
        ByteString authData = ByteString.encodeUtf8(authString);
        return "Basic " + authData.base64();
    }

    public static void main(String[] args) throws IOException {
        MailgunService mailgunService = new MailgunService(
                HttpUrl.parse("https://api.mailgun.net/v3"),
                "sandbox4909363fb10c4cffb9b117f5de32565b.mailgun.org/messages",
                "key-a3629c2b0b22d3e51c5e0fa65034e709");

        MailgunEmail email = new MailgunEmail.Builder()
                .from(new MailgunEmail.Contact("Grapes Sales", "grapesalesapp@gmail.com"))
                .addTo(new MailgunEmail.Contact("Fatima", "fatoom.21.19911@gmail.com"))
                .subject("Hi here!")
                .html("<html>Inline image here: helllllo</html>")
                .build();

        boolean success = mailgunService.sendMail(email);
        System.out.println(success ? "MESSAGE SENT" : "FAILED TO SEND");
    }
}