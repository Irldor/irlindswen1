package com.company.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ServerHandler extends Thread {
    private final Socket clientConnection;
    private final BufferedReader bufferedReader;
    private final ResponseHandler responseHandler;
    private final HeaderReader headerReader = new HeaderReader();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RouteManager routeManager = new RouteManager();

    public ServerHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        responseHandler = new ResponseHandler(new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream())));
    }

    @Override
    public void run()  {
        try {
            final String httpMethodWithPath = bufferedReader.readLine();
            System.out.println(httpMethodWithPath);

            while (bufferedReader.ready()) {
                final String input = bufferedReader.readLine();
                if ("".equals(input)) {
                    break;
                }
                headerReader.ingest(input);
            }
            String token=headerReader.getHeader("Authorization");
            headerReader.print();
            System.out.println("In thread: " + Thread.currentThread().getName());
            char[] charBuffer= new char[0];
            if (headerReader.getContentLength() > 0) {
                charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());

            }
            HTTPResponse response = routeManager.handleRoute(httpMethodWithPath,new String(charBuffer),token);
            responseHandler.reply(response.getResponseObject(),response.getStatus());
            responseHandler.reply();
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}

