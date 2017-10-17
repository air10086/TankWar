import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {
	int msgType = Msg.MISSILE_DEAD_MSG;
	TankClient tc;
	int id;
	int tankId;
	
	public MissileDeadMsg(int id, int tankId) {
		this.id = id;
		this.tankId = tankId;
		
	}
	public MissileDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void parse(DataInputStream ddos) {
		try {
			int id = ddos.readInt();
			int tankId = ddos.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0;i<tc.missiles.size();i++) {
			Missile m = tc.missiles.get(i);
			if(m.id == id && m.tankId == tankId) {
				m.live = false;
				tc.explodes.add(new Explode(m.x, m.y, tc));
				break;
			}	
		}
	}

	@Override
	public void send(DatagramSocket ab, String IP, int udpPort) {
		ByteArrayOutputStream is = new ByteArrayOutputStream();
		DataOutputStream dis = new DataOutputStream(is);
		try {
			dis.writeInt(msgType);
			dis.writeInt(id);
			dis.writeInt(tankId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = is.toByteArray();
		try {
			
			DatagramPacket oo = new DatagramPacket(buf, buf.length,new InetSocketAddress(IP,udpPort));
			ab.send(oo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
