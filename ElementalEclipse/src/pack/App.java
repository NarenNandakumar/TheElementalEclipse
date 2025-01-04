package pack;

import javafx.animation.KeyFrame;
import javafx.scene.image.Image;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App extends Application {
	Box hitbox = new Box(15, 50, 15);
//	Cube b = new Cube(25, 25, 25, 100, 0, 0);
    public static Group root = new Group();
    private static double cameraRotationAngleX = 0;
    private static double cameraRotationAngleY = 0;
    private double previousX, previousY;
    private final static double sensitivity = 0.1;
    private static Robot robot;
    public static PerspectiveCamera camera = new PerspectiveCamera(true);
    static Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    static Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    static Scene scene = new Scene(root, 800, 600, true);
//    static Scene d2 = new Scene(root, 800, 600, true);
    static ArrayList<Rect> fs = new ArrayList<Rect>();
    
    static ArrayList<Rect> bs = new ArrayList<Rect>();
    static ArrayList<Rect> us = new ArrayList<Rect>();
    static ArrayList<Rect> ds = new ArrayList<Rect>();
    static ArrayList<Rect> rs = new ArrayList<Rect>();
    static ArrayList<Rect> ls = new ArrayList<Rect>();
    static double cameraX = 0;;
    static double cameraY = 0;
    static double cameraZ = 0;
    static double cameraFOV = 45;
    static double cameraRX = 0;
    static double cameraRY = 0;
    static double screenX = scene.getWidth();
    static double screenY = scene.getHeight();
    static ArrayList<Cube> blocks = new ArrayList<Cube>();
    boolean sup = true;
    private static long lastTime = System.nanoTime();
    static String gameStage = "Menu";
    static String element = "wind";
    static boolean devMode = true;
    Map<Point3DKey, String> map = new HashMap<>();
    
    static ArrayList<FlatRect> slots = new ArrayList<FlatRect>();
    static int selectedSlot = 0;
    static String selectedBlock = "";
    static ArrayList<String> options = new ArrayList<String>();
    static boolean godMode = false;
    static double playerVelocity = 0;
    static boolean sneaking = false;
    static Cube standOn;
    static boolean sneaker = false;
    static int playerX;
    static int playerY;
    static int playerZ;
    static int spawnX;
    static int spawnY;
    static int spawnZ;
    static boolean onEdge = false;
//    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    
    public App() throws AWTException {
        robot = new Robot(); // Create the robot for cursor control
    }
    public void setSpawn(int x, int y, int z) {
    	spawnX = x;
    	spawnY = y;
    	spawnZ = z;
    	 camera.setNearClip(1);
         camera.setFarClip(20000);
         camera.getTransforms().addAll(rotateY, rotateX);
         camera.setFieldOfView(45);
         camera.setTranslateY(-spawnY * 25);
         camera.setTranslateX(spawnX * 25);
         camera.setTranslateZ(spawnZ * 25);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
    	setSpawn(0, 8, 0);
//    	b.setTexture("file:Textures/god.png");
//    	blocks.add(b);
       
        try (BufferedReader br = new BufferedReader(new FileReader("Maps/wind.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {  // Ensure there are exactly 4 values in the line
                    // Convert the first three values to integers (or a composite key)
//                    String key = values[0] + "-" + values[1] + "-" + values[2]; // Create a unique key from the first three integers
                    Point3DKey k = new Point3DKey(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    String word = values[3]; // The last value is the word

                    // Store in the dictionary
                    map.put(k, word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (element.equals("wind")) {
          	 for (Map.Entry<Point3DKey, String> entry : map.entrySet()) {
               	Point3D p = entry.getKey().point;
               	String t = entry.getValue();
                   Cube temp = new Cube(25F, 25F, 25F, (float)(p.getX()*25), (float)(p.getY()*25), (float)(p.getZ()*25));
                   temp.setTexture(t);
                   blocks.add(temp);
               }
          }
        
       
        
        scene.setFill(Color.DARKSLATEGRAY);
        scene.setCamera(camera);
        StackPane s = new StackPane();
//        b.setTranslateY(100);
//        root.getChildren().add(rootroot);
        
//        root.getChildren().add(b.box);
        primaryStage.setTitle("3D Camera Control");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        AmbientLight light = new AmbientLight(Color.WHITE);
        root.getChildren().add(light);
        setupMovement(scene, camera, primaryStage);
        screenX = primaryStage.getWidth();
        screenY = primaryStage.getHeight();
//        FlatRect hotbar1 = new FlatRect(0.05, 0.1, -0.3, 0.35);
//        FlatRect h1test = new FlatRect(0.03, 0.07, -0.3, 0.35);
//        h1test.setColor(Color.RED);
        
        try (BufferedReader br = new BufferedReader(new FileReader("UI/WindHUD.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {  // Ensure there are exactly 4 values in the line
                    // Convert the first three values to integers (or a composite key)
//                    String key = values[0] + "-" + values[1] + "-" + values[2]; // Create a unique key from the first three integers
                    Point2D k = new Point2D(Double.parseDouble(values[2]), Double.parseDouble(values[3]));
//                    String word = values[3]; // The last value is the word
                    FlatRect temp = new FlatRect(Double.parseDouble(values[0]), Double.parseDouble(values[1]), k.getX(), k.getY());
                    String block = values[4];
                    if (block.equals("hb")) {
                    	temp.setColor(Color.BLACK);
                    }
                    if (block.equals("scooby")) {
                    	Image image = new Image("file:Textures/god.png");
                    	temp.r.setFill(new ImagePattern(image));
                    	options.add("scooby");
                    }
                    else if (block.equals("blueBlock")) {
                    	temp.setColor(Color.BLUE);
                    	
                    	options.add("blueBlock");
                    }
                    else if (block.equals("blueBlockTransparent")) {
                    	temp.setColor(Color.BLUE);
                    	temp.r.setOpacity(0.4);
                    	options.add(block);
                    	
                    }
                    if (slots.size() == 0) {
                    	temp.setColor(Color.WHITE);
                    }
                    slots.add(temp);
                    if (options.size() > 0) {
                    	selectedBlock = options.get(0);
                    }
                    
                    // Store in the dictionary
//                    map.put(k, word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
//        scene.setCursor(Cursor.NONE);

        // Initial position of the cursor
        previousX = scene.getWidth() / 2;
        previousY = scene.getHeight() / 2;
        robot.mouseMove((int) previousX, (int) previousY);
        Timeline gen = new Timeline(new KeyFrame(Duration.millis(100), e -> {
//        	int hiddenBlocks = 0;
        	for (Cube block : blocks) {
        		if (block.box.computeAreaInScreen() == 0) {
//        			System.out.println(1);
        			block.box.setVisible(false);
//        			hiddenBlocks++;
        		}
        		else {
        			block.box.setVisible(true);
        		}
//            	System.out.println(b.box.getBoundsInParent().getHeight());
//            	System.out.println(b.box.computeAreaInScreen());
        	}
        	if (camera.getTranslateY() >= 300) {
        		if (scene.getFill() == Color.DARKSLATEGRAY) {
        			scene.setFill(Color.RED);
        		}
        		else {
        			scene.setFill(Color.DARKSLATEGRAY);
        		}
        	}
        	if (sneaker) {
        		
        		hitbox.setTranslateY(hitbox.getTranslateY() - 20);
            	boolean possible = true;
            	for (Cube block : blocks) {
            		if (T(block.box, hitbox)) {
            			possible = false;
            			break;
            		}
            	}
            	if (possible) {
            		sneaker = false;
            		sneaking = false;
                	camera.setTranslateY(camera.getTranslateY() - 15);
            	}
            	hitbox.setTranslateY(hitbox.getTranslateY() + 20);
            	
        	}
        	
        	if (!sneaking) {
        		hitbox.setTranslateY(camera.getTranslateY() + 25);
        		hitbox.setTranslateX(camera.getTranslateX());
        		hitbox.setTranslateZ(camera.getTranslateZ());
        	}
        	else if (sneaking) {
        		hitbox.setTranslateY(camera.getTranslateY() + 17.5);
        		hitbox.setTranslateX(camera.getTranslateX());
        		hitbox.setTranslateZ(camera.getTranslateZ());
        	}
//        	System.out.println(-(int)Math.round(hitbox.getTranslateY()/25 - 1));
        	playerX = (int)Math.round(hitbox.getTranslateX()/25);
        	playerY = (-(int)Math.floor(hitbox.getTranslateY() + 50)/25) + 1;
        	playerZ = (int)Math.round(hitbox.getTranslateZ()/25);
        	
        	cameraX = camera.getTranslateX();
        	cameraY = camera.getTranslateY();
        	cameraZ = camera.getTranslateZ();
//        	System.out.println("x: " + playerX + " y: " + playerY + " z: " + playerZ);
        }));
        
        gen.setCycleCount(Timeline.INDEFINITE);
        gen.play();
        Timeline motion = new Timeline(new KeyFrame(Duration.millis(10), e -> {
        	playerVelocity += 0.03;
        	hitbox.setHeight(50);
        	hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 25);
        	if (sneaking) {
        		hitbox.setHeight(35);
        		hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 17.5);
        		
        	}
        	boolean gotHit = false;
        	hitbox.setHeight(hitbox.getHeight());
        	for (Cube block : blocks) {
            	if (T(hitbox, block.box)) {
            		playerVelocity = 0;
            		gotHit = true;
            		standOn = block;
                    break;
                }
                
            }
        	if (!gotHit) {
//        		standOn = null;
        		camera.setTranslateY(camera.getTranslateY() + playerVelocity);
            	cameraY = camera.getTranslateY();
            	hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 25);
            	hitbox.setHeight(50);
            	if (sneaking) {
            		hitbox.setHeight(35);
            		hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 17.5);
            	}
        	}
        	else {
        		hitbox.setHeight(50);
        		hitbox.setTranslateY(camera.getTranslateY()+25);
        		if (sneaking) {
        			hitbox.setHeight(35);
            		hitbox.setTranslateY(camera.getTranslateY() +17.5);
            	}
        	}
        	hitbox.setHeight(hitbox.getHeight());
//        	System.out.println(playerVelocity);
        	
        }));
        if (!godMode) {
        	motion.setCycleCount(Timeline.INDEFINITE);
        	motion.play();
        }
        scene.setOnMouseMoved(event -> {
            // Calculate the time difference in milliseconds
//        	Point3DKey aaa = new Point3DKey(0,0,0);
//        	Point3DKey bbb = new Point3DKey(0,0,0);
//        	System.out.println(aaa == bbb);
            long currentTime = System.nanoTime();
            long deltaTime = (currentTime - lastTime) / 1000000; // Convert to ms

            // Only update if 10ms have passed
            if (deltaTime >= 10) {
                double deltaX = event.getScreenX() - (scene.getWidth() / 2);
                double deltaY = event.getScreenY() - (scene.getHeight() / 2);

                cameraRotationAngleY += deltaX * sensitivity;
                cameraRotationAngleX -= deltaY * sensitivity;
                cameraRY = cameraRotationAngleX;
                cameraRotationAngleX = clamp(cameraRotationAngleX, -90, 90);
                cameraRX = cameraRotationAngleY;
                rotateY.setAngle(cameraRotationAngleY);
                rotateX.setAngle(cameraRotationAngleX);
//                for (FlatRect aa : FlatRect.menuList) {
//                	Group g = new Group(camera, aa.r);
////                	Rotate rotateY = new Rotate(0, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.Y_AXIS);
////                	Rotate rotateX = new Rotate(0, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.X_AXIS);
////                	rotateX.setAngle(cameraRotationAngleX);
////                	rotateY.setAngle(cameraRotationAngleY);
//                    g.getTransforms().addAll(rotateY, rotateX);
//                    
//                }
                // Re-center the cursor
                Platform.runLater(() -> {
                    robot.mouseMove((int) (scene.getWidth() / 2), (int) (scene.getHeight() / 2));
                });

                // Update lastTime to current time
                lastTime = currentTime;
            }
        });
        scene.setOnMouseClicked(e -> {
        	
        	var r = e.getPickResult().getIntersectedNode();
        	e.getPickResult().getIntersectedFace();
        	
        	
        	for (int i = 0; i < fs.size(); i++) {
        		if (r == fs.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
	        			Cube temp = new Cube(25F, 25F, 25F, (float)(fs.get(i).c.box.getTranslateX()), (float)fs.get(i).c.box.getTranslateY(), (float)fs.get(i).c.box.getTranslateZ() - 25);
	//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
	        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
	        			
	        			if (T(temp.box, hitbox)) {
	        				root.getChildren().remove(temp.box);
	        	            App.root.getChildren().remove(temp.f.r);
	        	            App.root.getChildren().remove(temp.b.r);
	        	            App.root.getChildren().remove(temp.l.r);
	        	            App.root.getChildren().remove(temp.r.r);
	        	            App.root.getChildren().remove(temp.u.r);
	        	            App.root.getChildren().remove(temp.d.r);
	        			}
	        			else {
	        				temp.setTexture(selectedBlock);
	        				blocks.add(temp);
	        				map.put(p, selectedBlock);
	        			}
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
        			if (T(temp.box, hitbox)) {
        				root.getChildren().remove(temp.box);
        	            App.root.getChildren().remove(temp.f.r);
        	            App.root.getChildren().remove(temp.b.r);
        	            App.root.getChildren().remove(temp.l.r);
        	            App.root.getChildren().remove(temp.r.r);
        	            App.root.getChildren().remove(temp.u.r);
        	            App.root.getChildren().remove(temp.d.r);
        			}
        			else {
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        			}
        			
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
        			if (T(temp.box, hitbox)) {
        				root.getChildren().remove(temp.box);
        	            App.root.getChildren().remove(temp.f.r);
        	            App.root.getChildren().remove(temp.b.r);
        	            App.root.getChildren().remove(temp.l.r);
        	            App.root.getChildren().remove(temp.r.r);
        	            App.root.getChildren().remove(temp.u.r);
        	            App.root.getChildren().remove(temp.d.r);
        			}
        			else {
//        				System.out.println("2");
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        			}
        			
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
        			if (T(temp.box, hitbox)) {
        				root.getChildren().remove(temp.box);
        	            App.root.getChildren().remove(temp.f.r);
        	            App.root.getChildren().remove(temp.b.r);
        	            App.root.getChildren().remove(temp.l.r);
        	            App.root.getChildren().remove(temp.r.r);
        	            App.root.getChildren().remove(temp.u.r);
        	            App.root.getChildren().remove(temp.d.r);
        			}
        			else {
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        			}
        			
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
        			if (T(temp.box, hitbox)) {
        				root.getChildren().remove(temp.box);
        	            App.root.getChildren().remove(temp.f.r);
        	            App.root.getChildren().remove(temp.b.r);
        	            App.root.getChildren().remove(temp.l.r);
        	            App.root.getChildren().remove(temp.r.r);
        	            App.root.getChildren().remove(temp.u.r);
        	            App.root.getChildren().remove(temp.d.r);
        			}
        			else {
        				temp.setTexture(selectedBlock);
        				
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        			}
        			
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
//        			temp.setTexture("file:/C:/Users/shunm/Force.png");
        			Point3DKey p = new Point3DKey(temp.box.getTranslateX()/25, temp.box.getTranslateY()/25, temp.box.getTranslateZ()/25);
        			if (T(temp.box, hitbox)) {
        				root.getChildren().remove(temp.box);
        	            App.root.getChildren().remove(temp.f.r);
        	            App.root.getChildren().remove(temp.b.r);
        	            App.root.getChildren().remove(temp.l.r);
        	            App.root.getChildren().remove(temp.r.r);
        	            App.root.getChildren().remove(temp.u.r);
        	            App.root.getChildren().remove(temp.d.r);
        			}
        			else {
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        			}
        			
        			}
        			else {
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX(), cub.getTranslateY(), cub.getTranslateZ());
        				map.remove(h);
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
        Timeline moveF = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, .02, 0);
        	}
        	else {
        		moveCamera(camera, 0, .005, 0);
        	}
        }));
        moveF.setCycleCount(Timeline.INDEFINITE);

        Timeline moveR = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, .02, 0, 0);
        	}
        	else {
        		moveCamera(camera, 0.005, 0, 0);
        	}
        }));
        moveR.setCycleCount(Timeline.INDEFINITE);

        Timeline moveB = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, -.02, 0);
        	}
        	else {
        		moveCamera(camera, 0, -.005, 0);
        	}
        }));
        moveB.setCycleCount(Timeline.INDEFINITE);

        Timeline moveL = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, -.02, 0, 0);
        	}
        	else {
        		moveCamera(camera, -0.005, 0, 0);
        	}
        }));
        moveL.setCycleCount(Timeline.INDEFINITE);

        Timeline moveU = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, 0, -.02);
        	}
        	else {
        		moveCamera(camera, 0, 0, -0.005);
        	}
        }));
        moveU.setCycleCount(Timeline.INDEFINITE);

        Timeline moveD = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, 0, .02);
        	}
        	else {
        		moveCamera(camera, 0, 0, 0.005);
        	}
        }));
        moveD.setCycleCount(Timeline.INDEFINITE);
        
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) moveF.play();
            if (e.getCode() == KeyCode.D) moveR.play();
            if (e.getCode() == KeyCode.S) moveB.play();
            if (e.getCode() == KeyCode.A) moveL.play();
            if (e.getCode() == KeyCode.SPACE) {
            	if (playerVelocity == 0) {
            		playerVelocity = -1.5;
            	}
            	
            }
            if (e.getCode() == KeyCode.R) { 
            	camera.setTranslateY(-spawnY * 25);
            	camera.setTranslateX(spawnX);
            	camera.setTranslateZ(spawnZ);
            	scene.setFill(Color.DARKSLATEGRAY);
            	playerVelocity = 0;
            }
            if (e.getCode() == KeyCode.SHIFT) {
            	sneaking = true;
            	camera.setTranslateY(camera.getTranslateY() + 15);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            }
            if (e.getCode() == KeyCode.DIGIT1) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(0).setColor(Color.WHITE);
            	selectedSlot = 0;
            	if (options.size() > 0) {
            		selectedBlock = options.get(0);
            	}
            	
            }
            if (e.getCode() == KeyCode.DIGIT2) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(1).setColor(Color.WHITE);
            	selectedSlot = 1;
            	if (options.size() > 1) {
            		selectedBlock = options.get(1);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT3) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(2).setColor(Color.WHITE);
            	selectedSlot = 2;
            	if (options.size() > 2) {
            		selectedBlock = options.get(2);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT4) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(3).setColor(Color.WHITE);
            	selectedSlot = 3;
            	if (options.size() > 3) {
            		selectedBlock = options.get(3);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT5) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(4).setColor(Color.WHITE);
            	selectedSlot = 4;
            	if (options.size() > 4) {
            		selectedBlock = options.get(4);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT6) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(5).setColor(Color.WHITE);
            	selectedSlot = 5;
            	if (options.size() > 5) {
            		selectedBlock = options.get(5);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT7) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(6).setColor(Color.WHITE);
            	selectedSlot = 6;
            	if (options.size() > 6) {
            		selectedBlock = options.get(6);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT8) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(7).setColor(Color.WHITE);
            	selectedSlot = 7;
            	if (options.size() > 7) {
            		selectedBlock = options.get(7);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT9) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(8).setColor(Color.WHITE);
            	selectedSlot = 8;
            	if (options.size() > 8) {
            		selectedBlock = options.get(8);
            	}
            }
            if (e.getCode() == KeyCode.DIGIT0) {
            	slots.get(selectedSlot).setColor(Color.BLACK);
            	slots.get(9).setColor(Color.WHITE);
            	selectedSlot = 9;
            	if (options.size() > 9) {
            		selectedBlock = options.get(9);
            	}
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) moveF.stop();
            if (e.getCode() == KeyCode.D) moveR.stop();
            if (e.getCode() == KeyCode.S) moveB.stop();
            if (e.getCode() == KeyCode.A) moveL.stop();
//            if (e.getCode() == KeyCode.SPACE) moveU.stop();
            if (e.getCode() == KeyCode.SHIFT) {
            	sneaker = true;
            }
            
        });
        
    }

    private void moveCamera(PerspectiveCamera camera, double deltaX, double deltaZ, double deltaY) {

        double yaw = Math.toRadians(cameraRotationAngleY);
        
        // Calculate forward and right vectors based on yaw angle
        double forwardX = Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = Math.cos(yaw);
        double rightZ = -Math.sin(yaw);
        hitbox.setHeight(50);
        if (!sneaking) {
	        hitbox.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
	        hitbox.setTranslateY(camera.getTranslateY() + deltaY + 25);
	        hitbox.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
    	}
        else {
        	hitbox.setHeight(35);
        	hitbox.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
	        hitbox.setTranslateY(camera.getTranslateY() + deltaY + 17.5);
	        hitbox.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
	        
        }
        boolean gotHit = false;
        
        for (Cube block : blocks) {
        	if (T(hitbox, block.box)) {
            	gotHit = true;
                break;
            }
            
        }
        if (!gotHit) {
//        	
        	
        	if (sneaking) {
//        		if (map.containsKey(new Point3DKey(playerX, playerY-1, playerZ)) || onEdge) {
        		if (playerVelocity == 0) {
            		Point3D cest = new Point3D(playerX, playerY-1, playerZ);
            		double nextX = Math.round(hitbox.getTranslateX()/25);
            		double nextZ = Math.round(hitbox.getTranslateZ()/25);
            		boolean onTop = false;
            		for (Cube block: blocks) {
            			hitbox.setTranslateY(hitbox.getTranslateY() + 10);
            			if (T(block.box, hitbox)) {
            				onTop = true;
            				hitbox.setTranslateY(hitbox.getTranslateY() - 10);
            				break;
            			}
            			hitbox.setTranslateY(hitbox.getTranslateY() - 10);
            		}
            		if (map.containsKey(new Point3DKey(nextX, playerY-1, nextZ)) || onTop) {
            			
            			camera.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
            	        camera.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
            	        camera.setTranslateY(camera.getTranslateY() + deltaY);
            			
            		}
            		else {
            			hitbox.setTranslateX(camera.getTranslateX());
            			hitbox.setTranslateY(camera.getTranslateY() + 25);
            			hitbox.setTranslateZ(camera.getTranslateZ());
            		}
            	}
        		else {
        			camera.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
                	camera.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
                	camera.setTranslateY(camera.getTranslateY() + deltaY);
        		}
        		
        	}
        	else {
        		camera.setTranslateX(camera.getTranslateX() + deltaX * rightX + deltaZ * forwardX);
            	camera.setTranslateZ(camera.getTranslateZ() + deltaX * rightZ + deltaZ * forwardZ);
            	camera.setTranslateY(camera.getTranslateY() + deltaY);
        	}
        }
        	hitbox.setHeight(50);
        	if (!sneaking) {
        		hitbox.setTranslateX(camera.getTranslateX());
            	hitbox.setTranslateY(camera.getTranslateY() + 25);
            	hitbox.setTranslateZ(camera.getTranslateZ());
        	}
        	else {
        		hitbox.setHeight(35);
        		hitbox.setTranslateX(camera.getTranslateX());
            	hitbox.setTranslateY(camera.getTranslateY() + 17.5);
            	hitbox.setTranslateZ(camera.getTranslateZ());
        	}
        
        cameraX = camera.getTranslateX();
        cameraY = camera.getTranslateY();
        cameraZ = camera.getTranslateZ();
        
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
    public static boolean T2(Box box1, Box box2) {
        // Calculate boundaries for box1
        double box1MinY = box1.getTranslateY() - box1.getHeight() / 2;
        double box1MaxY = box1.getTranslateY() + box1.getHeight() / 2;
        double box1MinZ = box1.getTranslateZ() - box1.getDepth() / 2;
        double box1MaxZ = box1.getTranslateZ() + box1.getDepth() / 2;

        // Calculate boundaries for box2
        double box2MinY = box2.getTranslateY() - box2.getHeight() / 2;
        double box2MaxY = box2.getTranslateY() + box2.getHeight() / 2;
        double box2MinZ = box2.getTranslateZ() - box2.getDepth() / 2;
        double box2MaxZ = box2.getTranslateZ() + box2.getDepth() / 2;

        // Check for vertical overlap (Y-axis) and alignment in Z-axis
        boolean isOverlappingY = box1MaxY >= box2MinY && box1MinY <= box2MaxY;
        boolean isAlignedZ = box1MaxZ >= box2MinZ && box1MinZ <= box2MaxZ;

        // The boxes are vertically touching if they overlap in Y and are aligned in Z
        return isOverlappingY && isAlignedZ;
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
	PhongMaterial m;
	public Cube(float length, float width, float height, float x, float y, float z) {
		
		m = new PhongMaterial();
		box = new Box(length, width, height);
		box.setTranslateX(x);
		box.setTranslateY(y);
		box.setTranslateZ(z);
//		box.setEffect(new GaussianBlur(5));
		
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
	}
	public void setTexture(String path) {
        if (path.equals("scooby")) {
        	Image textureImage = new Image("file:Textures/god.png");
            if (textureImage.isError()) {
                System.err.println("Error loading texture: " + textureImage.getException());
                return;
            }
            m.setDiffuseMap(textureImage);
            this.box.setMaterial(m);
        }
        else if (path.equals("blueBlock")) {
        	m.setDiffuseColor(Color.BLUE);
        	this.box.setMaterial(m);
        }
        else if (path.equals("blueBlockTransparent")) {
//        	System.out.println("hello");
        	m.setDiffuseColor(new Color(0, 0, 1, 0.4));
        	
//        	box.setOpacity(0.1);
        	this.box.setMaterial(m);
        }
    }
	public boolean isInFrustum() {
		Box object = box;
	    // Get object's position in world coordinates
	    double objectWorldX = object.getTranslateX();
	    double objectWorldY = object.getTranslateY();
	    double objectWorldZ = object.getTranslateZ();

	    // Transform to camera space
	    double cameraWorldX = App.camera.getTranslateX();
	    double cameraWorldY = App.camera.getTranslateY();
	    double cameraWorldZ = App.camera.getTranslateZ();

	    // Relative position in camera space
	    double cameraX = objectWorldX - cameraWorldX;
	    double cameraY = objectWorldY - cameraWorldY;
	    double cameraZ = objectWorldZ - cameraWorldZ;

	    // Near and far clip checks (JavaFX uses negative Z for depth into the screen)
	    double nearClip = App.camera.getNearClip();
	    double farClip = App.camera.getFarClip();
	    if (cameraZ > -nearClip || cameraZ < -farClip) {
	        return false;
	    }

	    // Calculate aspect ratio of the scene
	    double aspectRatio = App.scene.getWidth() / App.scene.getHeight();

	    // Horizontal and vertical FOV calculations
	    double horizontalFOV = Math.toRadians(App.camera.getFieldOfView());
	    double verticalFOV = 2 * Math.atan(Math.tan(horizontalFOV / 2) / aspectRatio);

	    // Frustum extents at the object's depth
	    double halfWidthAtZ = -cameraZ * Math.tan(horizontalFOV / 2);
	    double halfHeightAtZ = -cameraZ * Math.tan(verticalFOV / 2);

	    // Check if the object is within the frustum bounds
	    return (cameraX >= -halfWidthAtZ && cameraX <= halfWidthAtZ &&
	            cameraY >= -halfHeightAtZ && cameraY <= halfHeightAtZ);
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
class FlatRect {
	static double ratio = App.screenX/App.screenY;
	static double vfov =  App.cameraFOV;
	static double radian = vfov * (Math.PI/180);
	static double hfov = 2*(Math.atan(Math.tan(radian/2) * ratio));
	static double z = 1000;
	static double screenWidth = Math.abs(2 * (z * Math.tan(hfov/2)));
	static double screenHeight = Math.abs(2 * (z * Math.tan(radian/2)));
	static Rotate tempX = App.rotateX;
	static Rotate tempY = App.rotateY;
	Rectangle r;
	static ArrayList<FlatRect> menuList = new ArrayList<FlatRect>();
	public FlatRect(double x, double y, double xp, double yp) throws NonInvertibleTransformException {
//		Transform inv = App.camera.getLocalToSceneTransform().createInverse();
		// Create a rectangle
		r = new Rectangle(x * screenWidth, y * screenHeight);
		r.setTranslateZ(100);
		r.setOpacity(0.8);
		r.setDepthTest(DepthTest.DISABLE);
		App.root.getChildren().add(r);
//		r.getTransforms().add(inv)
		Rotate xt = new Rotate(0, Rotate.Y_AXIS);
		Rotate yt = new Rotate(0, Rotate.X_AXIS);
		r.getTransforms().addAll(xt, yt);
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(.1), e -> {
			App.root.getChildren().remove(r);
			xt.setAngle(App.cameraRX);
			yt.setAngle(App.cameraRY);
			Transform local = App.camera.getLocalToSceneTransform();
			Point3D newlocal = local.transform(new Point3D(xp * screenWidth, yp * screenHeight, z));
			r.setTranslateX(newlocal.getX());
			r.setTranslateY(newlocal.getY());
			r.setTranslateZ(newlocal.getZ());
			App.root.getChildren().add(r);
			
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
	public void setColor(Color c) {
		r.setFill(c);
	}
			
}
class Point3DKey {
    public Point3D point;

    public Point3DKey(double x, double y, double z) {
        this.point = new Point3D(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point3DKey)) return false;
        Point3DKey other = (Point3DKey) obj;
        return point.equals(other.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point.getX(), point.getY(), point.getZ());
    }
}