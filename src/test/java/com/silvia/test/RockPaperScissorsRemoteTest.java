package com.silvia.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.boot.SpringApplication;

import com.silvia.remote.RockPaperScissorsRemote;

public class RockPaperScissorsRemoteTest {

	RockPaperScissorsRemote testRockPaperScissors;
	String[] options = {"rock", "paper", "scissors"};
	

	@Test
	public void makeAFairMoveTest(){
		int[] retrievedValues = {};
		testRockPaperScissors = new RockPaperScissorsRemote();
		retrievedValues = testRockPaperScissors.makeAFairMove();
		for (int i=0; i<retrievedValues.length; i++){
			assertEquals(true,(0<=retrievedValues[i] && retrievedValues[i]<=2));
		}
	}
	
	@Test
	public void makeAnUnfairMoveTest(){
		int[] retrievedValues = {};
		testRockPaperScissors = new RockPaperScissorsRemote();
		retrievedValues = testRockPaperScissors.makeAnUnfairMove();
		assertEquals(true, retrievedValues[1]==0);
	}
	
	@Test
	public void makeAnUnfairMoveTest2(){
		int[] retrievedValues = {};
		testRockPaperScissors = new RockPaperScissorsRemote();
		retrievedValues = testRockPaperScissors.makeAnUnfairMove();
		assertEquals(true, (0<=retrievedValues[0] && retrievedValues[0]<=2));
	}
	
	@Test
	public void makeARemoteMoveTest(){
		int[] retrievedValues = {};
		String[] args = {};
		SpringApplication.run(RockPaperScissorsRemote.class, args);
		System.out.println("Correctly Started!");
		testRockPaperScissors = new RockPaperScissorsRemote();
		retrievedValues = testRockPaperScissors.makeARemoteMove();
		assertEquals(false,retrievedValues==null);
		for (int i=0; i<retrievedValues.length; i++){
			assertEquals(true,(0<=retrievedValues[i] && retrievedValues[i]<=2));
		}
	}
	
	@Test 
	public void computeWinnerTest(){
		int player1;
		int player2;
		for (int i=0; i<options.length; i++){
			player1 = i;
			for (int j=0; j<options.length; j++){
				player2 = j;
				int substraction = player1 - player2;
				testRockPaperScissors = new RockPaperScissorsRemote();
				testRockPaperScissors.computeWinner(player1, player2);
				if ((substraction)==0){
					assertEquals(1, testRockPaperScissors.drawCounter);
				} else if (substraction==-1 || substraction==2){
					assertEquals(1, testRockPaperScissors.player2WinCounter);
				} else if (substraction==1 || substraction==-2){
					assertEquals(1, testRockPaperScissors.player1WinCounter);
				}
			}
		}
	}
	
}
