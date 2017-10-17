import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg{
	int msgType = TANK_MOVE_MSG;
	TankClient tc;
	int x, y;
	int id;
	Dir dir;
	Dir ptDir;
	public TankMoveMsg(int id, int x, int y, Dir dir, Dir ptDir) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.ptDir = ptDir;
		
	}
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}
	@Override
	public void send(DatagramSocket ad, String IP, int udpPort) {
		ByteArrayOutputStream dos = new ByteArrayOutputStream();
		DataOutputStream dis = new DataOutputStream(dos);
		
		try {
			dis.writeInt(msgType);
			dis.writeInt(id);
			dis.writeInt(x);
			dis.writeInt(y);
			dis.writeInt(dir.ordinal());
			dis.writeInt(ptDir.ordinal());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = dos.toByteArray();
		try {
			DatagramPacket oo = new DatagramPacket(buf,buf.length, new InetSocketAddress(IP,udpPort));
			ad.send(oo);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	@Override
	public void parse(DataInputStream ddos) {
		try {
			int id = ddos.readInt();
			if(tc.myTank.id == id) {
				return;
			}
			int x = ddos.readInt();
			int y = ddos.readInt();
			Dir dir = Dir.values()[ddos.readInt()];
			Dir ptDir = Dir.values()[ddos.readInt()];
			boolean exist = false;
			for(int i=0;i<=tc.tanks.size();i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					t.dir = dir;
					t.ptDir = ptDir;
					exist = true;
					break;
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
