import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Client {

    private final int width, height;
    private final ArrayList<ServiceRaytracer> servers = new ArrayList<>();
    private final Scene scene;

    /**
     * @param width          Width of the image
     * @param height         Height of the image
     * @param sceneFile      Scene to render
     * @param serverListFile List of servers to use
     * @throws IOException If the server list file is not found
     */
    public Client(int width, int height, File sceneFile, File serverListFile) throws IOException {
        this.width = width;
        this.height = height;
        this.scene = new Scene(sceneFile.getAbsolutePath(), width, height);

        for (String registryAddress : Files.readAllLines(serverListFile.toPath())) {
            try {
                ServiceRaytracer raytracer = getServer(registryAddress);
                servers.add(raytracer);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * @param registryAddress Address of the registry (127.0.0.1:1099)
     * @return ServiceRaytracer from the registry
     * @throws Exception If the registry is not found or if the registry doesn't have a raytracer service / an invalid service
     */
    private ServiceRaytracer getServer(String registryAddress) throws Exception {
        String[] address = registryAddress.split(":");
        Registry registry;

        try {
            registry = LocateRegistry.getRegistry(address[0], Integer.parseInt(address[1]));
        } catch (RemoteException e) {
            throw new Exception(address[0] + ":" + address[1] + " registry not found");
        }

        Remote raytracer;
        try {
            raytracer = registry.lookup("raytracer");
        } catch (NotBoundException e) {
            throw new Exception(address[0] + ":" + address[1] + " registry doesn't have a raytracer service");
        }

        if (!(raytracer instanceof ServiceRaytracer)) {
            throw new Exception(address[0] + ":" + address[1] + " registry doesn't have a raytracer service");
        }

        return (ServiceRaytracer) raytracer;
    }

    /**
     * Starts the worker to render the image
     * It will split the image in X subscenes, where X is the number of servers
     * and ask each server to render a subscene, each in a different thread to render the image faster
     * The method will be blocking until all the threads are finished
     *
     * @param disp Disp to render the image
     */
    private void startWorker(Disp disp) {
        int subSceneWidth = width / servers.size();
        int subSceneHeight = height;

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < servers.size(); i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    Image subScene = servers.get(finalI).renderSubScene(scene, subSceneWidth, subSceneHeight, finalI * subSceneWidth, 0);
                    disp.setImage(subScene, finalI * subSceneWidth, 0);
                } catch (RemoteException e) {
                    System.err.println(e.getMessage());
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final int WIDTH = 1920, HEIGHT = 1080;

        File sceneFile = new File("resources/simple.txt");
        File serverListFile = new File("resources/server_list.txt");
        Client client = new Client(WIDTH, HEIGHT, sceneFile, serverListFile);

        Disp disp = new Disp("Raytracer", WIDTH, HEIGHT);

        Instant debut = Instant.now();
        client.startWorker(disp);
        Instant fin = Instant.now();

        long duree = Duration.between(debut, fin).toMillis();

        System.out.println("Image calculée en :" + duree + " ms");
    }
}
