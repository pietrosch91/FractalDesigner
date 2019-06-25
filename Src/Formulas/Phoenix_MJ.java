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
 
class Phoenix_Mandelbrot extends Formula{
	double px,py;
	double Kx,Ky;
	double X,Y;
	double bailout;
	double zrprev;
	double ziprev;
	
	
	public Phoenix_Mandelbrot(){
		NPars=3;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		Kx=pR[2];
		Ky=pI[2];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=px;
		zi[0]=py;
		X=x;
		Y=y;
		Niters=0;		
		sig=fcon;
		zrprev=ziprev=0;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		zr[Niters+1]=zrlast*zrlast-zilast*zilast+Kx*zrprev-Ky*ziprev+X;
		zi[Niters+1]=sig*(2*zrlast*zilast+Kx*ziprev+Ky*zrprev+Y);
		zrprev=zrlast;
		ziprev=zilast;
		sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	} 
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters+1+log(log(bailout)/log(GetRho(Niters)))/log(2);
	}
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}
  
class Phoenix_Julia extends Formula{
	double px,py;
	double Kx,Ky;
	double X,Y;
	double bailout;
	double zrprev;
	double ziprev;
	
	
	public Phoenix_Julia(){
		NPars=3;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		Kx=pR[2];
		Ky=pI[2];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		Niters=0;		
		sig=fcon;
		zrprev=ziprev=0;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		zr[Niters+1]=zrlast*zrlast-zilast*zilast+Kx*zrprev-Ky*ziprev+X;
		zi[Niters+1]=sig*(2*zrlast*zilast+Kx*ziprev+Ky*zrprev+Y);
		zrprev=zrlast;
		ziprev=zilast;
		sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	} 
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters+1+log(log(bailout)/log(GetRho(Niters)))/log(2);
	}
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}

class Phoenix_Mandelbrot_General extends Formula{
	double px,py;
	double X,Y;
	double Kx,Ky;
	double []cr;
	double []ci;
	double []rho;
    double []phi;
    double highestnonzero;
    double zrprev;
	double ziprev;
	
	double bailout;
	
	public Phoenix_Mandelbrot_General(){
		NPars=7;
		cr=new double[4];
		ci=new double[4];
		rho=new double[4];
		phi=new double[4];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		Kx=pR[2];
		Ky=pI[2];
		for(int i=0;i<4;i++){
			cr[i]=pR[i+3];
			ci[i]=pI[i+3];		
			rho[i]=sqrt(pow(cr[i],2)+pow(ci[i],2));
			if(rho[i]>0) highestnonzero=i+1;
			phi[i]=atan2(ci[i],cr[i]);	
		}		
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=px;
		zi[0]=py;
		X=x;
		Y=y;
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		double rholast=sqrt(zrlast*zrlast+zilast*zilast);
		double philast=atan2(zilast,zrlast);
		double nextR,nextI;
		nextR=Kx*zrprev-Ky*ziprev+X;
		nextI=Kx*ziprev+Ky*zrprev+Y;
		for(int i=0;i<highestnonzero;i++){
			nextR+=pow(rholast,i+1)*rho[i]*cos((i+1)*philast+phi[i]);
			nextI+=pow(rholast,i+1)*rho[i]*sin((i+1)*philast+phi[i]);
		}
		zrprev=zrlast;
		ziprev=zilast;
		zr[Niters+1]=nextR;
        zi[Niters+1]=sig*nextI;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		if(highestnonzero==0) return Niters;
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}
 
class Phoenix_Julia_General extends Formula{
	double px,py;
	double X,Y;
	double Kx,Ky;
	double []cr;
	double []ci;
	double []rho;
    double []phi;
    double highestnonzero;
    double zrprev;
	double ziprev;
	
	double bailout;
	
	public Phoenix_Julia_General(){
		NPars=7;
		cr=new double[4];
		ci=new double[4];
		rho=new double[4];
		phi=new double[4];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		Kx=pR[2];
		Ky=pI[2];
		for(int i=0;i<4;i++){
			cr[i]=pR[i+3];
			ci[i]=pI[i+3];		
			rho[i]=sqrt(pow(cr[i],2)+pow(ci[i],2));
			if(rho[i]>0) highestnonzero=i+1;
			phi[i]=atan2(ci[i],cr[i]);	
		}		
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		double rholast=sqrt(zrlast*zrlast+zilast*zilast);
		double philast=atan2(zilast,zrlast);
		double nextR,nextI;
		nextR=Kx*zrprev-Ky*ziprev+X;
		nextI=Kx*ziprev+Ky*zrprev+Y;
		for(int i=0;i<highestnonzero;i++){
			nextR+=pow(rholast,i+1)*rho[i]*cos((i+1)*philast+phi[i]);
			nextI+=pow(rholast,i+1)*rho[i]*sin((i+1)*philast+phi[i]);
		}
		zrprev=zrlast;
		ziprev=zilast;
		zr[Niters+1]=nextR;
        zi[Niters+1]=sig*nextI;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		if(highestnonzero==0) return Niters;
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}

class LPMJ_Phoenix extends Formula{
	double []pr;
	double []pi;
	double Kx,Ky;
	double zrprev,ziprev;
	double X,Y;
	String LString;
	int L;
	double bailout;
	
	
	public LPMJ_Phoenix(){
		//System.out.printf("Building LPM class\n");
		NPars=2;
		LString="";
		L=0;
		pr=new double[2];
		pi=new double[2];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		pr[0]=pR[1];
		pi[0]=pI[1];
		Kx=pR[2];
		Ky=pI[2];
		LString=Special;
		if(LString==null) LString="A";
		//System.out.printf("%s %s \n",Special,LString);
		L=LString.length();
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
	//	System.out.printf("INIT\n");
		pr[1]=x;
		pi[1]=y;
		int which=((int)LString.charAt(0))-65;
		//System.out.printf("Which = %d\n",which);
		ziprev=zrprev=0;
		zr[0]=pr[1-which];
		zi[0]=pi[1-which];
	//	System.out.printf("%f %f \n",zr[0],zi[0]);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		int which=((int)LString.charAt(Niters%L))-65;
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=zrlast*zrlast-zilast*zilast+Kx*zrprev-Ky*ziprev+pr[which];
        zi[Niters+1]=sig*(2*zrlast*zilast+Kx*ziprev+Ky*zrprev+pi[which]);
        zrprev=zrlast;
		ziprev=zilast;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters+1+log(log(bailout)/log(GetRho(Niters)))/log(2);
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
}

class General_LPMJ_Phoenix extends Formula{
	double []pr;
	double []pi;
	double Kx,Ky;
	double zrprev,ziprev;
	double X,Y;
	double []cr;
	double []ci;
	double []rho;
    double []phi;
    double highestnonzero;
    String LString;
	int L;
	double bailout;
	
	public General_LPMJ_Phoenix(){
		NPars=8;
		cr=new double[4];
		ci=new double[4];
		rho=new double[4];
		phi=new double[4];
		LString="";
		L=0;
		pr=new double[2];
		pi=new double[2];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		pr[0]=pR[1];
		pi[0]=pI[1];
		Kx=pR[2];
		Ky=pI[2];
		for(int i=0;i<4;i++){
			cr[i]=pR[i+3];
			ci[i]=pI[i+3];		
			rho[i]=sqrt(pow(cr[i],2)+pow(ci[i],2));
			if(rho[i]>0) highestnonzero=i+1;
			phi[i]=atan2(ci[i],cr[i]);	
		}		
		LString=Special;
		if(LString==null) LString="A";
		//System.out.printf("%s %s \n",Special,LString);
		L=LString.length();
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		pr[1]=x;
		pi[1]=y;
		int which=((int)LString.charAt(0))-65;
		zr[0]=pr[1-which];
		zi[0]=pi[1-which];	
		zrprev=ziprev=0;
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		int which=((int)LString.charAt(Niters%L))-65;
		double zrlast=zr[Niters];
		double zilast=zi[Niters];
		double rholast=sqrt(zrlast*zrlast+zilast*zilast);
		double philast=atan2(zilast,zrlast);
		double nextR,nextI;
		nextR=Kx*zrprev-Ky*ziprev+pr[which];
		nextI=Kx*ziprev+Ky*zrprev+pi[which];
		for(int i=0;i<highestnonzero;i++){
			nextR+=pow(rholast,i+1)*rho[i]*cos((i+1)*philast+phi[i]);
			nextI+=pow(rholast,i+1)*rho[i]*sin((i+1)*philast+phi[i]);
		}
		zr[Niters+1]=nextR;
        zi[Niters+1]=sig*nextI;
        zrprev=zrlast;
        ziprev=zilast;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double FinalSmoothingNorm(){
		if(highestnonzero==0) return Niters;
		//double d=1+log(log(bailout)/log(GetRho(Niters)*rho[(int)highestnonzero-1]))/log(highestnonzero); //da rivedere
		//if(d<0|| d>1) System.out.printf("%f\n",d);
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);
	//	return Niters+1+log(log(bailout)/log(GetRho(Niters)*rho[(int)highestnonzero-1]))/log(highestnonzero); //da rivedere
	} 
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
} 
