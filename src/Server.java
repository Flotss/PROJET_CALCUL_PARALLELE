import raytracer.Image;
import raytracer.Scene;

import java.rmi.RemoteException;

public class Server implements ServiceRaytracer{
    final int minSubWidth = 5, minSubHeight = 5;

    /**
     * @param scene Scene to render
     * @param width Width of the subscene
     * @param height Height of the subscene
     * @param x1 x coordinate of the subscene
     * @param y1 y coordinate of the subscene
     * @return Rendered Image from the subscene
     * @throws RemoteException If the server is not available
     */
    @Override
    public Image renderSubScene(Scene scene, int width, int height, int x1, int y1) throws RemoteException {
        System.out.println("Rendering subscene (" + x1 + ", " + y1 + ") (" + (x1 + width) + ", " + (y1 + height) + ")");
        return scene.compute(x1, y1, width, height);
    }

}
