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
import javax.swing.filechooser.FileNameExtensionFilter;

interface OrbitsPanel{
	public void UpdatePreview();
}

interface Comb{
    public double Combine(double x,double y,int n);
}

class min implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public min(OrbitCombiner o){
        host=o;
    }
   
    @Override
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

class max implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public max(OrbitCombiner o){
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

class sum implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public sum(OrbitCombiner o){
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

class suminv implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public suminv(OrbitCombiner o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        int i;
        double Sum=0;
        double d;
        for(i=0;i<host.N;i++){
			d=host.orbs[i].getDistance(x, y,n);
			if(d==0) return 0;
			Sum+=1./d;
		}
        return 1/Sum;
    }    
}

class dev implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public dev(OrbitCombiner o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        int i;
        double Sum=0;
        double Sum2=0;
        for(i=0;i<host.N;i++){
			 Sum+=host.orbs[i].getDistance(x, y,n);
			 Sum2+=pow(host.orbs[i].getDistance(x, y,n),2);
		}
		double std=Sum2/host.N-pow(Sum/host.N,2);
		return 10*(1-exp(-std/4.));
    }    
}

class geo implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public geo(OrbitCombiner o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        int i;
        double Prod=1;
        //double Sum2=0;
        for(i=0;i<host.N;i++){
			 Prod*=host.orbs[i].getDistance(x, y,n);
		}
		double std=pow(Prod,1./host.N);
		return std;
    }    
}

class wav implements Comb{
    //int norb;
    
    OrbitCombiner host;
    
    public wav(OrbitCombiner o){
        host=o;
    }
   
    @Override
    public double Combine(double x,double y,int n) {
        double SumWeight=0;
        double Sum=0;
        int i;
        if (host.N==1) return 0;
        else{
            SumWeight=1./(host.orbs[0].getDistance(x, y,n)+0.001);
            for(i=1;i<host.N;i++){
				SumWeight+=1./(host.orbs[i].getDistance(x, y,n)+0.001);
				Sum+=(double)i/(host.orbs[i].getDistance(x, y,n)+0.001);
            }         
            if(SumWeight==0) return (host.N-1.)/2.;
            else return Sum/SumWeight;   
        }       
    }    
}

//Second type of interface overall orbit 
interface Path{
    public double getnext(double last,double actual);
}

class PathMin implements Path{
    public PathMin(){};
    
    @Override
    public double getnext(double last, double actual) {
        if(actual<last) return actual;
        return last;//To change body of generated methods, choose Tools | Templates.
    }    
}

class PathMax implements Path{
    public PathMax(){};
    
    @Override
    public double getnext(double last, double actual) {
        if(actual>last) return actual;
        return last;//To change body of generated methods, choose Tools | Templates.
    }    
}

class PathSum implements Path{
    public PathSum(){};
    
    @Override
    public double getnext(double last, double actual) {
        return last+actual;//To change body of generated methods, choose Tools | Templates.
    }    
}

class PathInv implements Path{
    OrbitCombiner host;
    public PathInv(OrbitCombiner o){
        host=o;
    }
    
    @Override
    public double getnext(double last, double actual) {
        return last+1./(actual+host.off);//To change body of generated methods, choose Tools | Templates.
    }    
}

class PathStr implements Path{
	OrbitCombiner host;
    public PathStr(OrbitCombiner o){
        host=o;
    }
    @Override
    public double getnext(double last, double actual) {
        return last+0.5*sin(actual*host.off)+0.5;//To change body of generated methods, choose Tools | Templates.
    }    
}

public class OrbitCombiner extends JFrame implements OrbitsPanel,ActionListener,ChangeListener,MouseWheelListener,WindowListener{
	boolean has_gr;
	int bw,bh;
    boolean invert;
    boolean revert;
    double invoff;
    double redux;
    boolean sumNiters;
    double NitersFac;
    int N;
    double off;
    Orbit[] orbs;
    Comb actual;
    min tmin;
    max tmax;
    sum tsum;
    suminv tinv;
    dev tdev;
    geo tgeo;
    wav twav;
    Path actualp;
    PathMin pmin;
    PathMax pmax;
    PathSum psum;
    PathInv pinv;
    PathStr pstr;
    int ptype;
    int type;
    
    boolean updating;
    BufferedImage prev;
    double[] pmatrix;
    ImageIcon ico;
    int dim;
    int nprev;
    double prevdim;
    
    JPanel main;
    //GUI
    JPanel P0,P1,P2,P3;
    //P0
    JLabel CombLab,OrbLab,PathLab,OffLab;
    JSpinner OrbUD,OffUD;
    int CombL,CombH,PathL,PathH,OrbL,OrbH;
    JComboBox CombUD,PathUD;
    
    //P1
    JLabel InvLab,InvOffLab,RedLab,RevLab;
    JCheckBox InvUD,RevUD;
    JSpinner InvOffUD,RedUD;
    
    //P2
    JLabel SumLab,AttLab;
    JCheckBox SumUD;
    JSpinner AttUD;
    JButton SaveBtn,LoadBtn;
    
    //P3
    JLabel picLab,IntLab;
    
    
    public OrbitCombiner(boolean GRAPH){
		has_gr=GRAPH;
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
        type=0;
        off=0.01;
        orbs=new Orbit[6];
        for(int i=0;i<6;i++) orbs[i]=new Orbit(this,has_gr,i,false);
        tmin=new min(this);
        tmax=new max(this);
        tsum=new sum(this);
        tdev=new dev(this);
        tgeo=new geo(this);
        tinv=new suminv(this);
        twav=new wav(this);
        actual=tmin;
        pmin=new PathMin();
        pmax=new PathMax();
        psum=new PathSum();
        pinv=new PathInv(this);
        pstr=new PathStr(this);
        actualp=pmin;
        ptype=0;
        invert=false;
        revert=false;
        sumNiters=false;
        NitersFac=1;
        invoff=0.001;
        redux=1;
        if(has_gr) BuildGraphics();
    }
    
    public void SetType(int t){
        type=t;
        switch (t){
            case 0:
                actual=tmin;
                break;
            case 1:
                actual=tmax;
                break;
            case 2:
                actual=tsum;
                break;
            case 3:
                actual=tdev;
                break;
            case 4:
                actual=tgeo;
                break;
            case 5:
                actual=tinv;
                break;
            case 6:
                actual=twav;
                break;
            default:
                actual=tmin;
                break;                
        }
    }
    
    public int GetType(){
        return type;
    }
    
    public void SetPType(int p){
        ptype=p;
        switch (p){
            case 0:
                actualp=pmin;
                break;
            case 1:
                actualp=pmax;
                break;
            case 2:
                actualp=psum;
                break;
            case 3:
                actualp=pinv;
                break;
            case 4:
                actualp=pstr;
                break;                
            default:
                actualp=pmin;
                break;                
        }
		if(has_gr){
			if(ptype>=3) OffUD.setEnabled(true);
			else OffUD.setEnabled(false);
			if(ptype==4)OffLab.setText("Density");
			else OffLab.setText("Offset");
		}
    }
    
    public int GetPType(){
        return ptype;
    }
    
    public double getNext(double last,double Actual){
        return actualp.getnext(last, Actual);
    }
    
    public double getDistance(double x,double y,int n){
        return actual.Combine(x, y,n);
    }
  
    
    public void PrintData (BufferedWriter o) throws IOException{
		o.write("#ORBITS\n");
		String temp;
        temp=String.format("invert %b\n",invert);
        o.write(temp);
        temp=String.format("revert %b\n",revert);
        o.write(temp);
        temp=String.format("invoff %.9f\n",invoff);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("redux %.9f\n",redux);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("Norb %d\n",N);
        o.write(temp);
        temp=String.format("off %.9f\n",off);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("ptype %d\n",ptype);
        o.write(temp);
        temp=String.format("type %d\n",type);
        o.write(temp);          
        temp=String.format("sumNiters %b\n",sumNiters);
        o.write(temp);
        temp=String.format("NitersFac %.9f\n",NitersFac);
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
		if(vname.equals("invert")){
			invert=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("revert")){
			revert=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("invoff")){
			invoff=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("redux")){
			redux=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("Norb")){
			N=Integer.parseInt(st.nextToken());
		}
		else if(vname.equals("off")){
			 off=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("ptype")){
			SetPType(Integer.parseInt(st.nextToken()));
		}
		else if(vname.equals("type")){
			SetType(Integer.parseInt(st.nextToken()));
		}
		else if(vname.equals("sumNiters")){
			sumNiters=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("NitersFac")){
			NitersFac=Double.parseDouble(st.nextToken());
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
				else if(temp.equals("#ORBITS")){
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
			setTitle("Orbit Editor");
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
		CombLab=new JLabel("Combine");
		ApplySize(CombLab,1*bw,bh);
		P0.add(CombLab,c1);
		CombUD=new JComboBox();
		ApplySize(CombUD,2*bw,bh);
		CombUD.setName("CombUD");
		CombL=0;
		CombH=6;
		CombUD.setModel(new DefaultComboBoxModel<>(new String[] { "Minimum", "Maximum", "Sum", "Std Dev", "Geom. Avg", "Inverted Sum", "Weighted Average"}));
        CombUD.addActionListener(this);
        CombUD.addMouseWheelListener(this);
        c1.gridx=1;
        P0.add(CombUD,c1);
       
        c1.gridy=1;
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
			
		c1.gridy=2;
		c1.gridx=0;
		PathLab=new JLabel("Path Op.");
		ApplySize(PathLab,1*bw,bh);
		P0.add(PathLab,c1);
		PathUD=new JComboBox();
		ApplySize(PathUD,2*bw,bh);
		PathUD.setName("PathUD");
		PathL=0;
		PathH=4;
		PathUD.setModel(new DefaultComboBoxModel<>(new String[] { "Minimum", "Maximum", "Sum", "Inverted Sum","Stripes"}));
        PathUD.addActionListener(this);
        PathUD.addMouseWheelListener(this);
        c1.gridx=1;
        P0.add(PathUD,c1);
		
		c1.gridy=3;
        c1.gridx=0;
		OffLab=new JLabel("Offset");
		ApplySize(OffLab,bw,bh);
		c1.gridx=0;
		P0.add(OffLab,c1);
		OffUD=new JSpinner();
		ApplySize(OffUD,2*bw,bh);
		OffUD.setName("OffUD");
		OffUD.setModel(new SpinnerNumberModel(0.1d,0.0d,null,0.001d));
		OffUD.addChangeListener(this);
		OffUD.addMouseWheelListener(this);
		c1.gridx=1;
		P0.add(OffUD,c1);
		main.add(P0,c);
		ApplySize(P0,3*bw+10,3*bw+bh+10);
		
		//P1		
		c.gridx=1;
		P1=new JPanel();
		P1.setLayout(new GridBagLayout());
		P1.setBorder(new LineBorder(Color.BLACK));
		
		
		c1.gridy=0;
		c1.gridx=0;
		InvLab=new JLabel("Invert");
		ApplySize(InvLab,1*bw,bh);
		P1.add(InvLab,c1);
		InvUD=new JCheckBox();
		ApplySize(InvUD,2*bw,bh);
		InvUD.setName("InvUD");
		InvUD.addActionListener(this);
        c1.gridx=1;
        P1.add(InvUD,c1);
       
        c1.gridy=1;
        c1.gridx=0;
		InvOffLab=new JLabel("Inv. Offset");
		ApplySize(InvOffLab,bw,bh);
		c1.gridx=0;
		P1.add(InvOffLab,c1);
		InvOffUD=new JSpinner();
		ApplySize(InvOffUD,2*bw,bh);
		InvOffUD.setName("InvOffUD");
		InvOffUD.setModel(new SpinnerNumberModel(0.1d,0.0d,null,0.001));
		InvOffUD.addChangeListener(this);
		InvOffUD.addMouseWheelListener(this);
		c1.gridx=1;
		P1.add(InvOffUD,c1);
			
		c1.gridy=2;
        c1.gridx=0;
		RedLab=new JLabel("Reduction");
		ApplySize(RedLab,bw,bh);
		c1.gridx=0;
		P1.add(RedLab,c1);
		RedUD=new JSpinner();
		ApplySize(RedUD,2*bw,bh);
		RedUD.setName("RedUD");
		RedUD.setModel(new SpinnerNumberModel(1.0d,0.0d,null,0.01));
		RedUD.addChangeListener(this);
		RedUD.addMouseWheelListener(this);
		c1.gridx=1;
		P1.add(RedUD,c1);
		
		c1.gridy=3;
		c1.gridx=0;
		RevLab=new JLabel("Revert");
		ApplySize(RevLab,1*bw,bh);
		P1.add(RevLab,c1);
		RevUD=new JCheckBox();
		ApplySize(RevUD,2*bw,bh);
		RevUD.setName("RevUD");
		RevUD.addActionListener(this);
        c1.gridx=1;
        P1.add(RevUD,c1);
        
		main.add(P1,c);
		ApplySize(P1,3*bw+10,3*bw+bh+10);
		
		//P2		
		c.gridx=2;
		P2=new JPanel();
		P2.setLayout(new GridBagLayout());
		P2.setBorder(new LineBorder(Color.BLACK));
		
		
		c1.gridy=0;
		c1.gridx=0;
		SumLab=new JLabel("Sum Iterations");
		ApplySize(SumLab,1*bw+30,bh);
		P2.add(SumLab,c1);
		SumUD=new JCheckBox();
		ApplySize(SumUD,2*bw-30,bh);
		SumUD.setName("SumUD");
		SumUD.addActionListener(this);
        c1.gridx=1;
        P2.add(SumUD,c1);
       
        c1.gridy=1;
        c1.gridx=0;
		AttLab=new JLabel("Attenuation");
		ApplySize(AttLab,bw+30,bh);
		c1.gridx=0;
		P2.add(AttLab,c1);
		AttUD=new JSpinner();
		ApplySize(AttUD,2*bw-30,bh);
		AttUD.setName("AttUD");
		AttUD.setModel(new SpinnerNumberModel(1.0d,0.0d,null,0.001));
		AttUD.addChangeListener(this);
		AttUD.addMouseWheelListener(this);
		c1.gridx=1;
		P2.add(AttUD,c1);
			
		c1.gridy=2;
        c1.gridx=0;
		c1.gridwidth=2;
		SaveBtn=new JButton("Save Orbits");
		ApplySize(SaveBtn,3*bw,bh);
		SaveBtn.setName("SaveBtn");
		SaveBtn.addActionListener(this);
		P2.add(SaveBtn,c1);
		
		c1.gridy=3;
		c1.gridx=0;
		c1.gridwidth=2;
		LoadBtn=new JButton("Load Orbits");
		ApplySize(LoadBtn,3*bw,bh);
		LoadBtn.setName("LoadBtn");
		LoadBtn.addActionListener(this);
		P2.add(LoadBtn,c1);
        
		main.add(P2,c);
		ApplySize(P2,3*bw+10,3*bw+bh+10);
		
		//P3
		c.gridx=3;
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
			UpdateLock();
		}
		SetPType(ptype);
		if(has_gr){
			PathUD.setSelectedIndex(ptype);
			OffUD.setValue(off);
			InvUD.setSelected(invert);
			InvOffUD.setEnabled(invert);
			RevUD.setEnabled(invert);
			InvOffUD.setValue(invoff);
			RedUD.setValue(redux);
			RevUD.setSelected(revert);
			SumUD.setSelected(sumNiters);
			AttUD.setValue(NitersFac);
			AttUD.setEnabled(sumNiters);
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
		if(cmd.equals("CombUD")){
			if(!updating){
				SetType(CombUD.getSelectedIndex());
				UpdatePreview();			
			}
		}
		else if(cmd.equals("PathUD")){
			if(!updating){
				SetPType(PathUD.getSelectedIndex());
			}
		}
		else if(cmd.equals("InvUD")){
			if(!updating){
				invert=InvUD.isSelected();
				InvOffUD.setEnabled(invert);
				RevUD.setEnabled(invert);
			}
		}
		else if(cmd.equals("RevUD")){
			if(!updating){
				revert=RevUD.isSelected();
			}
		}
		else if(cmd.equals("SumUD")){
			if(!updating){
				sumNiters=SumUD.isSelected();
				AttUD.setEnabled(sumNiters);
			}
		}
		else if(cmd.equals("SaveBtn")){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Orbits"));
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
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Orbits"));
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
		else if (cmd.equals("OffUD")){
			if(!updating){
				off=(double)OffUD.getValue();
			}
		}
		else if (cmd.equals("InvOffUD")){
			if(!updating){
				invoff=(double)InvOffUD.getValue();
			}
		}
		else if (cmd.equals("RedUD")){
			if(!updating){
				redux=(double)RedUD.getValue();
			}
		}
		else if (cmd.equals("AttUD")){
			if(!updating){
				NitersFac=(double)AttUD.getValue();
			}
		}		
	}
	
	public void CloneFrom(OrbitCombiner other){
		invert=other.invert;
		revert=other.revert;
		invoff=other.invoff;
		redux=other.redux;
		sumNiters=other.sumNiters;
		NitersFac=other.NitersFac;
		N=other.N;
		off=other.off;
		for(int i=0;i<6;i++) orbs[i].CloneFrom(other.orbs[i]);
		SetType(other.type);
		SetPType(other.ptype);
    }
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		if(!has_gr) return;
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("CombUD")){
			int t=CombUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t<CombL) t=CombL;
			if(t>CombH) t=CombH;
			CombUD.setSelectedIndex(t);			
		}
		else if(cmd.equals("OrbUD")){
			int t=(int)OrbUD.getValue();
			t-=e.getWheelRotation();
			if(t<OrbL) t=OrbL;
			if(t>OrbH) t=OrbH;
			OrbUD.setValue(t);			
		}
		else if(cmd.equals("PathUD")){
			int t=PathUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t<PathL) t=PathL;
			if(t>PathH) t=PathH;
			PathUD.setSelectedIndex(t);		
		}
		else if(cmd.equals("OffUD")){
			if(OffUD.isEnabled()){
				double t=(double)OffUD.getValue();
				t-=0.1*e.getPreciseWheelRotation();
				if(t<0)t=0;
				OffUD.setValue(t);			
			}
		}
		else if(cmd.equals("InvOffUD")){
			if(InvOffUD.isEnabled()){
				double t=(double)InvOffUD.getValue();
				t-=0.1*e.getPreciseWheelRotation();
				if(t<0)t=0;
				InvOffUD.setValue(t);
			}
		}
		else if(cmd.equals("RedUD")){
			double t=(double)RedUD.getValue();
			t-=1*e.getPreciseWheelRotation();
			if(t<0)t=0;
			RedUD.setValue(t);			
		}
		else if(cmd.equals("AttUD")){
			if(AttUD.isEnabled()){
				double t=(double)AttUD.getValue();
				t-=1*e.getPreciseWheelRotation();
				if(t<0)t=0;
				AttUD.setValue(t);				
			}		
		}
		else if(cmd.equals("picLab")){
			prevdim*=pow(2,e.getPreciseWheelRotation());
			String t=String.format("(%.4f,%.4f)",-prevdim/2,prevdim/2);
			IntLab.setText("Interval: "+t);
			nprev-=e.getWheelRotation();
			if(nprev<0) nprev=0;
			if(nprev>1000) nprev=1000;
			UpdatePreview();			
		}
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
