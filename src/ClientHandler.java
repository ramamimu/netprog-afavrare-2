import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        // System.out.println(requestMsg);
    }

    @Override
    public void run(){
        // to be implemented
        // System.out.printf("Jalan [%d]\n", this.id);

        // loop reaad header + response
        while (true){
            try {
                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
                // read header
                String req = "";
                String clientRequest = "";
                while ((clientRequest = reader.readLine()) != null) {
                    if (req.equals("")) {
                            req  = clientRequest;
                    }
                    if (clientRequest.equals("")) {
                            break;
                    }
                }
                System.out.println(req);
                // System.out.println(clientRequest);
                System.out.printf("Accepted\n");

                String methods = req.substring(0, 3);
                String path = req.substring(4, req.length()-9).trim();
                String httpVersion = req.substring(req.length()-8, req.length()-1);

                ClientRequestMsg requestMsg =  this.requestReader.readRequest();
                requestMsg.setMethod(methods);
                requestMsg.setPath(path);
                requestMsg.setHttpVersion(httpVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }				
    
            // System.out.println(requestMsg);
            this.responseWriter.writeResponse();
            break; // sementara di break
        }
    }


}
