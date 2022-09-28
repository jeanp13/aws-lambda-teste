package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.nio.charset.Charset;

// Handler value: example.HandlerStream
public class App implements RequestStreamHandler {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("UTF-8"))));

        JSONObject genreJsonObject = new JSONObject();
        try {
            // writer.write(genreJsonObject0.toString());
            JSONParser parser = new JSONParser();
            JSONObject event = (JSONObject) parser.parse(reader);
            if (event.get("queryStringParameters") != null) {
                genreJsonObject.put("body", event.get("queryStringParameters").toString());

                writer.write(genreJsonObject.toString());
            }
            if (event.get("pathParameters") != null) {
                genreJsonObject.put("body", event.get("pathParameters").toString());

                writer.write(genreJsonObject.toString());
            } else if (event.get("body") != null) {
                Cep cep = gson.fromJson(event.get("body").toString(), Cep.class);

                // final String genreJson = IOUtils.toString(new URL("http://viacep.com.br/ws/"
                // + cep.getCep() + "/json/"), "UTF-8");
                // genreJsonObject = (JSONObject) JSONValue.parseWithException(genreJson);
                // genreJsonObject.put("body", genreJson.toString());
                genreJsonObject.put("body", event.get("body").toString());

                writer.write(genreJsonObject.toString());
                if (writer.checkError()) {
                    logger.log("WARNING: Writer encountered an error.");
                }
            } else {
                genreJsonObject.put("body", "vazio");

            }
        } catch (Exception exception) {
            genreJsonObject.put("body", "Erro");
            writer.write(genreJsonObject.toString());
            logger.log(exception.toString());
        } finally {
            reader.close();
            writer.close();
        }
    }
}