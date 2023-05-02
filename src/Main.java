// import java.io.DataInputStream;
// import java.io.DataOutputStream;
// import java.io.IOException;
// import java.net.ServerSocket;
// import java.net.Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(11311);
			while(true) {
				System.out.printf("Start acceptin' syre\n");
				Socket client = server.accept();

				System.out.printf("Hell ye ma fren, we got ourself a client, hup top\n");
				DataInputStream bis = new DataInputStream(client.getInputStream());
				DataOutputStream bos = new DataOutputStream(client.getOutputStream());

				System.out.printf("Aight, readin' oll of the um uh bytes yea\n");
				while (true) {
					String buff = bis.readLine();

					System.out.printf("Ey got: %s\n", buff);

					if (buff.equals("stop")) {
						break;
					}
					buff += " real sirrr\r\n\r\n";
					System.out.printf("Aight, writtin' syre\n");
					bos.write(buff.getBytes());
				}
				System.out.printf("Closin");
				client.close();
			}
			// server.close();
			// System.out.printf("End of life\n");
		}
		catch (IOException ex){
			System.err.print(ex);
		}
	}
}
