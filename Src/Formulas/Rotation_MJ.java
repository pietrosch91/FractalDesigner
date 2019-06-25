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


class Rotational_Mandelbrot_General extends Formula{
	double px,py;
	double X,Y;
	double Kx,Ky;
	double []cr;
	double []ci;
	double []rho;
    double []phi;
    boolean []iszero;
    double highestnonzero;
    double RotAngle;
    double RotR,RotI;
    double zrprev;
	double ziprev;
	
	double bailout;
	
	public Rotational_Mandelbrot_General(){
		NPars=8;
		cr=new double[4];
		ci=new double[4];
		rho=new double[4];
		phi=new double[4];
		iszero=new boolean[4];
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
			iszero[i]=true;
			if(rho[i]>0){
				 highestnonzero=i+1;
				 iszero[i]=false;
			 }
			phi[i]=atan2(ci[i],cr[i]);	
		}	
		RotAngle=pR[7]*PI;
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
		RotR=cos(Niters*RotAngle);
		RotI=sin(Niters*RotAngle);
		double zrlast=RotR*zr[Niters]-RotI*zi[Niters];
		double zilast=RotR*zi[Niters]+RotI*zr[Niters];
		double rholast=sqrt(zrlast*zrlast+zilast*zilast);
		double philast=atan2(zilast,zrlast);
		double nextR,nextI;
		nextR=Kx*zrprev-Ky*ziprev+X;
		nextI=Kx*ziprev+Ky*zrprev+Y;
		for(int i=0;i<highestnonzero;i++){
			if(!iszero[i]){
				nextR+=pow(rholast,i+1)*rho[i]*cos((i+1)*philast+phi[i]);
				nextI+=pow(rholast,i+1)*rho[i]*sin((i+1)*philast+phi[i]);
			}
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
 
class Rotational_Julia_General extends Formula{
	double px,py;
	double X,Y;
	double Kx,Ky;
	double []cr;
	double []ci;
	double []rho;
    double []phi;
    boolean []iszero;
    double highestnonzero;
    double RotAngle;
    double RotR,RotI;
    double zrprev;
	double ziprev;
	
	double bailout;
	
	public Rotational_Julia_General(){
		NPars=7;
		cr=new double[4];
		ci=new double[4];
		rho=new double[4];
		phi=new double[4];
		iszero=new boolean[4];
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
			iszero[i]=true;
			if(rho[i]>0){
				 highestnonzero=i+1;
				 iszero[i]=false;
			 }
			phi[i]=atan2(ci[i],cr[i]);	
		}	
		RotAngle=pR[7]*PI;
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
		RotR=cos(Niters*RotAngle);
		RotI=sin(Niters*RotAngle);
		double zrlast=RotR*zr[Niters]-RotI*zi[Niters];
		double zilast=RotR*zi[Niters]+RotI*zr[Niters];
		double rholast=sqrt(zrlast*zrlast+zilast*zilast);
		double philast=atan2(zilast,zrlast);
		double nextR,nextI;
		nextR=Kx*zrprev-Ky*ziprev+X;
		nextI=Kx*ziprev+Ky*zrprev+Y;
		for(int i=0;i<highestnonzero;i++){
			if(!iszero[i]){
				nextR+=pow(rholast,i+1)*rho[i]*cos((i+1)*philast+phi[i]);
				nextI+=pow(rholast,i+1)*rho[i]*sin((i+1)*philast+phi[i]);
			}
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

