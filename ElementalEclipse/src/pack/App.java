package pack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class App extends Application {
	Box hitbox = new Box(20, 40, 20);
	Cube b = new Cube(25, 25, 25, 100, 0, 0);
    public static Group root = new Group();
    private double cameraRotationAngleX = 0;
    private double cameraRotationAngleY = 0;
    private double previousX, previousY;
    private final double sensitivity = 0.1;
    private final Robot robot;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    static Scene scene = new Scene(root, 800, 600, true);
    static ArrayList<Rect> fs = new ArrayList<Rect>();
    static ArrayList<Rect> bs = new ArrayList<Rect>();
    static ArrayList<Rect> us = new ArrayList<Rect>();
    static ArrayList<Rect> ds = new ArrayList<Rect>();
    static ArrayList<Rect> rs = new ArrayList<Rect>();
    static ArrayList<Rect> ls = new ArrayList<Rect>();
    static ArrayList<Box> blocks = new ArrayList<Box>();
    boolean suppressMouseMove;
    public App() throws AWTException {
        this.robot = new Robot(); // Create the robot for cursor control
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	blocks.add(b.box);
        camera.setNearClip(1);
        camera.setFarClip(2000);
        camera.getTransforms().addAll(rotateY, rotateX);
        camera.setFieldOfView(45);
        camera.setTranslateY(-75);
        
        
        // Root group

        // Set up the scene
        
        scene.setFill(Color.CYAN);
        scene.setCamera(camera);
        
//        b.setTranslateY(100);
    
        
//        root.getChildren().add(b.box);
        primaryStage.setTitle("3D Camera Control");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        AmbientLight light = new AmbientLight(Color.WHITE);
//        root.getChildren().add(light);
        setupMovement(scene, camera, primaryStage);

//        scene.setCursor(Cursor.NONE);

        // Initial position of the cursor
        previousX = scene.getWidth() / 2;
        previousY = scene.getHeight() / 2;
//        robot.mouseMove((int) previousX, (int) previousY);

        suppressMouseMove = false;

        scene.setOnMouseMoved(event -> {
            if (suppressMouseMove) {
                suppressMouseMove = false;
                return;
            }

            double deltaX = event.getSceneX() - (scene.getWidth() / 2);
            double deltaY = event.getSceneY() - (scene.getHeight() / 2);

            System.out.println("Delta X: " + deltaX);
            System.out.println("Delta Y: " + deltaY);

            cameraRotationAngleY += deltaX * sensitivity;
            cameraRotationAngleX -= deltaY * sensitivity;

            cameraRotationAngleX = clamp(cameraRotationAngleX, -90, 90);

            rotateY.setAngle(cameraRotationAngleY);
            rotateX.setAngle(cameraRotationAngleX);

            // Re-center the cursor
            suppressMouseMove = true;
            robot.mouseMove((int) (scene.getWidth() / 2), (int) (scene.getHeight() / 2));
        });
        scene.setOnMouseClicked(e -> {
        	var r = e.getPickResult().getIntersectedNode();
        	for (int i = 0; i < fs.size(); i++) {
        		if (r == fs.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()), (float)fs.get(i).c.box.getTranslateY(), (float)fs.get(i).c.box.getTranslateZ() - 25);
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        		else if (r == bs.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()), (float)fs.get(i).c.box.getTranslateY(), (float)fs.get(i).c.box.getTranslateZ() + 25);
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        		else if (r == ls.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()-25), (float)fs.get(i).c.box.getTranslateY(), (float)fs.get(i).c.box.getTranslateZ());
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        		else if (r == rs.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()+25), (float)fs.get(i).c.box.getTranslateY(), (float)fs.get(i).c.box.getTranslateZ());
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        		else if (r == us.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()), (float)fs.get(i).c.box.getTranslateY()-25, (float)fs.get(i).c.box.getTranslateZ());
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        		else if (r == ds.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()), (float)fs.get(i).c.box.getTranslateY()+25, (float)fs.get(i).c.box.getTranslateZ());
        			blocks.add(temp.box);
        			}
        			else {
        				blocks.remove(fs.get(i).c.box);
        				App.root.getChildren().remove(fs.get(i).c.box);
        	            App.root.getChildren().remove(fs.get(i).c.f.r);
        	            App.root.getChildren().remove(fs.get(i).c.b.r);
        	            App.root.getChildren().remove(fs.get(i).c.l.r);
        	            App.root.getChildren().remove(fs.get(i).c.r.r);
        	            App.root.getChildren().remove(fs.get(i).c.u.r);
        	            App.root.getChildren().remove(fs.get(i).c.d.r);
        			}
        		}
        	}
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
        hitbox.setTranslateY(camera.getTranslateY() + deltaY +10);
        hitbox.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
        boolean gotHit = false;
        for (Box block : blocks) {
        	if (T(hitbox, block)) {
            	gotHit = true;
                break;
            }
            
        }
        if (!gotHit) {
        	camera.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
        	camera.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
        	camera.setTranslateY(camera.getTranslateY() + deltaY);
        	hitbox.setTranslateX(camera.getTranslateX());
            hitbox.setTranslateY(camera.getTranslateY()+10);
            hitbox.setTranslateZ(camera.getTranslateZ());
        }
        
        
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
class Cube {
	Box box;
	Rect f;
	Rect b;
	Rect l;
	Rect r;
	Rect u;
	Rect d;
	int opacity = 00;
//	private void handleClick(MouseEvent e, String face, float newX, float newY, float newZ) {
//	    if (e.getButton() == MouseButton.SECONDARY) {
//	        Cube temp = new Cube(25, 25, 25, newX, newY, newZ);
////	        App.root.getChildren().add(temp.box);
//	        Toolkit.getDefaultToolkit().beep();
//	    }
//	    if (e.getButton() == MouseButton.PRIMARY) {
//	        // Schedule node removal to avoid interfering with JavaFX's event handling
//	        Platform.runLater(() -> {
//	            App.root.getChildren().remove(box);
//	            App.root.getChildren().remove(f);
//	            App.root.getChildren().remove(b);
//	            App.root.getChildren().remove(u);
//	            App.root.getChildren().remove(d);
//	            App.root.getChildren().remove(l);
//	            App.root.getChildren().remove(r);
//	        });
//	    }
//	}
	public Cube(float length, float width, float height, float x, float y, float z) {
		box = new Box(length, width, height);
		box.setTranslateX(x);
		box.setTranslateY(y);
		box.setTranslateZ(z);
		App.root.getChildren().add(box);
		f = new Rect(25, 25, this);
		f.setTranslateX(x-12.5);
		f.setTranslateY(y-12.5);
		f.setTranslateZ(z - 12.6);
		f.setOpacity(opacity);
		App.root.getChildren().add(f.r);
		App.fs.add(f);
		b = new Rect(25, 25, this);
		b.setTranslateX(x-12.5);
		b.setTranslateY(y-12.5);
		b.setTranslateZ(z + 12.6);
		b.setOpacity(opacity);
		App.root.getChildren().add(b.r);
		App.bs.add(b);
		l = new Rect(25, 25, this);
		Rotate lr = new Rotate(90);
		lr.setAxis(Rotate.Y_AXIS);
//		lr.setPivotX(l.getX() + (l.getWidth())/2);
//		lr.setPivotY(l.getY() + (l.getHeight())/2);
//		lr.setPivotZ(l.getTranslateZ());
		l.r.getTransforms().add(lr);
		l.setTranslateX(x - 12.6);
		l.setTranslateY(y-12.5);
		l.setTranslateZ(z+12.5);
		l.setOpacity(opacity);
		App.root.getChildren().add(l.r);
		App.ls.add(l);
		r = new Rect(25, 25, this);
		r.setTranslateX(x+12.6);
		r.setTranslateY(y-12.5);
		r.setTranslateZ(z+12.5);
		Rotate rr = new Rotate(90);
		rr.setAxis(Rotate.Y_AXIS);
//		rr.setPivotX(l.getX() + (l.getWidth())/2);
//		rr.setPivotY(l.getY() + (l.getHeight())/2);
//		rr.setPivotZ(z);
//		rr.setAngle(90);
		r.r.getTransforms().add(rr);
		r.setOpacity(opacity);
		App.root.getChildren().add(r.r);
		App.rs.add(r);
		u = new Rect(25, 25, this);
		Rotate ur = new Rotate(90);
		ur.setAxis(Rotate.X_AXIS);
//		ur.setPivotX(l.getX() + (l.getWidth())/2);
//		ur.setPivotY(l.getY() + (l.getHeight())/2);
//		ur.setPivotZ(l.getTranslateZ());
		u.r.getTransforms().add(ur);
		u.setTranslateX(x-12.5);
		u.setTranslateY(y-12.6);
		u.setTranslateZ(z-12.5);
		
		u.setOpacity(opacity);
		App.root.getChildren().add(u.r);
		App.us.add(u);
		d = new Rect(25, 25, this);
		Rotate dr = new Rotate(90);
		dr.setAxis(Rotate.X_AXIS);
//		dr.setPivotX(l.getX() + (l.getWidth())/2);
//		dr.setPivotY(l.getY() + (l.getHeight())/2);
//		dr.setPivotZ(l.getTranslateZ());
		d.r.getTransforms().add(dr);
		d.setTranslateX(x-12.5);
		d.setTranslateY(y+12.6);
		d.setTranslateZ(z-12.5);
		d.setOpacity(opacity);
		App.root.getChildren().add(d.r);
		App.ds.add(d);
		//
//		App.scene.setOnMouseClicked(e -> {
//			
//        	if (e.getPickResult().getIntersectedNode() == u) {
////        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x, y-25, z);
////    			App.root.getChildren().add(temp.box);
////    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        	if (e.getPickResult().getIntersectedNode() == d) {
//        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x, y+25, z);
////    			App.root.getChildren().add(temp.box);
//    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        	if (e.getPickResult().getIntersectedNode() == l) {
//        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x-25, y, z);
////    			App.root.getChildren().add(temp.box);
//    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        	if (e.getPickResult().getIntersectedNode() == r) {
//        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x+25, y, z);
////    			App.root.getChildren().add(temp.box);
//    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        	if (e.getPickResult().getIntersectedNode() == f) {
//        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x, y, z-25);
////    			App.root.getChildren().add(temp.box);
////    			System.out.println(Math.random()*100);
//    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        	if (e.getPickResult().getIntersectedNode() == b) {
//        		System.out.println("h");
//    			if (e.getButton() == MouseButton.SECONDARY) { 
//    			Cube temp = new Cube(25, 25, 25, x, y, z+25);
////    			App.root.getChildren().add(temp.box);
//    			Toolkit.getDefaultToolkit().beep();
//    			}
//    			if (e.getButton() == MouseButton.PRIMARY) {
//    				App.root.getChildren().remove(box);
//    				App.root.getChildren().remove(f);
//    				App.root.getChildren().remove(b);
//    				App.root.getChildren().remove(u);
//    				App.root.getChildren().remove(d);
//    				App.root.getChildren().remove(l);
//    				App.root.getChildren().remove(r);
//    			}
//        	}
//        });
//		App.scene.setOnMouseClicked(e -> {
//		    if (e.getPickResult().getIntersectedNode() == u) {
//		        handleClick(e, "u", x, y - 25, z);
//		    } else if (e.getPickResult().getIntersectedNode() == d) {
//		        handleClick(e, "d", x, y + 25, z);
//		    } else if (e.getPickResult().getIntersectedNode() == l) {
//		        handleClick(e, "l", x - 25, y, z);
//		    } else if (e.getPickResult().getIntersectedNode() == r) {
//		        handleClick(e, "r", x + 25, y, z);
//		    } else if (e.getPickResult().getIntersectedNode() == f.r) {
//		        handleClick(e, "f", x, y, z - 25);
//		    } else if (e.getPickResult().getIntersectedNode() == b) {
//		        handleClick(e, "b", x, y, z + 25);
//		    }
//		});
	}
	
}
class Rect {
	Rectangle r;
	Cube c;
	public Rect(int x, int y, Cube cc) {
		r = new Rectangle(x, y);
		c = cc;
	}
	public void setTranslateX(double x) {
		r.setTranslateX(x);
	}
	public void setTranslateY(double y) {
		r.setTranslateY(y);
	}
	public void setTranslateZ(double z) {
		r.setTranslateZ(z);
	}
	public ObservableList<Transform> getTransforms() {
		return r.getTransforms();
	}
	public void setOpacity(int o) {
		r.setOpacity(o);
	}
}