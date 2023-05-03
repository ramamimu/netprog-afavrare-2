import java.net.Socket;

public class ClientHandler extends Thread{
    /*
    Jadi ini yang bakal ngehandle si client, di sini bakal ambil client, terus parsing header, response, loop etc
     */

    public Socket client;
    public int id; // perlu ga perlu si, isi aja dulu

    public ClientHandler(Socket client, int id) {
        this.client = client;
        this.id = id;
    }

    @Override
    public void run(){
        // to be implemented
        System.out.printf("Jalan [%d]\n", this.id);

        // loop reaad header + response

//        for(int i  = 0;i<20;i++){
//            System.out.printf("Ok t1 ni bozz %d\n", i);
//            try{
//                Thread.sleep(20);
//            }
//            catch (InterruptedException ex){
//                System.err.print(ex);
//            }
//        }
    }


}
