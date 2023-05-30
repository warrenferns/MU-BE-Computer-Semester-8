import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private Client() {
    }

    public static void main(String[] args) {
        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(null);

            // Looking up the registry for the remote object
            Adder stub = (Adder) registry.lookup("Hello");

            // Calling the remote method using the obtained object
            int result = stub.add(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            System.out.println("Result From Server: " + result);

            // System.out.println("Remote method invoked");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
