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


class OrbitData{
	String OrbitName;
	double[] DefaultR; //default values of parameter (real part)
	double[] MinR;//self explanatory....
	double[] MaxR;
	double[] StepR;
	int ParameterCount; //Number of parameter Formula
	String[] ParameterName; //list of names of parameters
	
	
	OrbitData(){
		OrbitName="";
		ParameterCount=0;
		ParameterName=new String[30];
		DefaultR=new double[30]; //default values of parameter (real part)
		MinR=new double[30];//self explanatory....
		MaxR=new double[30];
		StepR=new double[30];
	}
	
	
	void SetParameter(int index,String name,double defR,double minR,double maxR,double stepR){
		if(index<0 || index>=ParameterCount) return;
		ParameterName[index]=name;
		DefaultR[index]=defR; //default values of parameter (real part)
		MinR[index]=minR;//self explanatory....
		MaxR[index]=maxR;
		StepR[index]=stepR;
	}
	
	String GetName(){
		return OrbitName;
	}
	
	void SetName(String newname){
		OrbitName=newname;		
	}
	
	int GetCount(){
		return ParameterCount;
	}
	
	void SetCount(int newcount){
		ParameterCount=newcount;
		if(ParameterCount>10) ParameterCount=10;
	}
	
	String GetPName(int index){
		if(index<0 || index>=ParameterCount) return "";
		return ParameterName[index];
	}
	
	void SetPName(int index,String name){
		if(index<0 || index>=ParameterCount) return ;
		ParameterName[index]=name;
	}
	
	double GetDefaultR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return DefaultR[index];
	}
	
	void SetDefaultR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		DefaultR[index]=v;
	}
	
	double GetMinR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MinR[index];
	}
	
	void SetMinR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		MinR[index]=v;
	}
	
	double GetMaxR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MaxR[index];
	}
	
	void SetMaxR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		MaxR[index]=v;
	}
	
	double GetStepR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return StepR[index];
	}
	
	void SetStepR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		StepR[index]=v;
	}
}



class OrbitManager{
	//Structure of each formula
	int OrbitsCount;
	OrbitData[] fdata;
	
	
	
	public int GetOrbitsCount(){
		return OrbitsCount;
	}
	
	public String[] GetListOfNames(){
		String res[]=new String[OrbitsCount];
		for(int i=0;i<OrbitsCount;i++) res[i]=fdata[i].GetName();
		return res;
	}
	
	public OrbitData GetData(int index){
		if(index<0 || index>=OrbitsCount) return new OrbitData();
		return fdata[index];
	}
	
	public OrbitManager(){
	//	FormulaCount=17;
		fdata=new OrbitData[400];
		
		int i=0;
		//Point
		fdata[i]=new OrbitData();
		fdata[i].SetName("Point");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"X",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"Y",0,-1000,1000,0.1);
		i++;
		
		//Line
		fdata[i]=new OrbitData();
		fdata[i].SetName("Line");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"X0",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"Y0",0,-1000,1000,0.1);
		fdata[i].SetParameter(2,"Slope Angle",0,-1,1,0.25);
		i++;
		
		//Circle
		fdata[i]=new OrbitData();
		fdata[i].SetName("Circle");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"CX",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"CY",0,-1000,1000,0.1);
		fdata[i].SetParameter(2,"Radius",1,0,1000,0.25);		
		i++;
		
		//Circle Arc
		fdata[i]=new OrbitData();
		fdata[i].SetName("Circle Arc");
		fdata[i].SetCount(5);
		fdata[i].SetParameter(0,"CX",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"CY",0,-1000,1000,0.1);
		fdata[i].SetParameter(2,"Radius",1,0,1000,0.25);		
		fdata[i].SetParameter(3,"Theta Min",0,-1,1,0.25);	
		fdata[i].SetParameter(4,"Theta Max",1,-1,1,0.25);	
		i++;
		
		//Conic
		fdata[i]=new OrbitData();
		fdata[i].SetName("Conic");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"FX",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"FY",0,-1000,1000,0.1);
		fdata[i].SetParameter(2,"DX0",0,-1000,1000,0.1);
		fdata[i].SetParameter(3,"DY0",0,-1000,1000,0.1);
		fdata[i].SetParameter(4,"DSlope Angle",0,-1,1,0.25);
		fdata[i].SetParameter(5,"Eccentricity",1,0,1000,0.25);
		i++;
		
		//Segment
		fdata[i]=new OrbitData();
		fdata[i].SetName("Segment");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"X1",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"Y1",0,-1000,1000,0.1);		
		fdata[i].SetParameter(2,"X2",0,-1000,1000,0.1);
		fdata[i].SetParameter(3,"Y2",0,-1000,1000,0.1);
		i++;
		
		//Spiral
		fdata[i]=new OrbitData();
		fdata[i].SetName("Spiral");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"CX",0,-1000,1000,0.1);
		fdata[i].SetParameter(1,"CY",0,-1000,1000,0.1);		
		fdata[i].SetParameter(2,"Phase",0,-1,1,0.25);
		fdata[i].SetParameter(3,"Branches",0,-1000,1000,1);
		fdata[i].SetParameter(4,"Density",0,-1000,1000,0.1);
		fdata[i].SetParameter(5,"Gain",1,0,1000,0.25);
		i++;
		
		OrbitsCount=i;
	}
	
	
	public Trap GenerateOrbit(int type){
		Trap res;
		switch (type){
			case 0:
				return new TPoint();
			//	break;
			case 1:
				return new TLine();
				//break;
			case 2:
				return new Circle();
				//break;
			case 3:
				return new Arc();
				//break;
			case 4:
			    return new Conic();
			case 5:
			    return new TSegment();
			case 6:
				return new Spiral();
				//	break;
			default:
				return new TPoint();
				//break;
		}
	}		
}

