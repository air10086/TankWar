import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	public static int ID = 100;
	public static final int TCP_PORT = 9999;
	public static final int UDP_PORT = 7777;
	List<Client> clients = new ArrayList<Client>();
	
	public void start() {
		
		new Thread(new UDPThread()).start();
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			Socket s = null;
			try{
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(IP,udpPort);
				DataOutputStream is = new DataOutputStream(s.getOutputStream());
				is.writeInt(ID++);
				clients.add(c);
				System.out.println("a Client is connected: " + s.getInetAddress() + " /port:" + s.getPort());
			} catch(IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					if(s != null){
						s.close();
						s = null;
					}	
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}

	public static void main(String[] args) {
		new TankServer().start();

	}
	//client端的信息
	private class Client {
		String IP;
		int udpPort;
		public Client(String IP,int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
		
	}
	//转发从客户端发送过来的信息到别的客户端
	public class UDPThread implements Runnable{
		byte[] buf = new byte[1024];
		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
System.out.println("UDPThread start at port:" + UDP_PORT);
			while(ds != null) {
				DatagramPacket oo = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(oo);
					for(int i=0;i<clients.size();i++) {
						Client c = clients.get(i);
						oo.setSocketAddress(new InetSocketAddress(c.IP,c.udpPort));
						ds.send(oo);	
					}
System.out.println("a packet is revieved!");					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}		
	}
}
