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

class Newton_gen extends Formula{
	double []pr;
	double []pi;
	double bailin;
	double ar,ai;
	double rhoa,phia;
	double []rhop;
	double []phip;
	int highestnonzero;
	int K;
		
	public Newton_gen(){
		NPars=8;
		pr=new double[6];
		pi=new double[6];
		rhop=new double[6];
		phip=new double[6];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailin=pR[0];
		ar=pR[1];
		ai=pI[1];
		rhoa=sqrt(ar*ar+ai*ai);
		phia=atan2(ai,ar);	
		highestnonzero=0;	
		for(int i=0;i<6;i++){
			pr[i]=pR[i+2];
			pi[i]=pI[i+2];
			rhop[i]=sqrt(pr[i]*pr[i]+pi[i]*pi[i]);
			phip[i]=atan2(pi[i],pr[i]);			
			if(rhop[i]!=0) highestnonzero=i;
		}
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		//zrprev=ziprev=0;
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		double rhoz=sqrt(zr[Niters]*zr[Niters]+zi[Niters]*zi[Niters]);
		double phiz=atan2(zi[Niters],zr[Niters]);
		double NumR,NumI;
		NumR=NumI=0;
		double DeNumR,DeNumI;
		DeNumR=DeNumI=0;
		for(int i=0;i<=highestnonzero;i++){
			NumR+=rhop[i]*pow(rhoz,i)*cos(phip[i]+i*phiz);
			NumI+=rhop[i]*pow(rhoz,i)*sin(phip[i]+i*phiz);
			if(i>0){
				DeNumR+=i*rhop[i]*pow(rhoz,i-1)*cos(phip[i]+(i-1)*phiz);
				DeNumI+=i*rhop[i]*pow(rhoz,i-1)*sin(phip[i]+(i-1)*phiz);
			}			
		}
		double rhonum=sqrt(NumR*NumR+NumI*NumI);
		double phinum=atan2(NumI,NumR);
		double rhodenum=sqrt(DeNumR*DeNumR+DeNumI*DeNumI);
		double phidenum=atan2(DeNumI,DeNumR);
		double tx=zr[Niters]-rhoa*rhonum*cos(phia+phinum-phidenum)/rhodenum;
		double ty=zi[Niters]-rhoa*rhonum*sin(phia+phinum-phidenum)/rhodenum;
		zr[Niters+1]=tx;
        zi[Niters+1]=sig*ty;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		if(Niters==0) return true;
		return ((zr[Niters]-zr[Niters-1])*(zr[Niters]-zr[Niters-1])+(zi[Niters]-zi[Niters-1])*(zi[Niters]-zi[Niters-1])>bailin*bailin && Niters<maxiters);
	} 
	
	@Override
	public double FinalSmoothingNorm(){
		    if(Niters<2) return Niters;
		    double rho=sqrt((zr[Niters]-zr[Niters-1])*(zr[Niters]-zr[Niters-1])+(zi[Niters]-zi[Niters-1])*(zi[Niters]-zi[Niters-1]));
		    double rhoprev=sqrt((zr[Niters-2]-zr[Niters-1])*(zr[Niters-2]-zr[Niters-1])+(zi[Niters-2]-zi[Niters-1])*(zi[Niters-2]-zi[Niters-1]));
		    return Niters-1+log(1+log(bailin/rhoprev)/log(rho/rhoprev))/log(2);
		  // 	return Niters-1+frac;
	}
	
	@Override
	public double GetRadiusForOrbit(){
		return 1./bailin;
	}
}

class Rot_Newton_gen extends Formula{
	double []pr;
	double []pi;
	double bailin;
	double ar,ai;
	double RotAngle;
	double ARA;
	double RotI,RotR;
	double rhoa,phia;
	double []rhop;
	double []phip;
	int sumrot;
	int mulrot;
	int highestnonzero;
	int K;
		
	public Rot_Newton_gen(){
		NPars=12;
		pr=new double[8];
		pi=new double[8];
		rhop=new double[8];
		phip=new double[8];
	}
	
	@Override
	public void SetParameters(double[]pR,double[]pI){
		bailin=pR[0];
		ar=pR[1];
		ai=pI[1];
		rhoa=sqrt(ar*ar+ai*ai);
		phia=atan2(ai,ar);	
		highestnonzero=0;	
		for(int i=0;i<8;i++){
			pr[i]=pR[i+2];
			pi[i]=pI[i+2];
			rhop[i]=sqrt(pr[i]*pr[i]+pi[i]*pi[i]);
			phip[i]=atan2(pi[i],pr[i]);			
			if(rhop[i]!=0) highestnonzero=i;
		}
		RotAngle=pR[10]*PI;
		sumrot=(int)pR[11];
		mulrot=1;
		if(sumrot==2) mulrot=2;
		if(sumrot>1) sumrot=1;
	}
	
	@Override
	public void SetStartingPoint(double x,double y){
		zr[0]=x;
		zi[0]=y;
		//zrprev=ziprev=0;
		ARA=RotAngle;
		Niters=0;		
		sig=fcon;
	}
	
	@Override
	public void Evolve(){
		if(Niters>0){
			ARA=mulrot*ARA+sumrot*RotAngle;
		}
		RotR=cos(ARA);
		RotI=sin(ARA);
		double zrlast=RotR*zr[Niters]-RotI*zi[Niters];
		double zilast=RotR*zi[Niters]+RotI*zr[Niters];		
		double rhoz=sqrt(zrlast*zrlast+zilast*zilast);
		double phiz=atan2(zilast,zrlast);
		double NumR,NumI;
		NumR=NumI=0;
		double DeNumR,DeNumI;
		DeNumR=DeNumI=0;
		for(int i=0;i<=highestnonzero;i++){
			NumR+=rhop[i]*pow(rhoz,i)*cos(phip[i]+i*phiz);
			NumI+=rhop[i]*pow(rhoz,i)*sin(phip[i]+i*phiz);
			if(i>0){
				DeNumR+=i*rhop[i]*pow(rhoz,i-1)*cos(phip[i]+(i-1)*phiz);
				DeNumI+=i*rhop[i]*pow(rhoz,i-1)*sin(phip[i]+(i-1)*phiz);
			}			
		}
		double rhonum=sqrt(NumR*NumR+NumI*NumI);
		double phinum=atan2(NumI,NumR);
		double rhodenum=sqrt(DeNumR*DeNumR+DeNumI*DeNumI);
		double phidenum=atan2(DeNumI,DeNumR);
		double tx=zr[Niters]-rhoa*rhonum*cos(phia+phinum-phidenum)/rhodenum;
		double ty=sig*(zi[Niters]-rhoa*rhonum*sin(phia+phinum-phidenum)/rhodenum);
		zr[Niters+1]=RotR*tx+RotI*ty;
        zi[Niters+1]=RotR*ty-RotI*tx;
        sig*=falt;    
        Niters++;		
	}
	
	@Override
	public boolean CheckExit(){
		if(Niters==0) return true;
		return ((zr[Niters]-zr[Niters-1])*(zr[Niters]-zr[Niters-1])+(zi[Niters]-zi[Niters-1])*(zi[Niters]-zi[Niters-1])>bailin*bailin && Niters<maxiters);
	} 
	
	@Override
	public double FinalSmoothingNorm(){
		    if(Niters<2) return Niters;
		    double rho=sqrt((zr[Niters]-zr[Niters-1])*(zr[Niters]-zr[Niters-1])+(zi[Niters]-zi[Niters-1])*(zi[Niters]-zi[Niters-1]));
		    double rhoprev=sqrt((zr[Niters-2]-zr[Niters-1])*(zr[Niters-2]-zr[Niters-1])+(zi[Niters-2]-zi[Niters-1])*(zi[Niters-2]-zi[Niters-1]));
		    return Niters-1+log(1+log(bailin/rhoprev)/log(rho/rhoprev))/log(2);
		  // 	return Niters-1+frac;
	}
	
	@Override
	public double GetRadiusForOrbit(){
		return 1./bailin;
	}
}
