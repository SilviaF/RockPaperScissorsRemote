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

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class RockPaperScissorsRemote {

	//Variables definition
	String[] options = {"rock", "paper", "scissors"};
	String[] modesOfPlay = {"fair", "unfair", "remote"};
	int player2UnfairSelection = 0; //rock
	int gameIterations = 10;
	String filePath = "D:/RockPaperScissors_Output.txt";
	String remoteURL = "http://localhost:8080/remotePlayer";
	PrintWriter outputFile;
	boolean remote;
	int fairPlayer1;
	int fairPlayer2;
	int unfairPlayer2;
	int fairRemotePlayer2;
	public int player1WinCounter;
	public int player2WinCounter;
	public int drawCounter;

	//Constructor
	public RockPaperScissorsRemote(){
		this.player1WinCounter = 0;
		this.player2WinCounter = 0;
		this.drawCounter = 0;
		this.remote = false;
		this.fairPlayer1 = 0;
		this.fairPlayer2 = 0;
		this.unfairPlayer2 = 0;
		this.fairRemotePlayer2 = 0;
	}

	//Method definition
	public int[] makeAFairMove(){
		fairPlayer1 = new Random().nextInt(options.length);
		fairPlayer2 = new Random().nextInt(options.length);
		printToConsoleAndFile("Player 1 selected " + options[fairPlayer1]);
		printToConsoleAndFile("Player 2 selected " + options[fairPlayer2]);
		int[] playerSelections = {fairPlayer1, fairPlayer2};
		return playerSelections;
	}

	public int[] makeAnUnfairMove(){
		fairPlayer1 = new Random().nextInt(options.length);
		unfairPlayer2 = player2UnfairSelection;
		printToConsoleAndFile("Player 1 selected " + options[fairPlayer1]);
		printToConsoleAndFile("Player 2 selected " + options[unfairPlayer2]);
		int[] playerSelections = {fairPlayer1, unfairPlayer2};
		return playerSelections;
	}

	public int[] makeARemoteMove(){
		fairPlayer1 = new Random().nextInt(options.length);
		printToConsoleAndFile("Player 1 selected " + options[fairPlayer1]);
		
		try {
			URL url = new URL(remoteURL);
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
				fairRemotePlayer2 = Arrays.asList(options).indexOf(remoteOutput);
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			printToConsoleAndFile("URL error encountered, please try again.");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			printToConsoleAndFile("Error encountered, please try again.");
			return null;
		}
		
		int[] playerSelections = {fairPlayer1, fairRemotePlayer2};
		return playerSelections;
	}

	public void computeWinner(int player1RandomSelection, int player2RandomSelection){
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
			System.err.println("Error creating output file, logs will only be printed in console.");
		}
	}

	public void printToConsoleAndFile(String logString){
		System.out.println(logString);
		if (outputFile!=null){
			outputFile.println(logString);
		}		
	}

	final public void iterateGame(int player1, int player2, String modeOfPlay){
		if (!Arrays.asList(modesOfPlay).contains(modeOfPlay.toLowerCase())){
			printToConsoleAndFile("Option does not exist, please try again.");
			return;
		}
		
		int[] playerSelections = {};

		printToConsoleAndFile("Mode of play: " + modeOfPlay.toLowerCase());
		printToConsoleAndFile("---------------------------------------------------");
		printToConsoleAndFile("--------------------GAME START!--------------------");
		printToConsoleAndFile("---------------------------------------------------");

		for (int i=1; i<=gameIterations; i++){
			printToConsoleAndFile("Game " + i);
			if(modeOfPlay.equalsIgnoreCase("fair")){
				playerSelections = makeAFairMove();
			} else if (modeOfPlay.equalsIgnoreCase("unfair")){
				playerSelections = makeAnUnfairMove();
			} else if (modeOfPlay.equalsIgnoreCase("remote")){
				remote = true;
				playerSelections = makeARemoteMove();
			}
			computeWinner(playerSelections[0], playerSelections[1]);			
			printToConsoleAndFile("-------------------------------");
		}
		printStatistics();
	}

	public static void main(String[] args) {
		//Run server
		ConfigurableApplicationContext appContext;
		  
		appContext = SpringApplication.run(RockPaperScissorsRemote.class, args);
		System.out.println("Correctly Started!");

		int player1=0, player2=0;
		System.out.println("Select mode of play: [fair]/[unfair]/[remote]");
		Scanner inputReader = new Scanner(System.in);
		String modeOfPlay = inputReader.nextLine();
		inputReader.close();

		RockPaperScissorsRemote myGame = new RockPaperScissorsRemote();
		myGame.initializeOutputFile();
		myGame.iterateGame(player1, player2, modeOfPlay);
		myGame.outputFile.close();
		
		//Close server
		System.exit(SpringApplication.exit(appContext));
	}

}
