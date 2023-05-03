import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    /*
    Jadi ini yang bakal ngehandle si client, di sini bakal ambil client, terus parsing header, response, loop etc
     */

    public Socket client;
    public int id; // perlu ga perlu si, isi aja dulu
    public ClientRequestReader requestReader;
    public ResponseWriter responseWriter;

    public ClientHandler(Socket client, int id) {
        this.client = client;
        this.id = id;
        this.requestReader = new ClientRequestReader(client);
        this.responseWriter = new ResponseWriter(client);
    }

    @Override
    public void run(){
        // to be implemented
        System.out.printf("Jalan [%d]\n", this.id);

        // loop reaad header + response
        while (true){
            ClientRequestMsg requestMsg =  this.requestReader.readRequest();
            this.responseWriter.writeResponse();
            break; // sementara di break
        }
//        try{
//            System.out.printf("Ngeclose dulu\n");
//            this.client.close();
//        }
//        catch (IOException ex){
//            System.err.print(ex);
//        }

    }


}
