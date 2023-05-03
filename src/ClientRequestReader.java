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

    public ClientRequestReader(Socket client){
        this.client = client;
    }

    public ClientRequestMsg readRequest(){

        // loop baca request
        return new ClientRequestMsg();
    }

}
