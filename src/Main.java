// import java.io.BufferedReader;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		int port = 80;
		// Vector<ClientHandler> ClientList = new Vector<>();
		try(ServerSocket server = new ServerSocket(port)){
			System.out.println("web socket starting, listening on port " + port + ".");
			while(true) {
				System.out.printf("Start acceptin' syre\n");
				Socket client = server.accept();

				// buat thread handle client ini
				// ClientHandler clientHandler = new ClientHandler(client, ClientList.size());
				// clientHandler.start();
				// ClientList.add(clientHandler);

				RequestHandler requestHandler = new RequestHandler(client);
				requestHandler.start();
				/*
				 * Debugger multithreaded request handler
				 
				 requestHandler.run();
				 
				 */

				// System.out.printf("Now there are %d clients\n", ClientList.size());
				// ngapus yang udah selesai (tbi)
			}
      // server.close();
		}
		catch (IOException ex){
			System.err.print(ex);
		}
	}
}
