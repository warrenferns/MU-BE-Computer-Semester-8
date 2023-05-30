import java.io.*;
import java.net.*;

public class Slave {
    public static void main(String args[]) throws Exception {
        String r = "";
        Socket MyClient = new Socket("127.0.0.1", 8881);
        System.out.println("Connected as Slave");
        DataInputStream din = new DataInputStream(MyClient.getInputStream());
        do {
            r = din.readUTF();
            System.out.println("Master says: " + r);
        } while (!r.equals("stop"));
        din.close();

        MyClient.close();
    }
}
