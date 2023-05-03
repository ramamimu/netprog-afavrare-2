import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Main {
	public static void main(String[] args) {
		Vector<ClientHandler> ClientList = new Vector<>();
		try{
			ServerSocket server = new ServerSocket(80);
			while(true) {
				System.out.printf("Start acceptin' syre\n");
				Socket client = server.accept();

				// buat thread handle client ini
				ClientHandler clientHandler = new ClientHandler(client, ClientList.size());
				clientHandler.start();
				ClientList.add(clientHandler);

				System.out.printf("Now there are %d clients\n", ClientList.size());
				// ngapus yang udah selesai (tbi)
			}
//            server.close();
		}
		catch (IOException ex){
			System.err.print(ex);
		}
	}
}
