import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;





public class BooleanPanel extends JPanel implements ActionListener{
	int bw,bh;
	boolean Alternate,Conjugate,Fixed,useOrbit;
	//JCheckBox JuliaUD;
	JCheckBox AlternUD;
	JCheckBox ConjugUD;
	JCheckBox FixedUD;
	JCheckBox OrbitUD;
	boolean Updating;
	
	
	public BooleanPanel(int BW,int BH){
		bw=BW;
		bh=BH;
		Updating=false;
		Alternate=Conjugate=Fixed=useOrbit=false;
		BuildGraphics();			
		UpdateData();
	}
	
	

//End of image management

	
	
	public void UpdateData(){
		Updating=true;
		AlternUD.setSelected(Alternate);
		ConjugUD.setSelected(Conjugate);
		FixedUD.setSelected(Fixed);
		OrbitUD.setSelected(useOrbit);
		Updating=false;		
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
		setLayout(new GridLayout(1,4,1,1));
		//setBorder(new LineBorder(Color.BLACK));
				
		AlternUD=new JCheckBox();
		AlternUD.setText("Alternate");
		AlternUD.setName("AlternUD");
		AlternUD.addActionListener(this);
		add(AlternUD);
		
		ConjugUD=new JCheckBox();
		ConjugUD.setText("Conjugate");
		ConjugUD.setName("ConjugUD");
		ConjugUD.addActionListener(this);
		add(ConjugUD);
		
		FixedUD=new JCheckBox();
		FixedUD.setText("Fixed Iter");
		FixedUD.setName("FixedUD");
		FixedUD.addActionListener(this);
		add(FixedUD);
		
		OrbitUD=new JCheckBox();
		OrbitUD.setText("Use Orbits");
		OrbitUD.setName("OrbitUD");
		OrbitUD.addActionListener(this);
		add(OrbitUD);
		
		ApplySize(this,6*bw+20,bh+10);
		//End of Second panel
	}	
	
	 public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("FixedUD")){
			if(!Updating){
				 Fixed=FixedUD.isSelected();
				 UpdateData();
			 }
		}
		else if(cmd.equals("AlternUD")){
			if(!Updating){
				Alternate=AlternUD.isSelected();
				UpdateData();
				//UpdatePreview();
			}
		}
		else if(cmd.equals("OrbitUD")){
			if(!Updating){
				useOrbit=OrbitUD.isSelected();
				UpdateData();
				//UpdatePreview();
			}
		}
		else if(cmd.equals("ConjugUD")){
			if(!Updating){
				Conjugate=ConjugUD.isSelected();
				UpdateData();
				//UpdatePreview();
			}
		}		
	}
		
	public void SetAlternate(boolean v){
		Alternate=v;
		UpdateData();
	}
		
	public void SetConjugate(boolean v){
		Conjugate=v;
		UpdateData();
	}
	
	public void SetFixed(boolean v){
		Fixed=v;
		UpdateData();
	}
	
	public void SetOrbit(boolean v){
		useOrbit=v;
		UpdateData();
	}
	
	public boolean GetAlternate(){
		return Alternate;
	}
		
	public boolean GetConjugate(){
		return Conjugate;
	}
	
	
	public boolean GetFixed(){
		return Fixed;
	}
	
	public boolean GetOrbit(){
		return useOrbit;
	}
  
	
}
