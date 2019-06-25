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


class FormulaData{
	//Structure of formula
	String FormulaName; //Name of the formula
	int ParameterCount; //Number of parameter Formula
	String[] ParameterName; //list of names of parameters
	int[] ParameterType; //list of parameters by type (0->Integer,1->Double,2->Complex)
	double[] DefaultR; //default values of parameter (real part)
	double[] DefaultI; //default values of parameter (imaginary part)
	double[] MinR;//self explanatory....
	double[] MaxR;
	double[] MinI;
	double[] MaxI;
	
	int SpecialCount;
	String SpecialName;
	boolean has_Special;
	String Special_def;
	
	double XUL,XDR,YUL,YDR; //default corner coordinates
	//Formula formula;
	
	FormulaData(){
		FormulaName="";
		ParameterCount=0;
		ParameterName=new String[30];
		ParameterType=new int[30];
		DefaultR=new double[30]; //default values of parameter (real part)
		DefaultI=new double[30]; //default values of parameter (imaginary part)
		MinR=new double[30];//self explanatory....
		MaxR=new double[30];
		MinI=new double[30];
		MaxI=new double[30];
		has_Special=false;
		Special_def="";
		SpecialName="";
		SpecialCount=0;
		XUL=XDR=YUL=YDR=0;		
	}
	
	
	void SetParameter(int index,String name,int type,double defR,double minR,double maxR,double defI,double minI,double maxI){
		if(index<0 || index>=ParameterCount) return;
		ParameterName[index]=name;
		ParameterType[index]=type;
		DefaultR[index]=defR; //default values of parameter (real part)
		DefaultI[index]=defI; //default values of parameter (imaginary part)
		MinR[index]=minR;//self explanatory....
		MaxR[index]=maxR;
		MinI[index]=minI;
		MaxI[index]=maxI;
	}
	
	void SetParameter(int index,String name,int type,double DefR,double MinR,double MaxR){
		SetParameter(index,name,type,DefR,MinR,MaxR,0,0,0);
	}
	
	void SetDefaultView(double xul,double yul,double xdr,double ydr){
		XUL=xul;
		YUL=yul;
		XDR=xdr;
		YDR=ydr;
	}
	
/*	Formula GetFormula(){
		return formula;
	}
	
	void SetFormula(Formula baseF){
		formula=baseF;
	}*/
	boolean HasSpecial(){
		return has_Special;
	}
	
	void SetSpecial(boolean vv){
		has_Special=vv;
	}
	
	String GetSpecialDefault(){
		if(!has_Special)return "";
		return Special_def;
	}
	
	void SetSpecialDefault(String s){
		Special_def=s;
	}
	
	String GetSpecialName(){
		if(!has_Special)return "";
		return SpecialName;
	}
	
	void SetSpecialName(String s){
		SpecialName=s;
	}
	
	int GetSpecialCount(){
		return SpecialCount;
	}
	
	void SetSpecialCount(int cc){
		SpecialCount=cc;
	}
	
	String GetName(){
		return FormulaName;
	}
	
	void SetName(String newname){
		FormulaName=newname;		
	}
	
	int GetCount(){
		return ParameterCount;
	}
	
	void SetCount(int newcount){
		ParameterCount=newcount;
		if(ParameterCount>12) ParameterCount=12;
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
		if(nt>2) nt=2;
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
	
	double GetDefaultI(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return DefaultI[index];
	}
	
	void SetDefaultI(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		DefaultI[index]=v;
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
	
	double GetMinI(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MinI[index];
	}
	
	void SetMinI(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		MinI[index]=v;
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
	
	double GetMaxI(int index){
		if(index<0 || index>=ParameterCount) return 0;
		return MaxI[index];
	}
	
	void SetMaxI(int index,double Value){
		if(index<0 || index>=ParameterCount) return;
		double v=Value;
		if(ParameterType[index]==0) v=(int)v;		
		MaxI[index]=v;
	}
	
	double GetXUL(){
		return XUL;
	}
	
	void SetXUL(double value){
		XUL=value;
	}
	
	double GetXDR(){
		return XDR;
	}
	
	void SetXDR(double value){
		XDR=value;
	}
	
	double GetYUL(){
		return YUL;
	}
	
	void SetYUL(double value){
		YUL=value;
	}
	
	double GetYDR(){
		return YDR;
	}
	
	void SetYDR(double value){
		YDR=value;
	}
}





class FormulaManager{
	//Structure of each formula
	int FormulaCount;
	FormulaData[] fdata;
	
	
	
	public int GetFormulaCount(){
		return FormulaCount;
	}
	
	public String[] GetListOfNames(){
		String res[]=new String[FormulaCount];
		for(int i=0;i<FormulaCount;i++) res[i]=fdata[i].GetName();
		return res;
	}
	
	public FormulaData GetData(int index){
		if(index<0 || index>=FormulaCount) return new FormulaData();
		return fdata[index];
	}
	
	public FormulaManager(){
	//	FormulaCount=17;
		fdata=new FormulaData[400];
		
		int i=0;
		//Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Mandelbrot");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		i++;
		//Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Julia");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[1].SetFormula(new Julia());		
		/*
		i++;
		//Cubic Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Cubic Mandelbrot");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[2].SetFormula(new Mandelbrot());
		
		i++;
		//Cubic Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Cubic Julia");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[3].SetFormula(new Julia());		
		*/
		i++;
		//General Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Mandelbrot General");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//General Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Julia General");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//LPM
		fdata[i]=new FormulaData();
		fdata[i].SetName("LyMaJu");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		//fdata[i].SetParameter(2,"Starting",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
		
		i++;
		//General LPMJ
		fdata[i]=new FormulaData();
		fdata[i].SetName("LyMaJu General");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		//fdata[i].SetParameter(2,"Starting",2,0,-2,2,0,-2,2);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix Mandelbrot");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[4].SetFormula(new Mandelbrot());
		
		i++;
		//Phoenix Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix Julia");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[5].SetFormula(new Julia());		
		
		/*i++;
		//Cubic Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Cubic Phoenix Mandelbrot");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[2].SetFormula(new Mandelbrot());
		
		i++;
		//Cubic Phoenix Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Cubic Phoenix Julia");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[3].SetFormula(new Julia());		*/
		
		i++;
		//General Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix Mandelbrot General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//General Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix Julia General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Phoenix LPMJ
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix LPMJ");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[5].SetFormula(new Julia());		
		
		i++;
		//General Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Phoenix LPMJ General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
	/*	i++;
		//Divergence Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Divergence Mandelbrot");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Delta",2,0.1,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"Scale",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//Divergence Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Divergence Julia");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Delta",2,0.1,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"Scale",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		*/
		
		i++;
		fdata[i]=new FormulaData();
		fdata[i].SetName("Burning Ship (Both)");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
/*		i++;
		fdata[i]=new FormulaData();
		fdata[i].SetName("Burning Ship (Real)");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		i++;
		fdata[i]=new FormulaData();
		fdata[i].SetName("Burning Ship (Imag)");
		fdata[i].SetCount(2);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());*/
		
		i++;
		//General Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Burning Ship General");
		fdata[i].SetCount(6);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);		
		
		i++;
		//Glynn Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn Julia");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent",1,1.5,1,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Glynn Julia phoenix
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn Julia Phoenix");
		fdata[i].SetCount(5);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent",1,1.5,0,10);
		fdata[i].SetParameter(3,"Feedback",2,0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(4,"Feed Exponent",1,1.5,0,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
	/*	i++;
		//Glinn 2 Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn2 Mandelbrot");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation 1",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent 1",1,1.5,1,10);
		//fdata[i].SetParameter(3,"Perturbation 2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"Exponent 2",1,1,1,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);*/
		
		i++;
		//Glinn 2 Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn2 Julia");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation 1",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent 1",1,1.5,0,10);
		//fdata[i].SetParameter(3,"Perturbation 2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"Exponent 2",1,1,0,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
	
		
		i++;
		//Glynn Lyap
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn Julia Lyapunov");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation A",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Exponent A",1,1.5,0,10);
		fdata[i].SetParameter(3,"Perturbation B",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(4,"Exponent B",1,1,0,10);
		fdata[i].SetParameter(5,"Perturbation C",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(6,"Exponent C",1,1,0,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(3);
		//fdata[1].SetFormula(new Julia());	
		
		i++;
		//General Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Rotational Mandelbrot General");
		fdata[i].SetCount(8);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(7,"Rotation",1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//General Phoenix Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Rotational Julia General");
		fdata[i].SetCount(8);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Feedback",2,-0.5,-2,2,0,-2,2);
		fdata[i].SetParameter(3,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(7,"Rotation",1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Newton_alt
		fdata[i]=new FormulaData();
		fdata[i].SetName("Newton General");
		fdata[i].SetCount(8);
		fdata[i].SetParameter(0,"Bailin",1,0.01,0,1);
		fdata[i].SetParameter(1,"Feedback",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"C0",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C1",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(7,"C5",2,0,-10,10,0,-10,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
	/*	i++;
		//Glynn Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Glynn Mandelbrot");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent",1,1.5,1,10);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);*/
		
			
		
	/*	i++;
		//Mandelbrot Rotation
		fdata[i]=new FormulaData();
		fdata[i].SetName("Mandelbrot Rotation");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Rotation",1,0,-1,1);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[1].SetFormula(new Julia());		
		
		i++;
		//Mandelbrot Rotation
		fdata[i]=new FormulaData();
		fdata[i].SetName("Julia Rotation");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Rotation",1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[1].SetFormula(new Julia());		
		*/
		
		
		
			i++;
		//Newton_alt
		fdata[i]=new FormulaData();
		fdata[i].SetName("Rotational Newton General");
		fdata[i].SetCount(12);
		fdata[i].SetParameter(0,"Bailin",1,0.01,0,1);
		fdata[i].SetParameter(1,"Feedback",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"C0",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C1",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C3",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"C4",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(7,"C5",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(8,"C6",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(9,"C7",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(10,"Rotation",1,0,-1,1);
		fdata[i].SetParameter(11,"Rotation Sum",0,0,0,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
			i++;
		//Lambda
		fdata[i]=new FormulaData();
		fdata[i].SetName("Lambda");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-2,2,0,-2,2);
		fdata[i].SetParameter(2,"Theta",1,0,0,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Invert Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("InvMandelbrot");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Invert Weight",1,1,-5,5);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		i++;
		//Invert Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("InvJulia");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Invert Weight",1,1,-5,5);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Inv General Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Inv Mandelbrot General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"Invert Weight",1,1,-5,5);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//Inv General Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Inv Julia General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"Invert Weight",1,1,-5,5);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//InvLPM
		fdata[i]=new FormulaData();
		fdata[i].SetName("LyMaJu Inv");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Invert Weight",1,1,-5,5);
		//fdata[i].SetParameter(2,"Starting",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
		
		i++;
		//InvGeneral LPMJ
		fdata[i]=new FormulaData();
		fdata[i].SetName("LyMaJu General Inv");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		//fdata[i].SetParameter(2,"Starting",2,0,-2,2,0,-2,2);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"Invert Weight",1,1,-5,5);		
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);		
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//InvGlynn Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Inv Glynn");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent",1,1.5,1,10);
		fdata[i].SetParameter(3,"Invert Weight",1,1,-5,5);				
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Glinn 2 Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Inv Glynn2 Julia");
		fdata[i].SetCount(5);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation 1",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent 1",1,1.5,0,10);
		//fdata[i].SetParameter(3,"Perturbation 2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"Exponent 2",1,1,0,10);
		fdata[i].SetParameter(4,"Invert Weight",1,1,-5,5);				
		
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Diff Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Diff Mandelbrot");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
	
		i++;
		//Diff Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Diff Julia");
		fdata[i].SetCount(3);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		i++;
		//General Mandelbrot Diff
		fdata[i]=new FormulaData();
		fdata[i].SetName("Mandelbrot DIff General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-2,1.5,1.,-1.5);
		
		i++;
		//General Julia Diff
		fdata[i]=new FormulaData();
		fdata[i].SetName("Julia Diff General");
		fdata[i].SetCount(7);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"C1",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(3,"C2",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(4,"C3",2,0,-10,10,0,-10,10);
		fdata[i].SetParameter(5,"C4",2,1,-10,10,0,-10,10);
		fdata[i].SetParameter(6,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		
		i++;
		//Diff Mandelbrot
		fdata[i]=new FormulaData();
		fdata[i].SetName("Bari Mandelbrot");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Aver. Count",0,3,2,10);
		fdata[i].SetParameter(3,"Reduction",1,1,0,10);
		//fdata[i].SetParameter(2,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
	
		i++;
		//Diff Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Bari Julia");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Aver. Count",0,3,2,10);
		fdata[i].SetParameter(3,"Reduction",1,1,0,10);
		//fdata[i].SetParameter(2,"Weight",1,0,-2,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		
	/*	i++;
		//Mandelbrot Sierp
		fdata[i]=new FormulaData();
		fdata[i].SetName("Sierpinsky Mandelbrot");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Exponent 1",1,2,0,5);
		fdata[i].SetParameter(3,"Exponent 2",1,2,0,10);
		fdata[i].SetDefaultView(-2,1.5,1,-1.5);
		//fdata[0].SetFormula(new Mandelbrot());
		
		i++;
		//Julia Sierp
		fdata[i]=new FormulaData();
		fdata[i].SetName("Sierpinsky Julia");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Exponent 1",1,2,0,5);
		fdata[i].SetParameter(3,"Exponent 2",1,2,0,10);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		//fdata[1].SetFormula(new Julia());		
		*/
		i++;
		//LPM
		fdata[i]=new FormulaData();
		fdata[i].SetName("Sierpinsky LyMaJu");
		fdata[i].SetCount(4);
		fdata[i].SetParameter(0,"Bailout",0,100,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,0.26,-1,1,0,-1,1);
		fdata[i].SetParameter(2,"Exponent 1",1,2,0,5);
		fdata[i].SetParameter(3,"Exponent 2",1,2,0,10);
		//fdata[i].SetParameter(2,"Starting",2,0,-1,1,0,-1,1);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		fdata[i].SetSpecial(true);
		fdata[i].SetSpecialName("Lyapunov String");
		fdata[i].SetSpecialDefault("AB");
		fdata[i].SetSpecialCount(2);
	
		/*i++;
		//Rot Glynn Julia
		fdata[i]=new FormulaData();
		fdata[i].SetName("Rot Glynn Julia");
		fdata[i].SetCount(5);
		fdata[i].SetParameter(0,"Bailout",0,4,1,1000000);
		fdata[i].SetParameter(1,"Perturbation",2,-0.2,-10,10,0,-10,10);
		fdata[i].SetParameter(2,"Exponent",1,1.5,1,10);
		fdata[i].SetParameter(3,"Rotation",1,0,-1,1);
		fdata[i].SetParameter(4,"Rotation Sum",0,0,0,2);
		fdata[i].SetDefaultView(-1.5,1.5,1.5,-1.5);
		*/
		
		
		
		FormulaCount=i+1;
	}
	
	
	public Formula GenerateFormula(int type){
		Formula res;
		switch (type){
			case 0:
				return new Mandelbrot();
			//	break;
			case 1:
				return new Julia();
				//break;
			case 2:
				return new General_Mandelbrot();
				//break;
			case 3:
				return new General_Julia();
				//break;
			case 4:
			    return new LPMJ();
			case 5:
			    return new General_LPMJ();
			case 6:
				return new Phoenix_Mandelbrot();
			//	break;
			case 7:
				return new Phoenix_Julia();
				//break;			
			case 8:
				return new Phoenix_Mandelbrot_General();
				//break;			
			case 9:
				return new Phoenix_Julia_General();		
			case 10:
			    return new LPMJ_Phoenix();
			case 11:
			    return new General_LPMJ_Phoenix();		
			case 12:
			    return new BurningShip();
			/*case 13:
			    return new BurningShip_R();
			case 14:
			    return new BurningShip_I();*/
			case 13:
			    return new General_Burning();
			//case 15:
			//	return new Glynn_Mandelbrot();
			case 14:
				return new Glynn_Julia();
			case 15:
				return new Glynn_Julia_Phoenix();
			//case 18:
			//	return new Glynn2_Mandelbrot();
			case 16:
				return new Glynn2_Julia();
			case 17:
				return new Glynn_Julia_Lyap();
			/*case 20:
				return new Rotational_Mandelbrot();
			case 21:
				return new Rotational_Julia();*/
			case 18:
				return new Rotational_Mandelbrot_General();
			case 19:
				return new Rotational_Julia_General();
			case 20:
				return new Newton_gen();
			case 21:
				return new Rot_Newton_gen();
			case 22:
				return new Lambda();
			case 23:
				return new InvMandelbrot();
			case 24:
				return new InvJulia();
			case 25:
				return new InvGeneral_Mandelbrot();
			case 26:
				return new InvGeneral_Julia();
			case 27:
				return new InvLPMJ();
			case 28:
				return new InvGeneral_LPMJ();
			case 29:
				return new Inv_Glynn_Julia();
			case 30:
				return new Inv_Glynn2_Julia();
			case 31:
				return new Diff_Mandelbrot();
			case 32:
				return new Diff_Julia();
			case 33:
				return new Diff_General_Mandelbrot();
				//break;
			case 34:
				return new Diff_General_Julia();
			case 35:
				return new Bari_Mandelbrot();
				//break;
			case 36:
				return new Bari_Julia();
			/*case 37:
				return new Sierp_Mandelbrot();
			case 38:
				return new Sierp_Julia();*/
			case 37:
				return new Sierp_LPMJ();
			/*case 37:
				return new Triple_Mandelbrot();
				//break;
			case 38:
				return new Triple_Julia();*/
			
			
			
/*			case 31:
				return new Rot_Glynn_Julia();
			//break;				
				/*
			case 6:
				return new Newton();
				//break;
			case 7:
				return new Newton_gen();
				//break;*/
			default:
				return new Mandelbrot();
				//break;
		}
	}		
	
	
	
}
