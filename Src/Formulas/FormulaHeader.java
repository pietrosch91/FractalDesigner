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

public class FormulaHeader extends JPanel{
	JLabel namelab,reallab,imaglab;
	int bw,bh;
	
	public FormulaHeader(int BW,int BH){
		super();
	   	bw=BW;
		bh=BH;
        BuildGraphics();
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
	//	setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridLayout(1,3,1,1));
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.02;
		c.gridy=0;
		
		//new line
		namelab=new JLabel("Name");
		ApplySize(namelab,2*bw,bh);
		c.gridx=0;
		add(namelab);
		
		reallab=new JLabel("Real");
		ApplySize(reallab,2*bw,bh);
		c.gridx=1;
        add(reallab);
        
        imaglab=new JLabel("Imaginary");
		ApplySize(imaglab,2*bw,bh);
		c.gridx=2;
        add(imaglab);
        
        ApplySize(this,6*bw+20,bh+10);
    }	
}
