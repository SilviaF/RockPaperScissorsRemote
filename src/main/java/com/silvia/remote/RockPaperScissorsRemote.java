package com.silvia.remote;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RockPaperScissorsRemote {
	
	//Variables definition
		public String[] options = {"rock", "paper", "scissors"};
		public String[] modesOfPlay = {"fair", "unfair", "remote"};
		int player1RandomSelection;
		int player2RandomSelection;
		int player2UnfairSelection = 0; //rock
		int gameIterations = 10;
		String logString;
		String filePath = "D:/RockPaperScissors_Output.txt";
		PrintWriter outputFile;
		boolean remote = false;
		//Counters
		int player1WinCounter = 0;
		int player2WinCounter = 0;
		int drawCounter = 0;

		//Method definition
		public void makeAFairMove(){
			player1RandomSelection = new Random().nextInt(options.length);
			player2RandomSelection = new Random().nextInt(options.length);
			printToConsoleAndFile("Player 1 selected " + options[player1RandomSelection]);
			printToConsoleAndFile("Player 2 selected " + options[player2RandomSelection]);
		}

		public void makeAnUnfairMove(){
			player1RandomSelection = new Random().nextInt(options.length);
			player2RandomSelection = player2UnfairSelection;
			printToConsoleAndFile("Player 1 selected " + options[player1RandomSelection]);
			printToConsoleAndFile("Player 2 selected " + options[player2RandomSelection]);
		}
				
		public void makeARemoteMove(){
			player1RandomSelection = new Random().nextInt(options.length);
			printToConsoleAndFile("Player 1 selected " + options[player1RandomSelection]);
			try {
				URL url = new URL("http://localhost:8080/remotePlayer");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				if (connection.getResponseCode() != 200) {
					throw new RuntimeException("HTTP error code : " + connection.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));
				String remoteOutput;
				while ((remoteOutput = br.readLine()) != null) {
					printToConsoleAndFile("Player 2 (remote) selected " + remoteOutput);
					player2RandomSelection = Arrays.asList(options).indexOf(remoteOutput);
				}
				connection.disconnect();
			  } catch (MalformedURLException e) {
				e.printStackTrace();
			  } catch (IOException e) {
				e.printStackTrace();
			  }
		}

		public void computeWinner(){
			System.out.println("Results:");
			int substractSelections = player1RandomSelection - player2RandomSelection;
			if (substractSelections==0){
				printToConsoleAndFile("It's a draw!");
				drawCounter++;
			} else if (substractSelections==-1 || substractSelections==2){
				if (remote){
					printToConsoleAndFile("Player 2 (remote) wins!");					
				} else {
					printToConsoleAndFile("Player 2 wins!");
				}
				player2WinCounter++;
			} else if (substractSelections==1 || substractSelections==-2){
				printToConsoleAndFile("Player 1 wins!");
				player1WinCounter++;
			}
		}

		public void iterateGame(String fairnessSelection){
			if (!Arrays.asList(options).contains(fairnessSelection.toLowerCase())){
				printToConsoleAndFile("Option does not exist, please try again.");
				return;
			}
			
			printToConsoleAndFile("Mode of play: " + fairnessSelection.toLowerCase());
			printToConsoleAndFile("---------------------------------------------------");
			printToConsoleAndFile("--------------------GAME START!--------------------");
			printToConsoleAndFile("---------------------------------------------------");
			
			for (int i=1; i<=gameIterations; i++){
				printToConsoleAndFile("Game " + i);
				if(fairnessSelection.equalsIgnoreCase("fair")){
					makeAFairMove();
				} else if (fairnessSelection.equalsIgnoreCase("unfair")){
					makeAnUnfairMove();
				} else if (fairnessSelection.equalsIgnoreCase("remote")){
					remote = true;
					makeARemoteMove();
				}
				computeWinner();			
				printToConsoleAndFile("-------------------------------");
			}
			printStatistics();
		}

		public void printStatistics(){
			printToConsoleAndFile("");
			printToConsoleAndFile("===========GAME STATS==========");
			printToConsoleAndFile("Player 1 won a total of " + player1WinCounter + " times.");
			if (remote){
				printToConsoleAndFile("Player 2 (remote) won a total of " + player2WinCounter + " times.");				
			} else {
				printToConsoleAndFile("Player 2 won a total of " + player2WinCounter + " times.");
			}
			printToConsoleAndFile("Out of all the games, " + drawCounter + " were a draw.");
			printToConsoleAndFile("");
		}

		public void initializeOutputFile(){
			try{
				outputFile = new PrintWriter(new FileWriter(filePath, true));
			} catch (Exception e){
				System.err.println(e.getMessage());
			}
		}
		
		public void printToConsoleAndFile(String logString){
			System.out.println(logString);
			outputFile.println(logString);
		}
	
	
	public static void main(String[] args) {
		//Run server
		SpringApplication.run(RockPaperScissorsRemote.class, args);
		System.out.println("Correctly Started!");
		
		System.out.println("Select mode of play: [fair]/[unfair]/[remote]");
		Scanner inputReader = new Scanner(System.in);
		String modeOfPlay = inputReader.nextLine();
		inputReader.close();
		
		RockPaperScissorsRemote myGame = new RockPaperScissorsRemote();
		myGame.initializeOutputFile();
		myGame.iterateGame(modeOfPlay);
		myGame.outputFile.close();
		
	}

}
