package com.silvia.remote;

import java.util.Random;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemotePlayerController {
	
	public String[] options = {"rock", "paper", "scissors"};
	int remoteRandomSelection;

	@RequestMapping("/remotePlayer")
	public String randomSelection(){
		remoteRandomSelection = new Random().nextInt(options.length);
		return (options[remoteRandomSelection]);
	}
	
}
