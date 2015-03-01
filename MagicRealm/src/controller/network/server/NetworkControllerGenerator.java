package controller.network.server;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import utils.handler.Handler;
import utils.structures.LinkedQueue;
import utils.structures.Queue;
import controller.ClientController;
import controller.ControllerGenerator;
import controller.network.server.handlers.ServerFull;
import model.controller.ModelControlInterface;
import network.server.Server;

public class NetworkControllerGenerator implements ControllerGenerator {

	public NetworkControllerGenerator() throws IOException {
		server = new Server<ClientController, ModelControlInterface>();
		wait = new Semaphore(0);
		queued = new LinkedQueue<Integer>();
		server.setConnectionHandler(new Handler<Integer>() {

			@Override
			public void handle(Integer id) {
				synchronized(queued) {
					queued.push(id);
				}
				wait.release();
			}
			
		});
		server.start();
	}
	
	public void start() {
		server.start();
	}

	@Override
	public void rejectNew() {
		server.setConnectionHandler(new Handler<Integer>() {

			@Override
			public void handle(Integer clid) {
				server.send(new ServerFull(), clid);
			}
			
		});
	}
	
	@Override
	public void setModelController(ModelControlInterface mci) {
		server.setData(mci);
	}

	@Override
	public NetworkClientController generateController() {
		while(queued.isEmpty()) {
			try {
				wait.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int id;
		synchronized(queued) {
			id = queued.pop();
		}
		return new NetworkClientController(server, id);
	}

	private Server<ClientController, ModelControlInterface> server;
	
	private Semaphore wait;
	
	private Queue<Integer> queued;

}
