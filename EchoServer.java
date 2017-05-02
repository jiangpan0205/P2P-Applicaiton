/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.net.*;
import java.util.HashMap;
import java.io.*;

public class EchoServer {
    HashMap<Integer, Socket> onlinePeers = new HashMap<>();
    //ArrayList<Socket> onlinePeers = new ArrayList<>();
    public EchoServer(ServerSocket serverSocket) throws IOException {
        HandleASession task1 = null;
        while(true){
            Socket clientSocket = serverSocket.accept();
            task1 = new HandleASession(clientSocket);
            new Thread(task1).start();
        }
    }
    public static void main(String[] args) throws IOException {

        int portNumber = 23427;

        ServerSocket serverSocket =new ServerSocket(portNumber);
        new EchoServer(serverSocket);

    }

    public class HandleASession implements Runnable {
        private Socket clientSocket;

        public HandleASession (Socket player) {
            this.clientSocket = player;
        }


        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equalsIgnoreCase("JOIN"))
                    {
                        if(onlinePeers.containsKey(clientSocket.getPort()))
                        {
                            out.println(new String("You are already in the online list"));
                        }
                        else
                        {
                            onlinePeers.put(clientSocket.getPort(),clientSocket);
                            out.println("Join success");
                        }
                    }
                    else if (inputLine.equalsIgnoreCase("LEAVE"))
                    {
                        if (!onlinePeers.containsKey(clientSocket.getPort()))
                        {
                            out.println(new String("You are already not in the online list"));
                        }
                        else
                        {
                            onlinePeers.remove(clientSocket.getPort());
                            out.println("LEAVE success");
                        }
                    }
                    else if (inputLine.equalsIgnoreCase("LIST"))
                    {
                        out.println("LIST");
                        out.println(onlinePeers.size());

                        if(onlinePeers.size() == 0)
                        {
                            out.println("No peers online");
                        }
                        else
                        {
                            for (Socket socket : onlinePeers.values())
                                out.println(socket.toString());
                        }

                    }
                    else if (inputLine.equalsIgnoreCase("INVITE"))
                    {
                        int iport = Integer.valueOf(in.readLine());

                        if (!onlinePeers.containsKey(iport) )
                        {
                            out.println("invite_result");
                            out.println("invite fail: NO Such Port online");
                        }
                        else if (!onlinePeers.containsKey(clientSocket.getPort()))
                        {
                            out.println("Invite_result");
                            out.println("Invite fail: You must join the chat list");
                        }
                        else if (iport == clientSocket.getPort())
                        {
                            out.println("Invite_result");
                            out.println("Invite fail:You can not invite yourself");
                        }
                        else
                        {
                            out.println("Invite_result");
                            out.println("Invite success");

                            PrintWriter tout = new PrintWriter(onlinePeers.get(iport).getOutputStream(), true);

                            tout.println("INVITE");
                            tout.println(clientSocket.getInetAddress() + " " + clientSocket.getPort()+ " invite you to chat");
                            tout.flush();
                        }
                    }
                    else if (inputLine.equalsIgnoreCase("chat"))
                    {
                        int desport = Integer.valueOf(in.readLine());
                        String mes = in.readLine();

                        PrintWriter tout = new PrintWriter(onlinePeers.get(desport).getOutputStream(), true);
                        tout.println("chat");
                        tout.println(mes);
                        tout.flush();
                    }
                    else
                    {
                        out.println(new String("error"));
                    }

                    out.flush();
                }

            } catch (IOException e) {
                try
                {
                    clientSocket.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            }
            try
            {
                clientSocket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

}