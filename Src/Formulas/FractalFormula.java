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


class Formula{
 int maxiters;
 int Niters;
 int sig,falt,fcon;
 String Special;
 boolean Alternate,Conjugate;
 double []zr;
 double []zi;
 int NPars;
 boolean fixed;
 boolean UseOrbits;
 boolean dosumOrb;
 double OrbDistOld;
 int nskip;
 double OrbDist;
 boolean Diverge;
 OrbitConverger oo;
 
 public void SetGeneral(MandelFrac Client){
	 maxiters=Client.NIters;
	 UseOrbits=Client.useOrbits;
	 if(UseOrbits){
		//oo=new OrbitConverger(false);
		//oo.CloneFrom(Client.myorb_c);	 
		oo=Client.myorb_c;
		dosumOrb=oo.DoSum;
		Diverge=oo.Diverge;
		//if(!Diverge) dosumOrb=false;
		OrbDist=OrbDistOld=0;
		nskip=oo.GetNskip();
		if(nskip>maxiters) nskip=maxiters;
		//System.out.printf("%d\n",nskip);
	 }
	 Alternate=Client.Alternate;
	 Conjugate=Client.Conjugate;
	 
	 zr=new double [maxiters+1];
	 zi=new double [maxiters+1];
	 falt=1;
     if(Alternate) falt=-1;
     fcon=1;
     if(Conjugate) fcon=-1;  
	 Special=Client.Special;
	 fixed=Client.Fixed;
	 SetParameters(Client.ParamR,Client.ParamI);   
 }
	 
 public void SetParameters(double[]pR,double[]pI){}; //mapping of the parameters
 public void SetStartingPoint(double x,double y){};
 public void Evolve(){};
 
 public void UpdateOrbDist(){
	if(Diverge){
		OrbDistOld=OrbDist;
		if(dosumOrb)OrbDist=OrbDistOld+oo.getDistance(zr[Niters],zi[Niters],Niters);
		else OrbDist=oo.getDistance(zr[Niters],zi[Niters],Niters);
	}
	else{
		OrbDistOld=OrbDist;
		if(dosumOrb){
			if(OrbDistOld==0)OrbDist=oo.getDistance(zr[Niters],zi[Niters],Niters);		
			else OrbDist=1./(1./OrbDistOld+1./oo.getDistance(zr[Niters],zi[Niters],Niters));
		}
		else{
			OrbDist=oo.getDistance(zr[Niters],zi[Niters],Niters);
		}
	}
}
 
 public void BuildPath(){
	 if(UseOrbits){
		while(Niters<nskip){
			Evolve();
		}
		UpdateOrbDist();
		while(CheckExitOrbits()){
			Evolve();
			UpdateOrbDist();
		}
		//System.out.printf("Niters=%d\n",Niters);
	}
	else{
		while(CheckExit()) Evolve();
	}
 }
 
 /*public boolean CheckExitOrbits(){//by default this is the same
	 return CheckExit();
 }*/
 
 public boolean CheckExit(){
	 return false;
 }
 
 public int Iters(){
	 return Niters;
 }
 
 public double GetZr(int index){
	 if(index<0 || index>Niters) return 0;
	 return zr[index];
 }
 
 public double GetZi(int index){
	 if(index<0 || index>Niters) return 0;
	 return zi[index];
 }
 
 public double GetPhi(int index){
	 if(index<0 || index>Niters) return 0;
	 return atan2(zi[index],zr[index]);
 }
 
 public double GetRho(int index){
	 if(index<0 || index>Niters) return 0;
	 return sqrt(pow(zi[index],2)+pow(zr[index],2));
 }
 
 public double FinalSmoothing(){
	 if(UseOrbits) return FinalSmoothingOrbits();
	 return FinalSmoothingNorm();
 }
 
 public double GetRadiusForOrbit(){
	return 1;
 }
 
 public boolean CheckExitOrbits(){
		//System.out.printf("N=%d dist=%f\n",Niters,oo.getDistance(zr[Niters],zi[Niters],Niters));
		if(Diverge) return (OrbDist<GetRadiusForOrbit() && Niters<maxiters);
		else return (OrbDist>1./GetRadiusForOrbit() && Niters<maxiters);
		
	}
	
 public double FinalSmoothingOrbits(){
 		if(Niters<1) return 0;
		if(Niters<=nskip) return nskip;
		if(Diverge)return Niters-1+(GetRadiusForOrbit()-OrbDistOld)/(OrbDist-OrbDistOld);
		else return Niters-1+(1./GetRadiusForOrbit()-OrbDistOld)/(OrbDist-OrbDistOld);
 } 
 
  public double FinalSmoothingNorm(){
	return 0;
 }
}
