/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.net.*;


public class EchoClient {

    boolean isChat = false;
    private int chatport = 0;

    public EchoClient(String hostName,int portNumber) throws UnknownHostException, IOException {

        Socket echoSocket = new Socket(hostName,portNumber);
        System.out.println("****************************************");
        System.out.println("*        NOTE: Case Insensitive.       *");
        System.out.println("****************************************");
        System.out.print("Client: ");
        System.out.println(echoSocket.getLocalPort());

        HandServer task = new HandServer(echoSocket);
        new Thread(task).start();
        try (
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            System.out.println("Please input \'JOIN\', \'LEAVE\', \'LIST\', \'INVITE \' and \'EXIT\'.");

            while ((userInput = stdIn.readLine()) != null) {
                if (userInput.equalsIgnoreCase("EXIT")){
                    break;
                }
                else if (userInput.equalsIgnoreCase("INVITE"))
                {
                    System.out.println("\nPlease input the port you want to invite");
                    int iport = Integer.valueOf(stdIn.readLine());
                    chatport = iport;
                    out.println(userInput);
                    out.println(iport);
                    out.flush();
                }
                else
                {
                    if (isChat)
                    {
                        do {
                            if(isChat == false)
                            {
                                System.out.println("Please input \'JOIN\', \'LEAVE\', \'LIST\', \'INVITE \' and \'EXIT\'.");
                                break;
                            }

                            out.println(new String("chat"));
                            out.println(new String(""+ chatport));
                            out.println(userInput);
                            out.flush();

                            if(userInput.equalsIgnoreCase("exit"))
                            {
                                isChat = false;
                                System.out.println("Please input \'JOIN\', \'LEAVE\', \'LIST\', \'INVITE \' and \'EXIT\'.");
                                break;
                            }

                        } while ((userInput = stdIn.readLine()) != null);
                    }
                    else
                    {
                        out.println(userInput);
                        out.flush();
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java EchoClient <host name>");
            System.exit(1);
        }

        String hostName = args[0];
        //String hostName = "localhost";
        int portNumber = 23427;

        try
        {
            new EchoClient(hostName, portNumber);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    public class HandServer implements Runnable{
        Socket echoSocket = null;

        public HandServer (Socket echoSocket) {
            this.echoSocket = echoSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.echoSocket.getInputStream()))) {
                String userInput;

                while (true) {
                    userInput = in.readLine();
                    if (!isChat)
                        System.out.println();

                    if (userInput.equalsIgnoreCase("LIST"))
                    {
                        userInput = in.readLine();
                        int rnum = Integer.valueOf(userInput);

                        if (rnum > 0)
                        {
                            System.out.println("The online peers are:");

                            for(int i = 0 ; i < rnum ; i++)
                                System.out.println(in.readLine());
                        }
                        else
                            System.out.println(in.readLine());

                    }
                    else if (userInput.equalsIgnoreCase("invite"))
                    {

                        userInput = in.readLine();

                        System.out.println(userInput);

                        isChat = true;

                        String[] strings= userInput.split(" ");
                        StringBuffer tip = new StringBuffer(strings[0]);
                        String ip = tip.substring(1,tip.length());
                        chatport = Integer.valueOf(strings[1]);
                        System.out.println("Now you are chat with " + chatport);

                    }
                    else if (userInput.equalsIgnoreCase("Invite_result"))
                    {
                        userInput = in.readLine();
                        System.out.println(userInput);
                        String[] ts = userInput.split(" ");

                        if(ts[1].equalsIgnoreCase("success"))
                        {

                            isChat = true;
                            System.out.println("Now you are chat with "+ chatport);
                        }
                    }
                    else if (userInput.equalsIgnoreCase("chat")){
                        userInput = in.readLine();
                        System.out.println(userInput);
                        if(userInput.equalsIgnoreCase("exit"))
                            isChat = false;

                    }
                    else
                        System.out.println(userInput);

                    if (!isChat)
                    {
                        System.out.println();
                        System.out.println("Please input \'JOIN\', \'LEAVE\', \'LIST\', \'INVITE \' and \'EXIT\'.");
                    }

                }
            }
            catch (UnknownHostException e)
            {
                try
                {
                    echoSocket.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                System.exit(1);

            }
            catch (IOException e)
            {
                try
                {
                    echoSocket.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                System.exit(1);

            }
            try
            {
                echoSocket.close();
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
