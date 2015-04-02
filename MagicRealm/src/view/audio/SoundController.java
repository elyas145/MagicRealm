package view.audio;

import model.enums.MapChitType;

public interface SoundController {
	
	void playMainTheme();
	
	void playLobbyTheme();
	
	void pauseMusic();
	
	void pauseGameSounds();
	
	void alert();
	
	void revealSoundChit(MapChitType sct);
	
	void cleanUp();

}
