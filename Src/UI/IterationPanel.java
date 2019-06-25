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





public class IterationPanel extends JPanel implements MouseWheelListener,MouseMotionListener,MouseListener,ChangeListener{
	//MandelFrac Parent;
	//FracPreviewer Preview;
	int Niters;
	int bw,bh;
	JPanel P1;
	JPanel PIter;
	JPanel PSlide;
	JLabel SliderInf,SliderSup;
	JLabel IterLab;
	boolean SliderMaster;
	JSlider IterUD1;
	JSpinner IterUD2;
	boolean Updating;
	
	
	public IterationPanel(int BW,int BH){
		//Parent=p;
		bw=BW;
		bh=BH;
		SliderMaster=Updating=false;
		BuildGraphics();			
	/*	Preview=new FracPreviewer(PrevLab,Parent);
		Preview.mylayer=Parent.mylayer;
		Preview.myorb=Parent.myorb;*/
		UpdateData();
	}
	
	

//End of image management

	
	
	public void UpdateData(){
		Updating=true;
		IterUD2.setValue(Niters);	
		if(!SliderMaster){
		IterUD1.setMaximum(2*(int)IterUD2.getValue());
		if (IterUD1.getMaximum()>100000) IterUD1.setMaximum(100000);
		IterUD1.setValue((int)IterUD2.getValue());
		SliderSup.setText(String.format("%d",IterUD1.getMaximum()));
		}
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
		//setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridBagLayout());
		GridBagConstraints c1=new GridBagConstraints();
		c1.fill=GridBagConstraints.NONE;
		c1.gridwidth=c1.gridheight=1;
		c1.weightx=c1.weighty=1;
		
		c1.gridy=0;
		c1.gridx=0;
		c1.gridwidth=1;
		IterLab=new JLabel("Iterations");
		IterLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(IterLab,3*bw,2*bh);
		add(IterLab,c1);
		
		c1.gridx=1;
		IterUD2=new JSpinner();
		ApplySize(IterUD2,3*bw,2*bh);
		IterUD2.setName("iterSpin");
		IterUD2.setModel(new SpinnerNumberModel(1,1,100000,1));
		IterUD2.addChangeListener(this);
		IterUD2.addMouseWheelListener(this);
		add(IterUD2,c1);
		
		{
				//Slide Panel
				PSlide=new JPanel();
				PSlide.setLayout(new GridBagLayout());
				GridBagConstraints c2=new GridBagConstraints();
				c2.fill=GridBagConstraints.NONE;
				c2.gridwidth=c2.gridheight=1;
				
				c2.gridwidth=1;
				c2.gridx=c2.gridy=0;		
				SliderInf=new JLabel("1");
				SliderInf.setHorizontalAlignment(SwingConstants.CENTER);
				ApplySize(SliderInf,1*bw,bh);
				PSlide.add(SliderInf,c2);
		
				c2.gridwidth=1;
				c2.gridx=1;
				IterUD1=new JSlider();
				ApplySize(IterUD1,4*bw,bh);
				IterUD1.setMinimum(1);
				IterUD1.addChangeListener(this);
				IterUD1.addMouseListener(this);
				IterUD1.setName("iterSlide");
				PSlide.add(IterUD1,c2);
		
				c2.gridwidth=1;
				c2.gridx=2;
				SliderSup=new JLabel("");
				SliderSup.setHorizontalAlignment(SwingConstants.CENTER);
				ApplySize(SliderSup,1*bw,bh);
				PSlide.add(SliderSup,c2);
				
				ApplySize(PSlide,6*bw+20,bh+30);	
		}
		c1.gridy=1;
		c1.gridx=0;
		c1.gridwidth=2;
		add(PSlide,c1);
		
		ApplySize(this,6*bw,2*bh+15);		
	}	
	
	
    public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("iterSlide")){
			if(!Updating && SliderMaster){
				int nv=(int)IterUD1.getValue();
				if(nv==0) nv++;
				IterUD2.setValue(nv);
			}
		}
		else if(cmd.equals("iterSpin")){
			if(!SliderMaster){
				IterUD1.setMaximum(2*(int)IterUD2.getValue());
				if (IterUD1.getMaximum()>100000) IterUD1.setMaximum(100000);
				IterUD1.setValue((int)IterUD2.getValue());
				SliderSup.setText(String.format("%d",IterUD1.getMaximum()));
			}
			if (!Updating){
				Niters=(int)IterUD2.getValue();				
				UpdateData();
			}
		}
	
	}
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("iterSpin")){
			int t=(int)IterUD2.getValue();
			int stepval=100;
			if(t==1) stepval=99;
			t-=stepval*e.getWheelRotation();
			if(t<1) t=1;
			if(t>100000) t=100000;
			IterUD2.setValue(t);	
		}
	
	}
	
	public void mousePressed(MouseEvent e) {
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In mousePressed() : "+cmd);
		if(cmd.equals("iterSlide")){
			SliderMaster=true;
		}
    }

    public void mouseReleased(MouseEvent e) {
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In mouseReleased() : "+cmd);
		if(cmd.equals("iterSlide")){
			SliderMaster=false;
			if(!Updating){
				IterUD1.setMaximum(2*IterUD1.getValue());
				if (IterUD1.getMaximum()>100000) IterUD1.setMaximum(100000);
				SliderSup.setText(String.format("%d",IterUD1.getMaximum()));
				IterUD2.setValue((int)IterUD1.getValue());// TODO add your handling code here:
			}
		}		
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {
	/*	String cmd=((Component)e.getSource()).getName();
		System.out.println("In mouseExited() : "+cmd);
		if(cmd.equals("MousePlab")){
			newPXUD.setText("0.00");
			newPYUD.setText("0.00");			
		}*/
	}

    public void mouseClicked(MouseEvent e) {
		/*String cmd=((Component)e.getSource()).getName();
		System.out.println("In mouseClicked() : "+cmd);
		if(cmd.equals("MousePlab")){
			final Point mousePos = MousePlab.getMousePosition();
			double emi=10*bw;
			Parent.px=(mousePos.x-emi)/emi;
			Parent.py=(emi-mousePos.y)/emi;
			UpdateData();			
		}	*/
	}
	
	public void mouseMoved(MouseEvent e) {
		/*String cmd=((Component)e.getSource()).getName();
		System.out.println("In mouseMoved() : "+cmd);
		if(cmd.equals("MousePlab")){
			final Point mousePos = MousePlab.getMousePosition();
			double emi=10*bw;
			newPXUD.setText(String.format("%.2f",(mousePos.x-emi)/emi));
			newPYUD.setText(String.format("%.2f",(emi-mousePos.y)/emi));
		}	*/
    }
    
    public void mouseDragged(MouseEvent e) {
		
	}
    
    
	
	public int GetValue(){
		return Niters;
	}
	
	public void SetValue(int vv){
		Niters=vv;
		UpdateData();
	}
	
}
