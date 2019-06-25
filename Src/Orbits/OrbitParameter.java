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

public class OrbitParameter extends JPanel implements ChangeListener,MouseWheelListener {
	Orbit parent;
	double Value,Speed;
	String pname;
	JLabel namelab;
	JSpinner ValUD,SpeedUD;
	int index;
	int bw,bh;
	double MinR,MaxR;
	double StepR;
	boolean updating;
	boolean sp_ena;
	boolean ena;

	
	public OrbitParameter(int ind,int BW,int BH,Orbit p){
		super();
	    Value=Speed=0;
	    index=ind;
		pname="";		
		bw=BW;
		bh=BH;
		parent=p;
        BuildGraphics();        
    }
  
    void UpdateViewer(){
		ValUD.setEditor(new javax.swing.JSpinner.NumberEditor(ValUD, "###0.0000"));        
		SpeedUD.setEditor(new javax.swing.JSpinner.NumberEditor(SpeedUD, "###0.0000"));	
	}
    
    
    public void UpdateData(){
        updating=true;
        namelab.setText(pname);
        ValUD.setValue(Value);
        SpeedUD.setValue(Speed);        
        updating=false;
    }
          
    public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}		
		
    public void BuildGraphics(){
		//setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridLayout(1,3,1,1));
		
		//new line
		namelab=new JLabel("");
		//ApplySize(namelab,2*bw,bh);
		add(namelab);
		
		ValUD=new JSpinner();
		//ApplySize(ValUD,2*bw,bh);
		ValUD.setName("ValUD");
		MinR=0;
		MaxR=10;
		StepR=1;
		ValUD.setModel(new SpinnerNumberModel(0d, MinR, MaxR, 1d));
		ValUD.setEditor(new javax.swing.JSpinner.NumberEditor(ValUD, "###0.0000"));        
		ValUD.addChangeListener(this);
        ValUD.addMouseWheelListener(this);
        //c.gridx=1;
        add(ValUD);
        
        SpeedUD=new JSpinner();
		//ApplySize(SpeedUD,bw,bh);
		SpeedUD.setName("SpeedUD");
		SpeedUD.setModel(new SpinnerNumberModel(0d, null, null, 1d));
		SpeedUD.setEditor(new javax.swing.JSpinner.NumberEditor(SpeedUD, "###0.0000"));		
        SpeedUD.addChangeListener(this);
        SpeedUD.addMouseWheelListener(this);
        //c.gridx=2;
        add(SpeedUD);
        ApplySize(this,5*bw,bh+10);
        UpdateData();
    }
    
    
    public void Rebuild(OrbitData input){
		updating=true;
		//update  name
		pname=input.GetPName(index);
		//Updating Value Controller
		MinR=input.GetMinR(index);
		MaxR=input.GetMaxR(index);
		Value=input.GetDefaultR(index);
		StepR=input.GetStepR(index);
		//System.out.printf("New Parameter (R) %d %s %f %f %f\n",index,pname,MinR,MaxR,Value);
		ValUD.setModel(new SpinnerNumberModel(Value, MinR, MaxR, StepR));
		SpeedUD.setModel(new SpinnerNumberModel(0d, null, null, 0.1d));
		UpdateViewer();
		UpdateData();
		updating=false;
	}
	
		
	//listener functions
	//MouseWheelListener
	public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("ValUD")){
			double t=(double) ValUD.getValue();
			t-=StepR*e.getWheelRotation();
			if(t>MaxR) t=MaxR;
			if(t<MinR) t=MinR;
			ValUD.setValue(t);				
		}
		else if(cmd.equals("SpeedUD")){
			if(SpeedUD.isEnabled()){
				double t=(double) SpeedUD.getValue();
				t-=0.01*e.getWheelRotation();
				SpeedUD.setValue(t);				
			}
		}		
	}
		
	//changeListener
	public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("ValUD")){
			if(!updating){
				Value=(double)ValUD.getValue();	
				parent.Regenerate();
				parent.updatePreview();			
			}
		}
		else if(cmd.equals("SpeedUD")){
			if(!updating){
				Speed=(double)SpeedUD.getValue();
				parent.Regenerate();
				parent.updatePreview();
			}
		}
	}
	
	public double GetValue(){
		return Value;
	}
	
	public void SetValue(double val){
		Value=val;
		updating=true;
		ValUD.setValue(Value);
		updating=false;
	}
	
	
	public double GetSpeed(){
		return Speed;
	}
	
	public void SetSpeed(double val){
		Speed=val;
		updating=true;
		SpeedUD.setValue(Speed);
		updating=false;
	}
	
	
	public void Set_Speed(boolean en){
		sp_ena=en;
		if(ena){			
			SpeedUD.setEnabled(en);
		}
		else{
			SpeedUD.setEnabled(false);
		}
	}
	
	public void SetEnabled(boolean en){
		ena=en;
		ValUD.setEnabled(en);
		if(en) Set_Speed(sp_ena);
		else SpeedUD.setEnabled(false);
	}
	
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
