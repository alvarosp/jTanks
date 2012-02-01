package tanks;

import java.awt.*;
import java.awt.geom.*;


public class Tank {
	double coorX, coorY, angulo, giro, mov, antmov, diag;
	int  ancho, largo, circulo;
	int velocidad, velMax, vida, vidamax;
	int iaLevel, tipoShot, id, shieldred;
	int shape, moveIA, shotIA;
	double[] iaControl;
	boolean[] atributos = new boolean[10];
	boolean shield, vuln, shieldabs;
	Disparo[] disp;// para ia 8
	double shieldAbs;//% de disparos que devuelve el shield
	public static final int numAtt = 20;//Numero de atributos que tiene el tanque
	static final int[] mn ={1,1,-1,-1,1};
	static final double PI = Math.PI;
	/*
	 * int[] data
	 * 0:vida máxima
	 * 1:coorX del centro del tanque
	 * 2:coorY
	 * 3:ang
	 * 4:velmax
	 * 5:id
	 * 6:shieldred     %de disparos que rebotan/se absorben. Si es 0 es que el tanque no tiene escudo
	 * 7:shieldabs          1:el escudo absorbe el disparo. 0:el escudo hace rebotar el disparo
	 * 8:ancho
	 * 9:largo
	 * 10:regen  puntos de vida que gana cada regentimer rondas. Si es 0 el tanque no tiene regeneracion
	 * 11:regentimer 
	 * 12:shape
	 * 13:movimientoIA
	 * 14:disparosIA
	 * 15:tipoShot
	 * 16:atributos
	 * 		0:invisible
	 * 		1:
	 * 17:
	 * 18:
	 * 19:
	 * 
	 */
	public Tank(int[] data){
		iaControl = new double[20];
		shieldabs = true;
		vidamax = data[0];
		coorX = data[1];
		coorY = data[2];
		angulo = data[3];
		velMax = data[4];
		id = data[5];
		shieldred= data[6];
		if(data[7] == 0) shieldabs = false;
		ancho = data[8];
		largo = data[9];
		shape = data[12];
		moveIA = data[13];
		shotIA = data[14];
		tipoShot= data[15];
		atributos[0] = data[16] == 1;
		//= data[];
		diag = Math.sqrt(Math.pow(ancho/2, 2) + Math.pow(largo/2, 2));
		vuln = true;
		velocidad = 0;
		giro = 0;
		mov = 0;
		vida = vidamax;
		circulo = 6;//tamaño del circulo central del tanque
	}
	/*public Tank(double x, double y, double ang, int v, int il, int vm, int id){
		coorX = x;	coorY = y;	angulo = ang;	vida = v;
		if(il == 6)
			vida = 300;
		if (il == 5)
			vida = 60;
		iaLevel = il;
		velMax = vm;
		if(il == 8)
			velMax = 1;
		this.id = id;
		vuln = true;
		if (iaLevel == 3){
			shield = true;
			shieldAbs = 0.3;
		}else if(iaLevel == 5){
			shield = true;
			shieldAbs = 1;
		}else
			shield = false;
		velocidad = 0;
		tipoShot = 1;
		iaControl = new double[10];
		if(iaLevel < 6){
			ancho = 40;
			largo = 30;
		}else{
			if(iaLevel == 6){
				ancho = 100;
				largo = 70;
			}
			if(iaLevel == 7){
				ancho = 10;
				largo = 8;
			}
			if(iaLevel == 8){
				disp = new Disparo[1000];
				ancho = 40;
				largo = 40;
				vuln = false;
				vida = 400;
				tipoShot = 4;
			}
			if(iaLevel == 9){
				ancho = 40;
				largo = 30;
				iaControl[1] = 10;
			}
			if(iaLevel == 10){
				ancho = 40;//radio
			}
		}
		diag = Math.sqrt(Math.pow(ancho/2, 2) + Math.pow(largo/2, 2));
		circulo = 6;
		giro = 0;
		mov = 0;
	}*/
	public void setid(int i) {
		id = i;
	}
	private void actualizar(){
		if (giro == -1)
			angulo -= Math.PI/16;
		if (giro == 1)
			angulo += Math.PI/16;
		if(coorX > 490 && mov*Math.cos(angulo) > 0 || coorX < 10 && mov*Math.cos(angulo) < 0 ||
				coorY > 490 && mov*Math.sin	(angulo) > 0 ||coorY < 10 && mov*Math.sin(angulo) < 0){
			velocidad = 0;
			mov = 0;
		}
		if(mov == 0){
			if (velocidad != 0){
				if(velocidad < 0)
					velocidad++;
				if(velocidad > 0)
					velocidad--;
				coorY += velocidad*Math.sin(angulo);
				coorX += velocidad*Math.cos(angulo);
			}
		}else {
			if(Math.abs(velocidad) < velMax || Math.abs(velocidad)/velocidad != Math.abs(mov)/mov)
				velocidad += mov;
			coorY += velocidad*Math.sin(angulo);
			coorX += velocidad*Math.cos(angulo);
		}
		mov = 0;
		giro = 0;
	}
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		actualizar();//ZXC
		if(!atributos[0]){
			AffineTransform original = g2.getTransform();
			AffineTransform rotate = new AffineTransform();
			rotate.rotate(angulo,coorX, coorY);
			g2.setTransform(rotate);
			if(shape == 0){//Rectangulo
				g2.draw(new Rectangle((int)coorX - ancho/2, (int)coorY - largo/2, ancho, largo));
				g2.drawOval((int)coorX + ancho/4,(int)coorY , 2, 2);//pequeño circulo que señala la parte de alante del tanque
				g.drawOval((int)coorX - circulo/2, (int)coorY - circulo/2, circulo, circulo);//circulo central
			}else if(shape == 1){//elipse
				g.drawOval((int)coorX - ancho/2, (int)coorY - largo/2, ancho, largo);
			}else if(shape == 2){//estrella
				g.drawOval((int)coorX - ancho/2, (int)coorY - ancho/2, ancho/3, ancho/3);
				for(int i = 0; i < 5 ; i++)
					g.drawLine((int)(coorX - Math.cos(2*PI*i/9)*ancho/3),(int)(coorY - Math.sin(PI*(2*i -1)/9)*ancho/3),
							(int)(coorX - Math.cos(2*PI*i/9)*ancho),(int)(coorY - Math.sin(PI*(2*i -1)/9)*ancho));
			}
			g2.setTransform(original);
			if (shieldred > 0){//Escudo
				g.setColor(Color.blue);
				g.drawOval((int)coorX - (int)(diag), (int)coorY - (int)(diag), 2*(int)diag, 2*(int)diag);//(int)(diag + 60)/2
				g.setColor(Color.black);
			}
		}
	}
	public void girar(int i){
		giro = i;
	}
	public void mover(int i){
		mov = i;
	}
		
	/*
	 * Parado
	 * Izd-Dcha
	 * Arriba-Abajo
	 * circulos
	 * utilizando el mapa
	 */
	private void movimientoIA(){
		//moveIA == 0: El tanque no se mueve
		if(moveIA ==1){//Izda-Dcha
			if(angulo != 0) giro = 1;
			else if(coorX < 50 || velocidad > 0 && coorX <450) mov = 1;
			else if(coorX > 450 || velocidad < 0 && coorX > 50)	mov = -1;
			else if(velocidad == 0)	mov = 1;
			
		}else if(moveIA ==2){//Arriba-Abajo
			if(angulo != PI/2) giro = 1;
			else if(coorY < 50 || velocidad > 0 && coorY <450)	mov = 1;
			else if(coorY > 450 || velocidad < 0 && coorY > 50)	mov = -1;
			else if(velocidad == 0)	mov = 1;
		}else if(moveIA ==3){//En circulos pequeños
			mov = 1 ;
			giro = 1;
		}else if(moveIA ==4){//En horizontal o vertical, va cambiando ZXC
			if (iaControl[10] == 100) iaControl[10] = -100;
			if (iaControl[10] < 0) {
				//System.out.println(angulo - (int)(angulo/(2*PI))*2*PI);
				if(angulo - (int)(angulo/(2*PI)) != 0) giro = 1;
				else if(coorX < 50 || velocidad > 0 && coorX <450) mov = 1;
				else if(coorX > 450 || velocidad < 0 && coorX > 50)	mov = -1;
				else if(velocidad == 0)	mov = 1;
			}else{
				if(angulo != PI/2) giro = 1;
				else if(coorY < 50 || velocidad > 0 && coorY <450)	mov = 1;
				else if(coorY > 450 || velocidad < 0 && coorY > 50)	mov = -1;
				else if(velocidad == 0)	mov = 1;
			}
			iaControl[10]++;
		}
	}
	
	/*
	 * 1 disparo cada n
	 * rafagas de r disparos cada n
	 */
	private void disparosIA(){
		//shotIA == 0: El tanque no dispara, pej el carrier
		if(shotIA == 1){//10% de disparar cada ronda
			if(Math.random() < 0.1) shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[0] = Math.random() - 0.5;
		}else if(shotIA ==2){//1 disparo cada 10 rondas
			if(iaControl[3] >= 10){
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
				iaControl[3] = 5*Math.random() - 5;
			}
			iaControl[3]++;
		}else if(shotIA ==3){//rafagas
			if(iaControl[1] == -8)
				iaControl[1] = 15;
			if(iaControl[1] < 0)
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[1]--;
			iaControl[0] = Math.random() - 0.5;
		}else if(shotIA ==4){
			shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
		}else if(shotIA ==5){//Dispara todo el rato en una direccion random
			shot((int)(420*Math.random() + 40), (int)(420*Math.random() + 40));
		}else if(shotIA ==6){
			
		}
	}
	public void ia(){
		/*
		 * iaControl[i]:
		 * 		0: variacion del angulo
		 * 		1: tiempo restante para disparar rafaga
		 * 		2: tiempo que le queda al Carrier(6) para sacar un Interceptor
		 * 		3:
		 */
		movimientoIA();
		disparosIA();
		if (true) return;
		/*if(iaLevel == 1){//no se mueve, y solo dispara de vez en cuando
			//SHOT
			if(Math.random() < 0.1)shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[0] = Math.random() - 0.5;
		}
		if(iaLevel == 2){//se mueve de lado a lado y dispara de vez en cuando
			if(coorX < 50 || velocidad > 0 && coorX <450){
				mov = 1;
			}else if(coorX > 450 || velocidad < 0 && coorX > 50){
				mov = -1;
			}else if(velocidad == 0){
				mov = 1;
			}
			//SHOT
			if(Math.random() < 0.1) shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[0] = Math.random() - 0.5;
		}
		if(iaLevel == 3){//no se mueve, dispara de vez en cuando y tiene un escudo del 20%
			//SHOT
			if(Math.random() < 0.1)shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[0] = Math.random() - 0.5;
		}
		if(iaLevel == 4){//no se mueve pero dispara rafagas de 8
			//SHOT
			if(iaControl[1] == -8)
				iaControl[1] = 15;
			if(iaControl[1] < 0)
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
			iaControl[1]--;
			iaControl[0] = Math.random() - 0.5;
		}
		if(iaLevel == 5){//Mini boss.Tiene un escudo del 100% que desactiva solo cuando dispara rafagas.No se mueve
			if(iaControl[1] == -8){
				iaControl[1] = 25;
				shield = true;
			}
			if(iaControl[1] < 0){
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
				shield = false;
			}
			iaControl[1]--;
			iaControl[0] = Math.random() - 0.5;
		}
		if(iaLevel == 6){//Carrier. No se mueve
			if(iaControl[2] == 30){
				int a = 1;
				for(; a < TanksApp.tanques.length && null != TanksApp.tanques[a]; a++){}
				if(a <= 9)
					TanksApp.tanques[a] = new Tank(coorX + 20,coorY + 20,Math.atan2(200 - coorY,200 - coorX), 5, 7, 10, a);
				iaControl[2] = -(int)Math.pow(a, 2)/3;
			}
			iaControl[2]++;
		}
		if(iaLevel == 7){//Interceptor
			if(coorX + 15*velocidad*Math.cos(angulo) >= 500 || coorX + 15*velocidad*Math.cos(angulo) <= 0 ||
					coorY + 15*velocidad*Math.sin(angulo) >= 500 || coorY + 15*velocidad*Math.sin(angulo) <= 0 ){
				giro = 1;
			}else{
				if(null != TanksApp.tanques[1] && TanksApp.tanques[1].vida <= 100){
					double tmp = Math.atan2(TanksApp.tanques[0].coorY - coorY,
							TanksApp.tanques[0].coorX - coorX);
					double angtmp = angulo - (int)(angulo/(2*Math.PI))*2*Math.PI;
					
					if (tmp < -Math.PI)	tmp += 2*Math.PI;
					if (tmp > Math.PI)	tmp -= 2*Math.PI;
					if (angtmp < -Math.PI)	angtmp += 2*Math.PI;
					if (angtmp > Math.PI)	angtmp -= 2*Math.PI;
					
					if(tmp > 0){
						if (angtmp > tmp || angtmp < tmp - Math.PI) giro = -1;
						else giro = 1;
					}
					if(tmp <= 0){
						if (angtmp < tmp || angtmp > tmp + Math.PI)	giro = 1;
						else giro = -1;
					}
					//System.out.println(Math.toDegrees(tmp) + "  " + Math.toDegrees(angtmp) + "  " + giro);
				}
				else if(Math.random() < 0.1) giro = 1;
			}
			mov = 1;
			//SHOT
			if(iaControl[3] >= 10){
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
				iaControl[3] = 5*Math.random() - 5;
			}
			iaControl[3]++;
		}
		if(iaLevel == 8){//2º Jefe.Bola que hace orbitar disparos. Sigue lentamente al jugador
			iaControl[4]++;//tiempo de vida del tanque
			angulo = Math.atan2(TanksApp.tanques[0].coorY - coorY,
					TanksApp.tanques[0].coorX - coorX);
			if(iaControl[4] == 40) vuln = false;
			if(iaControl[4] > 40)
				mov = 1;
			if(Math.sqrt(Math.pow(coorX - TanksApp.tanques[0].coorX,2) + 
					Math.pow(coorY - TanksApp.tanques[0].coorY,2)) < diag + TanksApp.tanques[0].diag)
				if(TanksApp.tanques[0].hit(10))
					TanksApp.tanques[0] = null;
			if(iaControl[4]/50 == (int)(iaControl[4]/50) && iaControl[4]/10 > iaControl[3]){
				velMax++;
			}else{
				if(velMax > 1 && iaControl[4]/10 < iaControl[3] && (iaControl[4] - 20)/50 == (int)((iaControl[4] - 20)/50)){
					velMax--;
					velocidad--;
				}
			}
			if(iaControl[3] > 100){
				for(int i = 0; i < disp.length && null != disp[i]; i++){
					disp[i].rotate(-1,0,id);
					disp[i] = null;
				}
				iaControl[4] = 0;
				iaControl[3] = 0;
				vuln = true;
			}
			if(iaControl[4] > 41){
				if(iaControl[4]/20 == (int)(iaControl[4]/20)){
					shot((int)(460*Math.random()) +20, (int)(460*Math.random()) + 20);
					//shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
				}
			}
		}
		if(iaLevel == 9){//tanque invisible
			if(coorX < 50 || velocidad > 0 && coorX <450){
				mov = 1;
			}else if(coorX > 450 || velocidad < 0 && coorX > 50){
				mov = -1;
			}else if(velocidad == 0){
				mov = 1;
			}
			iaControl[1]--;
			if(iaControl[1] == 0){
				iaControl[1] = 15;
				shot((int)TanksApp.tanques[0].coorX, (int)TanksApp.tanques[0].coorY);
				iaControl[0] = Math.random() - 0.5;
			}
		}
		if(iaLevel == 10){
			
		}*/
	}
	public void shot(int x, int y){
		int i = 0;
		for(; i < TanksApp.disparos.length && null != TanksApp.disparos[i]; i++){}
		if (i < TanksApp.disparos.length) {
			TanksApp.disparos[i] = new Disparo(coorX, coorY, Math.max(1,velocidad) + 10,
					tipoShot, i, id, x, y, iaControl[0]);
		}else {
			System.out.println("Max shot has been reached!!!!!!");
		}
	}
	
	public boolean hit(int damage){
		vida -= damage;
		if (vida <= 0){
			return true;
		}
		return false;
	}
	public boolean isHit(double x, double y, int index){
		if(iaLevel == 8 && !vuln && Math.abs(coorX - x) < diag + 15 && Math.abs(coorY - y) < diag + 15 ){
			if(!TanksApp.disparos[index].rotate){
				TanksApp.disparos[index].rotate(coorX, coorY, id);
				iaControl[3]++;
				int i = 0;
				for(; i < disp.length && null != disp[i]; i++){}
				disp[i] = TanksApp.disparos[index];
			}
			return false;
		}
		if(vuln && Math.abs(coorX - x) < diag && Math.abs(coorY - y) < diag ){
			if(iaLevel >= 6 && TanksApp.disparos[index].owner > 0 )
				return false;
			if(shield){
				if(Math.random() < shieldAbs){
					TanksApp.disparos[index].desviar(Math.PI, id);
					return false;
				}
			}
			return true;
		}
		return false;
	}
	public void changeShot(int a){
		tipoShot = a;
	}
	public static Tank[] randomTanks(int len) {
		Tank[] tq = new Tank[len];
		for(int i = 0 ; i < len; i ++) {
			int[] data = new int[numAtt];
			data[0] = 100 + 10*i;
			data[1] = (int)(420*Math.random() + 40);
			data[2] = (int)(420*Math.random() + 40);
			data[4] = (int)(5*Math.random() + i/10 + 1);//vmax
			data[5] = i + 1;//id
			data[8] = (int)(20*Math.random() + 20);
			data[9] = (int)(20*Math.random() + 20);
			data[12] = (int)(3*Math.random());//shape
			data[13] = (int)(Math.min(i/6 + 2,4)*Math.random());//movIA
			data[14] = 5;//(int)(Math.min(i/9 + 1,4)*Math.random() + 1);//ShotIA
			data[15] = (int)(Math.min(i/5 + 1,5)*Math.random() + 1);//TipoShot
			data[16] = (int)(i/30*Math.random()) ;
			tq[i] = new Tank(data);
		}
		int[] lastT = {1000,200,200,0,4,len -2,0,0,20,20,0,0,1,4,4,4,0};
		tq[len -2] = new Tank(lastT);
		return tq;
	}
	public static int[] randomTime(int len) {
		int[] time = new int[len];
		time[0] = 2;
		for(int i = 1 ; i < len - 2; i ++) {
			time[i] = Math.max(1, 10 - (int)(i/8));
		}
		//Pausa cada 10 tanques
		/*for(int i = 8 ; i < len - 1; i = i +10) {
			time[i] = -1;
		}*/
		time[len - 2] = -1;
		time[len - 1] = -8;
		return time;
	}
}
