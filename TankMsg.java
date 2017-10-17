import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMsg implements Msg {
	int msgType = TANK_MSG;
	Tank tank;
	TankClient tc;
	public TankMsg(Tank tank) {
		this.tank = tank;
	}
	public TankMsg(TankClient tc) {
		this.tc = tc;
		
	}
	public void send(DatagramSocket ab, String IP, int udpPort) {
		ByteArrayOutputStream dos = new ByteArrayOutputStream();
		DataOutputStream dis = new DataOutputStream(dos);
		
		try {
			dis.writeInt(msgType);
			dis.writeInt(tank.id);
			dis.writeInt(tank.x);
			dis.writeInt(tank.y);
			dis.writeInt(tank.dir.ordinal());
			dis.writeBoolean(tank.good);
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
	
	public void parse(DataInputStream ddos) {	
		try {
			int id = ddos.readInt();
			if(tc.myTank.id == id) {
				return;
			}
			int x = ddos.readInt();
			int y = ddos.readInt();
			Dir dir = Dir.values()[ddos.readInt()];
			boolean good = ddos.readBoolean();
//System.out.println("id: " + id + "-x: " + x + "-y: " + y + "-Dir: " + dir + "-good: " + good);
	//将Server传过来的坦克信息打包成坦克显示在客户端
			boolean exist = false;
			for(int i=0;i<tc.tanks.size();i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					exist = true;
					break;
				}
			}
			
			if(!exist) {
				TankMsg ntmsg = new TankMsg(tc.myTank);
				tc.nc.send(ntmsg);
				
				Tank t = new Tank(x,y,good,dir,tc);
				t.id = id;
				tc.tanks.add(t);
			}
			
		 } catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
