/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import example.model.ClientUser;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ChatServer extends WebSocketServer {

    public List<ClientUser> clientUsers = new ArrayList();

    public ChatServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    public ChatServer(int port, Draft_17 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        // broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        if (message.contains("*--*")) {
            Gson gson = new Gson();
            ClientUser clientUser = gson.fromJson(message.substring(4), ClientUser.class);
            clientUser.setWebSocket(conn);

            clientUsers.add(clientUser);
            System.out.println("User joined to server " + conn.getRemoteSocketAddress() + ": " + message);
        } else {
            System.out.println("received message from " + conn.getRemoteSocketAddress() + ": " + message);
        }

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }


    public static void main(String[] args) throws InterruptedException, IOException {

        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
        }
        ChatServer s = new ChatServer(port);
        s.start();

        System.out.println("ChatServer started on port: " + s.getPort());


        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            //s.broadcast(in);
            if (in.equals("exit")) {
                s.stop(1000);
                break;
            } else if (in.contains("messagex")) {
                System.out.println("******* Client List *******");
                s.showClients();

                System.out.println("Select clients that you want to message (1,2,3...): ");
                String whichClient = sysin.readLine();
                System.out.println("Enter your message: ");
                String serverMessage = sysin.readLine();


                for (String m : whichClient.split(",")) {
                    if (StringUtils.isNumeric(m)) {
                        s.clientUsers.get(Integer.valueOf(m) - 1).getWebSocket().send(serverMessage);
                    } else {
                        System.out.println("Passed");
                    }

                }

            }
        }


    }

    public void showClients() {
        int counter = 0;
        for (ClientUser i : clientUsers) {
            counter++;
            System.out.println(counter + "-" + "Android ID: " + i.getAndroidId() + "Phone Model: " + i.getPhoneModel());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }


    public void onStart() {
        System.out.println("Server started!");
    }


}
