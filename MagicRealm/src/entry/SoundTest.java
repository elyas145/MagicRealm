package entry;

import view.audio.SoundController;
import jogamp.audio.JogAmpAudio;
import jogamp.audio.JogAmpSoundController;

public class SoundTest {

	public static void main(String[] args) throws InterruptedException {
		JogAmpAudio sounds = JogAmpAudio.getInstance();
		sounds.playSound("BeforeTheBattle.wav");
		Thread.sleep(2000);
		//sounds.playSound("Mists.wav");
		//Thread.sleep(5000);
		sounds.playSound("Highlander.wav");
		Thread.sleep(2000);
		sounds.playSound("spell_fury_final_mix.wav");
		Thread.sleep(30000);
		sounds.killALData();
	}

}
