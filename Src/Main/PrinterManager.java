import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Point;
import java.io.*;
import java.awt.GraphicsEnvironment;
import java.net.*;
import java.util.*;
import java.text.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.round;
import static java.lang.Math.ceil;
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





public class PrinterManager extends JFrame implements ActionListener,MouseWheelListener,WindowListener,ChangeListener{
	String []DPIValues={"50","100","200","300","500","600","900","1200"};
	int nDPI;
	
	JPanel P0;
	JLabel Hlab,Wlab,DPIlab;
	JSpinner HUD,WUD;
	JComboBox DPIUD;
	
	double Hcm,Wcm;
	int Wmm,Hmm;
	int Hpix,Wpix;
	int SelectedDPI;
	int DPIvalue;
	static int bw=10;
	static int bh=20;
	
	boolean Updating;
	MandelFrac Parent; 
	
	public PrinterManager(MandelFrac p){
		Parent=p;
		Hcm=30.;
		Wcm=40.;
		SelectedDPI=1;
		Updating=false;
		BuildGraphics();			
	}
	
	public void UpdateData(){
		Updating=true;
		HUD.setValue(Hcm);
		WUD.setValue(Wcm);
		DPIUD.setSelectedIndex(SelectedDPI);
		DPIvalue=Integer.parseInt((String)DPIUD.getSelectedItem());
		RegenerateHW();
		Updating=false;		
	}
	
	//OK
	public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}
	
	
	public void BuildGraphics(){
		setTitle("Printer Settings");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setLayout(new GridLayout(1,1,1,1));
		GridBagConstraints c1;
		c1=new GridBagConstraints();
		c1.fill=GridBagConstraints.NONE;
		c1.gridwidth=c1.gridheight=1;
		c1.weightx=c1.weighty=1;
		
		c1.gridx=0;
		c1.gridy=0;
			
		P0=new JPanel();
		P0.setLayout(new GridBagLayout());
		P0.setBorder(new LineBorder(Color.BLACK));
		
		//First control->Height selection
		c1.gridy=0;
		c1.gridx=0;
		Hlab=new JLabel("Img Height(cm)");
		Hlab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(Hlab,20*bw,bh);
		P0.add(Hlab,c1);

		c1.gridx=1;
		HUD=new JSpinner();
		ApplySize(HUD,20*bw,bh);
		HUD.setName("HUD");
		HUD.setModel(new SpinnerNumberModel(10.0d,1.0d,500.0d,1.0d));
		HUD.setEditor(new javax.swing.JSpinner.NumberEditor(HUD, "###0.00"));        		
		HUD.addChangeListener(this);
		HUD.addMouseWheelListener(this);
		P0.add(HUD,c1);	
		
		//Second control->Width selection
		c1.gridy=1;
		c1.gridx=0;
		Wlab=new JLabel("Img Width(cm)");
		Wlab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(Wlab,20*bw,bh);
		P0.add(Wlab,c1);

		c1.gridx=1;
		WUD=new JSpinner();
		ApplySize(WUD,20*bw,bh);
		WUD.setName("WUD");
		WUD.setModel(new SpinnerNumberModel(10.0d,1.0d,500.0d,1.0d));
		WUD.setEditor(new javax.swing.JSpinner.NumberEditor(WUD, "###0.00"));        		
		WUD.addChangeListener(this);
		WUD.addMouseWheelListener(this);
		P0.add(WUD,c1);	
		
		//Third DPI selection
		c1.gridy=2;
		c1.gridx=0;
		DPIlab=new JLabel("Img DPI");
		DPIlab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(DPIlab,20*bw,bh);
		P0.add(DPIlab,c1);
		
		c1.gridx=1;
		DPIUD=new JComboBox();
		DPIUD.setName("DPIUD");
		ApplySize(DPIUD,20*bw,bh);
		nDPI=DPIValues.length;
		DPIUD.setModel(new DefaultComboBoxModel<>(DPIValues));
		DPIUD.addActionListener(this);
        DPIUD.addMouseWheelListener(this);
		P0.add(DPIUD,c1);	
		
		
				
		ApplySize(P0,45*bw,3*bh+70);		
		//Addition to main
		add(P0);		
		//main resize
		setSize(P0.getWidth()+10,P0.getHeight()+10);		
		UpdateData();
	}	
	
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);	
		if(cmd.equals("DPIUD")){
			if(!Updating){
				SelectedDPI=(int)DPIUD.getSelectedIndex();
				DPIvalue=Integer.parseInt((String)DPIUD.getSelectedItem());
				RegenerateHW();
			}				
		}		
	}
	
	 public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("HUD")){
			if(!Updating){
				Hcm=(double)HUD.getValue();
				RegenerateHW();				
			}
		}
		else if(cmd.equals("WUD")){
			if(!Updating){
				Wcm=(double)WUD.getValue();
				RegenerateHW();			
			}
		}
	}
		
	public String GenerateName(){
		String res=String.format("%s_%dx%d_%dDPI",Parent.FractalName,Wmm,Hmm,DPIvalue);
		return res;
	}
	
	
	public void PrintData (BufferedWriter o) throws IOException{
		o.write("#PRINTER\n");
		String temp;
        temp=String.format("imgh %f\n",Hcm);
        temp=temp.replace(',','.');
        o.write(temp);
        temp=String.format("imgw %f\n",Wcm);
        temp=temp.replace(',','.');
        o.write(temp);
        temp=String.format("imgdpi %d\n",SelectedDPI);
	    o.write(temp);
	    o.write("#END\n");
	}
	
	
	void PrintToFile(BufferedWriter o) throws IOException{
		PrintData(o);	
	}
	
	
    
	public void ReadData(BufferedReader i) throws IOException{
		StringTokenizer st;
		while(true){
			String temp=i.readLine();
			if(temp==null) return;
			if(temp.equals("#END")) return;
			st=new StringTokenizer(temp);
			String vname=st.nextToken();
			if(vname.equals("imgh")){
				Hcm=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("imgw")){
				Wcm=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("imgdpi")){
				SelectedDPI=Integer.parseInt(st.nextToken());
			}			
			else{	
				System.out.printf("Reading PRINTER, found unknown field %s\n",vname);
			}		
		}
	}
 
	public void ReadFromFile(File f){	
		int res;
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			while(true){
				String temp=in.readLine();
				if(temp==null){
					in.close();
					return ;
				}
				if(temp.equals("#PRINTER")){
					ReadData(in);
					UpdateData();     
					in.close();	
					return ;
				}
			}			
		} catch (IOException ex) {
		}
		return;
	}
	
	void RegenerateHW(){
		Hmm=(int)round(10*Hcm);
		Wmm=(int)round(10*Wcm);	
		Hpix=(int)round(Hmm*DPIvalue/25.4);
		Wpix=(int)round(Wmm*DPIvalue/25.4);
		if(Parent.mywm!=null){
			Parent.mywm.RequestRefresh();
			Parent.mywm.ClearBackup();
		}
	}
		
	public double GetHeightCM(){
		return (double)Hmm/10;
	}
    
    public double GetWidthCM(){
		return (double)Wmm/10;
	}
	
	public int GetHeight(){
		return Hpix;
	}
    
    public int GetWidth(){
		return Wpix;
	}
	
	public int CmToPixel(double cm){
		return (int)round(cm*DPIvalue/2.54);	
	}
	
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);	
		if(cmd.equals("HUD")){
			double t=(double)HUD.getValue();
			t-=1*e.getWheelRotation();
			if(t<1) t=1;
			if(t>500) t=500;
			HUD.setValue(t);	
		}
		else if(cmd.equals("WUD")){
			double t=(double)WUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<1) t=1;
			if(t>500) t=500;
			WUD.setValue(t);	
		}
		else if(cmd.equals("DPIUD")){
			int t=(int) DPIUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>=nDPI) t=nDPI-1;
			if(t<0) t=0;
			DPIUD.setSelectedIndex(t);		
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
