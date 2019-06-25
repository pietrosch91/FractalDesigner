import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.atan2;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE;
import static java.lang.Math.pow;
import javax.swing.ImageIcon;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import javax.swing.border.*;

class Trap{
	double Count;
	double[] Param_start;
	double[] Param_speed;
	double[] Param_Actual;
	
	public void setIsConv(){
	}
	
	
	public double getDist(double x,double y){
		return 0;
	}
	
	public void UpdateParams(int nIter,boolean IsConv){
		for(int i=0;i<Count;i++) Param_Actual[i]=Param_start[i]+nIter*Param_speed[i];
		regen();
		if(IsConv) setIsConv();
	}
	
	public void UpdateValues(int index,double Val,double Sp){
	 if(index<0 || index>=Count) return;
	 Param_speed[index]=Sp;
	 Param_start[index]=Val;
	}
	
	public Trap(int c){
		Count=c;
		Param_start=new double[c];
		Param_speed=new double[c];
		Param_Actual=new double[c];
	}	
	
	public void regen(){};
}

class TPoint extends Trap{
    double X;
    double Y;
    
    public TPoint(){
       super(2);
    }  

    @Override
    public double getDist(double x, double y) {
       return sqrt(pow(x-X,2)+pow(y-Y,2));
	}
	
	@Override
	public void regen(){
		X=Param_Actual[0];
		Y=Param_Actual[1];	
	}
}

class TLine extends Trap{
    double a,b,c;
    double norm;
    
    public TLine(){
		super(3);
	}
	
    @Override
    public void regen(){
        a=sin(Param_Actual[2]*PI);
        b=-cos(Param_Actual[2]*PI);
        c=-a*Param_Actual[0]-b*Param_Actual[1];
        norm=sqrt(a*a+b*b);
    }

    @Override
    public double getDist(double x, double y) {
       return abs(a*x+b*y+c)/norm;
    }

}

class Circle extends Trap{
    double xc,yc,r;
    
    public Circle(){
        super(3);
    }
    
    @Override
    public void regen(){
        xc=Param_Actual[0];
        yc=Param_Actual[1];
        r=Param_Actual[2];
    }
    
    @Override
    public double getDist(double x, double y) {
        return abs(sqrt(pow(x-xc,2)+pow(y-yc,2))-r);
    }    
}

class Arc extends Trap{
	double xc,yc,r,ThetaMin,ThetaMax;	
	boolean wraparound;
	
	public Arc(){
        super(5);
    }
    
    @Override
	public void regen(){
        xc=Param_Actual[0];
        yc=Param_Actual[1];
        r=Param_Actual[2];
        ThetaMin=Param_Actual[3]*PI;
        while(ThetaMin>PI) ThetaMin-=2*PI;
        while(ThetaMin<-PI) ThetaMin+=2*PI;
        ThetaMax=Param_Actual[4]*PI;
        while(ThetaMax>PI) ThetaMax-=2*PI;
        while(ThetaMax<-PI) ThetaMax+=2*PI;   
        if(ThetaMin>ThetaMax) wraparound=true;    
        else wraparound=false; 
    }
    
     @Override
    public double getDist(double x, double y) {
		double Theta=atan2(y-yc,x-xc);
		double d1,d2;
		if(wraparound){
			if(Theta>=ThetaMin || Theta<=ThetaMax){
				return abs(sqrt(pow(x-xc,2)+pow(y-yc,2))-r);
			}
			else{
				d1=pow(xc+r*cos(ThetaMin)-x,2)+pow(yc+r*sin(ThetaMin)-y,2);
				d2=pow(xc+r*cos(ThetaMax)-x,2)+pow(yc+r*sin(ThetaMax)-y,2);
				if(d1>d2) return sqrt(d2);
				return sqrt(d1);
			}
		}
		else{
			if(Theta>=ThetaMin && Theta<=ThetaMax){
				return abs(sqrt(pow(x-xc,2)+pow(y-yc,2))-r);
			}
			else{
				d1=pow(xc+r*cos(ThetaMin)-x,2)+pow(yc+r*sin(ThetaMin)-y,2);
				d2=pow(xc+r*cos(ThetaMax)-x,2)+pow(yc+r*sin(ThetaMax)-y,2);
				if(d1>d2) return sqrt(d2);
				return sqrt(d1);
			}
		}
	}    
}
    
class Conic extends Trap{
    double xf,yf;
    double a,b,c,norm;
    double e;
    
    public Conic(){
       super(6);
    }
    
    @Override
    public void regen(){
        xf=Param_Actual[0];
        yf=Param_Actual[1];
        a=sin(Param_Actual[4]*PI);
        b=-cos(Param_Actual[4]*PI);
        c=-a*Param_Actual[2]-b*Param_Actual[3];
        norm=sqrt(a*a+b*b);
        e=Param_Actual[5];
    }
    
    @Override
    public double getDist(double x, double y) {
        return abs(sqrt(pow(x-xf,2)+pow(y-yf,2))-e*abs(a*x+b*y+c)/norm);
    }
}

class TSegment extends Trap{
    double a,b;
    double norm;
    double a1,b1,a2,b2;
    double x1,x2,y1,y2;
    
    public TSegment(){
        super(4);
    }
    
    @Override
    public void regen(){
        x1=Param_Actual[0];
        y1=Param_Actual[1];
        x2=Param_Actual[2];
        y2=Param_Actual[3];
        a=y2-y1;
        b=x2-x1;
        a2=b;
        b2=a;
        a1=-b;
        b1=-a;
        b=-b;
        norm=sqrt(a*a+b*b);
    }

    @Override
    public double getDist(double x, double y) {
       if (a1*(x-x1)+b1*(y-y1)>0) return sqrt(pow(x-x1,2)+pow(y-y1,2));
       if (a2*(x-x2)+b2*(y-y2)>0) return sqrt(pow(x-x2,2)+pow(y-y2,2));
       return abs(a*(x-x1)+b*(y-y1))/norm;
    }

}

class Spiral extends Trap{
    double cx,cy;
    double branches;
    double Density;
    double phase;
    double Mag;
    double decay;
    
    @Override 
    public void setIsConv(){
		decay=0;
	}
	 
    public Spiral(){
		super(6);
	}
    
    @Override
    public void regen(){
        cx=Param_Actual[0];
        cy=Param_Actual[1];
        phase=Param_Actual[2]*PI;
        branches=Param_Actual[3];
        Density=Param_Actual[4]*2*PI;
        Mag=Param_Actual[5];
        decay=1;
    }

    @Override
    public double getDist(double x, double y) {
		double X=x-cx;
		double Y=y-cy;
		double D=sqrt(X*X+Y*Y);
		double T=atan2(Y,X);
		if(decay!=0) return D*(1-pow(0.5+cos(branches*(T+phase)+Density*D)/2.,Mag)*pow(2,-D/decay));
		else return D*(1-pow(0.5+cos(branches*(T+phase)+Density*D)/2.,Mag));
    }
}

