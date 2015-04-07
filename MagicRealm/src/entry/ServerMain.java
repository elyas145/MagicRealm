/**
 * Start point for the server process.
 */

package entry;
import config.NetworkConfiguration;
import server.Server;

public class ServerMain {
	
	public static void main(String[] args) {
		new Server(NetworkConfiguration.DEFAULT_PORT);
	}

}
