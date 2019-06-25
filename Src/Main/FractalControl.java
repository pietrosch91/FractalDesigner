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





public class FractalControl extends JFrame implements ActionListener,MouseWheelListener,ChangeListener,WindowListener{
	String []Angles={"0°","45°","90°","135°","180°","225°","270°","315°"};
	static int bw=10;
	static int bh=20;
	MandelFrac Parent;
	//FracPreviewer Preview;
	JPanel P3;
	JPanel P3_Top;
	JCheckBox ForceUD;
	JSpinner fwUD,fhUD;
	JLabel fwlab,fhlab;
	int RotL,RotH;
	JPanel P4;
	JLabel CxLab,CyLab,PdimLab;
	JSpinner CxUD,CyUD,PdimUD;
	JLabel RotLab;
	JComboBox RotUD;
	
	
	
	
	
	
	
	
	boolean Updating;
	
	
	public FractalControl(MandelFrac p){
		Parent=p;
		Updating=false;
		BuildGraphics();			
		UpdateData();
	}
	
	

//End of image management

	
	
	public void UpdateData(){
		Updating=true;
		ForceUD.setSelected(Parent.ForceRes);
		fwUD.setValue(Parent.ForcedW);
		fhUD.setValue(Parent.ForcedH);
		CxUD.setValue(Parent.Cx);
		CyUD.setValue(Parent.Cy);
		PdimUD.setValue(Parent.Pdim);
		RotUD.setSelectedIndex(Parent.RotID);		
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
		setTitle("Main Settings");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridwidth=c.gridheight=1;
		c.weightx=c.weighty=1;
		GridBagConstraints c1;
		
		
		//Third panel First two moved
		//Top panel
		P3_Top=new JPanel();
		P3_Top.setLayout(new GridLayout(1,3,1,1));
		P3_Top.add(new JLabel());
		ForceUD=new JCheckBox();
		ForceUD.setText("Force Resolution");
		ForceUD.setName("ForceUD");
		ForceUD.addActionListener(this);
		P3_Top.add(ForceUD);
		P3_Top.add(new JLabel());
		ApplySize(P3_Top,44*bw,bh);
		
		//P3
		P3=new JPanel();
		P3.setLayout(new GridBagLayout());
		P3.setBorder(new LineBorder(Color.BLACK));
		
		c1=new GridBagConstraints();
		c1.fill=GridBagConstraints.NONE;
		c1.gridwidth=c1.gridheight=1;
		c1.weightx=c1.weighty=1;
		
		c1.gridx=0;
		c1.gridy=0;
		c1.gridwidth=4;
		P3.add(P3_Top,c1);
		
		c1.gridy=1;
		c1.gridx=0;
		c1.gridwidth=1;
		fwlab=new JLabel("Width");
		fwlab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(fwlab,10*bw,bh);
		P3.add(fwlab,c1);
		
		c1.gridx=1;
		fwUD=new JSpinner();
		ApplySize(fwUD,10*bw,bh);
		fwUD.setName("fwUD");
		fwUD.setModel(new SpinnerNumberModel(100,100,30000,1));
		fwUD.addChangeListener(this);
		fwUD.addMouseWheelListener(this);
		P3.add(fwUD,c1);	
		
		c1.gridx=2;
		fhlab=new JLabel("Height");
		fhlab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(fhlab,10*bw,bh);
		P3.add(fhlab,c1);
		
		c1.gridx=3;
		fhUD=new JSpinner();
		ApplySize(fhUD,10*bw,bh);
		fhUD.setName("fhUD");
		fhUD.setModel(new SpinnerNumberModel(100,100,30000,1));
		fhUD.addChangeListener(this);
		fhUD.addMouseWheelListener(this);
		P3.add(fhUD,c1);	
		
		ApplySize(P3,45*bw,3*bh);
		//End of third panel
		
		//Fourth Panel
		P4=new JPanel();
		P4.setLayout(new GridBagLayout());
		P4.setBorder(new LineBorder(Color.BLACK));
		
		c1=new GridBagConstraints();
		c1.fill=GridBagConstraints.NONE;
		c1.gridwidth=c1.gridheight=1;
		c1.weightx=c1.weighty=1;
		
		c1.gridx=c1.gridy=0;
		CxLab=new JLabel("Center X");
		CxLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(CxLab,10*bw,bh);
		P4.add(CxLab,c1);
		
		c1.gridx=1;
		CxUD=new JSpinner();
		ApplySize(CxUD,30*bw,bh);
		CxUD.setName("CxUD");
		CxUD.setModel(new SpinnerNumberModel(0.0d, null, null, 0.001d));
        CxUD.setEditor(new javax.swing.JSpinner.NumberEditor(CxUD, "###0.0000000000000000000"));        
		CxUD.addChangeListener(this);
		CxUD.addMouseWheelListener(this);
		P4.add(CxUD,c1);
		
		c1.gridx=0;
		c1.gridy=1;
		CyLab=new JLabel("Center Y");
		CyLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(CyLab,10*bw,bh);
		P4.add(CyLab,c1);
		
		c1.gridx=1;
		CyUD=new JSpinner();
		ApplySize(CyUD,30*bw,bh);
		CyUD.setName("CyUD");		
		CyUD.setModel(new SpinnerNumberModel(0.0d, null, null, 0.001d));
        CyUD.setEditor(new javax.swing.JSpinner.NumberEditor(CyUD, "###0.0000000000000000000"));
		CyUD.addMouseWheelListener(this);
		CyUD.addChangeListener(this);
		P4.add(CyUD,c1);
		
		c1.gridx=0;
		c1.gridy=2;
		PdimLab=new JLabel("Pixel dim");
		PdimLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(PdimLab,10*bw,bh);
		P4.add(PdimLab,c1);
		
		c1.gridx=1;
		PdimUD=new JSpinner();
		ApplySize(PdimUD,30*bw,bh);
		PdimUD.setName("PdimUD");
		PdimUD.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 0.001d));
        PdimUD.setEditor(new javax.swing.JSpinner.NumberEditor(PdimUD, "###0.0000000000000000000"));
		PdimUD.addChangeListener(this);
		PdimUD.addMouseWheelListener(this);
		P4.add(PdimUD,c1);	
		
		c1.gridx=0;
		c1.gridy=3;
		RotLab=new JLabel("Rotation Angle");
		RotLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(RotLab,10*bw,bh);
		P4.add(RotLab,c1);
		
		c1.gridx=1;
		RotUD=new JComboBox();
		RotUD.setName("RotUD");
		ApplySize(RotUD,30*bw,bh);
		RotL=0;
		RotH=7;
		RotUD.setModel(new DefaultComboBoxModel<>(Angles));
        RotUD.addActionListener(this);
        RotUD.addMouseWheelListener(this);
		P4.add(RotUD,c1);	
	
		ApplySize(P4,45*bw,5*bh);
		
		//end of fourth panel

		
		//Addition to main
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		add(P3,c);
		c.gridy=1;
		add(P4,c);
		
		//main resize
		setSize(P3.getWidth()+20,P3.getHeight()+P4.getHeight()+60);	
	}	
	
	 public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("ForceUD")){
			if(!Updating){
				Parent.ForceRes=ForceUD.isSelected();
				UpdateData();
			}
		}
		if(cmd.equals("RotUD")){
			if(!Updating){
				Parent.RotID=RotUD.getSelectedIndex();
			}		
		}
	}
		
	
    public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("fwUD")){
			if(!Updating){
				Parent.ForcedW=(int)fwUD.getValue();			
				UpdateData();
			}
		}
		else if(cmd.equals("fhUD")){
			if(!Updating){
				Parent.ForcedH=(int)fhUD.getValue();			
				UpdateData();
			}
		}
		else if(cmd.equals("CxUD")){
			if(!Updating){
				Parent.SetCenter((double)CxUD.getValue(),(double)CyUD.getValue());
				UpdateData();
			}
		}
		else if(cmd.equals("CyUD")){
			if(!Updating){
				Parent.SetCenter((double)CxUD.getValue(),(double)CyUD.getValue());
				UpdateData();
			}
		}
		else if(cmd.equals("PdimUD")){
			if(!Updating){
				Parent.SetPdim((double)PdimUD.getValue());
				UpdateData();
			}
		}
	}
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("fwUD")){
			int t=(int)fwUD.getValue();
			t-=100*e.getWheelRotation();
			if(t<100) t=100;
			if(t>20000) t=20000;
			fwUD.setValue(t);	
		}	
		else if(cmd.equals("fhUD")){
			int t=(int)fhUD.getValue();
			t-=100*e.getWheelRotation();
			if(t<100) t=100;
			if(t>20000) t=20000;
			fhUD.setValue(t);	
		}
		else if(cmd.equals("CxUD")){
			double t=(double)CxUD.getValue();
			t-=Parent.imgW*Parent.Pdim*e.getWheelRotation(); //move one image width left/right
			CxUD.setValue(t);	
		}	
		else if(cmd.equals("CyUD")){
			double t=(double)CyUD.getValue();
			t-=Parent.imgH*Parent.Pdim*e.getWheelRotation();//move one image height up/down
			CyUD.setValue(t);	
		}
		else if(cmd.equals("PdimUD")){
			double t=(double)PdimUD.getValue();
			t*=pow(2,e.getWheelRotation());//halves/doubles dimension
			PdimUD.setValue(t);	
		}	
		else if(cmd.equals("RotUD")){
			int t=(int) RotUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>RotH) t=RotH;
			if(t<RotL) t=RotL;
			RotUD.setSelectedIndex(t);		
		}
	}
	
    //WindowListener
	public void 	windowActivated(WindowEvent e){}
	public void 	windowClosed(WindowEvent e){System.out.println("Here Closing");}
	public void 	windowClosing(WindowEvent e){
			//System.out.println("Here");
			setVisible(false);
		}
	public void 	windowDeactivated(WindowEvent e){}
	public void 	windowDeiconified(WindowEvent e){}
	public void 	windowIconified(WindowEvent e){}
	public void 	windowOpened(WindowEvent e){}
	
	
}
