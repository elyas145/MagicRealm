package jogamp.audio;

import com.jogamp.openal.*;
import com.jogamp.openal.util.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.resources.ResourceHandler;

public class JogAmpAudio {
	
	//private static final int MAX_SOUNDS = 10;

	public static JogAmpAudio getInstance() {
		if (instance == null) {
			instance = new JogAmpAudio();
		}
		return instance;
	}

	public void killALData() {
		int i = 0, n = buffers.size();
		buffer = new int[n];
		for (int id : buffers) {
			buffer[i++] = id;
			al.alSourceStop(id);
		}
		al.alDeleteBuffers(n, buffer, 0);
		i = 0;
		n = sounds.size();
		buffer = new int[n];
		for (int id : sounds.values()) {
			buffer[i++] = id;
		}
		al.alDeleteSources(n, buffer, 0);
		ALut.alutExit();
	}

	public void playSound(String file) {
		if (!sounds.containsKey(file)) {
			if (loadALData(file) != AL.AL_TRUE) {
				throw new RuntimeException("Could not load the sound " + file);
			}
		}
		int sourceID = sounds.get(file);
		setListenerValues();
		al.alSourcePlay(sourceID);
	}

	public void pauseSound(String file) {
		if(sounds.containsKey(file)) {
			al.alSourcePause(sounds.get(file));
		}
	}

	public void stopSound(String file) {
		if(sounds.containsKey(file)) {
			al.alSourceStop(sounds.get(file));
		}
	}

	private JogAmpAudio() {
		ALut.alutInit();
		sounds = new HashMap<String, Integer>();
		buffers = new ArrayList<Integer>();
		//dataBuffer = new int[MAX_SOUNDS];
		//sourceBuffer = new int[MAX_SOUNDS];
		//al.alGenBuffers(dataBuffer.length, dataBuffer, 0);
	}

	private int loadALData(String fname) {

		// variables to load into

		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];

		// Load wav data into a buffer.
		al.alGenBuffers(1, buffer, 0);
		if (al.alGetError() != AL.AL_NO_ERROR)
			return AL.AL_FALSE;

		int bid = buffer[0];
		buffers.add(bid);

		ALut.alutLoadWAVFile(
				ResourceHandler.joinPath("src", "resources", "sounds", fname), format,
				data, size, freq, loop);
		al.alBufferData(bid, format[0], data[0], size[0], freq[0]);

		// Bind buffer with a source.
		al.alGenSources(1, buffer, 0);

		if (al.alGetError() != AL.AL_NO_ERROR)
			return AL.AL_FALSE;

		int sid = buffer[0];
		sounds.put(fname, sid);

		al.alSourcei(sid, AL.AL_BUFFER, bid);
		al.alSourcef(sid, AL.AL_PITCH, 1.0f);
		al.alSourcef(sid, AL.AL_GAIN, 1.0f);
		al.alSourcefv(sid, AL.AL_POSITION, sourcePos, 0);
		al.alSourcefv(sid, AL.AL_VELOCITY, sourceVel, 0);
		al.alSourcei(sid, AL.AL_LOOPING, loop[0]);

		// Do another error check and return.
		if (al.alGetError() == AL.AL_NO_ERROR)
			return AL.AL_TRUE;

		return AL.AL_FALSE;
	}

	private void setListenerValues() {
		al.alListenerfv(AL.AL_POSITION, listenerPos, 0);
		al.alListenerfv(AL.AL_VELOCITY, listenerVel, 0);
		al.alListenerfv(AL.AL_ORIENTATION, listenerOri, 0);
	}

	private static JogAmpAudio instance = null;

	private AL al = ALFactory.getAL();

	// Buffers hold sound data.
	private int[] buffer = new int[1];

	// Position of the source sound.
	private float[] sourcePos = { 0.0f, 0.0f, 0.0f };

	// Velocity of the source sound.
	private float[] sourceVel = { 0.0f, 0.0f, 0.0f };

	// Position of the listener.
	private float[] listenerPos = { 0.0f, 0.0f, 0.0f };

	// Velocity of the listener.
	private float[] listenerVel = { 0.0f, 0.0f, 0.0f };

	// Orientation of the listener. (first 3 elements are "at", second 3 are
	// "up")
	private float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	// filename to source identifier
	private Map<String, Integer> sounds;
	private List<Integer> buffers;
	
	//private int[] dataBuffer;
	//private int[] sourceBuffer;
	//private int numSounds;

}