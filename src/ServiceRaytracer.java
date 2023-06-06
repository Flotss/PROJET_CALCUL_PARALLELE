import raytracer.Scene;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceRaytracer extends Remote {

    Image renderSubScene(Scene scene, int width, int height, int x1, int y1) throws RemoteException;

}
