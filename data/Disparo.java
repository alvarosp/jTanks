package tanks;

import java.awt.*;

class Disparo {
	double coorX, coorY, angulo, velocidad, rotateX, rotateY, rotateRa, objX, objY;
	int tipo, id, owner, rotateTankId, rotateTime, daño, tiempo, radio;
	
	boolean girado, rotate;
	/*
	 * tipo:
	 * 0:
	 * 1:1 de daño
	 * 2:2 de daño
	 * 3:9 de daño
	 * 4:1 de daño, explota y aparecen 20 disparos de tipo 1
	 * 5:10 de daño, se mueve lento pero va cogiendo velocidad
	 */
	public Disparo(double x, double y , double v, int tp, int id, int own, double obX, double obY, double angDes){
		coorX = x;
		coorY = y;
		objX = obX;
		objY = obY;
		tiempo = 0;
		angulo = Math.atan2(objY - coorY, objX - coorX) + angDes;
		velocidad = v;
		tipo = tp;
		radio = tipo;
		if(tipo == 3) {
			daño = 9;
			radio = 9;
		}
		else if (tipo == 4){
			daño = 1;
			velocidad = 3;
		}
		else if (tipo == 5){
			daño = 10;
			velocidad = 1;
			radio = 1;
		}
		else{
			daño = tipo;
		}
		this.id = id;
		owner = own;
		girado = false;
		rotate = false;
	}
	public boolean paint(Graphics g){
		if(tipo == 5){
			velocidad = Math.min(20,1 + 0.000001*Math.pow(tiempo,4));
			tiempo++;
		}
		if(!rotate){
			coorX += velocidad * Math.cos(angulo);
			coorY += velocidad * Math.sin(angulo);
		}else{
			rotateTime++;
			rotateX = TanksApp.tanques[rotateTankId].coorX;
			rotateY = TanksApp.tanques[rotateTankId].coorY;
			coorX = rotateRa*Math.cos(rotateTime*velocidad/rotateRa + angulo)+ rotateX;
			coorY = rotateRa*Math.sin(rotateTime*velocidad/rotateRa + angulo)+ rotateY;
		}
		//System.out.println((int)coorX + " , " + (int)coorY + "   " + id);
		for(int i = 0; i < TanksApp.tanques.length; i++){
			if(null != TanksApp.tanques[i] && owner != i ){//&&(!rotate || rotate && owner!= rotateTankId)
				if(TanksApp.tanques[i].isHit(coorX, coorY, id)){
					if(TanksApp.tanques[i].hit(daño))
						TanksApp.tanques[i] = null;
					return true;
				}
			}
		}
		if(tipo == 4 && Math.sqrt(Math.pow(coorX - objX,2) + Math.pow(coorY - objY,2)) < 5){
			for(double a = Math.random()*Math.PI/10; a < 2*Math.PI; a += Math.PI/10){
				int i = 0;
				for(; i < TanksApp.disparos.length && null != TanksApp.disparos[i]; i++){}
				if (i < TanksApp.disparos.length)
					TanksApp.disparos[i] = new Disparo(coorX,coorY, 6, 1, i, -1, coorX + 10*Math.cos(a),
							coorY + 10*Math.sin(a), 0);
			}
			return true;
		}
		if(!rotate && (coorX < 0 || coorY < 0 || coorX > 500 || coorY > 500 ))
			return true;
		if(TanksApp.mapa.hit((int)coorX, (int)coorY))
			return true;
		if (tipo == 5)	g.fillOval((int)coorX - 3*radio/2, (int)coorY - 3*radio/2, 3*radio, 3*radio);
		else 			g.drawOval((int)coorX - 3*radio/2, (int)coorY - 3*radio/2, 3*radio, 3*radio);
		return false;
	}
	public void desviar(double ang, int own){
		if(!girado){
			girado = true;
			angulo += ang;
		}
		owner = own;
	}
	public void rotate(double x, double y, int tankId){
		if(!rotate){
			owner = tankId;
			rotateTankId = tankId;
			rotate = true;
			rotateTime = 0;
			angulo += Math.PI;
			rotateRa = Math.sqrt(Math.pow(coorX - x,2) + Math.pow(coorY - y,2));
		}
		if(rotate && x == -1){
			rotate = false;
			angulo = Math.atan2(coorY - rotateY, coorX - rotateX);
		}else{
			rotateX = x;
			rotateY = y;
		}
	}
}
