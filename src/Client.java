import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) throws RemoteException {

        ArrayList<ServiceRaytracer> serveurs = new ArrayList<ServiceRaytracer>();

        String[] serveur = new String[]{"localhost"};    // par défaut le serveur est sur la même machine
        int[] port = new int[]{1099, 8080, 8081, 8082};                      // le port de la rmiregistry par défaut
        /*if(args.length > 0)
            serveur[0]=args[0];
        if(args.length > 1)
            port=Integer.parseInt(args[1]);*/
        if (args.length > 3) {
            serveur = args;
        }

        try {
            //addition = (ServiceCalcul) LocateRegistry.getRegistry(serveur[0], port).lookup("Addition");
            serveurs.add((ServiceRaytracer) LocateRegistry.getRegistry(serveur[0], port[0]).lookup("calcul"));
            serveurs.add((ServiceRaytracer) LocateRegistry.getRegistry(serveur[0], port[1]).lookup("calcul"));
            serveurs.add((ServiceRaytracer) LocateRegistry.getRegistry(serveur[0], port[2]).lookup("calcul"));
            serveurs.add((ServiceRaytracer) LocateRegistry.getRegistry(serveur[0], port[3]).lookup("calcul"));
        } catch (Exception e) {
            System.out.println(e.getClass().toString());
        }

        String fichier_description = "resources/copy.txt";

        int largeur = 4000, hauteur = 4000;

        Disp disp = new Disp("Raytracer", largeur, hauteur);

        Scene scene = new Scene(fichier_description, largeur, hauteur);

        System.out.println("debut");

        new Thread(() -> {
            try {
                disp.setImage(serveurs.get(0).renderSubScene(scene, 2000, 2000, 0, 0), 0, 0);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                disp.setImage(serveurs.get(2).renderSubScene(scene, 2000, 2000, 0, 2000), 0, 2000);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                disp.setImage(serveurs.get(1).renderSubScene(scene, 2000, 2000, 2000, 0), 2000, 0);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                disp.setImage(serveurs.get(3).renderSubScene(scene, 2000, 2000, 2000, 2000), 2000, 2000);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();

/*        System.out.println("debut");

        Instant debut = Instant.now();

        Image image = scene.compute(2000, 2000, 2000, 2000);

        Instant fin = Instant.now();

        long duree = Duration.between(debut, fin).toMillis();

        System.out.println("Image calculée en :" + duree + " ms");
        disp.setImage(image, 2000, 2000);*/

    }
}
