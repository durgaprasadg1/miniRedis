import java.io.*;
import java.net.*;


public class EchoServer {

    public static void main(String[] args) throws Exception {
        
        ServerSocket serverSocket = new ServerSocket(6379);
        System.out.println("Server Started on port 6379");

        Socket socket = serverSocket.accept();  // Jbb tkk client connect nhi hota tbb tkk server kaa code yha blocked rhega 
        System.out.println("Client Connected");

        InputStream inputStream = socket.getInputStream() ;// gets data from client in raw byte
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // converts bytes into Character;
        

        BufferedReader reader = new BufferedReader(inputStreamReader); // gives convinient linebased reading.   

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        String message;
        while((message = reader.readLine()) != null){
            System.out.println("Received "+ message);
        }
        socket.close();
        serverSocket.close();
    }
    
}