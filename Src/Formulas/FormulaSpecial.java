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







public class FormulaSpecial extends JPanel implements ActionListener {
	String Value;
	int index;
	int count;
	String pname;
	JLabel namelab;
	JTextField valueUD;
	int bw,bh;
	//double MinR,MaxR,MinI,MaxI;
	boolean updating;
	
	String FormatString(String input){
		String out="";
		char c;
		for(int i=0;i<input.length();i++){
			c=input.charAt(i);
			if(c<65) continue;
			if(c>=97) c-=32;
			if(c>=65+count) continue;
			out+=c;
		}
		return out;
	}
	
	public FormulaSpecial(int BW,int BH){
		super();
		Value="";
		count=2;
	    pname="";		
		bw=BW;
		bh=BH;
        BuildGraphics();
    }
        
    
    public void UpdateData(){
        updating=true;
        namelab.setText(pname);
		valueUD.setText(Value);
        updating=false;
    }
          
    public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
	//	target.setFocusable(false);
	}		
		
    public void BuildGraphics(){
		//setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridLayout(1,2,1,1));
		
		//new line
		namelab=new JLabel("");
		ApplySize(namelab,2*bw,bh);
		add(namelab);
		
		valueUD=new JTextField("");
		ApplySize(valueUD,4*bw,bh);
		valueUD.setName("valueUD");
		valueUD.setActionCommand("valueUD"); 
        valueUD.addActionListener(this);
        //c.gridx=1;
        add(valueUD);
        
        ApplySize(this,6*bw+20,bh+10);
        UpdateData();
    }
    
    
    public void Rebuild(FormulaData input){
		updating=true;
		//update  name
		pname=input.GetSpecialName();
		//type
		//Updating Real Controller
		Value=input.GetSpecialDefault();
		System.out.printf("New Parameter (S) %d %s %s\n",index,pname,Value);
		valueUD.setEnabled(input.HasSpecial());
		count=input.GetSpecialCount();
		UpdateData();
		updating=false;
	}
	
		
	//listener functions
	//MouseWheelListener
	
		
	//changeListener
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("valueUD")){
			if(!updating){
				Value=FormatString(valueUD.getText());
				UpdateData();				
			}
		}
	}
	
	public String GetValue(){
		return Value;
	}
	
	public void SetCount(int cc){
		count=cc;
	}
	
	public void SetValue(String vr){
		Value=FormatString(vr);
		UpdateData();
	}
	
}
