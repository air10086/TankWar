import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	Tank myTank = new Tank(50, 50, true, Dir.STOP, this);
	
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();

	Image offScreenImage = null;
	NetClient nc = new NetClient(this);
	
	ConnectDialog dialog = new ConnectDialog();
	
	@Override
	public void paint(Graphics g) {
		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("explodes count:" + explodes.size(), 10, 70);
		g.drawString("tanks    count:" + tanks.size(), 10, 90);
		//绘制坦克死亡信息
		for(int i=0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
			if(m.hitTank(myTank)) {
				TankDeadMsg msg = new TankDeadMsg(myTank.id);
				nc.send(msg);
				MissileDeadMsg amsg = new MissileDeadMsg(m.id,m.tankId);
				nc.send(amsg);
			}
			m.draw(g);
		}
		
		for(int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
	
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.draw(g);
		}	
		myTank.draw(g);
	}

	@Override
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(800, 600);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	//运行游戏界面
	public void launchFrame() {
		this.setLocation(400, 300);		
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);//不可调整大小
		this.setBackground(Color.GREEN);
		
		this.addKeyListener(new KeyMonitor());
		
		this.setVisible(true);
		
		new Thread(new PaintThread()).start();//启动线程画出坦克和游戏界面
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}
	
	
	class PaintThread implements Runnable {

		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	class KeyMonitor extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_C) {
				dialog.setVisible(true);		
			} else{
				myTank.keyPressed(e);
			}
		}	
	}
	//连接端口 通过dialog进行联网
	class ConnectDialog extends Dialog {
		TextField tfIP = new TextField("192.168.1.104",12);
		TextField tfPort = new TextField("" + TankServer.TCP_PORT,4);
		TextField tfudpPort = new TextField("2225",4);
		Button b = new Button("确定");
		public ConnectDialog () {
			super(TankClient.this,true);
			this.setLayout(new FlowLayout());
			this.add(new Label("IP: "));
			this.add(tfIP);
			this.add(new Label("Port: "));
			this.add(tfPort);
			this.add(new Label("MY UDPPort: "));
			this.add(tfudpPort);
			this.add(b);
			this.setLocation(300,300);
			this.pack();
			this.addWindowListener(new WindowAdapter(){

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}	
			});
			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText();
					int port = Integer.parseInt(tfPort.getText().trim());
					int udpPort = Integer.parseInt(tfudpPort.getText().trim());
					nc.setUdpPort(udpPort);
					nc.connect(IP, port);
					setVisible(false);
				}});
		}
		
	}
	
}
