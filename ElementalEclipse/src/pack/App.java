package pack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.util.Arrays;

public class App extends Application {
	Box hitbox = new Box(25, 62.5, 25);
	Box b = new Box(25, 25, 25);
    private double cameraRotationAngleX = 0;
    private double cameraRotationAngleY = 0;
    private double previousX, previousY;
    private final double sensitivity = 0.2;
    private final Robot robot;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    public App() throws AWTException {
        this.robot = new Robot(); // Create the robot for cursor control
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        camera.setNearClip(1);
        camera.setFarClip(2000);
        camera.getTransforms().addAll(rotateY, rotateX);
        camera.setFieldOfView(45);
        camera.setTranslateY(-75);
        
        
        // Root group
        javafx.scene.Group root = new javafx.scene.Group();

        // Set up the scene
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.CYAN);
        scene.setCamera(camera);
        
        b.setTranslateY(100);
    
        
        root.getChildren().add(b);
        primaryStage.setTitle("3D Camera Control");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        
        setupMovement(scene, camera, primaryStage);

        scene.setCursor(Cursor.NONE);

        // Initial position of the cursor
        previousX = scene.getWidth() / 2;
        previousY = scene.getHeight() / 2;
        robot.mouseMove((int) previousX, (int) previousY);

        scene.setOnMouseMoved(event -> {
            double currentX = event.getSceneX();
            double currentY = event.getSceneY();

            double deltaX = currentX - previousX;
            double deltaY = currentY - previousY;

            // Update rotation angles
            cameraRotationAngleY += deltaX * sensitivity; // Horizontal rotation (yaw)
            cameraRotationAngleX -= deltaY * sensitivity; // Vertical rotation (pitch)

            // Clamp vertical rotation (pitch) to avoid flipping the camera
            cameraRotationAngleX = clamp(cameraRotationAngleX, -90, 90);

            // Apply rotations to camera
            rotateY.setAngle(cameraRotationAngleY);
            rotateX.setAngle(cameraRotationAngleX);

            // Move the mouse cursor back to the center of the screen
            robot.mouseMove((int) previousX, (int) previousY);
            previousX = (int) previousX;
            previousY = (int) previousY;
        });
    }

    private void setupMovement(Scene scene, PerspectiveCamera camera, Stage primaryStage) {
        Timeline moveF = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, 0, 2, 0);
        }));
        moveF.setCycleCount(Timeline.INDEFINITE);

        Timeline moveR = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, 2, 0, 0);
        }));
        moveR.setCycleCount(Timeline.INDEFINITE);

        Timeline moveB = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, 0, -2, 0);
        }));
        moveB.setCycleCount(Timeline.INDEFINITE);

        Timeline moveL = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, -2, 0, 0);
        }));
        moveL.setCycleCount(Timeline.INDEFINITE);

        Timeline moveU = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, 0, 0, -2);
        }));
        moveU.setCycleCount(Timeline.INDEFINITE);

        Timeline moveD = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            moveCamera(camera, 0, 0, 2);
        }));
        moveD.setCycleCount(Timeline.INDEFINITE);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) moveF.play();
            if (e.getCode() == KeyCode.D) moveR.play();
            if (e.getCode() == KeyCode.S) moveB.play();
            if (e.getCode() == KeyCode.A) moveL.play();
            if (e.getCode() == KeyCode.SPACE) moveU.play();
            if (e.getCode() == KeyCode.SHIFT) moveD.play();
            if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) moveF.stop();
            if (e.getCode() == KeyCode.D) moveR.stop();
            if (e.getCode() == KeyCode.S) moveB.stop();
            if (e.getCode() == KeyCode.A) moveL.stop();
            if (e.getCode() == KeyCode.SPACE) moveU.stop();
            if (e.getCode() == KeyCode.SHIFT) moveD.stop();
        });
    }

    private void moveCamera(PerspectiveCamera camera, double deltaX, double deltaZ, double deltaY) {
        double yaw = Math.toRadians(cameraRotationAngleY);
        
        // Calculate forward and right vectors based on yaw angle
        double forwardX = Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = Math.cos(yaw);
        double rightZ = -Math.sin(yaw);
        hitbox.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
        hitbox.setTranslateY(camera.getTranslateY() + deltaY);
        hitbox.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
        if (!T(hitbox, b)) {
        	 
        	camera.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
            camera.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
            camera.setTranslateY(camera.getTranslateY() + deltaY);
           
        }
        hitbox.setTranslateX(camera.getTranslateX());
        hitbox.setTranslateY(camera.getTranslateY());
        hitbox.setTranslateZ(camera.getTranslateZ());
        
//        System.out.println(T(hitbox, b));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public static boolean T(Box box1, Box box2) {
        // Calculate boundaries for box1
        double box1MinX = box1.getTranslateX() - box1.getWidth() / 2;
        double box1MaxX = box1.getTranslateX() + box1.getWidth() / 2;
        double box1MinY = box1.getTranslateY() - box1.getHeight() / 2;
        double box1MaxY = box1.getTranslateY() + box1.getHeight() / 2;
        double box1MinZ = box1.getTranslateZ() - box1.getDepth() / 2;
        double box1MaxZ = box1.getTranslateZ() + box1.getDepth() / 2;

        // Calculate boundaries for box2
        double box2MinX = box2.getTranslateX() - box2.getWidth() / 2;
        double box2MaxX = box2.getTranslateX() + box2.getWidth() / 2;
        double box2MinY = box2.getTranslateY() - box2.getHeight() / 2;
        double box2MaxY = box2.getTranslateY() + box2.getHeight() / 2;
        double box2MinZ = box2.getTranslateZ() - box2.getDepth() / 2;
        double box2MaxZ = box2.getTranslateZ() + box2.getDepth() / 2;

        // Check for overlap in all three dimensions
        boolean isOverlappingX = box1MaxX >= box2MinX && box1MinX <= box2MaxX;
        boolean isOverlappingY = box1MaxY >= box2MinY && box1MinY <= box2MaxY;
        boolean isOverlappingZ = box1MaxZ >= box2MinZ && box1MinZ <= box2MaxZ;

        // The boxes are touching if they overlap in all three dimensions
        return isOverlappingX && isOverlappingY && isOverlappingZ;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
