import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetClient {
	TankClient tc;
	private int udpPort;
	String IP;
	
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	DatagramSocket ab = null;
	
	
	public NetClient(TankClient tc) {
		this.tc = tc;
	}
	
	
	//连接到服务器端，保存坦克的id和客户端的udpport，区分坦克的好坏
	//把连接后接受到的坦克的信息转发到其他其他客户端
	public void connect(String IP, int port) {
		this.IP = IP;
		try {
			ab = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		Socket s = null;
	try {
		s = new Socket(IP,port);
		DataOutputStream dos = new DataOutputStream (s.getOutputStream());
		dos.writeInt(udpPort);
		DataInputStream im = new DataInputStream(s.getInputStream());
		int id = im.readInt();
		tc.myTank.id = id;
		if(id%2 == 0) {
			tc.myTank.good = false;
		} else {
			tc.myTank.good = true;
		}
		System.out.println("Connected to Server and Server give me a ID :" + id);
	} catch (UnknownHostException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if(s!=null){
			try {
				s.close();
				s = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	TankMsg msg = new TankMsg(tc.myTank);
	send(msg);
	new Thread(new UDPRecvThread()).start();
 }
	
	public void send(Msg msg) {
		msg.send(ab,IP,TankServer.UDP_PORT);	
	}
	//客户端接受坦克的信息，并解析
	private class UDPRecvThread implements Runnable {
		byte[] buf = new byte[1024];
		
		@Override
		public void run() {
			while(ab != null) {
				DatagramPacket oo = new DatagramPacket(buf,buf.length);				
				try {
					ab.receive(oo);
					parse(oo);
System.out.println("a packet received from Server");		
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		//解析接受到的是哪种消息并做相应的操作
		private void parse(DatagramPacket as) {
			ByteArrayInputStream dios = new ByteArrayInputStream(buf,0,as.getLength());
			DataInputStream ddos = new DataInputStream(dios);
			int msgType = 0;
			Msg msg = null;
			try {
				msgType = ddos.readInt();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			switch(msgType) {
			case Msg.TANK_MSG:
				msg = new TankMsg(NetClient.this.tc);
				msg.parse(ddos);
				break;
			case Msg.TANK_MOVE_MSG:
				msg = new TankMoveMsg(NetClient.this.tc);
				msg.parse(ddos);
				break;
			case Msg.MISSIILE_MSG:
				msg = new MissileMsg(NetClient.this.tc);
				msg.parse(ddos);
				break;
			case Msg.TANK_DEAD_MSG:
				msg = new TankDeadMsg(NetClient.this.tc);
				msg.parse(ddos);
				break;
			case Msg.MISSILE_DEAD_MSG:
				msg = new MissileDeadMsg(NetClient.this.tc);
				msg.parse(ddos);
				break;
				
			
			}
			
		}

	}

}
	