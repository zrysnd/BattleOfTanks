package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

import Connect.ServerMultiTCPConnection;
//import Connect.ServerTCPConnection;
import View.Box;
import client.Models.Item;
import client.Models.ItemContainer;
import client.Models.Tank;
import trying.BoxContainer;

public class ServerStarter{
	private static final int DISPLAY_WIDTH = 700;
	private static final int DISPLAY_HEIGTH = 500;

	private static final int MAP_WIDTH = 2000;
	private static final int MAP_HEIGTH = 2000;

	private static final int FRAMES_PER_SECOND = 30;
	
	private static final int WALL_SIZE = 20;
	private static final int TANK_SIZE = 15;
	
	private static final int SPEED = 4;
	
	private int numberOfClients_;
	private int portNumber_;
	private ItemContainer AllMovings_;
	
	public ServerStarter( int numberOfClients, int portNumber ) {
		this.numberOfClients_ = numberOfClients;
		this.portNumber_ = portNumber;
		this.AllMovings_ = new ItemContainer();
	}
	
	public void startServer() {
		
		try {
			ServerMultiTCPConnection  tcpSM = new ServerMultiTCPConnection(numberOfClients_,
					this.portNumber_);
			System.out.println("Server created, waiting for connections");
			//wait for all connects
			tcpSM.connect();
			System.out.println("All connected");
			
			//Generate tanks
			for ( int i = 0 ; i < numberOfClients_; i++) {
//				Item thisItem = new Item( (float)30.0*i, (float)30.0*i, (float)100.0, (float)100.0, 0, 0);
				Tank tank =	new Tank(3*WALL_SIZE,WALL_SIZE,TANK_SIZE,TANK_SIZE,true,SPEED);
				Item thisItem = tank.box;
				this.AllMovings_.addItem(thisItem);
			}
			
			for ( int i = 0; i < numberOfClients_; i++) {
				ClientListener ch =  new ClientListener(i,tcpSM.getInputStream(i),tcpSM.getOutputStream(i), 
						this.AllMovings_);
				Thread sThread = new Thread(ch);
				sThread.start();
			}
			
			
			try {
				while(true) {
					//simulate part.
					
					try {
						tcpSM.sendToAll(this.AllMovings_);
					}catch(Exception e) {
						return;
					}
					
					int rp = 0;
						
					TimeUnit.MILLISECONDS.sleep(30);
				}
			}catch( InterruptedException e) {
				return;
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		
        int nofClients = 2;
		ServerStarter ss = new ServerStarter(nofClients, 8189);
		ss.startServer();
		
	}
}

class ClientListener implements Runnable{
	private DataInputStream input_;
	private ObjectOutputStream output_;
	private int clientIndex_;
	private ItemContainer allMovings_;
	
	public ClientListener(int i, DataInputStream input, ObjectOutputStream output, ItemContainer allMovings) {
		this.clientIndex_ = i;
		this.input_ = input;
		this.output_ = output;
		this.allMovings_ = allMovings;
	}
	
	
	@Override
	public void run() {
		
		try {
			while(true) {
				int clientResponse = this.input_.readInt();
				System.out.println(String.format("%d Received %d", this.clientIndex_,clientResponse));
				if( clientResponse == 1 ) {
					synchronized(allMovings_) {
						allMovings_.items_.get(clientIndex_).y += 2.0;
					}
					
//					tcpS.send(Movings);
//					int re = tcpS.receive();
				}
			}
		}catch (IOException e) {
			return;
		}
		
	}
	
}
	

