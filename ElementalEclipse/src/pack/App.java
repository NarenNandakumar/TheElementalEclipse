package pack;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create a 3D box
    	Rotate x = new Rotate(0, Rotate.Y_AXIS);
    	Rotate y = new Rotate(0, Rotate.X_AXIS);
        Box box = new Box(100, 100, 100); // Width, Height, Depth
        Box box2 = new Box(20, 10, 30);
        // Apply material to the box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUE); // Set the color of the box
        material.setSpecularColor(Color.LIGHTBLUE); // Add some light reflection
        box.setMaterial(material);
        
        // Position the box
        box.setTranslateX(0); // Move it along X-axis
        box.setTranslateY(0); // Move it along Y-axis
        box.setTranslateZ(-100);   // Move it along Z-axis (closer to or farther from the camera)
        
        box2.setTranslateX(0);
        box2.setTranslateY(0);
        box2.setTranslateZ(-150);

        // Create a group and add the box to it
        Group root = new Group(box);
        
        root.getChildren().add(box2);
        
        // Create a camera for better 3D rendering
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500); // Position the camera
        camera.setTranslateX(100);
        camera.setFarClip(1000.0D);
        // Create a scene and add the group to it
        Scene scene = new Scene(root, 1200, 800, true); // Enable depth buffer with `true`
        scene.setFill(Color.GRAY); // Background color
        scene.setCamera(camera);
        
        // Set up the stage
        primaryStage.setTitle("3D Box in JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

