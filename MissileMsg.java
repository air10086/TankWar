import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileMsg implements Msg {
	TankClient tc;
	Missile m;
	int msgType = Msg.MISSIILE_MSG;
	
	public MissileMsg(Missile m) {
		this.m = m;
		
	}
	
	public MissileMsg(TankClient tc) {
		this.tc = tc;
		
	}

	@Override
	public void parse(DataInputStream ddos) {
		try {
			int tankId = ddos.readInt();
			if(tankId == tc.myTank.id) {
				return;
			}
			int x = ddos.readInt();
			int y = ddos.readInt();
			Dir dir = Dir.values()[ddos.readInt()];
			boolean good= ddos.readBoolean();
			
			Missile m = new Missile(tankId, x, y, good, dir,tc);
			tc.missiles.add(m);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void send(DatagramSocket ab, String IP, int udpPort) {
		ByteArrayOutputStream dos = new ByteArrayOutputStream();
		DataOutputStream dis = new DataOutputStream(dos);
		
		try {
			dis.writeInt(msgType);
			dis.writeInt(m.tankId);
			dis.writeInt(m.x);
			dis.writeInt(m.y);
			dis.writeInt(m.dir.ordinal());
			dis.writeBoolean(m.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = dos.toByteArray();
		try {
			DatagramPacket oo = new DatagramPacket(buf,buf.length, new InetSocketAddress(IP,udpPort));
			ab.send(oo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
