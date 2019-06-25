import javax.imageio.ImageIO;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

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
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;


public class FracPreviewer {
	//Control variables
	int NIters;
	double BailOut;
	boolean JuliaMand;
	boolean Alternate;
	boolean Conjugate;
	boolean Fixed;
	boolean ForceRes;
	int ForcedW,ForcedH;
	double px,py;
	
	//Image frame variables
	double XUL,YUL,XDR,YDR,Cx,Cy,Pdim;
	//Label Frame variables
	double XUL_L,YUL_L,XDR_L,YDR_L;
	
	JLabel PicLabel;
	
	OrbitCombiner myorb;
	Layer mylayer;
	//DrawManager fracdrawer;
	MandelFrac Parent;
	
	int[] status;
	int nth;
    int next;
    int stopvar,endvar,repvar; //pointer to ext variables; 

	int Width,Height; //of the label
	int imgW,imgH;// of the image
    int[] Pixels;
    double[] Matrix;
    BufferedImage pic;
    ImageIcon picIco;
    Iterator iter;
    
    FracPreviewer(JLabel pLab,MandelFrac p){
		Parent=p;
		PicLabel=pLab;
		//Start set
		NIters=100;
		BailOut=100;
		JuliaMand=Alternate=Conjugate=Fixed=false;	
		ForceRes=false;
		ForcedW=1920;
		ForcedH=1080;
		Cx=-0.5;
		Cy=0;
		//dimension of pic
		Width=PicLabel.getWidth();
		Height=PicLabel.getHeight();
		imgW=Width;
		imgH=Height;
		ResetJuliaMand();
		pic=new BufferedImage(imgW,imgH,TYPE_INT_ARGB_PRE);
		Matrix=new double[imgW*imgH];
		Pixels=new int[imgW*imgH];
        picIco=new ImageIcon();
        iter=new std(Parent);
        status=new int[imgH];        
        //ResizeGraphics();
		//fracdrawer=new DrawManager(this);
		
		//fracdrawer.start();
    }
	
	public void ResetJuliaMand(){
		px=py=0;
		if(JuliaMand) px=0.26;
		YUL=1.5;
		YDR=-1.5;
		XUL=-2;
		XDR=1;
		if(JuliaMand){
			XUL=-1.5;
			XDR=1.5;
		}
		GetCpDfromCorners();
		GetCornersfromCpD();
		//UpdateLabelCorners();
	}
	
	
//Update routines to draw picture in label
    public void Update(){
		picIco.setImage(pic);
        PicLabel.setIcon(picIco);
		PicLabel.updateUI();
   }
    
//Color Update           
    public void UpdateLine (int iline){
        if ((iline<imgH)&&(iline>=0)){
            pic.setRGB(0, iline, imgW,1,Pixels,iline*imgW,imgW);
            status[iline]=2;
        }        
    }

//Pixel Calculations
	private double GetX(int i){
        return XUL+(double)i/imgW*(XDR-XUL);
    }
   
    private double GetY(int i){
        return YUL+(double)i/imgH*(YDR-YUL);
    }
    
    public void DrawLine (int iline){
        int j,n;
        if ((iline<imgH)&&(iline>=0)){
            for(j=0;j<imgW;j++){
                Matrix[iline*imgW+j]=iter.calcPoint(GetX(j),GetY(iline));
                //n=theLayer.GetColor(Matrix[iline*w+j]);
                Pixels[iline*imgW+j]=mylayer.GetColor(Matrix[iline*imgW+j]);
            }
            status[iline]=1;
        }
    }
    
    public void RecolorLine (int iline){
        int j,n;
        if ((iline<imgH)&&(iline>=0)){
            for(j=0;j<imgW;j++){
                //Matrix[iline*Width+j]=iter.calcPoint(GetX(j),GetY(iline));
                //n=theLayer.GetColor(Matrix[iline*w+j]);
                Pixels[iline*imgW+j]=mylayer.GetColor(Matrix[iline*imgW+j]);
            }
            status[iline]=1;
        }
    }
    
    
//Draw thread management
//Stop Draw Command
	public void StopDraw(){
		/*fracdrawer.active=false; //should stop everything
		while(true){
			try{
				Thread.sleep(100);
			}catch (InterruptedException e){}
			if(fracdrawer.is_clear){
				Update();
				return;
			}
		}*/		
	}
		
//Init Draw Command		 
	public void initDraw(){
	/*	//allocate image
		imgW=PicLabel.getWidth();
		imgH=PicLabel.getHeight();
		if(imgW==0 || imgH==0) return;
		GetCpDfromCorners();
		GetCornersfromCpD();
		if(imgW!=pic.getWidth() || imgH!=pic.getHeight()){
			pic=new BufferedImage(imgW,imgH,TYPE_INT_ARGB_PRE);
			Matrix=new double[imgW*imgH];
			Pixels=new int[imgW*imgH];
		}
		
        iter=null;
        switch (mylayer.cind){
            case 0://normal mode
                iter=new std(Parent);
                break;
            case 1://phase mode
                iter=new phase(Parent);
                break;
            case 2://triangle mode
                iter=new triangle(Parent);
                break;
            case 3 ://curve mode
                iter=new curve(Parent);
                break;
            case 4 ://stripes mode
                iter=new stripes(Parent);
                break;
            case 5 ://trisin mode
                iter=new trisin(Parent);
                break;
            case 6 ://carnot mode
                iter=new carnot(Parent);
                break;
            case 7://diagonal mode
                 iter=new diagonal(Parent);
                break;
            case 8://axis mode
                 iter=new axis(Parent);
                break;
            case 9://area mode
                 iter=new Area(Parent);
                 break;
            case 10://bisector mode
                iter=new bisec(Parent);
                break;
            case 11://median mode
                iter=new median(Parent);
                break;
            case 12://fakeaxis
                iter=new fakeaxis(Parent);
                break;
            case 13://Orbit
                iter=new OrbitDraw(Parent);
                break;
            case 14://Experimental
                iter=new Experimental(Parent);
                break;
            default:
                iter=new std(Parent);
                break;
        }
        iter.setN(100);
		//set parameters of thread
		status=new int[imgH];
		for(int i=0;i<imgH;i++) status[i]=0;
		fracdrawer.SetNLines(imgH,0);
		fracdrawer.coloronly=false;
		fracdrawer.active=true;//StartDraw here	*/	
	}
	
	public void initUpdate(){
	/*	//allocate image
		fracdrawer.SetNLines(imgH,0);
		fracdrawer.coloronly=true;
		fracdrawer.active=true;//StartDraw here		*/
	}
	
	public void GetCpDfromCorners(){
        Cx=(XUL+XDR)/2;
        Cy=(YUL+YDR)/2;
        double aspectRatio=(double)imgH/(double)imgW;
        if (((YUL-YDR)/(XDR-XUL))>aspectRatio) Pdim=(YUL-YDR)/((double)imgH-1);
        else Pdim=(XDR-XUL)/((double)imgW-1);        
    }
    
    public void GetCornersfromCpD(){
        XUL=Cx-Pdim*((double)imgW-1)/2;
        XDR=Cx+Pdim*((double)imgW-1)/2;
        YUL=Cy+Pdim*((double)imgH-1)/2;
        YDR=Cy-Pdim*((double)imgH-1)/2;
    }
    
}


