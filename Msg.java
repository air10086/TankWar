import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
	public static final int TANK_MOVE_MSG = 1;
	public static final int TANK_MSG = 2;
	public static final int MISSIILE_MSG = 3;
	public static final int TANK_DEAD_MSG = 4;
	public static final int MISSILE_DEAD_MSG = 5;
	
	public void parse(DataInputStream ddos);
	public void send(DatagramSocket ab, String IP, int udpPort);
	
}
