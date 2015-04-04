package entry;

import jogamp.audio.JogAmpAudio;

public class SoundTest {

	public static void main(String[] args) throws InterruptedException {
		JogAmpAudio sounds = JogAmpAudio.getInstance();
		sounds.playSound("VictoryMarch.wav");
		Thread.sleep(2000);
		for(int i = 0; i < 10; ++i) {
			sounds.playSound("alert1.wav");
			Thread.sleep(2000);
		}
		sounds.killALData();
	}

}
