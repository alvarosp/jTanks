package tanks;

import java.applet.*;
//import javax.swing.JApplet;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;

public class TanksApp extends Applet implements MouseListener , Runnable, MouseMotionListener, KeyListener{
	static final long serialVersionUID = 0;
	static final String version = "v 0.01.20080309";
	String info, data;
	Graphics g;
	static Map mapa;
	int tNextWave;
	public static Tank[] tanques;
	static Disparo[] disparos;
	int[] spawnTime;
	Tank[] spawnTanks;
	int wave;
	//Threads
	Thread main, spawn;
	//Control del teclado
	boolean thMove, thGiro;
	int thShotX, thShotY;
	char thChMove, thChGiro;
	
	boolean pause;
	public void init(){
		pause = true;
		mapa = new Map();
		g = this.getGraphics();
		tanques = new Tank[1000];
		//tanques[0] = new Tank(240,240,Math.PI/3, 40, 0, 5, 0);//El jugador
		disparos = new Disparo[10000];
		thMove = false;
		thGiro = false;
		thShotX = -1;
		wave = 0;//0
		processfile();
		main = new Thread(this, "Main");
		spawn = new Thread(this, "Spawn");
		main.start();
		spawn.start();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void paint(Graphics g){
		if (pause){
			g.drawString("Click to start", 100, 100);
		}
		else{
			for(int i = 0; i < tanques.length; i++){
				if(null != tanques[i]){
					tanques[i].paint(g);
					g.drawString("V" + i + ": " + tanques[i].vida, 440 - 60*(i/20), 40+20*i);
				}
			}
			boolean leave;
			for(int i = 0 ; i < disparos.length; i++){
				if (null != disparos[i]){
					leave = disparos[i].paint(g);
					if (leave)
						disparos[i] = null;
				}
			}
			
			g.drawString("Wave: " + wave, 350, 480);
			if(tNextWave > 0)
				g.drawString("NextW: " + tNextWave/1000, 430, 480);
			else
				g.drawString("NextW: inf", 430, 480);
			//g.drawString("Fi: " + Math.toDegrees(angrad)tanques[0].angulo, 430, 480);
			if(tanques[0] == null){
				repaint();
				main = null;
				spawn = null;
				g.setFont(new Font(null, Font.BOLD, 40));
				g.drawString("¡ G A M E   O V E R !", 40, 250);
			}
			mapa.paint(g);
		}
	}
	public void run(){
		while(Thread.currentThread() == spawn){
			while(pause){
				sleep(50);
			}
			tNextWave = spawnTime[wave] * 1000;
			//En este if espero el tiempo necesario para después añadir el tanque. O bien porque tenia que
			//esperar a que murieran los tanques enemigos o bien porque habia que esperar X segundos
			if(tNextWave < 0){//Hay que esperar a que mueran todos los enemigos para sacar más.
				boolean fin = false;
				while(!fin){
					sleep(100);
					int a = 1;
					for(; a < tanques.length; a++){
						if(null != tanques[a])break;//Queda al menos un enemigo vivo
					}
					if (a == tanques.length)
						fin = true;
				}
				tNextWave = 3000;
				sleep(3000);
			}else{
				while(tNextWave > 0){
					sleep(1000);
					tNextWave -= 1000;
				}
			}
			//Toca sacar un nuevo tanque enemigo
			//if(spawnInfo[2][wave] > 0)tanques[0].vida += spawnInfo[2][wave];// El jugador gana vida ZXC
			//Busco una casilla vacia de la matriz tanques para añadir el nuevo enemigo
			int a = 1;
			for(; a < tanques.length && null != tanques[a]; a++){}
			Tank temp;
			temp = spawnTanks[wave];
			temp.setid(a);
			/*do 
				temp = new Tank()
				//temp = new Tank(420*Math.random() + 40,420*Math.random() + 40,0, spawnInfo[3][wave], spawnInfo[0][wave], 3, a);
			while(TanksApp.mapa.inside(temp));//Busco una posicion válida para el tanque*/
			tanques[a] = temp;
			wave++;
			if(-8 == spawnTime[wave]){//Este es el último tanque. En cuanto muera se acaba el juego
				TanksApp.mapa.destroy();
				tNextWave = -1;
				int i = 1;
				while(i != 20){
					sleep(500);
					i = 1;
					while(i < 20 && null == tanques[i])
						i++;
				}
				main = null;
				spawn = null;
				repaint();
				g.setFont(new Font(null, Font.BOLD, 40));
				g.drawString("¡ V I C T O R Y !", 100, 250);
			}
		}
		while(Thread.currentThread() == main){
			while(pause){
				sleep(50);
			}
			//Mueve jugador
			if (thGiro){
				if(thChGiro == 'a')	tanques[0].girar(-1);
				if(thChGiro == 'd')	tanques[0].girar(1);
			}
			if (thMove){
				if(thChMove == 's') tanques[0].mover(-1);
				if(thChMove == 'w')	tanques[0].mover(1);
			}
			if(thShotX != -1)tanques[0].shot(thShotX, thShotY);
			//Mueve enemigo
			for(int i = 1; i< tanques.length; i++){
				if(null != tanques[i])
					tanques[i].ia();
			}
			//Tanks.refresh();
			repaint();
			sleep(50);
		}
	}
	private void info(){
		if(null != info)
			g.drawString(info, 100, 390);
	}
	private void sleep(long ms){
		try{
			Thread.sleep(ms);
		}catch(Exception e){}
	}
	private void processfile(){
		//spawnI
		BufferedReader bufferedReader = null;
		try {
			//Construct the BufferedReader object
			bufferedReader = new BufferedReader(new FileReader("wave.txt"));
			String line = null;
			//El jugador
			String[] d = bufferedReader.readLine().split("\t");
			int[] jugdata = new int[Tank.numAtt];
			for(int i = 1; i < d.length; i++){
				jugdata[i - 1] = Integer.parseInt(d[i]);
			}
			tanques[0] = new Tank(jugdata);
			//El resto de tanques
			if (true) {
				spawnTime = Tank.randomTime(50);
				spawnTanks = Tank.randomTanks(50);
				return;
			}
			int numwave = 0;
			while ((line = bufferedReader.readLine()) != null && line.charAt(0) != '_') {
				if(line.charAt(0) != '#'){
					String[] s = line.split("\t");
					spawnTime[numwave] = Integer.parseInt(s[0]);
					int[] tankdata = new int[Tank.numAtt];
					for(int i = 1; i < s.length; i++){
						tankdata[i-1] = Integer.parseInt(s[i]);
					}
					spawnTanks[numwave++] = new Tank(tankdata);
				}
				//data += line;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			//Close the BufferedReader
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public static void ins(){
		
	}
	public void keyPressed (KeyEvent ke){
		//System.out.println("Pulsado: " + ke.getKeyChar());
		if(!thMove && (ke.getKeyChar() == 'w' || ke.getKeyChar() == 's')){
			thChMove = ke.getKeyChar();
			thMove = true;
		}
		else if(!thGiro && (ke.getKeyChar() == 'a' || ke.getKeyChar() == 'd')){
			thChGiro = ke.getKeyChar();
			thGiro = true;
		}
	}
	public void keyReleased (KeyEvent ke){
		//System.out.println("Liberado: " + ke.getKeyChar());
		if(ke.getKeyChar() == thChMove)
			thMove = false;
		if(ke.getKeyChar() == thChGiro)
			thGiro = false;
	}
	public void keyTyped (KeyEvent ke){
		if(ke.getKeyChar() == '1' && wave > 10)			tanques[0].changeShot(1);
		else if(ke.getKeyChar() == '2' && wave > 10)	tanques[0].changeShot(2);
		else if(ke.getKeyChar() == '3' && wave > 22)	tanques[0].changeShot(3);
		else if(ke.getKeyChar() == '4' && wave > 35)	tanques[0].changeShot(4);
		else if(ke.getKeyChar() == '5' && wave > 310)	tanques[0].changeShot(5);
		else if(ke.getKeyChar() == 'l' && true)		tanques[0].vida += 100;
		else if(ke.getKeyChar() == 'p'){
			pause = true;
			g.drawString("GAME PAUSED. Click to start",50,20);
		}
	}
	public void mousePressed (MouseEvent me) {
		if(tanques[0] == null)
			return;
		thShotX = me.getX();
		thShotY = me.getY();
	}
	public void mouseReleased (MouseEvent me) {
		thShotX = -1 ;
	}
	public void mouseMoved (MouseEvent me) {}
	public void mouseDragged (MouseEvent me) {
		if(thShotX != -1){
			thShotX = me.getX();
			thShotY = me.getY();
		}
	}
	public void mouseClicked (MouseEvent me) {
		if(pause){
			pause = false;
		}
	}
	public void mouseEntered (MouseEvent me) {}
	public void mouseExited (MouseEvent me) {}
}
