public class HTTPHeader {
    /*

    Ini untuk nampung hasil parsingan request header

    */
    public String host;
    public String method;
    public String path;
    public String connectionType;

    public HTTPHeader(String host, String method, String path) {
        this.host = host;
        this.method = method;
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
