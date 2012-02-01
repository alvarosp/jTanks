package tanks;

import java.awt.*;

public class Map {
	Rectangle[] mapa;
	public Map(){
		mapa = new Rectangle[10];
		
		mapa[0] = new Rectangle(150,300,50,80);
		mapa[1] = new Rectangle(100,100,20,150);
		mapa[2] = new Rectangle(350,400,40,40);
	}
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for(int i = 0; i < mapa.length; i++){
			if(null != mapa[i])
				g2.draw(mapa[i]);
		}
	}
	public boolean hit(int x, int y){
		boolean a = false;
		for(int i = 0; i < mapa.length; i++){
			if(null != mapa[i] && mapa[i].contains(x, y))
				a = true;
		}
		return a;
	}
	public boolean inside(Tank t){
		if(hit((int)t.coorX - t.ancho/2,(int)t.coorY - t.largo/2))
			return true;
		if(hit((int)t.coorX + t.ancho/2,(int)t.coorY - t.largo/2))
			return true;
		if(hit((int)t.coorX - t.ancho/2,(int)t.coorY + t.largo/2))
			return true;
		if(hit((int)t.coorX + t.ancho/2,(int)t.coorY + t.largo/2))
			return true;
		return false;
	}
	public void destroy(){
		mapa = new Rectangle[10];
	}
}
