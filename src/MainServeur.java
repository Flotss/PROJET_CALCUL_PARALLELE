import utils.Destination;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServeur {

    public static void main(String[] args) throws Exception {
        Destination destination = Destination.pickDestination(args);

        Server raytracer = new Server();
        ServiceRaytracer sr = (ServiceRaytracer) UnicastRemoteObject.exportObject(raytracer, 0);

        Registry reg = LocateRegistry.getRegistry(destination.getHost(), destination.getPort());
        reg.rebind("raytracer", sr);

        System.out.println("\n" + reg);
    }

}
