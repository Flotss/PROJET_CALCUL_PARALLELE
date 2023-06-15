import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class RenderWorker implements Runnable {
    private final Disp disp;
    private final BlockingQueue<SubScene> subSceneQueue;
    private final ConcurrentHashMap<SubScene, ServiceRaytracer> failedSubScenes;
    private final ArrayList<ServiceRaytracer> servers;
    private final ServiceRaytracer server;
    private final Scene scene;

    public RenderWorker(Scene scene, Disp disp, BlockingQueue<SubScene> subSceneQueue, ConcurrentHashMap<SubScene, ServiceRaytracer> failedSubScenes, ArrayList<ServiceRaytracer> servers, ServiceRaytracer server) {
        this.scene = scene;
        this.disp = disp;
        this.subSceneQueue = subSceneQueue;
        this.failedSubScenes = failedSubScenes;
        this.servers = servers;
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            SubScene subScene = subSceneQueue.poll();
            if (subScene == null) {
                System.out.println("No more sub-scenes to render");
                break;
            }

            try {
                System.out.println("Rendering sub-scene " + subScene.getX() + " " + subScene.getY() + " " + subScene.getWidth() + " " + subScene.getHeight() + " on " + server);
                Image image = server.renderSubScene(scene, subScene.getWidth(), subScene.getHeight(), subScene.getX(), subScene.getY());
                disp.setImage(image, subScene.getX(), subScene.getY());
            } catch (RemoteException e) {
                System.err.println("Failed to render sub-scene " + subScene.getX() + " " + subScene.getY() + " " + subScene.getWidth() + " " + subScene.getHeight() + " on " + server);
                failedSubScenes.put(subScene, server);
            }
        }

        System.out.println("Adding back failed sub-scenes to queue");
        for (Map.Entry<SubScene, ServiceRaytracer> entry : failedSubScenes.entrySet()) {
            if (entry.getValue() == server) {
                subSceneQueue.add(entry.getKey());
            }
        }

        System.out.println("Removing server " + server + " from list");
        servers.remove(server);
    }
}