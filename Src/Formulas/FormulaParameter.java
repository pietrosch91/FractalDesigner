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







public class FormulaParameter extends JPanel implements ChangeListener,MouseWheelListener {
	double ValueR,ValueI;
	int paramtype;
	int index;
	String pname;
	JLabel namelab;
	JSpinner RealUD,ImagUD;
	int bw,bh;
	double MinR,MaxR,MinI,MaxI;
	double StepR,StepI;
	boolean updating;
	
	public FormulaParameter(int ind,int BW,int BH){
		super();
	    ValueR=ValueI=0;
	    index=ind;
		pname="";		
		bw=BW;
		bh=BH;
        BuildGraphics();
        setType(0);
    }
    
    public void setType(int newt){
		ValueI=ValueR=0;
		if(newt<0){
			ImagUD.setEnabled(false);
			RealUD.setEnabled(false);
		}
		else if(newt<2){
			ImagUD.setEnabled(false);
			RealUD.setEnabled(true);			
		}
		else{
			ImagUD.setEnabled(true);
			RealUD.setEnabled(true);			
		}
		
		paramtype=newt;
		UpdateData();
    }
    
    void UpdateViewer(){
		if(paramtype<=0){
			RealUD.setEditor(new javax.swing.JSpinner.NumberEditor(RealUD, "###0"));        
			ImagUD.setEditor(new javax.swing.JSpinner.NumberEditor(ImagUD, "###0"));        
		}
		else{
			RealUD.setEditor(new javax.swing.JSpinner.NumberEditor(RealUD, "###0.0000000000"));        
			ImagUD.setEditor(new javax.swing.JSpinner.NumberEditor(ImagUD, "###0.0000000000"));        
		}
	}
    /*
    //Save/load function
    public void Print(BufferedWriter o) throws IOException{
        String temp;
        temp=String.format("%d\n",type);
        o.write(temp);
        temp=String.format("%d\n",max);
        o.write(temp);
        temp=String.format("%d\n",min);
        o.write(temp);
        temp=String.format("%.9f\n",speed);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%.9f\n",phase);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%.9f\n",power);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%d\n",N);
        o.write(temp);
        for(int i=0;i<10;i++){
            temp=String.format("%.9f\n", params[i]);
            temp=temp.replace(',', '.');
            o.write(temp);
        }
    }*/
    /*
    public int Read(BufferedReader i) throws IOException{
        String t;
        int error=0;
        double[]vals=new double[7];
        int j;
        for(j=0;j<7;j++) vals[j]=0;
        for(j=0;((j<7)&&(error==0));j++){
            t=i.readLine();
            if(t==null){
                error++;
                continue;
            }
            vals[j]=Double.parseDouble(t);               
        }
        setType((int)vals[0]);
        max=(int)vals[1];
        min=(int)vals[2];
        speed=vals[3];
        phase=vals[4];
        power=vals[5];
        N=(int)vals[6];
        for(j=0;j<10;j++){
            t=i.readLine();
            params[j]=Double.parseDouble(t);
        }   
        Regenerate();
        UpdateData();
        return error;
    }*/
    
    public void UpdateData(){
        updating=true;
        namelab.setText(pname);
        RealUD.setValue(ValueR);
        ImagUD.setValue(ValueI);        
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
		ApplySize(namelab,2*bw,bh);
		add(namelab);
		
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
        //c.gridx=1;
        add(RealUD);
        
        ImagUD=new JSpinner();
		ApplySize(ImagUD,2*bw,bh);
		ImagUD.setName("ImagUD");
		MinI=0;
		MaxI=10;
		StepI=1;
		ImagUD.setModel(new SpinnerNumberModel(0, MinI, MaxI, 1));
		ImagUD.setEditor(new javax.swing.JSpinner.NumberEditor(ImagUD, "###0"));        
        ImagUD.addChangeListener(this);
        ImagUD.addMouseWheelListener(this);
        //c.gridx=2;
        add(ImagUD);
        ApplySize(this,6*bw+20,bh+10);
        UpdateData();
    }
    
    
    public void Rebuild(FormulaData input){
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
		//Other one
		if(paramtype>1){
			StepI=0.01;
			MinI=input.GetMinI(index);
			MaxI=input.GetMaxI(index);
			ValueI=input.GetDefaultI(index);
			ImagUD.setModel(new SpinnerNumberModel(ValueI, MinI, MaxI, StepI));
			System.out.printf("New Parameter (I) %d %s %d %f %f %f\n",index,pname,paramtype,MinI,MaxI,ValueI);
		}
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
		else if(cmd.equals("ImagUD")){
			if(ImagUD.isEnabled()){
				double t=(double) ImagUD.getValue();
				t-=StepI*e.getWheelRotation()/10.;
				if(t>MaxI) t=MaxI;
				if(t<MinI) t=MinI;
				ImagUD.setValue(t);				
			}
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
		else if(cmd.equals("ImagUD")){
			if(!updating){
				ValueI=(double)ImagUD.getValue();
			}
		}
	}
	
	public double GetValueR(){
		return ValueR;
	}
	
	
	public double GetValueI(){
		return ValueI;
	}
	
	public void SetValue(double vr,double vi){
		if(paramtype==0){
			ValueR=(int) vr;
		}
		else if(paramtype==1){
			ValueR=vr;
		}
		else if (paramtype==2){
			ValueR=vr;
			ValueI=vi;
		}
		else return;
		UpdateData();
	}
	
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
