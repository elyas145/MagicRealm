package jogamp.audio;

import model.enums.MapChitType;
import view.audio.SoundController;

public class JogAmpSoundController implements SoundController {

	private JogAmpAudio audio;
	
	private String mainTheme, lobbyTheme;
	
	private String playingTheme;
	
	private String alertSound, errorSound;
	
	public JogAmpSoundController() {
		audio = JogAmpAudio.getInstance();
		mainTheme = "VictoryMarch.wav";
		lobbyTheme = "spell_fury_final_mix.wav";
		playingTheme = null;
		alertSound = "alert1.wav";
		errorSound = "alert2.wav";
	}
	
	@Override
	public void playMainTheme() {
		playTheme(mainTheme);
	}

	@Override
	public void playLobbyTheme() {
		playTheme(lobbyTheme);
	}

	@Override
	public void pauseMusic() {
		if(playingTheme != null) {
			audio.pauseSound(playingTheme);
		}
	}

	@Override
	public void pauseGameSounds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revealSoundChit(MapChitType sct) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void cleanUp() {
		audio.stopSound(playingTheme);
		audio.stopSound(alertSound);
		audio.killALData();
	}
	
	private void playTheme(String fl) {
		if(playingTheme != null) {
			audio.stopSound(playingTheme);
		}
		audio.playSound(fl);
		playingTheme = fl;
	}

	@Override
	public void alert() {
		audio.playSound(alertSound);
	}

	@Override
	public void error() {
		audio.playSound(errorSound);
	}

}
