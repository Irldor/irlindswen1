package com.company.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    final ExecutorService executorService = Executors.newFixedThreadPool(10);
    ServerSocket serverSocket;

    public void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(10001, 5)){
            while (true) {
                final Socket clientConnection = serverSocket.accept();
                final ServerHandler server = new ServerHandler(clientConnection);
                executorService.submit(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
