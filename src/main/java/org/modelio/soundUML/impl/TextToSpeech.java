package org.modelio.soundUML.impl;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TextToSpeech {

	public static void speak(String text) {

		Voice voice;// Creating object of Voice class
		
		//Buscar a voice, se nao isto vai dar nullPointer 
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"); 
		
		voice = VoiceManager.getInstance().getVoice("kevin");// Getting voice
		if (voice != null) {
			voice.allocate();// Allocating Voice
		}
		try {
			voice.setRate(190);// Setting the rate of the voice
			voice.setPitch(150);// Setting the Pitch of the voice
			voice.setVolume(3);// Setting the volume of the voice
			voice.speak(text);// Calling speak() method

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}