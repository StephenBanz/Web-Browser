package blah;


        import java.io.IOException;
        import java.net.InetAddress;
        import java.net.URI;
        import java.net.URL;
        import java.net.UnknownHostException;

public class url {
    public static void main(String[] args){
        String a="www.india.gov.in";
        String b="www.m.facebook.com/a/language.php?l=te_IN&lref=https%3A%2F%2Fm.facebook.com%2F%3Frefsrc%3Dhttps%253A%252F%252Fm.facebook.com%252F&gfid=AQCfkBtdBhl37217&refid=8";
        //System.out.println(b.substring(7));
        try{url.getIp(a);
        url.getIp(b);}catch(Exception e){}
    }



    public static String getIp( String hostname ) throws IOException {      //gets the IP of URL entered
        try {
            InetAddress ipaddress = InetAddress.getByName(hostname);
            System.out.println("IP address: " + ipaddress.getHostAddress());
            return ipaddress.getHostAddress();
        }
        catch ( UnknownHostException e ){
            System.out.println("Could not find IP address for: " + hostname);
            throw new IOException("Could not find IP address for: " + hostname);
        }
    }
    }
