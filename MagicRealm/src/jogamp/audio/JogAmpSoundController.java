package jogamp.audio;

import java.util.ArrayList;
import java.util.List;

import model.enums.MapChitType;
import utils.random.Random;
import view.audio.SoundController;

public class JogAmpSoundController implements SoundController {

	private JogAmpAudio audio;
	
	private String mainTheme, lobbyTheme;
	
	private List<String> themes;
	
	private String playingTheme;
	
	private String alertSound, errorSound;
	
	public JogAmpSoundController() {
		audio = JogAmpAudio.getInstance();
		mainTheme = "VictoryMarch.wav";
		lobbyTheme = "spell_fury_final_mix.wav";
		themes = new ArrayList<String>();
		/*themes.add("BeforeTheBattle.wav");
		themes.add("CosmicFestival.wav");
		themes.add("FightScene.wav");
		themes.add("Highlander.wav");*/
		themes.add("Mists.wav");
		//themes.add("Nascimento.wav");
		Random.shuffle(themes);
		playingTheme = null;
		alertSound = "alert1.wav";
		errorSound = "alert2.wav";
	}
	
	@Override
	public void playMainTheme() {
		playTheme(mainTheme, new Runnable() {

			@Override
			public void run() {
				playMainTheme();
			}
			
		});
	}

	@Override
	public void playLobbyTheme() {
		playTheme(lobbyTheme, new Runnable() {

			@Override
			public void run() {
				playNext();
			}
			
		});
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
		String fl = getFile(sct);
		if(fl != null) {
			audio.playSound(fl);
		}
	}
	
	@Override
	public void cleanUp() {
		audio.stopSound(playingTheme);
		audio.stopSound(alertSound);
		audio.killALData();
	}

	private void playNext() {
		String fl = themes.remove(0);
		themes.add(fl);
		playTheme(fl, new Runnable() {

			@Override
			public void run() {
				playNext();
			}
			
		});
	}
	
	private void playTheme(String fl, Runnable onfinish) {
		if(playingTheme != null) {
			audio.stopSound(playingTheme);
		}
		audio.playSound(fl, onfinish);
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
	
	private static String getFile(MapChitType mct) {
		switch(mct) {
		case HOWL:
			return "coyote.wav";
		case FLUTTER:
			return "flutter.wav";
		case ROAR:
			return "roar.wav";
		case PATTER:
			return "patter.wav";
		case SLITHER:
			return "snake.wav";
		}
		return null;
	}

}
