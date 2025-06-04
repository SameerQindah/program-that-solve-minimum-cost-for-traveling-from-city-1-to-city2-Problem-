package application;

import java.io.*;
import java.util.*;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

//*********************************GLOBAL VARIABELS******************************************** 	
public class Main<T> extends Application {
	public int numOfCities = 0;
	public int fromCity = 0;
	public int toCity = 0;
	public String path = "";
	FileChooser fileChooser = new FileChooser();// Create a new FileChooser object
	File selectedFile;

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		
		//main scene 
		BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        Image pic = new Image("file:/C:/Users/Ahmad/Desktop/last_map.png");
        ImageView imageView = new ImageView(pic);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);
        

        Label title = new Label("Optimal Route Between Cities");
        title.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-font-weight: bold;");

        Button loadButton = new Button("Load Data");
        loadButton.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 14px;");

//
        VBox vbox = new VBox(20, title, imageView, loadButton);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-alignment: center;");
        root.setCenter(vbox);

        Scene main_scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Optimal Route Between Cities");
        primaryStage.setScene(main_scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        
      loadButton.setOnAction(e -> {
      
      
		// Define a font object with the Times New Roman font, light weight, and size 20
		Font font = Font.font("Times New Roman", FontWeight.LIGHT, 20);

		fileChooser.setTitle("Select Input File");// Set the title of the file chooser dialog

		fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Set the initial directory to the
																					// user's home directory

		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"), // Filter for

				new FileChooser.ExtensionFilter("All Files", "*.*") // Filter for all files
		);

		// Open a file dialog window for the user to select a file and store the
		// selected file in selectedFile
		selectedFile = fileChooser.showOpenDialog(new Stage());
		
		if (selectedFile == null) {
			// If no file is selected, show an alert dialog
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Please choose a file first.");
			alert.showAndWait();
		} else {

			try (// Create a Scanner object to read from the selected file
					Scanner sc = new Scanner(selectedFile)) {
				String number = sc.nextLine();// Read the number of cities from the first line
				numOfCities = Integer.parseInt(number);// of the file and parse it to an integer
				String[] city = new String[numOfCities];
				String input = "";// stores all the file exept first two lines

				String routeLine = "";
				while (sc.hasNextLine()) {
					routeLine = sc.nextLine().trim();
					if (!routeLine.isEmpty())
						break;
				}

				String[] startEnd = routeLine.split(",\\s*");
				String Start = startEnd[0];
				String End = startEnd[1];

				int cityIndex = 0;
				while (sc.hasNextLine() && cityIndex < numOfCities) {
					String line = sc.nextLine().trim();
					if (line.isEmpty())
						continue;
					input += line + "\n";

					// store it in the array "city"
					String[] parts = line.split(",\\s*");
					city[cityIndex] = parts[0];
					cityIndex++;
				}

				// Add the end city to the last slot
				if (cityIndex < numOfCities) {
					city[cityIndex] = End;
				}

				System.out.println(Start);
				System.out.println(End + "\n" + "_____________________________");

				// Print city information for debugging
				for (int i = 0; i < city.length; i++)
					System.out.println(city[i]);

				// to find the index of start city and end city
				for (int k = 0; k < city.length; k++) { // to use it in the methods
					if (city[k].equals(Start)) {
						fromCity = k;
						break;
					}
				}

				for (int k = 0; k < city.length; k++) { // to use it in the methods
					if (city[k].equals(End)) {
						toCity = k;
						break;
					}
				}

				// Initialize a table to store distances between cities//for cost
				int[][] table = new int[numOfCities][numOfCities];// 2 dim array

				for (int i = 0; i < numOfCities; i++) {// fill the table
					for (int j = 0; j < numOfCities; j++) {
						if (i == j)
							table[i][j] = 0; // cost from a city to itself is 0
						else
							table[i][j] = Integer.MAX_VALUE; // Initialize other distances to maximum value
					}
				}

				String[][] next = new String[numOfCities][numOfCities];

				for (int i = 0; i < numOfCities; i++) {

					for (int j = 0; j < numOfCities; j++) {

						next[i][j] = city[j];
						// System.out.print(next[i][j] + " ");
					}
					// System.out.println();
				}


				String[] x = new String[numOfCities - 1];
				x = input.split("\n"); // split every city line

				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < table.length; j++) {
						if (i == j)
							table[i][j] = 0;
						else
							table[i][j] = Integer.MAX_VALUE;

						next[i][j] = null; // keep null for “no edge”
					}
				}
				
				for (int i = 0; i < x.length; i++) {
				    // works whether there is a space before the '[' or not
				    String[] parts = x[i].split(",\\s*(?=\\[)");

				    int city1 = i;                     

				    for (int j = 1; j < parts.length; j++) {
				        // remove the brackets, then split into city name, petrol, hotel
				        String[] cityAndCosts = parts[j]
				                                 .replaceAll("[\\[\\]]", "")
				                                 .split(",");

				        String item = cityAndCosts[0].trim();  // destination city name
				        int petrolCost = Integer.parseInt(cityAndCosts[1].trim());
				        int hotelCost = Integer.parseInt(cityAndCosts[2].trim());

				        // look up the destination city index
				        int city2 = 0;
				        for (int k = 0; k < city.length; k++) {
				            if (city[k].equals(item)) {
				                city2 = k;
				                System.out.println(item + ":" + k +" "+ "in 'city' array"); 
				                break;
				            }
				        }
                        
				        // store edge weight
				        table[city1][city2] = petrolCost + hotelCost;
				        System.out.println("table"+"[" +city1+"]"+"["+city2+"]"+"="+ (petrolCost + hotelCost) +"\n"); 
				       
				        next[city1][city2]  = city[city2];
				    }
				}


				for (int k = 0; k < numOfCities; k++) {
					for (int i = 0; i < numOfCities; i++) {
						for (int j = 0; j < numOfCities; j++) {
							if (table[i][k] != Integer.MAX_VALUE && table[k][j] != Integer.MAX_VALUE
									&& table[i][j] > table[i][k] + table[k][j]) {

								table[i][j] = table[i][k] + table[k][j];
								next[i][j] = next[i][k]; 
							}
						}
					}
				}


				// Initialize an ArrayList to store arrays of strings
				List<String[]> data = new ArrayList<>();

				try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))) {
					String line;
					// Read each line from the input file
					while ((line = reader.readLine()) != null) {
						String[] lineData = line.split(",");// Split the line by comma to separate values
						// Initialize an array to store extracted strings
						String[] extractedStrings = new String[lineData.length];
						// Iterate over each element in the lineData array
						for (int i = 0; i < lineData.length; i++) {
							// Remove extra characters and trim whitespace from each element
							lineData[i] = lineData[i].split(",")[0].trim().replaceAll("[\\[\\]]", "");
						}
						int index = 0;
						// Iterate over each element in the modified lineData array
						for (String element : lineData) {
							// Check if the element is not numeric
							if (!isNumeric(element)) {
								// Store the non-numeric element in the extractedStrings array
								extractedStrings[index] = element.trim();
								index++;
							}
						}
						// guard against blank or header rows
						if (index == 0 || extractedStrings[0] == null || extractedStrings[0].isBlank()) {
							continue; // skip this row
						}

						data.add(Arrays.copyOf(extractedStrings, index));
					}
				} catch (IOException ex) {
					// Handle IOException by printing the stack trace
					ex.printStackTrace();
				}

				// Remove the first element (header) from the data list
				data.remove(0);

				TextField bestCostField = new TextField();// Create a TextField for displaying the minimum cost
				bestCostField.setEditable(false);// Make the TextField non-editable
				bestCostField.setPromptText("The Minimum Cost will appear here");// Set a prompt text for the TextField
				bestCostField.setPrefColumnCount(6); // Set the preferred column count for the TextField
				bestCostField.setPrefWidth(400);// Set the preferred width for the TextField
				bestCostField.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));// Set the font for the
																								// TextField
				bestCostField.setStyle("-fx-background-color: black; -fx-text-fill: orange; -fx-font-size: 24px; -fx-font-weight: bold;");// Apply custom styling to the TextField (assuming Style3 is a defined
												// style)


				Button findMinCost = new Button("Find The Minimum Cost");// Create a button for finding the minimum cost
				findMinCost.setFont(font);// Set the font for the button
				findMinCost.setTextFill(Color.BLACK);// Set the text color for the button
				findMinCost.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8px 15px;");// Apply custom styling to the button (assuming Style2 is a defined style)

				HBox best = new HBox(bestCostField, findMinCost);// Create an HBox to hold the TextField and the button
				best.setAlignment(Pos.CENTER);// Set the alignment of elements within the HBox to center
				best.setSpacing(20);// Set the spacing between elements in the HBox


				TextArea pathArea = new TextArea();// Create a TextArea for displaying the path
				pathArea.setEditable(false);// Make the TextArea non-editable
				pathArea.setPrefHeight(50); // Set the preferred height
				pathArea.setPrefWidth(300); // and width for the TextArea
				pathArea.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));// Set the font for the TextArea
				pathArea.setStyle("-fx-background-color: rgba(255, 255, 0, 0.3); -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: transparent;");// Apply custom styling to the TextArea (assuming Style4 is a defined style)

				Button printPath = new Button("Print Path");// Create a button for printing the path
				printPath.setFont(font);// Set the font for the button
				printPath.setTextFill(Color.BLACK); // Set the text color for the button
				printPath.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;");// Apply custom styling to the button (global variable)

				Button btTable = new Button("The Table");// Create a button for displaying the table
				btTable.setFont(font);// Set the font for the button
				btTable.setTextFill(Color.BLACK);// Set the text color for the button
				btTable.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;");// Apply custom styling to the button (global variable)

				HBox hbPath = new HBox(); // Create an HBox to hold the buttons related to the path
				hbPath.setStyle("-fx-alignment: CENTER;");// Set the alignment of elements within the HBox to center
				hbPath.getChildren().addAll(printPath, btTable);// Add the buttons to the HBox
				hbPath.setPadding(new Insets(0, 0, 0, 20)); // Set padding for the HBox

				HBox.setMargin(printPath, new Insets(0, 20, 0, 0));// Set margin for the "printPath" button

				TextArea othersArea = new TextArea(); // Create a TextArea for displaying other information

				othersArea.setEditable(false);// Make the TextArea non-editable
				othersArea.setPrefHeight(300);// Set the preferred height
				othersArea.setPrefWidth(300);// width for the TextArea
				othersArea.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));// Set the font for the TextArea
				othersArea.setStyle("-fx-background-color: rgba(255, 255, 0, 0.3); -fx-text-fill: red; -fx-font-size:20px; -fx-padding: 8px 15px; -fx-font-weight: bold; -fx-border-color: transparent; ");// Apply custom styling to the button (global variable)

				Button printOthers = new Button("The Alternative Paths");// Create a button for printing alternative
																			// paths
				printOthers.setFont(font);// Set the font for the button
				printOthers.setTextFill(Color.BLACK); // Set the text color for the button
				printOthers.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;");// Apply custom styling to the button (global variable)

				Button clearOtherPaths = new Button("Clear The Other Paths!");// Create a button for clearing other
																				// paths
				clearOtherPaths.setFont(font);// Set the font for the button
				clearOtherPaths.setTextFill(Color.BLACK);// Set the text color for the button
				clearOtherPaths.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;");// Apply custom styling to the button (assuming Style6 is a defined
													// style)

				HBox hbAlternative = new HBox();// Create an HBox to hold the buttons related to alternative paths
				hbAlternative.setAlignment(Pos.BOTTOM_CENTER);// Set the alignment of elements within the HBox to bottom
																// center
				hbAlternative.getChildren().addAll(printOthers, clearOtherPaths);// Add the buttons to the HBox
				hbAlternative.setSpacing(40);// Set spacing between elements in the HBox

				VBox vBox = new VBox(10);// Create a VBox to hold all elements of the GUI
				vBox.setAlignment(Pos.CENTER);// Set the alignment of elements within the VBox to center
				vBox.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 8px 15px;");// Apply custom styling to the button (global variable)
				vBox.getChildren().addAll(best, hbPath, pathArea, hbAlternative, othersArea);// Add all
																										// components
				vBox.setSpacing(50);// Set spacing between elements in the VBox
				vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))); // Set background to black
				vBox.setPrefHeight(200);// Set the preferred height for the VBox

				Scene scene2 = new Scene(vBox, 720, 720);
				primaryStage.setScene(scene2);
				primaryStage.show();
				primaryStage.setMaximized(true);
				//primaryStage.setFullScreen(false);
				primaryStage.setTitle("ProjectOneDynamicArray");

				TextArea taTable = new TextArea(); // Create a TextArea for displaying the dynamic table
				taTable.setEditable(false); // Make the TextArea non-editable
				taTable.setPrefHeight(1000); // Set the preferred height
				taTable.setPrefWidth(1000); // Set the width for the TextArea
				taTable.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20)); // Set the font for the TextArea
				taTable.setStyle("-fx-background-color: black; -fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 8px 15px;");// Apply custom styling to the TextArea (assuming Style1 is a defined style)

				Button showTable = new Button("Show The Dynamic Table");// Create a button for showing the dynamic table
				showTable.setFont(font);// Set the font for the button
				showTable.setTextFill(Color.BLACK);// Set the text color for the button
				showTable.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;"); // Apply custom styling to the button (assuming Style2 is a defined style)

				Button back = new Button("Back");// Create a button for going back
				back.setFont(font);// Set the font for the button
				back.setTextFill(Color.BLACK);// Set the text color for the button
				back.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-size: 17px; -fx-padding: 8px 15px;");// Apply custom styling to the button (global variable)

				HBox hbTable = new HBox();// Create an HBox to hold buttons for showing the dynamic table and going back
				hbTable.getChildren().addAll(showTable, back);// Add buttons to the HBox
				hbTable.setSpacing(100);// Set spacing between elements in the HBox
				hbTable.setAlignment(Pos.CENTER);// Set the alignment of elements within the HBox to center

				VBox vbTable = new VBox(taTable, hbTable);// Create a VBox to hold the dynamic table TextArea and
															// buttons
				vbTable.setSpacing(50);// Set spacing between elements in the VBox
				vbTable.setBackground(new Background(new BackgroundFill(Color.AZURE, CornerRadii.EMPTY, Insets.EMPTY)));// color
				vbTable.setStyle("-fx-background-color: black; -fx-text-fill: black; -fx-font-size: 15px; -fx-padding: 8px 15px;"); // Apply custom styling to the VBox (assuming Style8 is a defined style)

				Scene sceneTable = new Scene(vbTable, 720, 720);// Create a new scene for displaying the dynamic table
                  
				findMinCost.setOnAction(e1 -> { // When the "Find The Minimum Cost" button is clicked

					System.out.println("from City: " + fromCity);
					System.out.println("to City: " + toCity);

					// Check that city indices are valid
					if (fromCity < 0 || toCity < 0 || fromCity >= numOfCities || toCity >= numOfCities) {
						Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setTitle("Invalid Selection");
						alert.setHeaderText(null);
						alert.setContentText("Start or end city is invalid or not found in data.");
						alert.showAndWait();
						return;
					}

					// If no path exists between the cities
				    if (table[fromCity][toCity] == Integer.MAX_VALUE) {
				        bestCostField.setText("No Connection");
				        pathArea.setText("There is no connection between the selected cities.");
				        return;
				    }

					// Get best path using updated path-finding logic
					List<String> calculatedBestPath = findBestPath(table, next, city, fromCity, toCity);

					if (calculatedBestPath == null || calculatedBestPath.isEmpty()) {
						bestCostField.setText("No Connection");
						pathArea.setText("No path could be found.");
						return;
					}

					// Build the path string
					StringBuilder pathBuilder = new StringBuilder();
					for (String cityName : calculatedBestPath) {
						pathBuilder.append(cityName).append(" -> ");
					}
					pathBuilder.setLength(pathBuilder.length() - 3); // Remove last arrow

					// Display path and cost
					pathArea.setText("Best Path:\n" + pathBuilder.toString());
					bestCostField.setText(String.valueOf(table[fromCity][toCity]));
				});


				printOthers.setOnAction(e2 -> { // triggered when the "Show Alternative Paths" button is clicked

					
					List<List<String>> allPaths = findAllPaths(data, city[fromCity], city[toCity]);

					// check if any paths were found
					if (!allPaths.isEmpty()) {
						System.out.println("All Paths: "); // print header to console for debugging

						for (List<String> alternativePath : allPaths) {
							// display each alternative path in the TextArea
							othersArea.appendText(alternativePath.toString());
							othersArea.appendText("\n"); // move to next line for clarity

							System.out.println(alternativePath); // print to console for verification
						}
					} else {
						// if no paths were found show this to user
						othersArea.setText("No Alternative paths found.");
					}
				});

				clearOtherPaths.setOnAction(e3 -> {// Event handler for clearOtherPaths button
					othersArea.clear();// Clear the content of the othersArea TextArea
				});

				btTable.setOnAction(e4 -> {// go to the second scene
					primaryStage.setScene(sceneTable);
				});

				back.setOnAction(e5 -> {// get back to the first scene
					primaryStage.setScene(scene2);
				});

				showTable.setOnAction(e6 -> {
					// Display city names horizontally on the first line
					taTable.appendText("\t\t");
					for (int i = 0; i < numOfCities; i++) {
						taTable.appendText(city[i] + "\t\t");
					}
					taTable.appendText("\n\n");

					// Display table values
					for (int i = 0; i < numOfCities; i++) {
						taTable.appendText(city[i] + "\t\t");
						for (int j = 0; j < numOfCities; j++) {
							if (table[i][j] == Integer.MAX_VALUE) {
								taTable.appendText(" \t\t");
							} else {
								taTable.appendText(table[i][j] + "\t\t");
							}
						}
						taTable.appendText("\n\n");
					}
				});

				primaryStage.setScene(scene2);// second scene
			} catch (NumberFormatException e7) {
				// TODO Auto-generated catch block
				e7.printStackTrace();
			} catch (FileNotFoundException e8) {
				// TODO Auto-generated catch block
				e8.printStackTrace();
			}
		
			primaryStage.setMaximized(false);// window screen
		}
	   
	});
	
}
	
	public static List<String> findBestPath(int[][] table, String[][] next, String[] city, int from, int to) {
		List<String> path = new ArrayList<>();
		System.out.println("1");
		// Check if no path exists
		if (table[from][to] == Integer.MAX_VALUE) {
			System.out.println(from + "---" + to);

			for (int i = 0; i < table.length; i++) {

				for (int j = 0; j < table[i].length; j++) {

					System.out.print(table[i][j]);
				}
				System.out.println();
			}
			System.out.println(" No path: table[" + from + "][" + to + "] = ∞");
			return null;
		}
		System.out.println("2");
		// Start reconstructing path
		path.add(city[from]);
		while (from != to) {
			String nextCity = next[from][to];

			// If next is "x", this means  did not update this path
			if (nextCity.equals("x")) {
				System.out.println(" No path found in next[][] from " + city[from] + " to " + city[to]);
				return null;
			}
			System.out.println("3");
			// Add next city
			from = Arrays.asList(city).indexOf(nextCity);
			path.add(city[from]);
		}

		System.out.println(" Best path: " + path);
		return path;
	}

	public static void dfsMinCost(int[][] table, List<String[]> graph, String[] city, int current, int end,
			boolean[] visited, List<String> currentPath, List<String> bestPath, int currentCost, int[] minCost) {

		if (current == end) {
			if (currentCost < minCost[0]) {
				minCost[0] = currentCost;
				bestPath.clear();
				bestPath.addAll(new ArrayList<>(currentPath));
			}
			return;
		}

		// نبحث في جميع الجيران من قائمة arrayList
		for (String[] neighbors : graph) {
			if (!neighbors[0].equals(city[current]))
				continue;

			for (int i = 1; i < neighbors.length; i++) {
				String nextCity = neighbors[i];
				int nextIndex = Arrays.asList(city).indexOf(nextCity);

				if (!visited[nextIndex] && table[current][nextIndex] != Integer.MAX_VALUE) {
					visited[nextIndex] = true;
					currentPath.add(nextCity);

					dfsMinCost(table, graph, city, nextIndex, end, visited, currentPath, bestPath,
							currentCost + table[current][nextIndex], minCost);

					visited[nextIndex] = false;
					currentPath.remove(currentPath.size() - 1); // backtrack
				}
			}
		}
	}

	public static List<String> findBestPath(int[][] table, List<String[]> arrayList, String start, String end) {
		List<String> path = new ArrayList<>();
		path.add(start);
		Best(table, arrayList, start, end, path);
		return path;
	}

	public static boolean Best(int[][] table, List<String[]> arrayList, String current, String end, List<String> path) {
		if (current.equals(end)) {
			return true; // Base case: found the end city
		}

		for (String[] array : arrayList) {
			if (array[0].equals(current)) {

				for (int i = 1; i < array.length; i++) {
					String next = array[i];
					if (!path.contains(next)) { // Check if the neighboring city has not been visited yet
						path.add(next); // Mark the neighboring city as visited
						if (Best(table, arrayList, next, end, path)) {
							return true; // If a path is found from the neighboring city to the end city, return true
						}
						path.remove(path.size() - 1); // Backtrack: remove the last city from the path
					}
				}
			}
		}
		return false; // If no path is found from the current city to the end city
	}

	public static List<List<String>> findAllPaths(List<String[]> arrayList, String start, String end) {
		List<List<String>> allPaths = new ArrayList<>();
		List<String> path = new ArrayList<>();
		path.add(start);
		dfs(arrayList, start, end, path, allPaths);
		return allPaths.subList(0, Math.min(4, allPaths.size()));
	}

	public static void dfs(List<String[]> arrayList, String current, String end, List<String> path,
			List<List<String>> allPaths) {
		if (current.equals(end)) {
			allPaths.add(new ArrayList<>(path));
			return;
		}

		for (String[] array : arrayList) {
			if (array.length == 0) {
				System.err.println(" Skipped empty array in path data");
				continue; // Skip any empty line/data
			}

			if (array[0].equals(current)) {
				for (int i = 1; i < array.length; i++) {
					String next = array[i];
					path.add(next);
					dfs(arrayList, next, end, path, allPaths);
					path.remove(path.size() - 1); // Backtrack
				}
			}
		}
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}
}
//---------------------Relationship---------------------

//If ( i = j ) -> m[ I, j ] = 0
//If ( i < j ) -> m[ j, k ] = min { m[ j, i ] + m[ i, k ],m[j,k]}
//If ( i > j ) -> x 

//if (i == j) table[i][j] = 0;

//for (int i = 0; i < numOfCities; i++) {
//    for (int j = 0; j < numOfCities; j++) {
//        if (i != j) table[i][j] = Integer.MAX_VALUE;
//    }
//}

//for (int k = 0; k < numOfCities; k++) {
//    for (int i = 0; i < numOfCities; i++) {
//        for (int j = 0; j < numOfCities; j++) {
//            if (table[i][k] != Integer.MAX_VALUE && table[k][j] != Integer.MAX_VALUE) {
//                table[i][j] = Math.min(table[i][j], table[i][k] + table[k][j]);
//            }
//        }
//    }
//}
