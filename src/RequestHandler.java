import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RequestHandler extends Thread {     
// public class RequestHandler {
    Socket socket;
    String clientRequest;
    ClientRequestMsg requestMsg;
    Lock lock;
    Map<String, String> hostRootMap;

    /**
     * Construct
     * @param s, the socket which is to be monitored
     */
    public RequestHandler (Socket s)
    {
        socket = s;
        // locker for multi-threading
        this.lock = new ReentrantLock();
        this.requestMsg = new ClientRequestMsg();
        this.hostRootMap = new HashMap<>();

        // read config
        try {
            BufferedReader reader = new BufferedReader(new FileReader("config/config.txt"));
            String line = "empty";
            String curhost = "";
            String curroot = "";
            int counter = 1;
            while (true) {
                line = reader.readLine();
                if(line.equals("END")){
                    break;
                }
                if(line.startsWith("#") || line.equals("")){
                    continue; // skip line comment sama newline kosong
                }

                if(counter==1){
                    // baca host
                    curhost = line.substring(5);

                }
                else if(counter == 2){
                    curroot = line.substring(5);

                }
                else{
                    this.hostRootMap.put(curhost, curroot);
                    counter=0;
//                    System.out.printf("Obtain domain %s --> %s\n", curhost, curroot);
                }

                counter++;
            }
            reader.close();
        } catch (IOException ex) {
            System.err.print(ex);
        }
    }

    /**
     * Start to work, after being assigned tasks by the server
     */
    public void run(){
        try{
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            this.clientRequest = "";
            String clientRequest = "";

            // read the method, path, and the http version (not taht it matter)
            clientRequest = reader.readLine();
            System.out.printf("got %s\n", clientRequest);
            if(clientRequest.contains("GET")){
                this.requestMsg.method = "GET";
            }else if(clientRequest.contains("POST")){
                this.requestMsg.method = "POST";
            }else{
                this.requestMsg.method = "Uknown";
            }
            System.out.printf("Method is %s\n", this.requestMsg.method);

            int startSpaceId = clientRequest.indexOf(" ");
            int lastSpaceId = clientRequest.indexOf(" ", startSpaceId+1);
            String path = clientRequest.substring(startSpaceId, lastSpaceId);
            this.requestMsg.path = path.trim();
            System.out.printf("We got path %s\n", this.requestMsg.path);


            while ((clientRequest = reader.readLine()) != null) {
                if(clientRequest.toLowerCase().contains("host:")){
                    String host = clientRequest.substring(5).trim();
                    System.out.printf("te host is %s \n", host);
                    this.requestMsg.host = host;
                }
                else if(clientRequest.toLowerCase().contains("connection:")){
                    String connection = clientRequest.substring(11).trim();
                    this.requestMsg.connectionType =connection;
                    System.out.printf("Connection is %s \n", connection);
                }

                if(clientRequest.equals("")){
                    break;
                }

            }

            // lock.lock();
            // System.out.println("inner req: " + this.clientRequest);
            // lock.unlock();
            PrintStream printer = new PrintStream(socket.getOutputStream());

            // Get the request file path
//            String req = this.clientRequest.substring(4, this.clientRequest.length()-9).trim();
//            String req = path.trim();
//            System.out.printf("We obtain %s from %s\n", req, this.clientRequest);
            // Handle requests
            if (this.requestMsg.path.indexOf(".")>-1) { // Request for single file
                lock.lock();
                System.out.println("MASUK 1 ");
                lock.unlock();
                handleFileRequest(this.requestMsg.path, printer);
            }
            else { 
                lock.lock();
                System.out.println("MASUK 2 ");
                lock.unlock();
                handleExploreRequest(this.requestMsg.path, printer);
            }
            /*
             * This timer to show that our code is multithread
             
             try {
                 TimeUnit.SECONDS.sleep(10);
             } catch (InterruptedException e) {
                 System.out.println(e);
             }
             
             */
            
             // socket.close();
        }
        catch(IOException ex){
            // Handle the exception
            System.out.println(ex);
        } 
    }

    /**
     * Handle single file request
     * @param req, get request from client
     * @param printer, output printer
     */
    private void handleFileRequest(String req, PrintStream printer) throws FileNotFoundException, IOException {
        // Get the root folder of the webserver
        String rootDir = getRootFolder();
        String websiteRoot = this.hostRootMap.get(this.requestMsg.host);
        // Get the real file path
//        String path = Paths.get(rootDir, websiteRoot, req).toString();
        String path = rootDir + File.separator + websiteRoot + File.separator + req;
        System.out.printf("PATH %s\n", path);

        // Try to open the file
        File file = new File(path);
        if (!file.exists() || !file.isFile()) { // If not exists or not a file
            printer.println("No such resource:" + req);
            // LogUtil.write(">> No such resource:" + req);
        }
        else { // It's a file
            if (!req.startsWith("/images/")&&!req.startsWith("/favicon.ico")) {
                // LogUtil.write(">> Seek the content of file: " + file.getName());
            }
            // Print header
            String htmlHeader = buildHttpHeader(path, file.length());
            printer.println(htmlHeader);

            // Open file to input stream
            InputStream fs = new FileInputStream(file);
            byte[] buffer = new byte[1000];
            while (fs.available()>0) {
                printer.write(buffer, 0, fs.read(buffer));
            }
            fs.close();
        }
    }

    /**
     * Handle file and directory explore request
     * @param req, get request from client
     * @param printer, output printer
     */
    private void handleExploreRequest(String req, PrintStream printer) {
        // Get the root folder of the webserver
        String rootDir = getRootFolder();
        // Get the real file path
//        String path = Paths.get(rootDir, req).toString();
        String websiteRoot = this.hostRootMap.get(this.requestMsg.host);
        String path = rootDir + File.separator + websiteRoot + File.separator + req;
        System.out.printf("PATH %s\n", path);
        // Try to open the directory
        File file = new File (path) ;
        if (!file.exists()) { // If the directory does not exist
            printer.println("No such resource:" + req);
            // LogUtil.write(">> No such resource:" + req);
        }
        else { // If exists
            // LogUtil.write(">> Explore the content under folder: " + file.getName());
            // Get all the files and directory under current directory
            File[] files = file.listFiles();
            Arrays.sort(files);

            // detect index.html
            for (File f: files) {
                if(f.getName().toLowerCase().equals("index.html")){
                    try{
                        handleFileRequest(req + File.separator + f.getName(), printer);
                    }
                    catch (IOException e){
                        System.err.print(e);
                    }
                    return;
                }
            }
            
            // Build file/directory structure in html format
            StringBuilder sbDirHtml = new StringBuilder();
            // Title line
            sbDirHtml.append("<table>");
            sbDirHtml.append("<tr>");
            sbDirHtml.append("  <th>Name</th>");
            sbDirHtml.append("  <th>Last Modified</th>");
            sbDirHtml.append("  <th>Size(Bytes)</th>");
            sbDirHtml.append("</tr>");

            // Parent folder, show it if current directory is not root
            if (!path.equals(rootDir)) {
                String parent = path.substring(0, path.lastIndexOf(File.separator));
                if (parent.equals(rootDir)) { // The first level
                    parent = "../";
                }
                else { // The second or deeper levels
                    parent = parent.replace(rootDir, "");
                }
                // Replace backslash to slash
                parent = parent.replace("\\", "/");
                // Parent line
                sbDirHtml.append("<tr>");
                sbDirHtml.append("  <td><img src=\""+buildImageLink(req,"images/folder.png")+"\"></img><a href=\"" + parent +"\">../</a></td>");
                sbDirHtml.append("  <td></td>");
                sbDirHtml.append("  <td></td>");
                sbDirHtml.append("</tr>");
            }

            // Build lines for directories
            List<File> folders = getFileByType(files, true);
            for (File folder: folders) {
                // LogUtil.write(">>> Directory: " + folder.getName());
                sbDirHtml.append("<tr>");
                sbDirHtml.append("  <td><img src=\""+buildImageLink(req,"images/folder.png")+"\"></img><a href=\""+buildRelativeLink(req, folder.getName())+"\">"+folder.getName()+"</a></td>");
                sbDirHtml.append("  <td>" + getFormattedDate(folder.lastModified()) + "</td>");
                sbDirHtml.append("  <td></td>");
                sbDirHtml.append("</tr>");
            }
            // Build lines for files
            List<File> fileList = getFileByType(files, false);
            for (File f: fileList) {
                // LogUtil.write(">>> File: " + f.getName());
                sbDirHtml.append("<tr>");
                sbDirHtml.append("  <td><img src=\""+buildImageLink(req, getFileImage(f.getName()))+"\" width=\"16\"></img><a href=\""+buildRelativeLink(req, f.getName())+"\">"+f.getName()+"</a></td>");
                sbDirHtml.append("  <td>" + getFormattedDate(f.lastModified()) + "</td>");
                sbDirHtml.append("  <td>" + f.length() + "</td>");
                sbDirHtml.append("</tr>");
            }

            sbDirHtml.append("</table>");
            String htmlPage = buildHtmlPage(sbDirHtml.toString(), "");
            String htmlHeader = buildHttpHeader(path, htmlPage.length());
            printer.println(htmlHeader);
            printer.println(htmlPage);
        }
    }

    /**
     * Build http header
     * @param path, path of the request
     * @param length, length of the content
     * @return, header text
     */
    private String buildHttpHeader(String path, long length) {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("HTTP/1.1 200 OK");
        sbHtml.append("\r\n");
        sbHtml.append("Content-Length: " + length);
        sbHtml.append("\r\n");
        sbHtml.append("Content-Type: "+ getContentType(path));
        sbHtml.append("\r\n");
        return sbHtml.toString();
    }

    /**
     * Build http page
     * @param content, content of the page
     * @param header1, h1 content
     * @return, page text
     */
    private String buildHtmlPage(String content, String header) {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("<!DOCTYPE html>");
        sbHtml.append("<html>");
        sbHtml.append("<head>");
        sbHtml.append("<style>");
        sbHtml.append(" table { width:50%; } ");
        sbHtml.append(" th, td { padding: 3px; text-align: left; }");
        sbHtml.append("</style>");
        sbHtml.append("<title>My Web Server</title>");
        sbHtml.append("</head>");
        sbHtml.append("<body>");
        if (header != null && !header.isEmpty()) {
            sbHtml.append("<h1>" + header + "</h1>");
        }
        else {
            sbHtml.append("<h1>File Explorer in Web Server </h1>");
        }
        sbHtml.append(content);
        sbHtml.append("<hr>");
        sbHtml.append("<p>*This page is returned by Web Server.</p>");
        sbHtml.append("</body>");
        sbHtml.append("</html>");
        return sbHtml.toString();
    }

    /**
     * Build error page for bad request
     * @param code, http cde: 400, 301, 200
     * @param title, page title
     * @param msg, error message
     * @return, page text
     */
     private String buildErrorPage(String code, String title, String msg) {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("HTTP/1.1 " + code + " " + title + "\r\n\r\n");
        sbHtml.append("<!DOCTYPE html>");
        sbHtml.append("<html>");
        sbHtml.append("<head>");
        sbHtml.append("<title>" + code + " " + title + "</title>");
        sbHtml.append("</head>");
        sbHtml.append("<body>");
        sbHtml.append("<h1>" + code + " " + title + "</h1>");
        sbHtml.append("<p>" + msg + "</p>");
        sbHtml.append("<hr>");
        sbHtml.append("<p>*This page is returned by Web Server.</p>");
        sbHtml.append("</body>");
        sbHtml.append("</html>");
        return sbHtml.toString();
    }

     /**
     * Get file or directory list
     * @param filelist, original file/directory list
     * @param isfolder, flag indicates looking for file or directory list
     * @return, file/directory list
     */
    private List<File> getFileByType(File[] filelist, boolean isfolder) {
        List<File> files = new ArrayList<File>();
        if (filelist == null || filelist.length == 0) {
            return files;
        }

        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory() && isfolder) {
                files.add(filelist[i]);
            }
            else if (filelist[i].isFile() && !isfolder) {
                files.add(filelist[i]);
            }
        }
        return files;
    }

     /**
     * Get root path
     * @return, path of the current location
     */
    private String getRootFolder() {
        String root = "";
        try{
            File f = new File(".");
            root = f.getCanonicalPath();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return root;
    }

    /**
     * Convert date to specified format
     * @param lastmodified, long value represents date
     * @return, formatted date in string
     */
    private String getFormattedDate(long lastmodified) {
        if (lastmodified < 0) {
            return "";
        }

        Date lm = new Date(lastmodified);
        String lasmod = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lm);
        return lasmod;
    }

    /**
     * Build relative link
     * @param current, current request
     * @param filename, file name
    * @return, formatted file name
    */
    private String buildRelativeLink(String req, String filename) {
        if (req == null || req.equals("") || req.equals("/")) {
            return filename;
        }
        else {
            return req + "/" +filename;
        }
    }

    /**
     * Build image link for icons
     * @param current, current request
     * @param filename, file name
     * @return, formatted file name
     */
    private String buildImageLink(String req, String filename) {
        if (req == null || req.equals("") || req.equals("/")) {
            return filename;
        }
        else {
            String imageLink = filename;
            for(int i = 0; i < req.length(); i++) {
                if (req.charAt(i) == '/') {
                    // For each downstairs level, need a upstairs level path
                    imageLink = "../" + imageLink;
                }
            }
            return imageLink;
        }
    }

    /**
     * Get file icon according to its extension
     * @param path, file path
     * @return, icon path
     */
    private static String getFileImage(String path) {
        if (path == null || path.equals("") || path.lastIndexOf(".") < 0) {
            return "images/file.png";
        }

        String extension = path.substring(path.lastIndexOf("."));
        switch(extension) {
            case ".class":
                return "images/class.png";
            case ".html":
                return "images/html.png";
            case ".java":
                return "images/java.png";
            case ".txt":
                return "images/text.png";
            case ".xml":
                return "images/xml.png";
            default:
                return "images/file.png";
        }
    }

    /**
     * Get MIME type according to file extension
     * @param path, file path
     * @return, MIME type
     */
    private static String getContentType(String path) {
        if (path == null || path.equals("") || path.lastIndexOf(".") < 0) {
            return "text/html";
        }

        String extension = path.substring(path.lastIndexOf("."));
        switch(extension) {
            case ".html":
            case ".htm":
                return "text/html";
            case ".txt":
                return "text/plain";
            case ".ico":
                return "image/x-icon .ico";
            case ".wml":
                return "text/html"; //text/vnd.wap.wml
            default:
                return "text/plain";
        }
    }
}
