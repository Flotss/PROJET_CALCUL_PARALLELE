import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {

    List<ServiceRaytracer> services = new ArrayList<>();

    public Client() throws IOException {
        Files.readAllLines(new File("resources/server_list.txt").toPath() ).forEach( line -> {
            try {
                String[] parts = line.split(":");
                Registry registry = LocateRegistry.getRegistry(parts[0], Integer.parseInt(parts[1]));
                services.add((ServiceRaytracer) registry.lookup("raytracer"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) throws RemoteException {



    }
}
