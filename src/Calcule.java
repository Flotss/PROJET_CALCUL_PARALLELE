import raytracer.Image;
import raytracer.Scene;

import java.rmi.RemoteException;

public class Calcule implements ServiceRaytracer{

    @Override
    public Image renderSubScene(Scene scene, int width, int height, int x1, int y1) throws RemoteException {
        System.out.println("ici");
        return scene.compute(x1, y1, width, height);
    }
}
