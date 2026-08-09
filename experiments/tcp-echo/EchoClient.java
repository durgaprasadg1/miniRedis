import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 6379);

        InputStream input = System.in;
        InputStreamReader inputStreamReader = new InputStreamReader(input);
        BufferedReader keyBoard = new BufferedReader(inputStreamReader);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);


        while(true){
            System.out.println("> ");

            String message  = keyBoard.readLine();


            if(message == null ||message.equalsIgnoreCase("exit")) {
                break;
            }

            writer.println(message);

            String response = reader.readLine();
            System.out.println("Server " + response);
        }
        socket.close();
    }
}


