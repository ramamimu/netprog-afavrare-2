import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientRequestReader {
    /*
    ini kelas buat ngeparsing si request header dari client
    rencananya ini bakal di buat sama client handler, terus dia makek ini buat read header
    nanti di sini ada fungsi get header yang return http header
    si dari http header yang di parsing sama kelas ini bakal di pakek sama client handler
    buat ngehandle apapun yang diminta
     */

    public Socket client;
    public DataInputStream bis;

    public ClientRequestReader(Socket client){
//        this.client = client;
//        try{
//            this.bis = new DataInputStream(this.client.getInputStream());
//        }
//        catch (IOException ex){
//            throw(ex);
//        }


    }

    public ClientRequestMsg readRequest(){

        // loop baca request



        return new ClientRequestMsg();
    }

}
