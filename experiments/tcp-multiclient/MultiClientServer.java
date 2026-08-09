import java.io.*;
import java.net.*;
import java.util.concurrent.*;



public class MultiClientServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6379);

        System.out.println("System started on port 6379");

        while(true){
            Socket socket = serverSocket.accept();
            System.out.println("Client Connected " + socket.getRemoteSocketAddress());

            Thread clientThread = new Thread(() -> {
                handleClient(socket);
            });

            clientThread.start();
        }

    }

    static void handleClient(Socket socket){

        try (

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader reader = new BufferedReader(inputStreamReader);


            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

        ){


            String message ; 
            while((message = reader.readLine()) !=  null){
                System.out.println(Thread.currentThread().getName() + " received: " + message);

                writer.println(message);
            }
        }catch (Exception e) {
              System.out.println(
                    "Client connection error: " + e.getMessage()
            );
        }
        finally{
            try{
                socket.close();
            }
            catch(IOException ignored){

            }
        }

    }
}
