import java.util.ArrayList;

import java.util.EventObject;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientUI {
	
	private HashMap<String, Scene> sceneMap;
	private ListView<String> listItems;
	public HBox letters = new HBox();
	private ArrayList<Button> buttons;
	private Stage primaryStage;
	private Client clientConnection;
	private static int MaxLetters = 12;
	private int playerID, startPosition, length;
	private int Hchance = 6;
	private GameInfo roundGameInfo = new GameInfo();	//use to keep track every round guess
	private boolean foodpass = false, animalspass = false, statespass = false;
	private int foodfail = 0, animalfail = 0, statesfail = 0;
	private String category;
	private Button nextbtn, sendBtn;
	

	public ClientUI(HashMap<String, Scene> sceneMap, Stage primaryStage) {
		this.sceneMap = sceneMap;
		this.primaryStage = primaryStage;
	}
	public ClientUI() {
		
	}
	
	public Scene createServeScene() {
		sceneMap.put("ClientScene1", createScene1());
		return sceneMap.get("ClientScene1");
	}

	//------Client Scene 1
	private Scene createScene1() {
		//------Declare
		listItems = new ListView<String>();
		Text ipText = new Text("Server IP: ");
		Text portText = new Text("Client Port:");
		ipText.setFont(Font.font ("Verdana", 50));
		portText.setFont(Font.font ("Verdana", 50));
		TextField ipTextField = new TextField("127.0.0.1");
		TextField portTextField = new TextField("5555");
		Button portEnterBtn = new Button("Enter");
		
		//------Layout
		VBox portVBox = new VBox (40, ipText, ipTextField, portText, portTextField, portEnterBtn);
		portVBox.setPadding(new Insets(0,150,0,150));
		portVBox.setAlignment(Pos.CENTER);
		
		//------Methods
		//the following function only accept user input integer
		portTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    if (!newValue.matches("\\d*")) {
		    	portTextField.setText(newValue.replaceAll("[^\\d]", ""));
		    }
		});
		
		portEnterBtn.setOnAction(e->{
			try {
				clientConnection = new Client(ipTextField.getText(), Integer.parseInt(portTextField.getText()), data->{
					Platform.runLater(()->{
						roundGameInfo = (GameInfo) data;
						String s = roundGameInfo.message;
						listItems.getItems().add(s);
						System.out.println("portEnterBtn-parse"+roundGameInfo.chance);
						parseCallback(roundGameInfo);
					});
				});
				
				clientConnection.start();
				System.out.println("client port: " + portTextField.getText());
				sceneMap.put("ClientScene2", createScene2());
				primaryStage.setScene(sceneMap.get("ClientScene2"));
				
			} catch (NumberFormatException e2) {
				System.out.println("Port number format error");
			}
		});
		
		return new Scene(portVBox,600,600);
	}
	
	//Client scene2: choose one of categories
	private Scene createScene2() {
		
		
		ObservableList<String> options = 
		    FXCollections.observableArrayList(
		        "Foods",
		        "Animals",
		        "U.S. States"
		    );
		
		/*if (foodpass == false) {
			options.addAll("Foods");
		}
		if (animalspass == false) {
			options.addAll("Animals");
		}
		if (statespass == false) {
			options.addAll("U.S. States");
		}    */
		
		ComboBox<String> comboBox = new ComboBox<String>(options);
		comboBox.setStyle("-fx-pref-width: 150;");
		//set first category default
		comboBox.getSelectionModel().selectFirst();
		
		Text text = new Text("Please select one category:");
		text.setFont(Font.font ("Verdana", 50));
		Button enterBtn = new Button("Enter");
		
		Text rule = new Text("Please read the rules of the game before you begin???");
		Text rule1 = new Text("       1.Your can choose from three categories.");
		Text rule2 = new Text("       2.To win the game, you need to successfully guess word from each categories once.");
		Text rule3 = new Text("       3.For each word, you have 6 chances.");
		Text rule4 = new Text("       4.For each category, you have at most three chances.");
		Text rule5 = new Text("       5.Once you successfully guess word from one category, you can NOT choose word form this category anymore.");
		Text rule6 = new Text("       6.Directly fail the game if you wrongly guess form one category three times.");
		Text rule7 = new Text("GOOD LUCK !!!");
		Text remind = new Text("");
		
		  //------Layout
		VBox ruleBox = new VBox(10);
		ruleBox.getChildren().addAll(rule, rule1,rule2,rule3,rule4,rule5,rule6,rule7);
		ruleBox.setPadding(new Insets(10));
		ruleBox.setAlignment(Pos.BASELINE_LEFT);
		  

		VBox vbox = new VBox(30, ruleBox, remind, comboBox, enterBtn);
		vbox.setAlignment(Pos.CENTER);
		
		//------Methods
		enterBtn.setOnAction(e->{
			sceneMap.remove("ClientScene2");
			category = comboBox.getSelectionModel().getSelectedItem().toString();
			if( (category.equals("Foods") && foodpass==true) ||   
			     (category.equals("U.S. State") && statespass ==true)||
			     (category.equals("Animals") && animalspass ==true ) ) {
				remind.setText("You already done "+category+" category"+" please choose another");
			}
			else {
				clientConnection.send(playerID, Hchance, category);
				listItems.getItems().add("Your category is " + category);
				sceneMap.put("ClientScene3", createScene3());
				primaryStage.setScene(sceneMap.get("ClientScene3"));
			}
			
				
			
			
		});
		
		return new Scene(vbox, 800, 400);
	}
	
	//main scene for playing game
	private Scene createScene3() {
		//------Declare
		//Maximum word letter is 12
		letters = new HBox(20);
		buttons = new ArrayList<Button>();
		for (int i = 0; i < MaxLetters; i++) {
			Button btn = new Button("_");
			btn.setVisible(false);
			letters.getChildren().add(btn);
			buttons.add(btn);
		}
		nextbtn = new Button("Next");
		nextbtn.setDisable(true);
		sendBtn = new Button("Send");
		sendBtn.setDisable(false);
		TextField textField = new TextField();
		VBox vbox = new VBox(10, textField, sendBtn, nextbtn);
		vbox.setAlignment(Pos.CENTER_RIGHT);
		Text gameInfo = new Text("Game Info: ");
		VBox vbox2 = new VBox(10, gameInfo, listItems);
		HBox buttomLayout = new HBox(100, vbox2, vbox);
		ImageView backgroundImageView = new ImageView("background.jpeg");
		backgroundImageView.setPreserveRatio(true);
		backgroundImageView.setFitWidth(1000);
		Group group = new Group();
		Scene scene = new Scene(group);
		
		
		
		//------Layout
		letters.setLayoutX(20);
		letters.setLayoutY(20);
		listItems.setPrefHeight(200);
		listItems.setPrefWidth(280);
		buttomLayout.setLayoutX(190);
		buttomLayout.setLayoutY(200);
		group.getChildren().addAll(backgroundImageView, letters, buttomLayout);
		
		//------Method
		gameInfo.setStyle("-fx-font-size: 32px;" + 
				"-fx-font-family: \"times\";" + 
				"-fx-fill: black;");
		//set textfield can only take one char
		textField.setTextFormatter(new TextFormatter<String>((Change change) -> {
		    String newText = change.getControlNewText();
		    if (newText.length() > 1) {
		        return null ;
		    } else {
		        return change ;
		    }
		}));
		
		nextbtn.getStylesheets().add
			(ClientUI.class.getResource("sendBtn.css").toExternalForm());
		sendBtn.getStylesheets().add
 			(ClientUI.class.getResource("sendBtn.css").toExternalForm());
		
		sendBtn.setOnAction(e->{
			if(!textField.getText().trim().isEmpty()) {
				
				if (roundGameInfo.chance != '\u0000') {
					Hchance = roundGameInfo.chance;
				}		
				clientConnection.send(playerID, Hchance, Character.toLowerCase(textField.getText().charAt(0)));
				System.out.println("sendBtn-parse"+roundGameInfo.chance);
				
				textField.clear();		
			}
		});
		
		letters.setMouseTransparent(true);
		letters.getStylesheets().add
	 		(ClientUI.class.getResource("letters.css").toExternalForm());
		
		return scene;
	}
	
	//After player win or lose, let then choose restart or quit
	 private Scene createScene4(boolean result) {
	  
	  
	  ImageView image4 = new ImageView("scen4.png");
	  image4.setPreserveRatio(true);
	  image4.setFitWidth(600);
	  Group group4 = new Group();
	  Scene scene4 = new Scene(group4);
	  
	  
	  Button restart = new Button("Restart");
	  restart.setFont(Font.font(null, FontWeight.BLACK, 20));
	  Button quit = new Button("Quit");
	  quit.setFont(Font.font(null, FontWeight.BLACK, 20));
	  HBox layout = new HBox(40, restart, quit);
	  layout.setPadding(new Insets(20));
	  layout.setAlignment(Pos.CENTER);

	  group4.getChildren().addAll(image4, layout);
	  //------Methods
	  sceneMap.remove("ClientScene4"); //remove this scene from scene map, because every time generating this scene won't cause hashmap collision
	  restart.setOnAction(e->{
	   //TODO: reset number of guesses and total number of categories win
			Hchance = 6;
			foodpass = false;
			animalspass = false;
			statespass = false;
			foodfail = 0;
			animalfail = 0;
			statesfail = 0;
			sceneMap.put("ClientScene2", createScene2());
			primaryStage.setScene(sceneMap.get("ClientScene2"));
	   
	  });
//	  quit.setOnAction(e->{
//	   primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//	             @Override
//	             public void handle(WindowEvent t) {
//	                 Platform.exit();
//	                 System.exit(0);
//	             }
//	         });
// 
//	  });
	  
	  quit.setOnAction(e -> Platform.exit());
	  return scene4;
	 }
	 
	
//	//After player win or lose, let then choose restart or quit
//	private Scene createScene4(boolean result) {
//		
//		Button restart = new Button("Restart");
//		Button quit = new Button("Quit");
//		HBox layout = new HBox(20, restart, quit);
//		layout.setAlignment(Pos.CENTER);
//		
//		//------Methods
//		sceneMap.remove("ClientScene4");	//remove this scene from scene map, because every time generating this scene won't cause hashmap collision
//		restart.setOnAction(e->{
//			//TODO: reset number of guesses and total number of categories win
//			Hchance = 6;
//			foodpass = false;
//			animalspass = false;
//			statespass = false;
//			foodfail = 0;
//			animalfail = 0;
//			statesfail = 0;
//			primaryStage.setScene(sceneMap.get("ClientScene2"));
//		});
//		quit.setOnAction(e->{
//			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//	            @Override
//	            public void handle(WindowEvent t) {
//	                Platform.exit();
//	                System.exit(0);
//	            }
//	        });
//		});
//		return new Scene(layout, 600, 600);
//	}

	private void parseCallback(GameInfo gameInfo) {
		String s = gameInfo.message;
		if (s.contains("You are player")){
			playerID = Integer.valueOf(s.substring(s.length() - 1, s.length()));
			System.out.println("This client is player " + playerID);
			System.out.println("You have " + Hchance + " chance.");
		}
		else if(s.contains("Your word length")) {
			length = Integer.parseInt(s.substring(s.length() - 1, s.length()));
			updateLetters(length);
		}
		else if (s.contains("You have no chance, Game Over")) {
			System.out.println("test: " + Hchance);
			// if get wrong with one word, count the category fail once 
				if (category == "Foods") {
					foodfail++;
				}
				else if (category == "Animals") {
					animalfail++;
				}
				else {
					statesfail++;
				}
				String w = "There still have ";
				if (foodpass != true) {
					w += "Foods ";
				}
				if (animalspass != true) {
					w += "Animals ";
				}
				if (statespass != true) {
					w += "U.S. states ";
				}
				w += "categories need to be finish.";
				listItems.getItems().add("You failed Foods: "+foodfail+" time \n"+"Animals: "+animalfail+" time"+" States: "+statesfail+" time");
				
				listItems.getItems().add(w);
				
				// if the category fail hit three times, game over
				if((foodfail == 3) || (animalfail == 3) || (statesfail == 3)) {
					//clientConnection.send(playerID, Hchance, Character.toLowerCase(textField.getText().charAt(0)));
					listItems.getItems().add("Press Next button to quit or restart a new game.");
					
					
					System.out.println("in option fail");
					sendBtn.setDisable(true);
					nextbtn.setDisable(false);
					nextbtn.setOnAction(e->{
						sceneMap.put("ClientScene4", createScene4(false));
						primaryStage.setScene(sceneMap.get("ClientScene4"));
					});
					
				}
				// else return scene2
				else {
					Hchance = 6;
					listItems.getItems().add("Press Next button to continue game.");
					sendBtn.setDisable(true);
					nextbtn.setDisable(false);
					nextbtn.setOnAction(e->{
						sceneMap.put("ClientScene2", createScene2());
						primaryStage.setScene(sceneMap.get("ClientScene2"));
					});
				}
			//}
		}
		else if(gameInfo.message.contains("correctly guessed") && gameInfo.positions.size() != 0) {
			updateLetters(gameInfo.letter, gameInfo.positions);
			if (checkWordComplete() == true) {
				if (category == "Foods") {
					foodpass = true; 
				}
				else if (category == "Animals") {
					animalspass = true; 
				}
				else {
					statespass = true;
				}
				
				if ((foodpass == true) && (animalspass == true) && (statespass == true)) {
					listItems.getItems().add("Congratulate! You won the game. Press Next button to quit or start a new game.");
					sendBtn.setDisable(true);
					nextbtn.setDisable(false);
					nextbtn.setOnAction(e->{
						sceneMap.put("ClientScene4", createScene4(false));
						primaryStage.setScene(sceneMap.get("ClientScene4"));
					});
				}
				
				else {
					String w = "congratulate! There still have ";
					if (foodpass != true) {
						w = w + "Foods ";
					}
					if (animalspass != true) {
						w = w + "Animals ";
					}
					if (statespass != true) {
						w = w + "U.S. states ";
					}
					w = w + "categories need to be finish.";
					listItems.getItems().add(w);
					listItems.getItems().add("Press next button to contine!");
					sendBtn.setDisable(true);
					Hchance = 6;
					nextbtn.setDisable(false);
					nextbtn.setOnAction(e->{
						sceneMap.put("ClientScene2", createScene2());
						primaryStage.setScene(sceneMap.get("ClientScene2"));
					});
				}
				
			}
		}
	}
	
	private void updateLetters(char letter, ArrayList<Integer> positions) {
		for(int i : positions) {
			buttons.get(i + startPosition).setText(String.valueOf(letter));
		}
		//after update matching letters on UI, check if word complete next
		//checkWordComplete();
	}

	public boolean checkWordComplete() {
		
		int letterNum = 0;
		for (Node n : letters.getChildren()) {
			//convert Node to Button
			if (n instanceof Button) {
		        if(((Button) n).getText().compareTo("_") != 0) {
		        	letterNum++;	        	
		        }
		    }
		}
		if (letterNum == length) {
			return true;
		}
		else {
			return false;
		}
		
	}

	private void updateLetters(int length) {
		int mid = MaxLetters/2;
		startPosition = mid - (length/2);
		int endPosition = mid + (length - (length/2));
		for(int i = startPosition; i < endPosition; i++) {
			letters.getChildren().get(i).setVisible(true);
		}
	}
}
