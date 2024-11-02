import java.net.*;
import java.util.Vector;
import java.io.*;

public class Server implements Runnable {

    // Vector to store all connected clients (DataOutputStream)
    public static Vector<DataOutputStream> clients = new Vector<>();

    // Vector to store the message history
    public static Vector<String> messageHistory = new Vector<>();

    // Each server thread will handle one client
    Socket socket;

    public Server(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            // Create input and output streams for communication with the client
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Add the client's output stream to the list of clients
            clients.add(dataOutputStream);

            // Send the message history to the new client
            sendHistoryToClient(dataOutputStream);

            // Read and broadcast messages from this client
            while (true) {
                String msgInput = dataInputStream.readUTF();
                System.out.println("Received: " + msgInput);

                // Add the new message to the history
                messageHistory.add(msgInput);

                // Broadcast the message to all connected clients
                broadcastMessage(msgInput);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Method to send message history to a newly connected client
    private void sendHistoryToClient(DataOutputStream client) {
        try {
            for (String message : messageHistory) {
                client.writeUTF(message);
            }
        } catch (IOException e) {
            System.out.println("Error sending history to client: " + e);
        }
    }

    // Method to broadcast a message to all clients
    public void broadcastMessage(String message) {
        for (int i = 0; i < clients.size(); i++) {
            try {
                DataOutputStream client = clients.get(i);
                client.writeUTF(message); // Send message to the client
            } catch (Exception e) {
                System.out.println("Error broadcasting message: " + e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Create a server socket to listen on port 6001
        ServerSocket serverSocket = new ServerSocket(6001);
        System.out.println("Server started on port 6001...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            Server server = new Server(socket);
            Thread thread = new Thread(server);
            thread.start(); 
        }
    }
}
