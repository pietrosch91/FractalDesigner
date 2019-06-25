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

class Sierp_Mandelbrot extends Formula{
	double px,py;
	double X,Y;
	double bailout;
	double expo1,expo2;
	double RhoX,PhiX;
	double RhoZ,PhiZ;
	
	public Sierp_Mandelbrot(){
		NPars=2;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		expo1=pR[2];
		expo2=pR[3];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=px;
		zi[0]=py;
		X=x;
		Y=y;
		RhoX=sqrt(X*X+Y*Y);
		PhiX=atan2(Y,X);
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		RhoZ=GetRho(Niters);
		PhiZ=GetPhi(Niters);
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=pow(RhoZ,expo1)*cos(PhiZ*expo1)+RhoX*pow(RhoZ,-expo2)*cos(PhiX-expo2*PhiZ);
        zi[Niters+1]=pow(RhoZ,expo1)*sin(PhiZ*expo1)+RhoX*pow(RhoZ,-expo2)*sin(PhiX-expo2*PhiZ);
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

class Sierp_Julia extends Formula{
	double px,py;
	double X,Y;
	double bailout;
	double expo1,expo2;
	double RhoX,PhiX;
	double RhoZ,PhiZ;
	
	public Sierp_Julia(){
		NPars=2;
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		px=pR[1];
		py=pI[1];
		expo1=pR[2];
		expo2=pR[3];
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		X=px;
		Y=py;
		RhoX=sqrt(X*X+Y*Y);
		PhiX=atan2(Y,X);
		//System.out.printf("%f %f %f %f\n",zr[0],zi[0],x,y);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		RhoZ=GetRho(Niters);
		PhiZ=GetPhi(Niters);
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=pow(RhoZ,expo1)*cos(PhiZ*expo1)+RhoX*pow(RhoZ,-expo2)*cos(PhiX-expo2*PhiZ);
        zi[Niters+1]=pow(RhoZ,expo1)*sin(PhiZ*expo1)+RhoX*pow(RhoZ,-expo2)*sin(PhiX-expo2*PhiZ);
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		return (zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]<bailout*bailout && Niters<maxiters);
	}
	
	@Override
	public double GetRadiusForOrbit(){
		return bailout;
	}
	
	@Override
	public double FinalSmoothingNorm(){
		return Niters-1+log(1+log(bailout/GetRho(Niters-1))/log(GetRho(Niters)/GetRho(Niters-1)))/log(2);
	} 	
}
 

class Sierp_LPMJ extends Formula{
	double []pr;
	double []pi;
	double X,Y;
	String LString;
	int L;
	double bailout;
	double expo1,expo2;
	double []RhoX;
	double []PhiX;
	double RhoZ,PhiZ;
	
	
	public Sierp_LPMJ(){
		//System.out.printf("Building LPM class\n");
		NPars=2;
		LString="";
		L=0;
		pr=new double[2];
		pi=new double[2];
		RhoX=new double[2];
		PhiX=new double[2];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailout=pR[0];
		pr[0]=pR[1];
		pi[0]=pI[1];
		expo1=pR[2];
		expo2=pR[3];
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
		RhoX[0]=sqrt(pr[0]*pr[0]+pi[0]*pi[0]);
		PhiX[0]=atan2(pi[0],pr[0]);
		RhoX[1]=sqrt(pr[1]*pr[1]+pi[1]*pi[1]);
		PhiX[1]=atan2(pi[1],pr[1]);
		int which=((int)LString.charAt(0))-65;
		//System.out.printf("Which = %d\n",which);
		
		zr[0]=pr[1-which];
		zi[0]=pi[1-which];
	//	System.out.printf("%f %f \n",zr[0],zi[0]);		
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		int which=((int)LString.charAt(Niters%L))-65;
		
		//double zrlast=zr[Niters];
		//double zilast=zi[Niters];
		RhoZ=GetRho(Niters);
		PhiZ=GetPhi(Niters);
	//	double tx=zr*zr-zi*zi+X;
	//	double ty=2*zr*zi+Y;
		zr[Niters+1]=pow(RhoZ,expo1)*cos(PhiZ*expo1)+RhoX[which]*pow(RhoZ,-expo2)*cos(PhiX[which]-expo2*PhiZ);
        zi[Niters+1]=pow(RhoZ,expo1)*sin(PhiZ*expo1)+RhoX[which]*pow(RhoZ,-expo2)*sin(PhiX[which]-expo2*PhiZ);
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


