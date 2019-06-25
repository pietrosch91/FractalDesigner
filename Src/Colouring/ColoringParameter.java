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







public class ColoringParameter extends JPanel implements ChangeListener,MouseWheelListener {
	double ValueR;
	int paramtype;
	int index;
	String pname;
	JLabel namelab;
	JSpinner RealUD;
	int bw,bh;
	double MinR,MaxR;
	double StepR;
	boolean updating;
	
	public ColoringParameter(int ind,int BW,int BH){
		super();
	    ValueR=0;
	    index=ind;
		pname="";		
		bw=BW;
		bh=BH;
        BuildGraphics();
        setType(0);
    }
    
    public void setType(int newt){
		ValueR=0;
		paramtype=newt;
		if(newt<0) RealUD.setEnabled(false);
		else RealUD.setEnabled(true);
		UpdateData();
	 }
    
    void UpdateViewer(){
		if(paramtype<=0){
			RealUD.setEditor(new javax.swing.JSpinner.NumberEditor(RealUD, "###0"));        
		}
		else{
			RealUD.setEditor(new javax.swing.JSpinner.NumberEditor(RealUD, "###0.0000000000"));        
		}
	}
	
    
    public void UpdateData(){
        updating=true;
        namelab.setText(pname);
        RealUD.setValue(ValueR);
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
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.02;
		c.gridy=0;
		
		//new line
		namelab=new JLabel("");
		ApplySize(namelab,bw,bh);
		c.gridx=0;
		add(namelab,c);
		
		RealUD=new JSpinner();
		ApplySize(RealUD,2*bw,bh);
		RealUD.setName("RealUD");
		MinR=0;
		MaxR=10;
		StepR=1;
		RealUD.setModel(new SpinnerNumberModel(0, MinR, MaxR, 1));
		RealUD.setEditor(new javax.swing.JSpinner.NumberEditor(RealUD, "###0"));        
        RealUD.addChangeListener(this);
        RealUD.addMouseWheelListener(this);
        c.gridx=1;
        add(RealUD,c);
        
        ApplySize(this,3*bw,bh);
        UpdateData();
    }
    
    
    public void Rebuild(ColoringData input){
		updating=true;
		//update  name
		pname=input.GetPName(index);
		//type
		setType(input.GetPtype(index));
		//Updating Real Controller
		MinR=input.GetMinR(index);
		MaxR=input.GetMaxR(index);
		ValueR=input.GetDefaultR(index);
		System.out.printf("New Parameter (R) %d %s %d %f %f %f\n",index,pname,paramtype,MinR,MaxR,ValueR);
		if(paramtype==0) StepR=1;
		else StepR=0.01;
		RealUD.setModel(new SpinnerNumberModel(ValueR, MinR, MaxR, StepR));
		UpdateViewer();
		UpdateData();
		updating=false;
	}
	
		
	//listener functions
	//MouseWheelListener
	public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("RealUD")){
			double t=(double) RealUD.getValue();
			if(paramtype>0)t-=StepR*e.getWheelRotation()/10.;
			else t-=StepR*e.getWheelRotation();
			if(t>MaxR) t=MaxR;
			if(t<MinR) t=MinR;
			RealUD.setValue(t);				
		}
	}
		
	//changeListener
	public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("RealUD")){
			if(!updating){
				ValueR=(double)RealUD.getValue();				
			}
		}
	}
	
	public double GetValue(){
		return ValueR;
	}
	
	public void SetValue(double V){
		if(paramtype==0){
			ValueR=(int) V;
			UpdateData();
		}
		else if(paramtype==1){
			ValueR=V;
			UpdateData();
		}
	}
	
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
