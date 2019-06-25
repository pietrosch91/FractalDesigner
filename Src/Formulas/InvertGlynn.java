import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.tan;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


class Inv_Glynn_Julia extends Formula{
	double px,py;
	double expo;	
	double X,Y;
	double winv;
	double bailout;
	
	public Inv_Glynn_Julia(){
		NPars=4;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		expo=pR[2];
		winv=pR[3];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		//System.out.printf("%f %f %f %f\n",zr[0],zi[0],x,y);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		double mod2=zrlast*zrlast+zilast*zilast;
		double zrinv=zrlast/mod2;
		double ziinv=-zilast/mod2;
		zrlast+=winv*zrinv;
		zilast+=winv*ziinv;
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		double rhonew=pow(zrlast*zrlast+zilast*zilast,expo/2.);
		double phinew=expo*atan2(zilast,zrlast);
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=rhonew*cos(phinew)+X;
        zi[Niters+1]=sig*(rhonew*sin(phinew)+Y);
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);		
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}
/*
class Glynn_Julia_Phoenix extends Formula{
	double px,py;
	double expo;	
	double X,Y;
	double fR,fI,expoF;
	double rhof,phif;
	double bailout;
	
	public Glynn_Julia_Phoenix(){
		NPars=5;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		expo=pR[2];
		fR=pR[3];
		fI=pI[3];
		expoF=pR[4];
		rhof=sqrt(fR*fR+fI*fI);
		phif=atan2(fI,fR);
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		//System.out.printf("%f %f %f %f\n",zr[0],zi[0],x,y);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double rhonew=pow(GetRho(Niters),expo);
		double phinew=expo*GetPhi(Niters);
		
		if(Niters>0){
			double rhoold,phiold;
			rhoold=rhof*pow(GetRho(Niters-1),expoF);
			phiold=phif+expoF*GetPhi(Niters-1);
			zr[Niters+1]=rhonew*cos(phinew)+rhoold*cos(phiold)+X;
			zi[Niters+1]=sig*(rhonew*sin(phinew)+rhoold*sin(phiold)+Y);
		}	
		else{			
			zr[Niters+1]=rhonew*cos(phinew)+X;
			zi[Niters+1]=sig*(rhonew*sin(phinew)+Y);
		}			
		
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothing(){
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);		
	} 
}*/

class Inv_Glynn2_Julia extends Formula{
	double px,py;
	double expo2;
	double expo1;	
	double winv;
	double X,Y;
	double bailout;
	
	public Inv_Glynn2_Julia(){
		NPars=5;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		expo1=pR[2];
		expo2=pR[3];
		winv=pR[4];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		//System.out.printf("%f %f %f %f\n",zr[0],zi[0],x,y);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		double mod2=zrlast*zrlast+zilast*zilast;
		double zrinv=zrlast/mod2;
		double ziinv=-zilast/mod2;
		zrlast+=winv*zrinv;
		zilast+=winv*ziinv;
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		double rho=pow(zrlast*zrlast+zilast*zilast,1/2.);
		double phi=atan2(zilast,zrlast);
		double rho1new=pow(rho,expo1);
		double phi1new=expo1*phi;
		double rho2new=pow(rho,expo2);
		double phi2new=expo2*phi;
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=rho1new*cos(phi1new)+rho2new*cos(phi2new)+X;
        zi[Niters+1]=sig*(rho1new*sin(phi1new)+rho2new*sin(phi2new)+Y);
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);		
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}
/*
class Glynn_Julia_Lyap extends Formula{
	double []px;
	double []py;
	double []expo;
	//double X,Y;
	String LString;
	int L;
	double bailout;
	
	public Glynn_Julia_Lyap(){
		NPars=7;
		px=new double[3];
		py=new double[3];
		expo=new double[3];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px[0]=pR[1];
		py[0]=pI[1];
		expo[0]=pR[2];
		px[1]=pR[3];
		py[1]=pI[3];
		expo[1]=pR[4];
		px[2]=pR[5];
		py[2]=pI[5];
		expo[2]=pR[6];
		LString=Special;
		if(LString==null) LString="A";
		//System.out.printf("%s %s \n",Special,LString);
		L=LString.length();
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		//=px;
		//Y=py;
		//System.out.printf("%f %f %f %f\n",zr[0],zi[0],x,y);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		int which=((int)LString.charAt(Niters%L))-65;
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		double rho=GetRho(Niters);
		double phi=GetPhi(Niters);
		double rhonew=pow(rho,expo[which]);
		double phinew=expo[which]*phi;
		zr[Niters+1]=rhonew*cos(phinew)+px[which];
        zi[Niters+1]=sig*(rhonew*sin(phinew)+py[which]);
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothing(){
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);		
	} 
}*/
