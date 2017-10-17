import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg {
	int mType = Msg.TANK_DEAD_MSG;
	int id;
	TankClient tc;
	public TankDeadMsg(int id) {
		this.id = id;
	}
	public TankDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void parse(DataInputStream ddos) {
		try {
			int id = ddos.readInt();
			if(tc.myTank.id == id) {
				return;
			}
			
			for(int i=0;i<tc.tanks.size();i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					t.setLive(false);
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		
		}
		

	}

	@Override
	public void send(DatagramSocket ab, String IP, int udpPort) {
		ByteArrayOutputStream is = new ByteArrayOutputStream();
		DataOutputStream dis = new DataOutputStream(is);
		
		try {
			dis.writeInt(mType);
			dis.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = is.toByteArray();
		
		try {
			DatagramPacket oo = new DatagramPacket(buf,buf.length,new InetSocketAddress(IP,udpPort));
			ab.send(oo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
