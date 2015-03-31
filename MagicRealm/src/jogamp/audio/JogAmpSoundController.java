package jogamp.audio;

import model.enums.MapChitType;
import view.audio.SoundController;

public class JogAmpSoundController implements SoundController {

	private JogAmpAudio audio;
	
	private String mainTheme, lobbyTheme;
	
	private String playingTheme;
	
	public JogAmpSoundController() {
		audio = JogAmpAudio.getInstance();
		mainTheme = "spell_fury_final_mix.wav";
		lobbyTheme = "BeforeTheBattle.wav";
		playingTheme = null;
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
		audio.killALData();
	}
	
	private void playTheme(String fl) {
		if(playingTheme != null) {
			audio.stopSound(playingTheme);
		}
		audio.playSound(fl);
		playingTheme = fl;
	}

}
