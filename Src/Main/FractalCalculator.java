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
import static java.lang.Math.tan;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;
import javax.swing.ImageIcon;
import javax.swing.JLabel;



//Colouring types
class Iterator{	
	MandelFrac Client;
	
	Iterator(){};
	void Specialize(MandelFrac client){};
	void setN(int N){};
	
	double calcPoint_full(double x,double y){
		double rho=sqrt(x*x+y*y);
		double phi=atan2(y,x);
		double X=rho*cos(phi+Client.RotID*PI/4.);
		double Y=rho*sin(phi+Client.RotID*PI/4.);
		return calcPoint(X,Y);
	}
	
    double calcPoint(double x, double y){return 0;};    
}

class std extends Iterator{
	//Formula IterateFormula;
    int niters;
   // double bailout;
    //double px,py;
    //boolean juliamand;
    //int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public std(){
		super();		
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;		
	}
	

    
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k;
        double res=0;
        //IterateFormula=new Formula();
        if(Client==null){
			System.out.printf("Wrong.... Missing client\n");
			return 0;
		}
        IterateFormula=Client.GetFormula();
        //System.out.printf("%f %f\n",x,y);
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        /*cr=IterateFormula.GetCr();
        ci=IterateFormula.GetCi();*/        
        /******************/
        if(k<niters){
            res=IterateFormula.FinalSmoothing();
        }
        else if (fixed) res=k;
        else      res=1e20;    
        return res;
    }
}

class qcount extends Iterator{
	//Formula IterateFormula;
    int niters;
   // double bailout;
    //double px,py;
    //boolean juliamand;
    //int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public qcount(){
		super();		
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;		
	}
	

    
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k;
        double res=0;
        double sum=0;
        double sumold=0;
        //IterateFormula=new Formula();
        if(Client==null){
			System.out.printf("Wrong.... Missing client\n");
			return 0;
		}
        IterateFormula=Client.GetFormula();
        //System.out.printf("%f %f\n",x,y);
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        for(int i=0;i<=k;i++){
			sumold=sum;
			double phi=IterateFormula.GetPhi(i);
			if(phi>PI/2.) phi=PI-phi;
			if(phi<-PI/2.) phi=-PI-phi;
			phi*=0.95;
			/*if (phi>1) sum+=2;
			else if (phi>0) sum+=1;
			else if (phi>-1) sum+=4;
			else sum+=3;        */
			sum+=tan(phi);
        }
        /*cr=IterateFormula.GetCr();
        ci=IterateFormula.GetCi();*/        
        /******************/
        if(k<niters){
			double d;
			double S1,S2;
			S1=sum;
			S2=sumold;
			d=IterateFormula.FinalSmoothing();
            res=floor(d);
            d-=res;
           // res=CatRomInterp(S1,S2,S3,S4,d);
            res=d*S1+(1-d)*S2;
		}
		else if (fixed){
			res=sumold;
		}
        else res=1e20;        
        return res;
	}
}




class phase extends Iterator{
    //Formula IterateFormula;
    int niters;
    //double bailout;
    //double px,py;
    //boolean juliamand;
    boolean dosum;
   // int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public phase(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		int temp=(int)client.mylayer.GetParam(0);	
		dosum=(temp!=0);
	}
	

    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k,i;
        double res=0;
        double sum;
        sum=0;
        //IterateFormula=new Formula();
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        if((k<niters)||fixed){
			if (!dosum){
				res=IterateFormula.GetPhi(k);
                if(res<0) res+=2*PI;
            }  
            else{
				res=0;
				for(i=1;i<=k;i++) res+=IterateFormula.GetPhi(i);
			}
		}
        else res=1e20;        
        return res;
    }
}

class angle extends Iterator{
    //Formula IterateFormula;
    int niters;
    //double bailout;
    //double px,py;
    //boolean juliamand;
    boolean invert;
    double invoff;
    int dosum;
    double TargetAngle;
   // int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public angle(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		//int temp=(int)client.mylayer.GetParam(0);	
		dosum=(int)client.mylayer.GetParam(0);	
		TargetAngle=client.mylayer.GetParam(1)*PI;		
		int temp=(int)client.mylayer.GetParam(2);	
		invert=(temp!=0);
		invoff=client.mylayer.GetParam(3);
	}
	
	double GetAngle(double in){
		double out=in;
		while(out>=2*PI) out-=2*PI;
		while(out<0)out+=2*PI;
		if(out>PI) out=2*PI-out;
		return out;
	}
	
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k,i;
        double res=0;
        double dmin;
        double dmins,dminsold;
        double dx1,dx2,dy1,dy2;
        double m1,m2,sc;
        double ang;
        double sum=0;
        double sumold;
        dmin=dmins=1000;
        dminsold=sumold=0;
        //IterateFormula=new Formula();
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        for(i=2;i<=k;i++){
			dx1=IterateFormula.GetZr(i)-IterateFormula.GetZr(i-1);
			dx2=IterateFormula.GetZr(i-1)-IterateFormula.GetZr(i-2);
			dy1=IterateFormula.GetZi(i)-IterateFormula.GetZi(i-1);
			dy2=IterateFormula.GetZi(i-1)-IterateFormula.GetZi(i-2);
			sc=dx1*dx2+dy1*dy2;
			m1=sqrt(dx1*dx1+dy1*dy1);
			m2=sqrt(dx2*dx2+dy2*dy2);
			ang=acos(sc/(m2*m1));
			if(dosum==0){
				sumold=sum;
				if(!invert)	sum+=abs(ang-GetAngle(TargetAngle));
				else sum+=1./(abs(ang-GetAngle(TargetAngle))+invoff);
			}
			else if (dosum==1){
				sumold=sum;
				if(!invert) sum+=abs(ang-GetAngle((i-1)*TargetAngle));
				else sum+=1./(abs(ang-GetAngle((i-1)*TargetAngle))+invoff);
			}				
			else if (dosum==2){
				sumold=sum;
				if(!invert) sum+=abs(ang-GetAngle((i-1)*(i)*TargetAngle/2.));
				else sum+=1./(abs(ang-GetAngle((i-1)*(i)*TargetAngle/2.))+invoff);
			}
			else{
				sumold=sum;
				if(!invert) sum+=abs(ang-GetAngle(cos(i)*TargetAngle));
				else sum+=1./(abs(ang-GetAngle(cos(i)*TargetAngle))+invoff);
			}
		}
        if(k<niters){
			double d;
			double S1,S2;
			S1=sum;
			S2=sumold;
			d=IterateFormula.FinalSmoothing();
            res=floor(d);
            d-=res;
           // res=CatRomInterp(S1,S2,S3,S4,d);
            res=d*S1+(1-d)*S2;
		}
		else if (fixed){
			res=sumold;
		}
        else res=1e20;        
        return res;
    }
}

class angle_dev extends Iterator{
    //Formula IterateFormula;
    int niters;
    //double bailout;
    //double px,py;
    //boolean juliamand;
    //boolean dosum;
  //  double TargetAngle;
   // int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public angle_dev(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
	/*	int temp=(int)client.mylayer.GetParam(0);	
		dosum=(temp!=0);
		TargetAngle=client.mylayer.GetParam(1)*PI;		*/
	}
	

    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k,i;
        double res=0;
        double dmin;
        double dmins,dminsold;
        double dx1,dx2,dy1,dy2;
        double m1,m2,sc;
        double ang;
        double sum=0;
        double sum2=0;
        int N=0;
        double ave;
        double aveold;
        ave=aveold=0;
        //IterateFormula=new Formula();
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        for(i=2;i<=k;i++){
			dx1=IterateFormula.GetZr(i)-IterateFormula.GetZr(i-1);
			dx2=IterateFormula.GetZr(i-1)-IterateFormula.GetZr(i-2);
			dy1=IterateFormula.GetZi(i)-IterateFormula.GetZi(i-1);
			dy2=IterateFormula.GetZi(i-1)-IterateFormula.GetZi(i-2);
			sc=dx1*dx2+dy1*dy2;
			m1=sqrt(dx1*dx1+dy1*dy1);
			m2=sqrt(dx2*dx2+dy2*dy2);
			ang=acos(sc/(m2*m1));
			N++;
			sum+=ang;
			sum2+=ang*ang;
			aveold=ave;
			ave=sum2-sum*sum/N;			
		}
        if(k<niters){
			double d;
			double S1,S2;
			S1=ave;
			S2=aveold;
			d=IterateFormula.FinalSmoothing();
            res=floor(d);
            d-=res;
           // res=CatRomInterp(S1,S2,S3,S4,d);
            res=d*S1+(1-d)*S2;
		}
		else if (fixed) res=aveold;
        else res=1e20;        
        return res;
    }
}
  

class triangle extends Iterator{
    int niters;
    boolean fixed;
    int ind1,ind2,ind3;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public triangle(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		int select=(int)client.mylayer.GetParam(0);
        ind1=select;
        ind2=(select+1)%3;
        ind3=(select+2)%3;
	}
	
   
    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<=1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        int k=IterateFormula.Iters();
        double zr,zi,zr1,zi1,zr2,zi2;
        double m,M;
        double []ro=new double[3];
        double S1,S2,S3,S4;
        double res,d;
        S1=S2=S3=S4=0;
        zr1=zi1=zr=zi=0;
        for(int i=0;i<=k;i++){
			zr2=zr1;
			zi2=zi1;
			zr1=zr;
			zi1=zi;
			zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(k>1){
				ro[0]=sqrt(pow(zr-zr1,2)+pow(zi-zi1,2));
				ro[1]=sqrt(pow(zr2-zr1,2)+pow(zi2-zi1,2));
				ro[2]=sqrt(pow(zr-zr2,2)+pow(zi-zi2,2));
				m=abs(ro[ind2]-ro[ind3]);
				M=ro[ind2]+ro[ind3];
				S4=S3;
                S3=S2;
                S2=S1;
                if(M-m>0) S1+=(ro[ind1]-m)/(M-m); 			
			}
		}
		if(k<niters){
            d=IterateFormula.FinalSmoothing();
            res=floor(d);
            d-=res;
           // res=CatRomInterp(S1,S2,S3,S4,d);
            res=d*S1+(1-d)*S2;
            //return S1;
        }
        else if (fixed) res=S3;
        else res=1e20;         
        return res;
    }
}
			

////class curve extends Iterator{
    ////int niters;
    ////boolean fixed;
    ////MandelFrac Client;
    
    ////@Override
    ////public void setN(int N){
		////niters=N;
	////}
	
    ////public curve(MandelFrac client){
        ////niters=client.NIters;
        ////fixed=client.Fixed;
        ////Client=client;
    ////}
    
    ////double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        ////double res=0;
        ////if((d>=0)&&(d<=1)){
            ////res=x0*0.5*(-d*d+d*d*d);
            ////res+=x1*0.5*(d+4*d*d-3*d*d*d);
            ////res+=x2*0.5*(2-5*d*d+3*d*d*d);
            ////res+=x3*0.5*(-d+2*d*d-d*d*d);            
        ////}    
        ////return res;
    ////}
    
    ////double KInterp(double x0,double x1,double x2,double x3,double d){
        ////double res=0;
        ////if((d>=0)&&(d<=1)){
            ////res=x3*(0.5*d*d*d-0.5*d*d-2*d);
            ////res+=x2*(-1.5*d*d*d+2*d*d+0.5*d);
            ////res+=x1*(1.5*d*d*d-2.5*d*d+1);
            ////res+=x0*(-0.5*d*d*d+d*d-0.5*d);            
        ////}    
        ////return res;
    ////}
    
    ////@Override
    ////public double calcPoint(double x, double y) {
		////Formula IterateFormula;
        ////int k;
        ////double res=0;
        ////double zro,zio;
        ////double zroo,zioo;
        ////IterateFormula=Client.GetFormula();
        ////IterateFormula.SetStartingPoint(x,y);
        ////double nR,nI,dR,dI,fR,fI;
        ////double S1,S2,S3,S4;
        ////double d;
        ////S1=S2=S3=S4=0;
        ////zroo=zioo=0;
        ////zro=zio=0;
        ////while(IterateFormula.CheckExit()){
			////zroo=zro;
            ////zioo=zio;
            ////zro=IterateFormula.GetZr();
            ////zio=IterateFormula.GetZi();
			////IterateFormula.Evolve();
			////if(IterateFormula.Iters()>0){
				////nR=IterateFormula.GetZr()-zro;
                ////nI=IterateFormula.GetZi()-zio;
                ////dR=zro-zroo;
                ////dI=zio-zioo;
                ////fR=(nR*dR+nI*dI)/(dR*dR+dI*dI);
                ////fI=(nI*dR-nR*dI)/(dR*dR+dI*dI);
                ////S4=S3;
                ////S3=S2;
                ////S2=S1;
                ////S1+=abs(atan2(fR,fI));      				
			////}
        ////}        
        ////k=IterateFormula.Iters();
        ////if(k<niters){
            ////d=IterateFormula.FinalSmoothing();
            ////d-=floor(d);
            ////res=CatRomInterp(S1,S2,S3,S4,d);
            //////res=S1;
        ////}
        ////else if(fixed) res=S3;
        ////else res=1e20;        
        ////return res;
    ////}
////}

class stripes extends Iterator{
    int density;
    int niters;
    boolean fixed;
   // MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public stripes(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		density=(int)client.mylayer.GetParam(0);
    }
	    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        int k;
		double temp,d;
        Formula IterateFormula;
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        for(int i=1;i<=k;i++){
			res3=res2;
            res2=res1;
            res1=res;
            res+=0.5*sin(density*IterateFormula.GetPhi(i))+0.5;
		}
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
            //res=d*res+(1-d)*res1;
        }
        else if (!fixed) res=1e20;       
        else res=res2;
        return res;
    }
}

class trisin extends Iterator{
    int niters;
  //  double bailout;
  //  double px,py;
  //  boolean juliamand;
    double[] pars;
  //  int falt,fcon;
    boolean fixed;
    //MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	public trisin(){
		  super();
		  pars=new double[6];
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		for(int i=0;i<6;i++) pars[i]=client.mylayer.GetParam(i);
	}
	    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        int k=0;
       double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        double a,b,c,r,al,be,ga;
        double temp,d;
        double zr1,zi1,zr2,zi2;
        double zr,zi;
     
         Formula IterateFormula;
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
        k=IterateFormula.Iters();
        zr=zi=zr1=zr2=zi1=zi2=0;
        res3=res2=res1=res=0;
        for(int i=0;i<=k;i++){
			zr2=zr1;
            zi2=zi1;
            zr1=zr;
            zi1=zi;
            zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>2){
				a=sqrt(pow(zr-zr1,2)+pow(zi-zi1,2));
                b=sqrt(pow(zr2-zr1,2)+pow(zi2-zi1,2));
                c=sqrt(pow(zr-zr2,2)+pow(zi-zi2,2));
                al=acos((a*a-c*c-b*b)/(2*c*b));
                be=acos((b*b-a*a-c*c)/(2*a*c));
                ga=acos((c*c-a*a-b*b)/(2*a*b));
                res3=res2;
                res2=res1;
                res1=res;
                res+=cos(pars[0]*al+pars[1]*be+pars[2]*ga)+cos(pars[3]*al+pars[4]*be+pars[5]*ga);
			}
		}
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
            }
        else if (!fixed) res=1e20; 
        else res=res2;
        return res;
    }
}

class carnot extends Iterator{
    int niters;
	int angle;
	int ind1,ind2,ind3;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public carnot(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		angle=(int)Client.mylayer.GetParam(0);
		ind1=(angle+2)%3;
		ind2=(ind1+1)%3;
		ind3=(ind2+1)%3;
	}
	    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        int k=0;
        Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
      
        double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        double []a=new double[3];
        double temp,d;
        double zr,zi,zr1,zi1,zr2,zi2;
        zr1=zr2=zi1=zi2=zr=zi=0;
        k=IterateFormula.Iters();
        for(int i=0;i<=k;i++){
			zr2=zr1;
			zi2=zi1;
			zr1=zr;
			zi1=zi;
			zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>1){
				a[ind1]=sqrt(pow(zr-zr1,2)+pow(zi-zi1,2));
                a[ind2]=sqrt(pow(zr2-zr1,2)+pow(zi2-zi1,2));
                a[ind3]=sqrt(pow(zr-zr2,2)+pow(zi-zi2,2));
				res3=res2;
                res2=res1;
                res1=res;
                res+=(a[0]*a[0]-a[1]*a[1]-a[2]*a[2])/(2*a[1]*a[2]);
			}
		}
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
            }
        else if (!fixed) res=1e20;    
        else res=res2;
        return res;
    }
}

class diagonal extends Iterator{
    double ex;
    int niters;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public diagonal(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;	
		ex=client.mylayer.GetParam(0);
	}
	    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
		int i;
        int k=IterateFormula.Iters();
        double res=0;
        double[] reso=new double[3];
        reso[0]=0;
        reso[1]=0;
        reso[2]=0;
        double a,b,c,r,al,be,ga;
        double temp,d;
        double[]zro=new double[3];
        double[]zio=new double[3];
        double zr,zi;
        zr=zi=0;
        for(i=0;i<3;i++) zro[i]=zio[i]=0;
        for(i=0;i<=k;i++){
            for(int j=2;j>0;j--){
                zro[j]=zro[j-1];
                zio[j]=zio[j-1];
            }
            zro[0]=zr;
            zio[0]=zi;
            zr=IterateFormula.GetZr(i);
            zi=IterateFormula.GetZi(i);
            if(i>2){
                a=sqrt(pow(zr-zro[1],2)+pow(zi-zio[1],2));
                b=sqrt(pow(zro[0]-zro[2],2)+pow(zio[0]-zio[2],2));
                c=abs((zr-zro[1])*(zro[0]-zro[2])+(zi-zio[1])*(zio[0]-zio[2]));
                 reso[2]=reso[1];
                reso[1]=reso[0];
                reso[0]=res;
                res+=cos(ex*acos(c/(a*b)));     
            }               
        }
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,reso[0],reso[1],reso[2],d);
            res=temp;
            }
        else if (!fixed) res=1e20;   
        else res=reso[1];
        return res;
    }
}


class axis extends Iterator{
    int niters;
    double invoff;
    double xc,yc;
    boolean fixed;
    boolean DoSum;
   // boolean useaverage;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public axis(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        xc=client.mylayer.GetParam(0);
        yc=client.mylayer.GetParam(1);
        invoff=client.mylayer.GetParam(2);
        int temp=(int) client.mylayer.GetParam(3);
        DoSum=(temp!=0);
       // useaverage=((int)client.mylayer.GetParam(3)==1);
	}
    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        int k=0;
        Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
      //  int sig=fcon;
       // boolean goon=true;
        //double res=0;
        double zrol=0;
        double ziol=0;
        double zr,zi;
        zr=zi=0;
        double[] res=new double[4];
        double a,b,c;
        double dist,distmin;
        double d,temp;
        k=IterateFormula.Iters();
        res[0]=res[1]=res[2]=res[3]=0;
        distmin=1e20;
       // if(useaverage) distmin=0;
        for(int i=0;i<=k;i++){
			zrol=zr;
			ziol=zi;
			zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>1){
				a=zr-zrol;
				b=zi-ziol;
				c=(zrol-zr)*(zrol+zr)/2.+(ziol-zi)*(zi+ziol)/2.;
				dist=abs((a*xc+b*yc+c)/sqrt(a*a+b*b));
				//if(!useaverage){
				if(dist<distmin) distmin=dist;
				res[3]=res[2];
				res[2]=res[1];
				res[1]=res[0];
				if(!DoSum) res[0]=1./(distmin+invoff);
				else res[0]+=1./(dist+invoff);
			}
		}
		if(k<niters){
			d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res[0],res[1],res[2],res[3],d);
            res[0]=temp;
            //res[0]=1./(res[0]+off);
		}
		else if(!fixed) res[0]=1e20; 
		else res[0]=res[2];
		return res[0];
    }
}

class Area extends Iterator{
    int niters;
    double angle;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public Area(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        angle=client.mylayer.GetParam(0);      
	}
    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
        int k=IterateFormula.Iters();
        double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        double a,b,c,r,al,be,ga;
        double temp,d;
        double zr1,zi1,zr2,zi2,zr,zi;
		zr=zi=zr1=zr2=zi1=zi2=0;
        for(int i=0;i<=k;i++){
			zr2=zr1;
            zi2=zi1;
            zr1=zr;
            zi1=zi;
			zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>1){
                a=sqrt(pow(zr-zr1,2)+pow(zi-zi1,2));
                b=sqrt(pow(zr2-zr1,2)+pow(zi2-zi1,2));
                c=sqrt(pow(zr-zr2,2)+pow(zi-zi2,2));
                ga=acos((c*c-a*a-b*b)/(2*a*b));
                al=a*b*sin(ga);
                be=a+b+c;
                res3=res2;
                res2=res1;
                res1=res;
                res+=10000*abs(sin(angle*al/(be*10000)));   
            }
        }        
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
            }
        else if (!fixed) res=1e20; 
        else res=res2;
        return res;
    }
}

class bisec extends Iterator{
    int niters;
    double xc,yc,off;
    boolean DoSum;
    int select;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public bisec(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        xc=client.mylayer.GetParam(1);
        yc=client.mylayer.GetParam(2);
        off=client.mylayer.GetParam(3);
        int temp=(int) client.mylayer.GetParam(4);
        DoSum=(temp!=0);
        select=-1;
        if ((int)client.mylayer.GetParam(0)!=0) select=1;
        
	}
	
	   
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
        int k=IterateFormula.Iters();
        double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        double sum=0;
        double a,b,c,A,B,C,af,bf,cf,m,M,af1,bf1,cf1;
        double distmin=1e20;
        double dist;
        double temp,d;
        double zr1,zi1,zr2,zi2,zr,zi;
        zr=zi=zr1=zr2=zi1=zi2=0;
        for(int i=0;i<=k;i++){
            zr2=zr1;
            zi2=zi1;
            zr1=zr;
            zi1=zi;
            zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>1){
                a=zi-zi1;
                b=zr1-zr;
                c=-(zi-zi1)*zr1+(zr-zr1)*zi1;
                A=zi1-zi2;
                B=zr2-zr1;
                C=-(zi1-zi2)*zr2+(zr1-zr2)*zi2;
                m=sqrt(a*a+b*b);
                M=sqrt(A*A+B*B);
                af=M*a+select*m*A;
                bf=M*b+select*m*B;
                cf=M*x+select*m*C;
                dist=abs(af*xc+bf*yc+cf)/sqrt(af*af+bf*bf);
                if(dist<distmin) distmin=dist;
                res3=res2;
                res2=res1;
                res1=res;
                if(DoSum){
					res+=1./(dist+off);
                }
                else{
					res=1./(distmin+off);
				}
				//res=-log(distmin)/log(2.);
            }
            //k++;               
        }
        if(k<niters){
			d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
            //res=1./(res+off);
        }
        else if (!fixed) res=1e20;    
        else res=res2;
        return res;
    }
}

class median extends Iterator{
    int niters;
    double xc,yc,off;
    int select;
    boolean DoSum;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public median(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        xc=client.mylayer.GetParam(1);
        yc=client.mylayer.GetParam(2);
        off=client.mylayer.GetParam(3);
        int temp=(int) client.mylayer.GetParam(4);
        DoSum=(temp!=0);
        select=(int)client.mylayer.GetParam(0);
	}
	    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
         Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
        int k=IterateFormula.Iters();
        double res=0;
        double res1=0;
        double res2=0;
        double res3=0;
        double a,b,c,ZR,ZI;
        double temp,d;
        double distmin=1e20;
        double dist;
        double []zr=new double[3];
        double []zi=new double[3];
        int i;
        for(i=0;i<3;i++) zr[i]=zi[i]=0;
        for(i=0;i<=k;i++){
			zr[2]=zr[1];
            zi[2]=zi[1];
            zr[1]=zr[0];
            zi[1]=zi[0];
            zr[0]=IterateFormula.GetZr(i);
			zi[0]=IterateFormula.GetZi(i);
            if(i>1){
				if(select<3){
					ZR=(zr[select%3]+zr[(select+2)%3])/2.;
					ZI=(zi[select%3]+zi[(select+2)%3])/2.;
					a=zi[(select+1)%3]-ZI;
					b=-zr[(select+1)%3]+ZR;
					c=-a*ZR+b*ZI;
					dist=abs(a*xc+b*yc+c)/sqrt(a*a+b*b);
					if(dist<distmin) distmin=dist;
				}
				else{
					dist=0;
					for(int jj=0;jj<3;jj++){
						ZR=(zr[jj%3]+zr[(jj+2)%3])/2.;
						ZI=(zi[jj%3]+zi[(jj+2)%3])/2.;
						a=zi[(jj+1)%3]-ZI;
						b=-zr[(jj+1)%3]+ZR;
						c=-a*ZR+b*ZI;
						dist+=abs(a*xc+b*yc+c)/sqrt(a*a+b*b);
					}
					if(dist<distmin) distmin=dist;					
				}
                res3=res2;
                res2=res1;
                res1=res;                
                if(!DoSum)res=1./(distmin+off);
                else res+=1./(dist+off);
            }
        }
        if(k<niters){
            d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res,res1,res2,res3,d);
            res=temp;
        }
        else if (!fixed) res=1e20;   
        else res=res2;
        return res;
    }
}

class fakeaxis extends Iterator{
    int niters;
    double xc,yc,off;
    boolean DoSum;
    boolean fixed;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public fakeaxis(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        xc=client.mylayer.GetParam(0);
        off=client.mylayer.GetParam(2);
        yc=client.mylayer.GetParam(1);
        int temp=(int) client.mylayer.GetParam(3);
        DoSum=(temp!=0);
      //  select=(int)client.mylayer.GetParam(0);
	}
	

    
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
        int k=0;
        Formula IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        
      //  int sig=fcon;
       // boolean goon=true;
        //double res=0;
        double zrol=0;
        double ziol=0;
        double zr,zi;
        zr=zi=0;
        double[] res=new double[4];
        double a,b,c;
        double dist,distmin;
        double d,temp;
        k=IterateFormula.Iters();
        res[0]=res[1]=res[2]=res[3]=0;
        distmin=1e20;
       // if(useaverage) distmin=0;
        for(int i=0;i<=k;i++){
			zrol=zr;
			ziol=zi;
			zr=IterateFormula.GetZr(i);
			zi=IterateFormula.GetZi(i);
			if(i>1){
				a=zr-zrol;
				b=ziol-zi;
				c=(zrol-zr)*(zrol+zr)/2.+(zi-ziol)*(zi+ziol)/2.;
				dist=abs((a*xc+b*yc+c)/sqrt(a*a+b*b));
				//if(!useaverage){
				if(dist<distmin) distmin=dist;
				res[3]=res[2];
				res[2]=res[1];
				res[1]=res[0];
				if(!DoSum)res[0]=1./(distmin+off);
				else res[0]+=1./(dist+off);
			}
		}
		if(k<niters){
			d=IterateFormula.FinalSmoothing();
            d-=floor(d);
            temp=CatRomInterp(res[0],res[1],res[2],res[3],d);
            res[0]=temp;
            //res[0]=1./(res[0]+off);
		}
		else if(!fixed) res[0]=1e20; 
		else res[0]=res[2];
		return res[0];
    }    
}

class OrbitDraw extends Iterator{
    OrbitCombiner o;
    int niters;
	boolean fixed;
   // MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public OrbitDraw(){
		super();
	}
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        o=new OrbitCombiner(false);
        o.CloneFrom(client.myorb);        
	}
	
    double CatRomInterp(double x0,double x1,double x2,double x3,double d){
        double res=0;
        if((d>=0)&&(d<1)){
            res=x0*0.5*(-d*d+d*d*d);
            res+=x1*0.5*(d+4*d*d-3*d*d*d);
            res+=x2*0.5*(2-5*d*d+3*d*d*d);
            res+=x3*0.5*(-d+2*d*d-d*d*d);            
        }    
        return res;
    }
    
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
		IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        int k=IterateFormula.Iters();
        int i;
        double[] res=new double[4];
        double d,temp,d1;       
        res[1]=res[2]=res[3]=res[0]=0;
        //br=bi=0;
        if(IterateFormula==null) return 0;
        res[0]=o.getDistance(IterateFormula.GetZr(0),IterateFormula.GetZi(0),0);
        for(i=1;i<=k;i++){
			res[3]=res[2];
			res[2]=res[1];
			res[1]=res[0];
			res[0]=o.getNext(res[0],o.getDistance(IterateFormula.GetZr(i),IterateFormula.GetZi(i),i));
		}
        //if (o.ptype>1){
        if(k<niters){
                d=IterateFormula.FinalSmoothing();
                d-=floor(d);
                //for(i=0;i<4;i++) res[i]/=(double)(k-i);
                temp=CatRomInterp(res[0],res[1],res[2],res[3],d);
                res[0]=temp;
                }
            else if (!fixed)res[0]=1e20;   
            else res[0]=res[2];
    //    }
      /*  else{
           if(k>=niters){
			   if(!fixed) res[0]=1e20;
			   else res[0]=res[2];
		   }           
        }*/
        if (res[0]<1e20){
            if(o.invert){
                res[0]=1./(res[0]/o.redux+o.invoff);
                if(o.revert) res[0]=1./o.invoff-res[0];
            }
            else res[0]/=o.redux;
        }
        if((o.sumNiters)&&(res[0]<1e20)){
            d1=IterateFormula.FinalSmoothing();
            res[0]+=d1/o.NitersFac;
        }
        return res[0];
    }
}




class gauss extends Iterator{
	//Formula IterateFormula;
    int niters;
   // double bailout;
    //double px,py;
    //boolean juliamand;
    //int falt,fcon;
    boolean fixed;
    double RedFactX,RedFactY,RedFactZ;
    boolean dosum;
    double norm;
  //  MandelFrac Client;
    
    @Override
    public void setN(int N){
		niters=N;
	}
	
	public double GetNearestX(double val){
		if(RedFactX==0) return 0;
		double res=floor(val*RedFactX);
		if(val*RedFactX-res>=0.5) res++;
		return res/RedFactX;
	}
	
	public double GetNearestY(double val){
		if(RedFactY==0) return 0;
		double res=floor(val*RedFactY);
		if(val*RedFactY-res>=0.5) res++;
		return res/RedFactY;
	}
	
	public gauss(){
		super();
	}
	
	
	@Override
	public void Specialize(MandelFrac client){
		niters=client.NIters;
		fixed=client.Fixed;
		Client=client;			
        RedFactX=client.mylayer.GetParam(1);
        if(RedFactX==0) RedFactX=1;
        RedFactY=client.mylayer.GetParam(2);
        if(RedFactY==0) RedFactY=1;
        RedFactZ=client.mylayer.GetParam(3);
        if(RedFactZ==0) RedFactZ=1;        //falt=1;
        dosum=((int)client.mylayer.GetParam(0)!=0);
	}
		
	
    @Override
    public double calcPoint(double x, double y) {
		Formula IterateFormula;
        int k;
        double res=0;
        double mindist=50;
        double sumdist=0;
        double dx;
        double dy;
        double dist;
        //IterateFormula=new Formula();
        IterateFormula=Client.GetFormula();
        IterateFormula.SetStartingPoint(x,y);
        IterateFormula.BuildPath();
        k=IterateFormula.Iters();
        if(k<niters || fixed){
			if(dosum){
				 res=0;
				 for(int i=1;i<=k;i++){
					 dx=IterateFormula.GetZr(i)/RedFactZ-GetNearestX(IterateFormula.GetZr(i));
					 dy=IterateFormula.GetZi(i)/RedFactZ-GetNearestY(IterateFormula.GetZi(i));
					 dist=sqrt(dx*dx+dy*dy);
					 res+=dist;
				}
				res/=(double)k;
			}
            else{
				res=1e20;
				for(int i=1;i<=k;i++){
					 dx=IterateFormula.GetZr(i)/RedFactZ-GetNearestX(IterateFormula.GetZr(i));
					 dy=IterateFormula.GetZi(i)/RedFactZ-GetNearestY(IterateFormula.GetZi(i));
					 dist=sqrt(dx*dx+dy*dy);
					 if(res>dist) res=dist;
				}			
			} 
        }        
        else      res=1e20;    
        return res;
	}
}

