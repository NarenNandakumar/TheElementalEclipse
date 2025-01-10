package pack;

import javafx.animation.KeyFrame;
import javafx.scene.image.Image;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
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
import javafx.scene.input.PickResult;
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
import javafx.scene.text.Text;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App extends Application {
	static Box hitbox = new Box(15, 50, 15);
    public static Group root = new Group();
    private static double cameraRotationAngleX = 0;
    private static double cameraRotationAngleY = 0;
    private double previousX, previousY;
    private final static double sensitivity = 0.1;
    private static Robot robot;
    public static PerspectiveCamera camera = new PerspectiveCamera(true);
    static Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    static Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    static Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
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
    static Map<Point3DKey, String> map = new HashMap<>();
    
    static ArrayList<FlatRect> slots = new ArrayList<FlatRect>();
    static int selectedSlot = 0;
    static String selectedBlock = "";
    static ArrayList<String> options = new ArrayList<String>();
    
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
    static boolean jumping = false;
    static boolean onEdge = false;
    static boolean gamePaused = true;
    static ArrayList<Integer> numItems = new ArrayList<Integer>();
    static ArrayList<FlatRect> inv = new ArrayList<FlatRect>();
    static double reach = 8;
    static double j = reach * 25;  
    static boolean sprinting = false;
    static boolean teleported = false;
    static ArrayList<FlatText> txts = new ArrayList<FlatText>();
    static boolean godMode = true;
    static ArrayList<Cube> teleporters = new ArrayList<Cube>();
//    static int waiter = 0;
    public App() throws AWTException {
        robot = new Robot();
    }
    public void setSpawn(int x, int y, int z) {
    	playerVelocity = 0;
    	spawnX = x;
    	spawnY = y;
    	spawnZ = z;
    	 camera.setNearClip(1);
         camera.setFarClip(2000);
         camera.getTransforms().addAll(rotateY, rotateX);
         camera.setFieldOfView(45);
         camera.setTranslateY(-spawnY * 25);
         camera.setTranslateX(spawnX * 25);
         camera.setTranslateZ(spawnZ * 25);
    }
    public void worldSet(String element, Stage primaryStage) throws NumberFormatException, NonInvertibleTransformException {
//    	Timeline warmUp = new Timeline(
//    		    new KeyFrame(Duration.millis(100), e -> {
//    		        
//    		    })
//    		);
//    		warmUp.setCycleCount(10); // Run this for 10 frames
//    		warmUp.play();
    	try (BufferedReader br = new BufferedReader(new FileReader("Maps/windtp.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {  
                    Cube y = new Cube(25F, 25F, 25F, Float.parseFloat(values[0])*25, Float.parseFloat(values[1])*25, Float.parseFloat(values[2]) * 25);
                    teleporters.add(y);
                    App.root.getChildren().remove(y.box);
                    App.root.getChildren().remove(y.f.r);
                    App.root.getChildren().remove(y.b.r);
                    App.root.getChildren().remove(y.l.r);
                    App.root.getChildren().remove(y.r.r);
                    App.root.getChildren().remove(y.u.r);
                    App.root.getChildren().remove(y.d.r);
                }
            }
        } catch (IOException e) {
        }
    	
    	if (element.equals("wind")) {
    		for (int i = 0; i < 10; i++) {
    			numItems.add(10);
    		}
    	}
    	setSpawn(0, 9, 0);
        setupMovement(scene, camera, primaryStage);
    	Platform.runLater(() -> {
    		try (BufferedReader br = new BufferedReader(new FileReader("Maps/wind.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 4) {  
                        Point3DKey k = new Point3DKey(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]));
                        String word = values[3];
                        map.put(k, word);
                    }
                }
            } catch (IOException e) {
            }
              	 for (Map.Entry<Point3DKey, String> entry : map.entrySet()) {
                   	Point3D p = entry.getKey().point;
                   	String t = entry.getValue();
                   	Platform.runLater(() -> {
                   		Cube temp = new Cube(25F, 25F, 25F, (float)(p.getX()*25), (float)(p.getY()*25), (float)(p.getZ()*25));
                   		
                        temp.setTexture(t);
                        blocks.add(temp);
                   	});
                       
                   }
            
            try (BufferedReader br = new BufferedReader(new FileReader("UI/WindHUD.csv"))) {
                String line;
                Image scooby = new Image("file:Textures/god.png");
                Image elementt = new Image("file:Textures/element.png");
                Image wind = new Image("file:Textures/wind.png");
                Image magma = new Image("file:Textures/magma.png");
                Image water = new Image("file:Textures/water.png");
                Image color = new Image("file:Textures/color.png");
                Image brick = new Image("file:Textures/brick.png");
                Image fireBrick = new Image("file:Textures/fireBrick.png");
                Image waterBrick = new Image("file:Textures/waterBrick.png");
                Image teleport = new Image("file:Textures/teleport.png");
                Image wood = new Image("file:Textures/wood.png");
                Image sand = new Image("file:Textures/sand.png");
                Image grass = new Image("file:Textures/grass.png");
                Image metal = new Image("file:Textures/metal.png");
                Image circuit = new Image("file:Textures/circuit.png");
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 5) { 
                        Point2D k = new Point2D(Double.parseDouble(values[2]), Double.parseDouble(values[3]));
                        
                        	FlatRect temp = null;
//                        	FlatRect tr = null;
    						try {
    							temp = new FlatRect(Double.parseDouble(values[0]), Double.parseDouble(values[1]), k.getX(), k.getY());
//    							tr = new FlatRect(Double.parseDouble(values[0])+0.1, Double.parseDouble(values[1])+0.1, k.getX(), k.getY());
    						} catch (NumberFormatException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						} catch (NonInvertibleTransformException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
                            String block = values[4];
                            if (block.equals("hb")) {
                            	temp.setColor(Color.BLACK);
                            }
                            
                            if (block.equals("scooby")) {
//                            	Image image = new Image("file:Textures/god.png");
                            	temp.r.setFill(new ImagePattern(scooby));
                            	options.add("scooby");
                            	inv.add(temp);
                            	
                            	
                            }
                            else if (block.equals("blueBlock")) {
                            	temp.setColor(Color.BLUE);
                            	inv.add(temp);
                            	options.add("blueBlock");
                            }
                            else if (block.equals("blueBlockTransparent")) {
                            	temp.setColor(Color.BLUE);
                            	temp.r.setOpacity(0.4);
                            	options.add(block);
                            	inv.add(temp);
                            	
                            }
                            else if (block.equals("element")) {
                            	
                            	temp.r.setFill(new ImagePattern(elementt));
                            	options.add("element");
                            	inv.add(temp);
                            }
                            else if (block.equals("wind")) {
                            	
                            	temp.r.setFill(new ImagePattern(wind));
                            	options.add("wind");
                            	inv.add(temp);
                            }
                            else if (block.equals("magma")) {
                            	
                            	temp.r.setFill(new ImagePattern(magma));
                            	options.add("magma");
                            	inv.add(temp);
                            }
                            else if (block.equals("water")) {
                            	
                            	temp.r.setFill(new ImagePattern(water));
                            	options.add("water");
                            	inv.add(temp);
                            }
                            else if (block.equals("color")) {
                            	
                            	temp.r.setFill(new ImagePattern(color));
                            	options.add("color");
                            	inv.add(temp);
                            }
                            else if (block.equals("brick")) {
                            	
                            	temp.r.setFill(new ImagePattern(brick));
                            	options.add("brick");
                            	inv.add(temp);
                            }
                            else if (block.equals("fireBrick")) {
                            	
                            	temp.r.setFill(new ImagePattern(fireBrick));
                            	options.add("fireBrick");
                            	inv.add(temp);
                            }
                            else if (block.equals("waterBrick")) {
                            	
                            	temp.r.setFill(new ImagePattern(waterBrick));
                            	options.add("waterBrick");
                            	inv.add(temp);
                            }
                            else if (block.equals("teleport")) {
                            	
                            	temp.r.setFill(new ImagePattern(teleport));
                            	options.add("teleport");
                            	inv.add(temp);
                            }
                            else if (block.equals("wood")) {
                            	
                            	temp.r.setFill(new ImagePattern(wood));
                            	options.add("wood");
                            	inv.add(temp);
                            }
                            else if (block.equals("sand")) {
                            	
                            	temp.r.setFill(new ImagePattern(sand));
                            	options.add("sand");
                            	inv.add(temp);
                            }
                            else if (block.equals("grass")) {
                            	
                            	temp.r.setFill(new ImagePattern(grass));
                            	options.add("grass");
                            	inv.add(temp);
                            }
                            else if (block.equals("metal")) {
                            	
                            	temp.r.setFill(new ImagePattern(metal));
                            	options.add("metal");
                            	inv.add(temp);
                            }
                            else if (block.equals("circuit")) {
                            	
                            	temp.r.setFill(new ImagePattern(circuit));
                            	options.add("circuit");
                            	inv.add(temp);
                            }
                            if (slots.size() == 0) {
                            	temp.setColor(Color.WHITE);
                            }
                            slots.add(temp);
                            if (options.size() > 0) {
                            	selectedBlock = options.get(0);
                            }
                            if (!block.equals("hb")) {
                            	FlatRect itemHolder = new FlatRect(temp.w, 0.02, (temp.xxp), (temp.yyp) + temp.h);
                            	itemHolder.setColor(Color.GRAY);
                            	FlatText t = new FlatText(temp.w, 0.02, (temp.xxp), (temp.yyp) + temp.h + 0.02, inv.size()-1);
                            	txts.add(t);
                            }
                        
                        
                        
                    }
                    
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NonInvertibleTransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
           
    	});
    	
        previousX = scene.getWidth() / 2;
        previousY = scene.getHeight() / 2;
        robot.mouseMove((int) previousX, (int) previousY);
        Timeline gen = new Timeline(new KeyFrame(Duration.millis(10), e -> {
        	
        	if (teleporters.size() % 2 == 0) {
        		for (int i = 0; i < teleporters.size(); i+=2) {
            		Cube tp1 = teleporters.get(i);
            		Cube tp2 = teleporters.get(i+1);
            		if (tp1 != null) {
            			if (T(hitbox, tp1.box)) { 
                    		if (tp2 != null) {
                    			camera.setTranslateX(tp2.box.getTranslateX());
                    			camera.setTranslateZ(tp2.box.getTranslateZ());
                    			camera.setTranslateY(tp2.box.getTranslateY() - 95);
                    			
                    		}
                    	}
            		}
            		if (tp2 != null) {
            			if (T(hitbox, tp2.box)) { 
            				if (tp1 != null) {
            					camera.setTranslateX(tp1.box.getTranslateX());
            					camera.setTranslateZ(tp1.box.getTranslateZ());
            					camera.setTranslateY(tp1.box.getTranslateY() - 95);
            					
            				}
            			}
            		}	  
            	}
        	}
        	
//        	if (tp1 != null && tp2 != null) {
//        		if (!T(hitbox, tp1.box) && !T(hitbox, tp2.box)) {
//        			teleported = false;
//        		}
//        	}
        	if (sprinting) {
        		if (camera.getFieldOfView() < 51) {
        			camera.setFieldOfView(camera.getFieldOfView() + 2);
        		}
        	}
        	else {
        		if (camera.getFieldOfView() > 45) {
        			camera.setFieldOfView(camera.getFieldOfView() - 2);
        		}
        	}
        	j = reach * 25;
        	if (jumping) {
        		if (playerVelocity == 0) {		
        			playerVelocity = -2;
        		}
        	}
        	Platform.runLater(() -> {
        		for (Cube block : blocks) {
        			
            		if (block.box.computeAreaInScreen() == 0) {
            			block.box.setVisible(false);
            			
            		}
            		else {
            			block.box.setVisible(true);
            		}
            	}
        	});
        	Color v = Color.LIGHTSKYBLUE;
        	double lowest = 9999;
        	for (Cube g : teleporters) {
        		double distance = Math.sqrt(Math.pow(hitbox.getTranslateX() - g.box.getTranslateX(), 2) + Math.pow(hitbox.getTranslateY() - g.box.getTranslateY(), 2) + Math.pow(hitbox.getTranslateZ() - g.box.getTranslateZ(), 2));
        		if (distance < lowest) {
        			lowest = distance;
        		}
        		
        	}
        	
        	if (lowest < 250) {
    			
    			double factor = lowest/250;
            	Color darkerColor = new Color(v.getRed(), v.getGreen() * factor, v.getBlue() * factor, v.getOpacity());
            	scene.setFill(darkerColor);
    		}
        	else {
        		scene.setFill(v);
        	}
        	
        	if (camera.getTranslateY() > 300) {
        		double factor = Math.max(0, Math.min(1, 1 - (camera.getTranslateY() - 500) / (2000 - 500)));
            	Color darkerColor = new Color(v.getRed() * factor, v.getGreen() * factor, v.getBlue() * factor, v.getOpacity());
            	scene.setFill(darkerColor);
        	}
//        	if (camera.getTranslateY() >= 300) {
//        		if (scene.getFill() == Color.DARKSLATEGRAY) {
//        			scene.setFill(Color.RED);
//        		}
//        		else {
//        			scene.setFill(Color.DARKSLATEGRAY);
//        		}
//        	}
//        	else {
//    			scene.setFill(Color.DARKSLATEGRAY);
//    		}
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
        	playerX = (int)Math.round(hitbox.getTranslateX()/25);
        	playerY = (-(int)Math.floor((hitbox.getTranslateY() + 25)/25)) + 1;
        	playerZ = (int)Math.round(hitbox.getTranslateZ()/25);
        	
        	cameraX = camera.getTranslateX();
        	cameraY = camera.getTranslateY();
        	cameraZ = camera.getTranslateZ();
        }));
        
        gen.setCycleCount(Timeline.INDEFINITE);
        gen.play();
        Timeline motion = new Timeline(new KeyFrame(Duration.millis(10), e -> {
        	
        	playerVelocity += 0.06;
        	hitbox.setHeight(50);
        	hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 25);
        	if (sneaking) {
        		hitbox.setHeight(35);
        		hitbox.setTranslateY(camera.getTranslateY() + playerVelocity + 17.5);
        		
        	}
        	boolean gotHit = false;
        	hitbox.setHeight(hitbox.getHeight());
        	for (Cube block : blocks) {
        		if (block.texture.equals("teleport")) {
        			continue;
        		}
            	if (T(hitbox, block.box)) {
            		playerVelocity = 0;
            		gotHit = true;
            		standOn = block;
                    break;
                }
                
            }
        	if (!gotHit) {
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
        		playerVelocity = 0;
        	}
        	hitbox.setHeight(hitbox.getHeight());

        	
        }));
        if (!godMode) {
        	motion.setCycleCount(Timeline.INDEFINITE);
        	motion.play();
        }
        scene.setOnMouseMoved(event -> {
        	
                double deltaX = event.getScreenX() - (scene.getWidth() / 2);
                double deltaY = event.getScreenY() - (scene.getHeight() / 2);

                cameraRotationAngleY += deltaX * sensitivity;
                cameraRotationAngleX -= deltaY * sensitivity;
                cameraRY = cameraRotationAngleX;
                cameraRotationAngleX = clamp(cameraRotationAngleX, -90, 90);
                cameraRX = cameraRotationAngleY;
                rotateY.setAngle(cameraRotationAngleY);
                rotateX.setAngle(cameraRotationAngleX);
                Platform.runLater(() -> {
                    robot.mouseMove((int) (scene.getWidth() / 2), (int) (scene.getHeight() / 2));
                });
        });
        scene.setOnMousePressed(e -> {
//        	 System.out.println(root.getChildren().size());
        	System.out.println(teleporters.size());
        	var r = e.getPickResult().getIntersectedNode();
        	e.getPickResult().getIntersectedFace();
        	
        	
        	for (int i = 0; i < fs.size(); i++) {
        		if (r == fs.get(i).r) { 
        			if (e.getButton() == MouseButton.SECONDARY) { 
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
	        				if (!godMode) {
		        				int index = options.indexOf(selectedBlock);
		        				if (!(numItems.get(index) > 0)) {
		        					root.getChildren().remove(temp.box);
			        	            App.root.getChildren().remove(temp.f.r);
			        	            App.root.getChildren().remove(temp.b.r);
			        	            App.root.getChildren().remove(temp.l.r);
			        	            App.root.getChildren().remove(temp.r.r);
			        	            App.root.getChildren().remove(temp.u.r);
			        	            App.root.getChildren().remove(temp.d.r);
		        					break;
		        				}
                                
		        				txts.get(index).reduce(1);
		        				numItems.set(index, numItems.get(index) - 1);
	        				}
	        				temp.setTexture(selectedBlock);
	        				blocks.add(temp);
	        				map.put(p, selectedBlock);
	        				if (selectedBlock.equals("teleport")) {
	        					teleporters.add(temp);
	        				}
	        				
	        			       
	        			}
        			}
        			else {
        				
        					
        				
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
        				if (!godMode) {
	        				int index = options.indexOf(selectedBlock);
	        				if (!(numItems.get(index) > 0)) {
	        					root.getChildren().remove(temp.box);
		        	            App.root.getChildren().remove(temp.f.r);
		        	            App.root.getChildren().remove(temp.b.r);
		        	            App.root.getChildren().remove(temp.l.r);
		        	            App.root.getChildren().remove(temp.r.r);
		        	            App.root.getChildren().remove(temp.u.r);
		        	            App.root.getChildren().remove(temp.d.r);
	        					break;
	        				}
	        			
	        				txts.get(index).reduce(1);
	        				numItems.set(index, numItems.get(index) - 1);
        				}
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        				if (selectedBlock.equals("teleport")) {
        					teleporters.add(temp);
        				}
        			}
        			
        			}
        			else {
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
        				if (!godMode) {
	        				int index = options.indexOf(selectedBlock);
	        				if (!(numItems.get(index) > 0)) {
	        					root.getChildren().remove(temp.box);
		        	            App.root.getChildren().remove(temp.f.r);
		        	            App.root.getChildren().remove(temp.b.r);
		        	            App.root.getChildren().remove(temp.l.r);
		        	            App.root.getChildren().remove(temp.r.r);
		        	            App.root.getChildren().remove(temp.u.r);
		        	            App.root.getChildren().remove(temp.d.r);
	        					break;
	        				}
	        			
	        				txts.get(index).reduce(1);
	        				numItems.set(index, numItems.get(index) - 1);
        				}
//        				System.out.println("2");
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        				if (selectedBlock.equals("teleport")) {
        					teleporters.add(temp);
        				}
        			}
        			
        			}
        			else {
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
        				if (!godMode) {
	        				int index = options.indexOf(selectedBlock);
	        				if (!(numItems.get(index) > 0)) {
	        					root.getChildren().remove(temp.box);
		        	            App.root.getChildren().remove(temp.f.r);
		        	            App.root.getChildren().remove(temp.b.r);
		        	            App.root.getChildren().remove(temp.l.r);
		        	            App.root.getChildren().remove(temp.r.r);
		        	            App.root.getChildren().remove(temp.u.r);
		        	            App.root.getChildren().remove(temp.d.r);
	        					break;
	        				}
	        			
	        				txts.get(index).reduce(1);
	        				numItems.set(index, numItems.get(index) - 1);
        				}
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        				if (selectedBlock.equals("teleport")) {
        					teleporters.add(temp);
        				}
        			}
        			
        			}
        			else {
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
        				if (!godMode) {
	        				int index = options.indexOf(selectedBlock);
	        				if (!(numItems.get(index) > 0)) {
	        					root.getChildren().remove(temp.box);
		        	            App.root.getChildren().remove(temp.f.r);
		        	            App.root.getChildren().remove(temp.b.r);
		        	            App.root.getChildren().remove(temp.l.r);
		        	            App.root.getChildren().remove(temp.r.r);
		        	            App.root.getChildren().remove(temp.u.r);
		        	            App.root.getChildren().remove(temp.d.r);
	        					break;
	        				}
	        			
	        				txts.get(index).reduce(1);
	        				numItems.set(index, numItems.get(index) - 1);
        				}
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        				if (selectedBlock.equals("teleport")) {
        					teleporters.add(temp);
        				}
        			}
        			
        			}
        			else {
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					if (pickResult.getIntersectedDistance() > j) {
        						break;
        					}
        				}
        				
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
        				if (!godMode) {
	        				int index = options.indexOf(selectedBlock);
	        				if (!(numItems.get(index) > 0)) {
	        					root.getChildren().remove(temp.box);
		        	            App.root.getChildren().remove(temp.f.r);
		        	            App.root.getChildren().remove(temp.b.r);
		        	            App.root.getChildren().remove(temp.l.r);
		        	            App.root.getChildren().remove(temp.r.r);
		        	            App.root.getChildren().remove(temp.u.r);
		        	            App.root.getChildren().remove(temp.d.r);
	        					break;
	        				}
	        			
	        				txts.get(index).reduce(1);
	        				numItems.set(index, numItems.get(index) - 1);
        				}
        				temp.setTexture(selectedBlock);
        				blocks.add(temp);
        				map.put(p, selectedBlock);
        				if (selectedBlock.equals("teleport")) {
        					teleporters.add(temp);
        				}
        			}
        			
        			}
        			else {
        				if (!godMode) {
        					PickResult pickResult = e.getPickResult();
        					String t = fs.get(i).c.texture;
        					if (((pickResult.getIntersectedDistance() > j || !fs.get(i).c.breakable)) || !options.contains(t)) {
        						break;
        					}
        					int index = options.indexOf(t);
        					if (index == -1) {
        						break;
        					}
        					txts.get(index).increase(1);
	        				numItems.set(index, numItems.get(index) + 1);
        				}
        				blocks.remove(fs.get(i).c);
        				Box cub = fs.get(i).c.box;
        				Point3DKey h = new Point3DKey(cub.getTranslateX()/25, cub.getTranslateY()/25, cub.getTranslateZ()/25);
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
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	
    	scene.setFill(Color.LIGHTSKYBLUE);
        scene.setCamera(camera);
        
//        b.setTranslateY(100);
//        root.getChildren().add(rootroot);
        
//        root.getChildren().add(b.box);
        primaryStage.setTitle("3D Camera Control");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        AmbientLight light = new AmbientLight(Color.WHITE);
        root.getChildren().add(light);
        screenX = primaryStage.getWidth();
        screenY = primaryStage.getHeight();
        worldSet("wind", primaryStage);
        
        
    }
    public void deleteWorld() {
    	
    	for (Cube bl : blocks) {
    		root.getChildren().remove(bl.box);
    		
    	}
    	blocks = new ArrayList<Cube>();
    	map = new HashMap<>();
    	sprinting = false;
    	slots = new ArrayList<FlatRect>();
    	inv = new ArrayList<FlatRect>();
    	numItems = new ArrayList<Integer>();
    	options = new ArrayList<String>();
    	txts = new ArrayList<FlatText>();
    	teleporters = new ArrayList<Cube>();
    	reach = 8;
    }
    private void setupMovement(Scene scene, PerspectiveCamera camera, Stage primaryStage) {
        Timeline moveF = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
        		if (!sprinting) {
        			moveCamera(camera, 0, .1, 0);
        		}
        		else {
        			moveCamera(camera, 0, .25, 0);
        		}
        	}
        	else {
        		moveCamera(camera, 0, .05, 0);
        	}
        }));
        moveF.setCycleCount(Timeline.INDEFINITE);

        Timeline moveR = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
            moveCamera(camera, .1, 0, 0);
        	}
        	else {
        		moveCamera(camera, 0.05, 0, 0);
        	}
        }));
        moveR.setCycleCount(Timeline.INDEFINITE);

        Timeline moveB = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, -.1, 0);
        	}
        	else {
        		moveCamera(camera, 0, -.05, 0);
        	}
        }));
        moveB.setCycleCount(Timeline.INDEFINITE);

        Timeline moveL = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
            moveCamera(camera, -.1, 0, 0);
        	}
        	else {
        		moveCamera(camera, -0.05, 0, 0);
        	}
        }));
        moveL.setCycleCount(Timeline.INDEFINITE);

        Timeline moveU = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, 0, -.1);
        	}
        	else {
        		moveCamera(camera, 0, 0, -0.05);
        	}
        }));
        moveU.setCycleCount(Timeline.INDEFINITE);

        Timeline moveD = new Timeline(new KeyFrame(Duration.millis(1), e -> {
        	if (!sneaking) {
            moveCamera(camera, 0, 0, .1);
        	}
        	else {
        		moveCamera(camera, 0, 0, 0.05);
        	}
        }));
        moveD.setCycleCount(Timeline.INDEFINITE);
        scene.setOnScroll(e -> {
        	
        	if (e.getDeltaY() < 0 || e.getDeltaX() < 0) {
        		slots.get(selectedSlot).setColor(Color.BLACK);
        		if (selectedSlot < 9) {
        			selectedSlot++;
        		}
        		else {
        			selectedSlot = 0;
        		}
        		slots.get(selectedSlot).setColor(Color.WHITE);
        		if (options.size() > selectedSlot) {
            		selectedBlock = options.get(selectedSlot);
            	}
        	}
        	else if (e.getDeltaY() > 0 || e.getDeltaX() > 0){
        		slots.get(selectedSlot).setColor(Color.BLACK);
        		if (selectedSlot > 0) {
        			selectedSlot--;
        		}
        		else {
        			selectedSlot = 9;
        		}
        		slots.get(selectedSlot).setColor(Color.WHITE);
        		if (options.size() > selectedSlot) {
            		selectedBlock = options.get(selectedSlot);
            	}
        	}
        });
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) moveF.play();
            if (e.getCode() == KeyCode.D) moveR.play();
            if (e.getCode() == KeyCode.S) moveB.play();
            if (e.getCode() == KeyCode.A) moveL.play();
            if (e.getCode() == KeyCode.Q) sprinting = true; 
            if (e.getCode() == KeyCode.SPACE) {
            	if (!godMode) {
            	jumping = true;
            	}
            	else {
            		moveU.play();
            	}
            }
            if (e.getCode() == KeyCode.O) deleteWorld(); 
            if (e.getCode() == KeyCode.R) { 
            	camera.setTranslateY(-spawnY * 25);
            	camera.setTranslateX(spawnX);
            	camera.setTranslateZ(spawnZ);
            	
            	scene.setFill(Color.LIGHTSKYBLUE);
            	playerVelocity = 0;
            }
            if (e.getCode() == KeyCode.SHIFT) {
            	if (!godMode) {
            	if (sneaking == false) {
            	sneaking = true;
            	camera.setTranslateY(camera.getTranslateY() + 15);
            	} 
            	}
            	else {
            		moveD.play();
            	}
            }
            if (e.getCode() == KeyCode.ESCAPE) {
            	if (godMode) {
	            	BufferedWriter bw;
	    			try {
	    				bw = new BufferedWriter(new FileWriter("Maps/wind.csv", false));
	    				for (Map.Entry<Point3DKey, String> entry : map.entrySet()) {
	    					Point3DKey p = entry.getKey();
	    					String sb = entry.getValue();
	    					String c = Double.toString(p.point.getX()) + "," + Double.toString(p.point.getY()) + "," + Double.toString(p.point.getZ()) + "," + sb;
	    					bw.write("\n" + c);
	    				}		
	    			    bw.flush();
	    			    bw.close();
	    			} catch (IOException e1) {
	    				// TODO Auto-generated catch block
	    				e1.printStackTrace();
	    			}
	    			
	    			BufferedWriter bb;
	    			try {
	    				bb = new BufferedWriter(new FileWriter("Maps/windtp.csv", false));
	    				for (Cube c : teleporters) {
	    					String wr = Double.toString(c.box.getTranslateX()/25) + "," + Double.toString(c.box.getTranslateY()/25) + "," + Double.toString(c.box.getTranslateZ()/25);
	    				    bb.write("\n" + wr);
	    				}		
	    			    bb.flush();
	    			    bb.close();
	    			} catch (IOException e1) {
	    				// TODO Auto-generated catch block
	    				e1.printStackTrace();
	    			}
            	}
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
            if (e.getCode() == KeyCode.Q) sprinting = false; 
            if (e.getCode() == KeyCode.SPACE) {
            	if (!godMode) {
            	jumping = false;
            	}
            	else {
            		moveU.stop();
            	}
            }
            if (e.getCode() == KeyCode.SHIFT) {
            	if (!godMode) {
            	sneaker = true;
            	}
            	else {
            		moveD.stop();
            	}
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
        	if (block.texture.equals("teleport")) {
        		continue;
        	}
        	if (Cube.TT(hitbox, block.box)) {
        		if (!block.movable) {
        			gotHit = true;
                    break;
        		}
        		else {
        			double tx = block.box.getTranslateX();
        			double tz = block.box.getTranslateZ(); 
        			block.box.setTranslateX(block.box.getTranslateX() + (deltaX * rightX + deltaZ * forwardX));
        			block.box.setTranslateZ(block.box.getTranslateZ() + (deltaX * rightZ + deltaZ * forwardZ));
//        			block.box.setTranslateY(block.box.getTranslateY() - 1);
        			boolean free = true;
//        			blocks.remove(block);
        			for (Cube b: blocks) {
        				if (Cube.TT(block.box, b.box) && !b.movable) {
        					free = false;
        					break;
        				}
        			}
//        			block.box.setTranslateY(block.box.getTranslateY() + 1);   			       			
        			if (!free) {
        				block.box.setTranslateX(tx);
        				block.box.setTranslateZ(tz);
        				gotHit = true;
//        				block.movable = false;
                        break;
        			}
//        			blocks.add(block);
        		}
        		
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
            			if (T(block.box, hitbox) && !block.movable) {
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
	static Image scooby = new Image("file:Textures/god.png");
	static Image elementt = new Image("file:Textures/element.png");
	static Image wind = new Image("file:Textures/wind.png");
	static Image magma = new Image("file:Textures/magma.png");
	static Image water = new Image("file:Textures/water.png");
	static Image color = new Image("file:Textures/color.png");
	static Image brick = new Image("file:Textures/brick.png");
	static Image fireBrick = new Image("file:Textures/fireBrick.png");
	static Image waterBrick = new Image("file:Textures/waterBrick.png");
	static Image teleport = new Image("file:Textures/teleport.png");
	static Image wood = new Image("file:Textures/wood.png");
	static Image sand = new Image("file:Textures/sand.png");
	static Image grass = new Image("file:Textures/grass.png");
	static Image metal = new Image("file:Textures/metal.png");
	static Image circuit = new Image("file:Textures/circuit.png");
	int opacity = 00;
	boolean breakable;
	boolean movable;
	PhongMaterial m;
	String texture;
	public Cube(float length, float width, float height, float x, float y, float z) {
		movable = false;
		breakable = true;
		m = new PhongMaterial();
		box = new Box(length, width, height);
		box.setTranslateX(x);
		box.setTranslateY(y);
		box.setTranslateZ(z);
//		box.setEffect(new GaussianBlur(5));
		box.setCache(true);
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
		texture = path;
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
        else if (path.equals("element")) {
            m.setDiffuseMap(elementt);
            this.box.setMaterial(m);
        }
        else if (path.equals("wind")) {
        	m.setDiffuseMap(wind);
            this.box.setMaterial(m);
        }
        else if (path.equals("magma")) {
        	m.setDiffuseMap(magma);
            this.box.setMaterial(m);
        }
        else if (path.equals("water")) {
        	m.setDiffuseMap(water);
            this.box.setMaterial(m);
        }
        else if (path.equals("color")) {
        	breakable = false;
        	m.setDiffuseMap(color);
            this.box.setMaterial(m);
        }
        else if (path.equals("brick")) {
        	m.setDiffuseMap(brick);
            this.box.setMaterial(m);
        }
        else if (path.equals("fireBrick")) {
        	m.setDiffuseMap(fireBrick);
            this.box.setMaterial(m);
        }
        else if (path.equals("waterBrick")) {
        	m.setDiffuseMap(waterBrick);
            this.box.setMaterial(m);
        }
        else if (path.equals("wood")) {
        	m.setDiffuseMap(wood);
            this.box.setMaterial(m);
        }
        else if (path.equals("sand")) {
        	m.setDiffuseMap(sand);
            this.box.setMaterial(m);
        }
        else if (path.equals("grass")) {
        	m.setDiffuseMap(grass);
            this.box.setMaterial(m);
        }
        else if (path.equals("metal")) {
        	breakable = false;
        	movable = true;
        	App.root.getChildren().remove(f.r);
        	App.root.getChildren().remove(b.r);
        	App.root.getChildren().remove(l.r);
        	App.root.getChildren().remove(r.r);
        	App.root.getChildren().remove(u.r);
        	App.root.getChildren().remove(d.r);
//        	box.setDepthTest(DepthTest.DISABLE);
        	
        	Timeline down = new Timeline(new KeyFrame(Duration.millis(10), e -> {
        		box.setTranslateY(box.getTranslateY() + 1) ;
        		boolean hit = false;
        		for (Cube block : App.blocks) {	
        			
        			if ((TT(box, block.box) && block.movable == false) || TT(box, App.hitbox)) {
        				hit = true;
        				break;
        			}
        		}
        		if (hit) {
        			box.setTranslateY(box.getTranslateY() - 1) ;
        		}
        		
        	}));
        	box.setOnMouseClicked(e -> {
        		if (e.getButton() == MouseButton.PRIMARY) { 
        			if (App.godMode) {
        				App.blocks.remove(this);
                		Point3DKey h = new Point3DKey(box.getTranslateX()/25, box.getTranslateY()/25, box.getTranslateZ()/25);
            			App.map.remove(h);
                		App.root.getChildren().remove(box);
        			}
        			
        		}
        		
        	});
        	down.setCycleCount(Timeline.INDEFINITE);
        	down.play();
        	m.setDiffuseMap(metal);
            this.box.setMaterial(m);
        }
        else if (path.equals("circuit")) {
        	m.setDiffuseMap(circuit);
            this.box.setMaterial(m);
        }
        else if (path.equals("teleport")) {
        	breakable = false;
//        	if (App.tp1 == null) {
//        		App.tp1 = this;
//        		
//        	}
//        	else if (App.tp2 == null) {
//        		App.tp2 = this;
//        	}
//        	App.teleporters.add(this);
        	m.setDiffuseMap(teleport);
            this.box.setMaterial(m);
        }
    }
	public void move() {
		if (!movable) {
			return;
		}
		
	}
	
	public static boolean TT(Box box1, Box box2) {
        // Get the bounds of both boxes
        Bounds bounds1 = box1.getBoundsInParent();
        Bounds bounds2 = box2.getBoundsInParent();

        // Check for overlap in the x-axis
        boolean overlapX = bounds1.getMinX() < bounds2.getMaxX() && bounds1.getMaxX() > bounds2.getMinX();

        // Check for overlap in the y-axis
        boolean overlapY = bounds1.getMinY() < bounds2.getMaxY() && bounds1.getMaxY() > bounds2.getMinY();

        // Check for overlap in the z-axis
        boolean overlapZ = bounds1.getMinZ() < bounds2.getMaxZ() && bounds1.getMaxZ() > bounds2.getMinZ();

        // Return true if they overlap in all axes
        return overlapX && overlapY && overlapZ;
    }
	public void unbreakable() {
		breakable = false;
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
	double xxp;
	double yyp;
	double w;
	double h;
	Rectangle r;
	static ArrayList<FlatRect> menuList = new ArrayList<FlatRect>();
	public FlatRect(double x, double y, double xp, double yp) throws NonInvertibleTransformException {
//		Transform inv = App.camera.getLocalToSceneTransform().createInverse();
		// Create a rectangle
		w = x;
		h = y;
		xxp = xp;
		yyp = yp;
		r = new Rectangle(x * screenWidth, y * screenHeight);
		r.setTranslateZ(100);
		r.setOpacity(0.8);
		r.setDepthTest(DepthTest.DISABLE);
//		r.setCache(true);
		App.root.getChildren().add(r);
//		r.getTransforms().add(inv)
		Rotate xt = new Rotate(0, Rotate.Y_AXIS);
		Rotate yt = new Rotate(0, Rotate.X_AXIS);
		r.getTransforms().addAll(xt, yt);
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(10), e -> {
			
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
class FlatText {
	static double ratio = App.screenX/App.screenY;
	static double vfov =  App.cameraFOV;
	static double radian = vfov * (Math.PI/180);
	static double hfov = 2*(Math.atan(Math.tan(radian/2) * ratio));
	static double z = 1000;
	static double screenWidth = Math.abs(2 * (z * Math.tan(hfov/2)));
	static double screenHeight = Math.abs(2 * (z * Math.tan(radian/2)));
	static Rotate tempX = App.rotateX;
	static Rotate tempY = App.rotateY;
	double xxp;
	double yyp;
	double w;
	double h;
	Text t;
	static ArrayList<FlatRect> menuList = new ArrayList<FlatRect>();
	public FlatText(double x, double y, double xp, double yp, int index) throws NonInvertibleTransformException {
//		Transform inv = App.camera.getLocalToSceneTransform().createInverse();
		// Create a rectangle
		w = x;
		h = y;
		xxp = xp;
		yyp = yp;
		t = new Text(Integer.toString(App.numItems.get(index)));
		t.setTranslateZ(100);
//		t.setCache(true);
		t.setDepthTest(DepthTest.DISABLE);
		App.root.getChildren().add(t);
//		r.getTransforms().add(inv)
		Rotate xt = new Rotate(0, Rotate.Y_AXIS);
		Rotate yt = new Rotate(0, Rotate.X_AXIS);
		t.getTransforms().addAll(xt, yt);
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(10), e -> {
			App.root.getChildren().remove(t);
			xt.setAngle(App.cameraRX);
			yt.setAngle(App.cameraRY);
			Transform local = App.camera.getLocalToSceneTransform();
			Point3D newlocal = local.transform(new Point3D(xp * screenWidth, yp * screenHeight, z));
			t.setTranslateX(newlocal.getX());
			t.setTranslateY(newlocal.getY());
			t.setTranslateZ(newlocal.getZ());
			App.root.getChildren().add(t);
			
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
	public void reduce(int num) {
		t.setText(Integer.toString(Integer.parseInt(t.getText()) - num));
	}
	public void increase(int num) {
		t.setText(Integer.toString(Integer.parseInt(t.getText()) + num));
	}
	public void setColor(Color c) {
		t.setFill(c);
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