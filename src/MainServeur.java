import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.Instant;

public class MainServeur {

    public static void main(String[] args) throws RemoteException {

        int port = 1099;
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }

        ServiceRaytracer sr = (ServiceRaytracer) UnicastRemoteObject.exportObject(new Calcule(), 0);
        Registry reg = LocateRegistry.createRegistry(port);
        reg.rebind("calcul", sr);
        System.out.println("\n"+reg);

    }
}
