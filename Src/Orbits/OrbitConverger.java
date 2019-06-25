import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.floor;
import static java.lang.Math.ceil;
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
import javax.swing.filechooser.FileNameExtensionFilter;



class conv_min implements Comb{
    //int norb;
    
    OrbitConverger host;
    
    public conv_min(OrbitConverger o){
        host=o;
    }
   
    public double Combine(double x,double y,int n) {
        double[] d=new double[6];
        double Min;
        int i;
        if (host.N==1) return host.orbs[0].getDistance(x, y, n);
        else{
            d[0]=Min=host.orbs[0].getDistance(x, y, n);
            for(i=1;i<host.N;i++){
                d[i]=host.orbs[i].getDistance(x, y, n);
                if (d[i]<Min) Min=d[i];
            }            
            return Min;
        }       
    }    
}

class conv_max implements Comb{
    //int norb;
    
    OrbitConverger host;
    
    public conv_max(OrbitConverger o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        double[] d=new double[6];
        double Max;
        int i;
        if (host.N==1) return host.orbs[0].getDistance(x, y,n);
        else{
            d[0]=Max=host.orbs[0].getDistance(x, y,n);
            for(i=1;i<host.N;i++){
                d[i]=host.orbs[i].getDistance(x, y,n);
                if (d[i]>Max) Max=d[i];
            }            
            return Max;
        }       
    }    
}

class conv_sum implements Comb{
    //int norb;
    
    OrbitConverger host;
    
    public conv_sum(OrbitConverger o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        int i;
        double Sum=0;
        for(i=0;i<host.N;i++) Sum+=host.orbs[i].getDistance(x, y,n);
        return Sum;
    }    
}



public class OrbitConverger extends JFrame implements OrbitsPanel,ActionListener,ChangeListener,MouseWheelListener,WindowListener{
	double SpaceX,SpaceY;
	boolean useRep;
	
	int nskip;
	JLabel skiplab;
	JSpinner SkipUD;
	
	
	JLabel Sxl,Syl;
	JSpinner SxUD,SyUD;
	JCheckBox RepUD;
	
	boolean has_gr;
	int bw,bh;
    int N;
    Orbit[] orbs;
    Comb actual;
    conv_min MinDist;
    conv_max MaxDist;
    conv_sum SumDist;
    int type;
    
	JCheckBox SumUD;
	boolean DoSum;
	
	JCheckBox DivUD;
	boolean Diverge;
    
     
    JLabel CombLab;   
    int CombL,CombH;
    JComboBox CombUD;
    
    boolean updating;
    BufferedImage prev;
    
    double[] pmatrix;
    
    ImageIcon ico;
    
    int dim;
    int nprev;
    double prevdim;
    
    JPanel main;
    //GUI
    JPanel P0,P1,P3;
    //P0
    JLabel OrbLab;
    JSpinner OrbUD;
    int OrbL,OrbH;
    JButton SaveBtn,LoadBtn;
    
    //P3
    JLabel picLab,IntLab;
    
    
    public OrbitConverger(boolean GRAPH){
		has_gr=GRAPH;
		DoSum=false;
		useRep=false;
		nskip=0;
		SpaceX=SpaceY=1;
		Diverge=false;
		nprev=0;
		bw=Layer.bw;bh=Layer.bh;
		updating=false;
		dim=3*bw;
		prevdim=4;
        
        if(has_gr){
			prev=new BufferedImage(dim,dim,TYPE_INT_ARGB_PRE);
			ico=new ImageIcon();
			pmatrix=new double[dim*dim];
		}
		
		
		N=1;
        orbs=new Orbit[6];
        for(int i=0;i<6;i++) orbs[i]=new Orbit(this,has_gr,i+6,true);
        MinDist=new conv_min(this);
        MaxDist=new conv_max(this);
        SumDist=new conv_sum(this);
        if(has_gr) BuildGraphics();
    }    
   
    public double getDistance(double x,double y,int n){
		double X=x;
		double Y=y;
		if(useRep){
			double Xi=X/SpaceX;
			double temp;
			if(Xi>0.5){
				temp=Xi-0.5;
				X=(-0.5+temp-floor(temp))*SpaceX;
				int tempI=(int)floor(temp);
				if(tempI%2 ==0) X=-X;
			}
			else if (Xi<-0.5){
				temp=Xi+0.5;
				X=(+0.5+temp-ceil(temp))*SpaceX;
				int tempI=(int)floor(-temp);
				if(tempI%2 ==0) X=-X;				
			}
			double Yi=Y/SpaceY;
			if(Yi>0.5){
				temp=Yi-0.5;
				Y=(-0.5+temp-floor(temp))*SpaceY;
				int tempI=(int)floor(temp);
				if(tempI%2 ==0) Y=-Y;
			}
			else if (Yi<-0.5){
				temp=Yi+0.5;
				Y=(+0.5+temp-ceil(temp))*SpaceY;
				int tempI=(int)floor(-temp);
				if(tempI%2 ==0) Y=-Y;	
			}		
		}		
        return actual.Combine(X,Y,n);
    }
  
	public void SetType(int t){
        type=t;
        switch (t){
            case 0:
                actual=MinDist;
                break;
            case 1:
                actual=MaxDist;
                break;
            case 2:
                actual=SumDist;
                break;
            default:
                actual=MinDist;
                break;                
        }
    }
    
    public int GetType(){
        return type;
    }
    
    public void PrintData (BufferedWriter o) throws IOException{
		o.write("#CONVORBITS\n");
		String temp;
        temp=String.format("Norb %d\n",N);
        o.write(temp);
        temp=String.format("type %d\n",type);
        o.write(temp);
        temp=String.format("nskip %d\n",nskip);
        o.write(temp);
        temp=String.format("dosum %b\n",DoSum);
        o.write(temp);
		temp=String.format("diverge %b\n",Diverge);
        o.write(temp);
		temp=String.format("periodic %b\n",useRep);
        o.write(temp);
        temp=String.format("spacex %f\n",SpaceX);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("spacey %f\n",SpaceY);
        temp=temp.replace(",",".");
        o.write(temp);
        o.write("#END\n");
	}
	
	void PrintToFile(BufferedWriter o) throws IOException{
		PrintData(o);
		for(int i=0;i<6;i++) orbs[i].PrintToFile(o);
	}


public void ReadData(BufferedReader i) throws IOException{
	StringTokenizer st;
	while(true){
		String temp=i.readLine();
		if(temp==null) return;
		if(temp.equals("#END")) return;
		st=new StringTokenizer(temp);
		String vname=st.nextToken();
		if(vname.equals("Norb")){
			N=Integer.parseInt(st.nextToken());
		}
		else if(vname.equals("type")){
			SetType(Integer.parseInt(st.nextToken()));
		}
		else if(vname.equals("nskip")){
			nskip=Integer.parseInt(st.nextToken());
		}
		else if(vname.equals("dosum")){
			DoSum=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("diverge")){
			Diverge=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("periodic")){
			useRep=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("spacex")){
			SpaceX=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("spacey")){
			SpaceY=Double.parseDouble(st.nextToken());
		}
		else{	
			System.out.printf("Reading ORBITS, found unknown field %s\n",vname);
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
					break;
				}
				else if(temp.equals("#CONVORBITS")){
					ReadData(in);
					in.close();	
					break;
				}
			}			
		} catch (IOException ex) {
		}
		UpdateData();
		for(int i=0;i<6;i++) orbs[i].ReadFromFile(f);		
		return;
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
		if(has_gr){
			setTitle("Convrger Orbit Editor");
			addWindowListener(this);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setLayout(new GridLayout(1,1,1,1));
			main=new JPanel();
			main.setBorder(new LineBorder(Color.BLACK));
			main.setLayout(new GridBagLayout());
			GridBagConstraints c=new GridBagConstraints();
			c.fill=GridBagConstraints.NONE;
			c.gridheight=1;
			c.gridwidth=1;
			c.weighty=0.02;
			c.weightx=0.02;
			c.gridy=-1;
			GridBagConstraints c1=new GridBagConstraints();
			c1.fill=GridBagConstraints.NONE;
			c1.gridheight=1;
			c1.gridwidth=1;
			c1.weighty=0.1;
			
			//newline
			c.gridy++;
			//P0		
			c.gridx=0;
			P0=new JPanel();
			P0.setLayout(new GridBagLayout());
			P0.setBorder(new LineBorder(Color.BLACK));
		
			c1.gridy=0;
			c1.gridx=0;
			OrbLab=new JLabel("Orbits");
			ApplySize(OrbLab,bw,bh);
			c1.gridx=0;
			P0.add(OrbLab,c1);
			OrbUD=new JSpinner();
			ApplySize(OrbUD,2*bw,bh);
			OrbUD.setName("OrbUD");
			OrbL=1;
			OrbH=6;
			OrbUD.setModel(new SpinnerNumberModel(1,1,6,1));
			OrbUD.addChangeListener(this);
			OrbUD.addMouseWheelListener(this);
			c1.gridx=1;
			P0.add(OrbUD,c1);
			
			c1.gridy=1;
			c1.gridx=0;
			CombLab=new JLabel("Combine");
			ApplySize(CombLab,1*bw,bh);
			P0.add(CombLab,c1);
			CombUD=new JComboBox();
			ApplySize(CombUD,2*bw,bh);
			CombUD.setName("CombUD");
			CombL=0;
			CombH=2;
			CombUD.setModel(new DefaultComboBoxModel<>(new String[] { "Minimum", "Maximum", "Sum"}));
			CombUD.addActionListener(this);
			CombUD.addMouseWheelListener(this);
			c1.gridx=1;
			P0.add(CombUD,c1);
			
			c1.gridy=2;
			c1.gridx=0;
			c1.gridwidth=2;
			SumUD=new JCheckBox("Sum distance");
			SumUD.setName("SumUD");
			SumUD.addActionListener(this);
			ApplySize(SumUD,2*bw,bh);
			P0.add(SumUD,c1);
			
			c1.gridy=3;
			DivUD=new JCheckBox("Enable Diverg.");
			DivUD.setName("DivUD");
			DivUD.addActionListener(this);
			ApplySize(DivUD,2*bw,bh);
			P0.add(DivUD,c1);
			c1.gridwidth=1;
        
			c1.gridy=4;
			c1.gridx=0;
			c1.gridwidth=2;
			SaveBtn=new JButton("Save Orbits");
			ApplySize(SaveBtn,3*bw,bh);
			SaveBtn.setName("SaveBtn");
			SaveBtn.addActionListener(this);
			P0.add(SaveBtn,c1);
		
			c1.gridy=5;
			c1.gridx=0;
			c1.gridwidth=2;
			LoadBtn=new JButton("Load Orbits");
			ApplySize(LoadBtn,3*bw,bh);
			LoadBtn.setName("LoadBtn");
			LoadBtn.addActionListener(this);
			P0.add(LoadBtn,c1);
        
			main.add(P0,c);
			ApplySize(P0,3*bw+10,3*bw+bh+10);
		
			//P1
			c.gridx=1;
			P1=new JPanel();
			P1.setLayout(new GridBagLayout());
			P1.setBorder(new LineBorder(Color.BLACK));
			
			c1.gridy=0;
			c1.gridx=0;
			c1.gridwidth=2;
			RepUD=new JCheckBox("Periodic Trap");
			RepUD.setName("RepUD");
			RepUD.addActionListener(this);
			ApplySize(RepUD,2*bw,bh);
			P1.add(RepUD,c1);
			c1.gridwidth=1;
			
			c1.gridy=1;
			c1.gridx=0;
			Sxl=new JLabel("X Spacing");
			ApplySize(Sxl,bw,bh);
			c1.gridx=0;
			P1.add(Sxl,c1);
			SxUD=new JSpinner();
			ApplySize(SxUD,2*bw,bh);
			SxUD.setName("SxUD");
			SxUD.setModel(new SpinnerNumberModel(0d, 0d, 50d, 0.1d));
			SxUD.setEditor(new javax.swing.JSpinner.NumberEditor(SxUD, "###0.00"));        
			SxUD.addChangeListener(this);
			SxUD.addMouseWheelListener(this);
			c1.gridx=1;
			P1.add(SxUD,c1);
			
			c1.gridy=2;
			c1.gridx=0;
			Syl=new JLabel("Y Spacing");
			ApplySize(Syl,bw,bh);
			c1.gridx=0;
			P1.add(Syl,c1);
			SyUD=new JSpinner();
			ApplySize(SyUD,2*bw,bh);
			SyUD.setName("SyUD");
			SyUD.setModel(new SpinnerNumberModel(0d, 0d, 50d, 0.1d));
			SyUD.setEditor(new javax.swing.JSpinner.NumberEditor(SyUD, "###0.00"));        
			SyUD.addChangeListener(this);
			SyUD.addMouseWheelListener(this);
			c1.gridx=1;
			P1.add(SyUD,c1);
			
			c1.gridy=3;
			c1.gridx=0;
			skiplab=new JLabel("N Skipped");
			ApplySize(skiplab,bw,bh);
			c1.gridx=0;
			P1.add(skiplab,c1);
			SkipUD=new JSpinner();
			ApplySize(SkipUD,2*bw,bh);
			SkipUD.setName("SkipUD");
			SkipUD.setModel(new SpinnerNumberModel(0, 0, null, 1));
			SkipUD.addChangeListener(this);
			SkipUD.addMouseWheelListener(this);
			c1.gridx=1;
			P1.add(SkipUD,c1);
			
			main.add(P1,c);
			ApplySize(P1,3*bw+10,3*bw+bh+10);
			
			
			//P3
			c.gridx=2;
			P3=new JPanel();
			P3.setLayout(new GridBagLayout());
			P3.setBorder(new LineBorder(Color.BLACK));
			c1.gridwidth=1;
			picLab=new JLabel("");
			ApplySize(picLab,3*bw,3*bw);
			picLab.setName("picLab");
			picLab.addMouseWheelListener(this);
			c1.gridx=c1.gridy=0;
			P3.add(picLab,c1);
			IntLab=new JLabel("");
			ApplySize(IntLab,3*bw,bh);
			c1.gridy=1;
			P3.add(IntLab,c1);
			main.add(P3,c);
			ApplySize(P3,3*bw+10,3*bw+bh+10);
		
			//new row
			c.gridy++;
			for(int i=0;i<6;i++){
				c.gridx=i;
				main.add(orbs[i],c);
			}
			ApplySize(main,19*bw+50,bh+4*bw-40+orbs[0].getHeight());
			add(main);
			setSize(main.getWidth()+10,main.getHeight()+10);
			setResizable(false);		
			UpdateData();
			//UpdatePreview();
		}
	}
    
    public void UpdateData(){
		updating=true;
		SetType(type);
		if(has_gr){
			CombUD.setSelectedIndex(type);
			OrbUD.setValue(N);
			SumUD.setSelected(DoSum);
			//SumUD.setEnabled(Diverge);
			DivUD.setSelected(Diverge);
			RepUD.setSelected(useRep);
			SxUD.setValue(SpaceX);
			SyUD.setValue(SpaceY);
			SxUD.setEnabled(useRep);
			SyUD.setEnabled(useRep);
			SkipUD.setValue(nskip);
			UpdateLock();
			String t=String.format("(%.4f,%.4f)",-prevdim/2,prevdim/2);
			IntLab.setText("Interval: "+t);
			UpdatePreview();		
		}
		updating=false;
	}
    
    
    
    private double gety(int Y){
        return -prevdim/2.+(dim-1-Y+0.5)*prevdim/(double)dim;
    }
    
    private double getx(int X){
        return -prevdim/2.+(X+0.5)*prevdim/(double)dim;
    }
    
    @Override
    public void UpdatePreview(){
		if(!has_gr) return;
        int i,j;
        double y,x;
        double Max,Min;
        double d;
        int[] prow=new int[dim];
        Max=Min=getDistance(0, 0,nprev);
        for(i=0;i<dim;i++){
            y=gety(i);
            for(j=0;j<dim;j++){
                x=getx(j);
                d=getDistance(x, y,nprev);
                pmatrix[i*dim+j]=d;
                if(d>Max) Max=d;
                if(d<Min) Min=d;
            }
        }
        int t,p;
        for(i=0;i<dim;i++){
            for(j=0;j<dim;j++){
                t=(int)(256*(1-(pmatrix[i*dim+j]-Min)/(Max-Min)));
                if (t>255) t=255;
                if(t<0) t=0;
                p=255<<24|t<<16|t<<8|t;
                prow[j]=p;                
            }
            prev.setRGB(0,i, dim, 1, prow, 0,dim);
        }
        ico.setImage(prev);
        picLab.setIcon(ico);
        picLab.updateUI();
    }
    
	public void UpdateLock(){
		if(!has_gr) return;
		for(int i=0;i<6;i++){
			boolean newena=(i<N);
			if(orbs[i].getEnabled()!=newena) orbs[i].setEnabled(newena);
		}		
    }
        
    public void actionPerformed(ActionEvent e){
		if(!has_gr) return;
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("SaveBtn")){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"ConvOrbits"));
			fc.setFileFilter(new FileNameExtensionFilter("Fractal Data file", "frc"));
			fc.showSaveDialog(this);
			File f=fc.getSelectedFile();
			if(f==null)return;
			String ptitle=f.getPath();
			if (!ptitle.endsWith(".frc")) ptitle+=".frc";
			f=new File(ptitle);// TODO add your handling code here:
			try {
				BufferedWriter o=new BufferedWriter(new FileWriter(f));
				PrintToFile(o);
				o.close();
			} catch (IOException ex) {
				//Logger.getLogger(MainFractalPanel.class.getName()).log(Level.SEVERE, null, ex);
			}			
		}
		else if(cmd.equals("LoadBtn")){
			JFileChooser fc =new JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"ConvOrbits"));
			fc.setFileFilter(new FileNameExtensionFilter("Fractal Data file", "frc"));
			fc.showOpenDialog(this);
			File f=fc.getSelectedFile();
			if(f==null)return;
			String ptitle=f.getPath();
			if (!ptitle.endsWith(".frc")) ptitle+=".frc";
			f=new File(ptitle);// TODO add your handling code here:
			ReadFromFile(f);
			UpdateData();
			UpdatePreview();			
		}
		else if(cmd.equals("CombUD")){
			if(!updating){
				SetType(CombUD.getSelectedIndex());
				UpdatePreview();			
			}
		}
		else if(cmd.equals("SumUD")){
			if(!updating){
				DoSum=SumUD.isSelected();
			}
		}
		else if(cmd.equals("DivUD")){
			if(!updating){
				Diverge=DivUD.isSelected();
				//SumUD.setEnabled(Diverge);
			}
		}
		else if(cmd.equals("RepUD")){
			if(!updating){
				useRep=RepUD.isSelected();
				SxUD.setEnabled(useRep);
				SyUD.setEnabled(useRep);
				//SumUD.setEnabled(Diverge);
			}
		}
	}
		
	public int GetNskip(){
		//System.out.printf("Sending nskip=%d\n",nskip);
		return nskip;
	}
	
    public void stateChanged(ChangeEvent e){
		if(!has_gr) return;
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("OrbUD")){
			 if (!updating){
				N=(int)OrbUD.getValue();
				UpdateLock();
				UpdatePreview();
			}	
		}	
		else if(cmd.equals("SkipUD")){
			 if (!updating){
				nskip=(int)SkipUD.getValue();
				System.out.printf("nskip=%d : ",nskip);
			}	
		}	
		else if(cmd.equals("SxUD")){
			 if (!updating){
				SpaceX=(double)SxUD.getValue();
				UpdatePreview();
			}	
		}
		else if(cmd.equals("SyUD")){
			 if (!updating){
				SpaceY=(double)SyUD.getValue();
				UpdatePreview();
			}	
		}
	}
	
	public void CloneFrom(OrbitConverger other){
		N=other.N;		
		for(int i=0;i<6;i++) orbs[i].CloneFrom(other.orbs[i]);		
    }
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		if(!has_gr) return;
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("OrbUD")){
			int t=(int)OrbUD.getValue();
			t-=e.getWheelRotation();
			if(t<OrbL) t=OrbL;
			if(t>OrbH) t=OrbH;
			OrbUD.setValue(t);			
		}
		else if(cmd.equals("SkipUD")){
			int t=(int)SkipUD.getValue();
			t-=e.getWheelRotation();
			if(t<0) t=0;
			SkipUD.setValue(t);			
		}
		else if(cmd.equals("SxUD")){
			double t=(double)SxUD.getValue();
			t-=e.getWheelRotation();
			if(t<0) t=0;
			if(t>50) t=50;
			SxUD.setValue(t);			
		}
		else if(cmd.equals("SyUD")){
			double t=(double)SyUD.getValue();
			t-=e.getWheelRotation();
			if(t<0) t=0;
			if(t>50) t=50;
			SyUD.setValue(t);			
		}
		else if(cmd.equals("picLab")){
			prevdim*=pow(2,e.getPreciseWheelRotation());
			String t=String.format("(%.4f,%.4f)",-prevdim/2,prevdim/2);
			IntLab.setText("Interval: "+t);
			nprev-=e.getWheelRotation();
			if(nprev<0) nprev=0;
			if(nprev>1000) nprev=1000;
						
		}
		else if(cmd.equals("CombUD")){
			int t=CombUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t<CombL) t=CombL;
			if(t>CombH) t=CombH;
			CombUD.setSelectedIndex(t);			
		}
		UpdatePreview();
	}
    
    //WindowListener
	public void 	windowActivated(WindowEvent e){}
	public void 	windowClosed(WindowEvent e){System.out.println("Here Closing");}
	public void 	windowClosing(WindowEvent e){
			System.out.println("Here");
			setVisible(false);
		}
	public void 	windowDeactivated(WindowEvent e){}
	public void 	windowDeiconified(WindowEvent e){}
	public void 	windowIconified(WindowEvent e){}
	public void 	windowOpened(WindowEvent e){}
    
    //******OLD METHOD*****/
    /*
    	
	
    
     public void Print(BufferedWriter o) throws IOException{
		o.write("#ORBITS\n"); //Header String
        String temp=String.format("%b\n",invert);
        o.write(temp);
        temp=String.format("%b\n",revert);
        o.write(temp);
        temp=String.format("%.9f\n",invoff);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("%.9f\n",redux);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("%d\n",N);
        o.write(temp);
        temp=String.format("%.9f\n",off);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("%d\n",ptype);
        o.write(temp);
        temp=String.format("%d\n",type);
        o.write(temp);
        orbs[0].Print(o);
        orbs[1].Print(o);
        orbs[2].Print(o);
        orbs[3].Print(o);
        orbs[4].Print(o);
        orbs[5].Print(o);        
        temp=String.format("%b\n",sumNiters);
        o.write(temp);
        temp=String.format("%.9f\n",NitersFac);
        temp=temp.replace(",",".");
        o.write(temp);
    }
    
    public int ReadFromFile(File f){
		int res;
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			while(true){
				String temp=in.readLine();
				if(temp==null){
					in.close();
					return 1;
				}
				if(temp.equals("#ORBITS")){
					res=Read(in);
					in.close();	
					return res;
				}
			}			
		} catch (IOException ex) {
		}
		return 1;
	}
	
	
	
    public int Read(BufferedReader i) throws IOException{
        String temp=i.readLine();
        if (temp==null) return 1;
        invert=Boolean.parseBoolean(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        revert=Boolean.parseBoolean(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        invoff=Double.parseDouble(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        redux=Double.parseDouble(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        N=Integer.parseInt(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        off=Double.parseDouble(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        SetPType(Integer.parseInt(temp));
        temp=i.readLine();
        if (temp==null) return 1;
        SetType(Integer.parseInt(temp));
        for(int j=0;j<6;j++) if(orbs[j].Read(i)>0) return 1;
        temp=i.readLine();
        if (temp==null) return 1;
        sumNiters=Boolean.parseBoolean(temp);
        temp=i.readLine();
        if (temp==null) return 1;
        NitersFac=Double.parseDouble(temp);
        UpdateData();
        return 0;
    }
	
	
	*/
}
