import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ResponseWriter {
    /*
    ini tugasnya nge buat response header sama body,
    dia generate header like status ok, content length, type, etc sama ngirim si file yang diperluin/yang di generate
     */
    public Socket client;
    public ResponseWriter(Socket client){
        this.client = client;
    }

    public void writeResponse(){
        try{
            DataOutputStream bos = new DataOutputStream(this.client.getOutputStream());
            String response = "HTTP/1.0 200 OK\r\n";
            response+="content-length:22\r\n"; // 22 panjang <html>MantapBoi</html>
            response += "\r\n";
            // body
            response += "<html>MantapBoi</html>";
            response+= "\r\n";
            bos.write(response.getBytes());
            System.out.printf("Sudah ke kirim\n");
//            this.client.close();
        }
        catch (IOException ex){
            System.err.print(ex);
        }


    }

}
