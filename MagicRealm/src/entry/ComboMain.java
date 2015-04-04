package entry;

import client.ControllerMain;
import server.Server;
import config.NetworkConfiguration;

public class ComboMain {

	public static void main(String[] args) {
		new Server(NetworkConfiguration.DEFAULT_PORT, false);
		new Thread() {
			public void run() {
				new ControllerMain();
			}
		}.start();
	}

}
