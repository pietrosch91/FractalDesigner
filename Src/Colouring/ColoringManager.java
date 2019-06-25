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


class ColoringData{
	//Structure of formula
	String ColoringName; //Name of the formula
	int ParameterCount; //Number of parameter Formula
	String[] ParameterName; //list of names of parameters
	int[] ParameterType; //list of parameters by type (0->Integer,1->Double,2->Complex)
	double[] DefaultR; //default values of parameter (real part)
	double[] MinR;//self explanatory....
	double[] MaxR;
	//Formula formula;
	
	ColoringData(){
		ColoringName="";
		ParameterCount=0;
		ParameterName=new String[30];
		ParameterType=new int[30];
		DefaultR=new double[30]; //default values of parameter (real part)
		MinR=new double[30];//self explanatory....
		MaxR=new double[30];
	}
	
	void SetParameter(int index,String name,int type,double defR,double minR,double maxR){
		if(index<0 || index>=ParameterCount) return;
		ParameterName[index]=name;
		ParameterType[index]=type;
		DefaultR[index]=defR; //default values of parameter (real part)
		MinR[index]=minR;//self explanatory....
		MaxR[index]=maxR;		
	}
	
	
	String GetName(){
		return ColoringName;
	}
	
	void SetName(String newname){
		ColoringName=newname;		
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
	
	int GetPtype(int index){
		if(index<0 || index>=ParameterCount) return -1;
		return ParameterType[index];
	}
	
	void SetPType(int index,int type){
		if(index<0 || index>=ParameterCount) return;
		int nt=type;
		if(nt<0) nt=0;
		if(nt>1) nt=1;
		ParameterType[index]=nt;
	}
	
	double GetDefaultR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return DefaultR[index];
	}
	
	void SetDefaultR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		DefaultR[index]=v;
	}
	
	double GetMinR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MinR[index];
	}
	
	void SetMinR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		MinR[index]=v;
	}
	
	double GetMaxR(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MaxR[index];
	}
	
	void SetMaxR(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		MaxR[index]=v;
	}
	
}





class ColoringManager{
	//Structure of each formula
	int ColoringCount;
	ColoringData[] cdata;
	
	
	
	public int GetColoringCount(){
		return ColoringCount;
	}
	
	public String[] GetListOfNames(){
		String res[]=new String[ColoringCount];
		for(int i=0;i<ColoringCount;i++) res[i]=cdata[i].GetName();
		return res;
	}
	
	public ColoringData GetData(int index){
		if(index<0 || index>=ColoringCount) return new ColoringData();
		return cdata[index];
	}
	
	public ColoringManager(){
		ColoringCount=17;
		cdata=new ColoringData[ColoringCount];
		
		int i=0;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Escape Time");
		cdata[i].SetCount(0);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Phase");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Do sum",0,0,0,1);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Triangle");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Side select",0,0,0,2);	
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Stripes");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Density",0,10,1,100);	
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("TriSin");
		cdata[i].SetCount(6);
		cdata[i].SetParameter(0,"Alfa1",1,1,-100,100);	
		cdata[i].SetParameter(1,"Beta1",1,0,-100,100);	
		cdata[i].SetParameter(2,"Gamma1",1,0,-100,100);	
		cdata[i].SetParameter(3,"Alfa2",1,0,-100,100);	
		cdata[i].SetParameter(4,"Beta2",1,1,-100,100);	
		cdata[i].SetParameter(5,"Gamma2",1,0,-100,100);	
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Carnot");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Angle",0,0,0,2);	
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Axis");
		cdata[i].SetCount(4);
		cdata[i].SetParameter(0,"Center X",1,0,-50,50);	
		cdata[i].SetParameter(1,"Center Y",1,0,-50,50);	
		cdata[i].SetParameter(2,"Offset",1,0.01,0,1);	
		cdata[i].SetParameter(3,"DoSum",0,0,0,1);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("FakeAxis");
		cdata[i].SetCount(4);
		cdata[i].SetParameter(0,"Center X",1,0,-50,50);	
		cdata[i].SetParameter(1,"Center Y",1,0,-50,50);	
		cdata[i].SetParameter(2,"Offset",1,0.01,0,1);	
		cdata[i].SetParameter(3,"DoSum",0,0,0,1);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Area");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Scaling",1,1,-100,100);		
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Bisector");
		cdata[i].SetCount(5);
		cdata[i].SetParameter(0,"Select",0,0,0,1);
		cdata[i].SetParameter(1,"Center X",1,0,-50,50);	
		cdata[i].SetParameter(2,"Center Y",1,0,-50,50);	
		cdata[i].SetParameter(3,"Offset",1,0.01,0,1);	
		cdata[i].SetParameter(4,"DoSum",0,0,0,1);	
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Median");
		cdata[i].SetCount(5);
		cdata[i].SetParameter(0,"Select",0,0,0,3);
		cdata[i].SetParameter(1,"Center X",1,0,-50,50);	
		cdata[i].SetParameter(2,"Center Y",1,0,-50,50);	
		cdata[i].SetParameter(3,"Offset",1,0.01,0,1);	
		cdata[i].SetParameter(4,"DoSum",0,0,0,1);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Gauss");
		cdata[i].SetCount(4);
		cdata[i].SetParameter(0,"Do Sum",0,0,0,1);
		cdata[i].SetParameter(1,"Scale X",1,1,0,100);	
		cdata[i].SetParameter(2,"Scale Y",1,1,0,100);	
		cdata[i].SetParameter(3,"Scale R",1,1,-10,10);			
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Diagonal");
		cdata[i].SetCount(1);
		cdata[i].SetParameter(0,"Scaling",1,1,-100,100);		
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Orbit");
		cdata[i].SetCount(0);		
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Angle");
		cdata[i].SetCount(4);
		cdata[i].SetParameter(0,"Do Sum",0,0,0,3);
		cdata[i].SetParameter(1,"Target Angle",1,0,0,2);
		cdata[i].SetParameter(2,"Invert",0,0,0,1);
		cdata[i].SetParameter(3,"Invert Offset",1,0.1,0,100);
		
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Angle_Dev");
		cdata[i].SetCount(0);
		
		i++;
		cdata[i]=new ColoringData();
		cdata[i].SetName("Quadrant Count");
		cdata[i].SetCount(0);
		
	}
	
	
	public Iterator GenerateIterator(int type){
		Iterator res;
		switch (type){
			case 0: 
				return new std();
			case 1:
				return new phase();
			case 2:
				return new triangle();
			case 3:
				return new stripes();
			case 4:
				return new trisin();
			case 5:
				return new carnot();
			case 6:
				return new axis();
			case 7:
				return new fakeaxis();
			case 8:
				return new Area();
			case 9:
				return new bisec();			
			case 10:
				return new median();	
			case 11:
				return new gauss();	
			case 12:
				return new diagonal();						
			case 13:
				return new OrbitDraw();		
			case 14:
				return new angle();		
			case 15:
				return new angle_dev();		
			case 16:
				return new qcount();		
			default:
				return new std();
				//break;
		}
	}	
}
