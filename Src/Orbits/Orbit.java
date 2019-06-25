import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.atan2;

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


public class Orbit extends JPanel implements ActionListener,MouseWheelListener,ChangeListener {
	OrbitManager obm;
	int Index;
	int bw,bh;
    Trap trap;
    boolean isconv;
    boolean Ena;
    boolean Modulated;
    boolean Inverted;
    boolean Evolutive;
    double modspeed;
    double[] params;
    double Gain;
    double Decay;
    boolean has_gr;
    private int type;
   
   
	OrbitsPanel parent;
    boolean updating;
    //boolean Enabled;
    BufferedImage prev;
    double[] pmatrix;
    ImageIcon ico;
    int dim;
    double prevdim;
    
    //graphics
    JLabel TypLab;
    JComboBox TypUD;
    int TypL,TypH;
    
    JLabel GainLab;
    JSpinner GainUD;
    
    JLabel DecayLab;
    JSpinner DecayUD;
    
    
    
    //JLabel []ParLab;
    //JSpinner []ParUD;
    
    OrbitParameter[] Params;
    
    JLabel prevLab;
    JLabel XLab,YLab;
    
    JLabel ModLab;
    JCheckBox ModUD;
    
    JLabel InvLab;
    JCheckBox InvUD;
    
    JLabel EvoLab;
    JCheckBox EvoUD;
    
    JLabel ModSpLab;
    JSpinner ModSpUD;
    
    public Orbit(OrbitsPanel p, boolean has_GR,int iii ,boolean conv){
		isconv=conv;
		Index=iii;
		has_gr=has_GR;
		obm=new OrbitManager();
		parent=p;
		updating=false;
		Gain=Decay=1;
		bw=(Layer.bw*3)/5;bh=Layer.bh;
		params=new double[10];
        for(int i=0;i<10;i++) params[i]=0;
        type=0;
        trap=new TPoint();
        Modulated=false;
        Inverted=false;
        Evolutive=false;
        prevdim=4;
        dim=5*bw;
        if(has_gr){
			prev=new BufferedImage(dim,dim,TYPE_INT_ARGB_PRE);
			ico=new ImageIcon();
			pmatrix=new double[dim*dim];
			BuildGraphics();
		}
		else{
			Params=new OrbitParameter[6];
			for(int i=0;i<6;i++){
				Params[i]=new OrbitParameter(i,bw,bh,this);
			}	
		}
		setEnabled(false);
	}
    
    public void SetModulated(boolean val){
        Modulated=val;
    }
    
    public boolean GetModulated(){
        return Modulated;
    }
    
    public void SetEvolutive(boolean val){
        Evolutive=val;
        for(int i=0;i<6;i++) Params[i].Set_Speed(val);
	}
    
    public boolean GetEvolutive(){
        return Evolutive;
    }
     
    public void Regenerate(){
		int N=obm.GetData(type).GetCount();
		for(int i=0;i<N;i++){
			trap.UpdateValues(i,Params[i].GetValue(),Params[i].GetSpeed());
		}
        trap.UpdateParams(0,isconv);
    }
    
    public void SetType(int newType){
        type=newType;
        trap=obm.GenerateOrbit(type);
        //update parameters
        OrbitData od=obm.GetData(type);
        int N=od.GetCount();
        for(int i=0;i<N;i++){
			Params[i].Rebuild(od);
			if(has_gr) Params[i].SetEnabled(true);
		}
		if(has_gr){
			if(N<5){
				for(int i=N;i<6;i++)Params[i].SetEnabled(false);
			}
		}
        Regenerate();
    }
     public void UpdateLock(){
		OrbitData od=obm.GetData(type);
        int N=od.GetCount();
        for(int i=0;i<6;i++){
			Params[i].Rebuild(od);
			if(has_gr) Params[i].SetEnabled(false);
		}
        for(int i=0;i<N;i++){
		//	Params[i].Rebuild(od);
			if(has_gr) Params[i].SetEnabled(true);
			//if(has_gr) Params[i].SetEnabled(true);
		}
		if(has_gr){
			Regenerate();
			updatePreview();
		}
		
	}
    
    public int GetType(){
        return type;
    }
    
    
    public double getDistance(double x,double y,int N){
        if(Evolutive) trap.UpdateParams(N,isconv);
        double res=trap.getDist(x, y);
        if(Modulated) res=1-(cos(modspeed*res)+1)*exp(-0.4*res)/2;
        if(!isconv){			
			res=(1-exp(-res/Decay));
			if(Inverted) res=1-res;
			res*=Gain;
		}
        return res;
    }
    
  
    
        //Save/load function
    
	  
  
    
	public void PrintData (BufferedWriter o) throws IOException{
		String temp;
		temp=String.format("#ORBIT%d\n",Index);
		o.write(temp);		
        temp=String.format("type %d\n", type);
        o.write(temp);
        temp=String.format("Modulated %b\n",Modulated);
        o.write(temp);
        
        temp=String.format("inverted %b\n",Inverted);
        o.write(temp);
        temp=String.format("evolutive %b\n",Evolutive);
        o.write(temp);
        temp=String.format("Gain %.9f\n",Gain);
        temp=temp.replace(",",".");
        o.write(temp);
        temp=String.format("Decay %.9f\n",Decay);
        temp=temp.replace(",",".");
        o.write(temp);        
        for(int i=0;i<6;i++){
            temp=String.format("params %d %.9f %.9f\n",i,Params[i].GetValue(),Params[i].GetSpeed());
            temp=temp.replace(",",".");
            o.write(temp);
        }
        temp=String.format("modspeed %.9f\n",modspeed);
        temp=temp.replace(",",".");
        o.write(temp);
		o.write("#END\n");
	}
	
	void PrintToFile(BufferedWriter o) throws IOException{
		PrintData(o);			
	}

	public void ReadData(BufferedReader i) throws IOException{
		String ccomp=String.format("#ORBIT%d",Index);
		StringTokenizer st;
		while(true){
			String temp=i.readLine();
			if(temp==null) return;
			if(temp.equals("#END")) return;
			st=new StringTokenizer(temp);
			String vname=st.nextToken();
			if(vname.equals("type")){
				SetType(Integer.parseInt(st.nextToken()));
			}
			else if(vname.equals("Modulated")){
				Modulated=Boolean.parseBoolean(st.nextToken());				
			}
			else if(vname.equals("inverted")){
				Inverted=Boolean.parseBoolean(st.nextToken());	
			}
			else if(vname.equals("evolutive")){
				Evolutive=Boolean.parseBoolean(st.nextToken());	
			}
			else if(vname.equals("Gain")){
				Gain=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("Decay")){
				Decay=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("params")){
				int index=Integer.parseInt(st.nextToken());
				if(index>=6) continue;
				Params[index].SetValue(Double.parseDouble(st.nextToken()));
				Params[index].SetSpeed(Double.parseDouble(st.nextToken()));
				System.out.printf("Setting parameter %d: %f %f\n",index,Params[index].GetValue(),Params[index].GetSpeed());
			}
			else if(vname.equals("modspeed")){
				modspeed=Double.parseDouble(st.nextToken());
			}
			else{	
				System.out.printf("Reading %s, found unknown field %s\n",ccomp,vname);
			}		
		}	
	}
 
public void ReadFromFile(File f){	
		int res;
		String ccomp=String.format("#ORBIT%d",Index);
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			while(true){
				String temp=in.readLine();
				if(temp==null){
					in.close();
					break;
				}
				else if(temp.equals(ccomp)){
					System.out.printf("Found ORbit %s\n",ccomp);
					ReadData(in);
					in.close();	
					break;
				}
			}			
		} catch (IOException ex) {
		}		
		Regenerate();
        updateData_NT();
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
		setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.02;
		c.gridy=-1;
		
		//newline
		
		c.gridy++;
		TypLab=new JLabel("Type");
		ApplySize(TypLab,2*bw,bh);
		c.gridx=0;
		add(TypLab,c);
		TypUD=new JComboBox();
		ApplySize(TypUD,3*bw,bh);
		TypUD.setName("TypUD");
		TypL=0;
		TypH=obm.GetOrbitsCount();
		TypUD.setModel(new DefaultComboBoxModel<>(obm.GetListOfNames()));
        TypUD.addActionListener(this);
        TypUD.addMouseWheelListener(this);
        c.gridx=1;
        add(TypUD,c);
        
        if(!isconv){
        //newline
		c.gridy++;
		GainLab=new JLabel("Gain");
		ApplySize(GainLab,2*bw,bh);
		c.gridx=0;
		add(GainLab,c);
		GainUD=new JSpinner();
		ApplySize(GainUD,3*bw,bh);
		GainUD.setName("GainUD");
		GainUD.setModel(new SpinnerNumberModel(0.0d,0.0d,null,0.01d));
		GainUD.addChangeListener(this);
		GainUD.addMouseWheelListener(this);
		c.gridx=1;
		add(GainUD,c);				
        
        c.gridy++;
		DecayLab=new JLabel("Decay");
		ApplySize(DecayLab,2*bw,bh);
		c.gridx=0;
		add(DecayLab,c);
		DecayUD=new JSpinner();
		ApplySize(DecayUD,3*bw,bh);
		DecayUD.setName("DecayUD");
		DecayUD.setModel(new SpinnerNumberModel(0.0d,0.0d,null,0.01d));
		DecayUD.addChangeListener(this);
		DecayUD.addMouseWheelListener(this);
		c.gridx=1;
		add(DecayUD,c);	
		}
        
        Params=new OrbitParameter[6];
         for(int i=0;i<6;i++){
			c.gridy++;
			c.gridwidth=2;
			Params[i]=new OrbitParameter(i,bw,bh,this);
			c.gridx=0;
			add(Params[i],c);
		}
		c.gridwidth=1;
		if(!isconv){		
		c.gridy++;
		EvoLab=new JLabel("Evolve");
        ApplySize(EvoLab,2*bw,bh);
		c.gridx=0;
		add(EvoLab,c);
		EvoUD=new JCheckBox("");
		EvoUD.setSelected(false);
		ApplySize(EvoUD,3*bw,bh);
		EvoUD.setName("EvoUD");
		EvoUD.addActionListener(this);
		c.gridx=1;
		add(EvoUD,c);	
		
		
		
		//newline
		c.gridy++;
		InvLab=new JLabel("Invert");
        ApplySize(InvLab,2*bw,bh);
		c.gridx=0;
		add(InvLab,c);
		InvUD=new JCheckBox("");
		InvUD.setSelected(false);
		ApplySize(InvUD,3*bw,bh);
		InvUD.setName("InvUD");
		InvUD.addActionListener(this);
		c.gridx=1;
		add(InvUD,c);	
		}
		//newline
		c.gridy++;
		ModLab=new JLabel("Modulate");
        ApplySize(ModLab,2*bw,bh);
		c.gridx=0;
		add(ModLab,c);
		ModUD=new JCheckBox("");
		ModUD.setSelected(false);
		ApplySize(ModUD,3*bw,bh);
		ModUD.setName("ModUD");
		ModUD.addActionListener(this);
		c.gridx=1;
		add(ModUD,c);	
		
		//newline
		c.gridy++;
		ModSpLab=new JLabel("Mod Speed");
		ApplySize(ModSpLab,2*bw,bh);
		c.gridx=0;
		add(ModSpLab,c);
		ModSpUD=new JSpinner();
		ApplySize(ModSpUD,3*bw,bh);
		ModSpUD.setName("ModSpUD");
		ModSpUD.setModel(new SpinnerNumberModel(0.0d,null,null,0.01d));
		ModSpUD.addChangeListener(this);
		ModSpUD.addMouseWheelListener(this);
		c.gridx=1;
		add(ModSpUD,c);					
		
		
		//newline
		c.gridy++;
		prevLab=new JLabel();
		prevLab.setName("prevlab");
		prevLab.addMouseWheelListener(this);
		ApplySize(prevLab,5*bw,5*bw);
		c.gridx=0;
		c.gridwidth=2;
		add(prevLab,c);
		
		//newline
		c.gridy++;
		XLab=new JLabel();
		ApplySize(XLab,5*bw,bh);
		c.gridx=0;
		c.gridwidth=2;
		add(XLab,c);
		
		//newline
		c.gridy++;
		YLab=new JLabel();
		ApplySize(YLab,5*bw,bh);
		c.gridx=0;
		c.gridwidth=2;
		add(YLab,c);
		ApplySize(this,5*bw+10,13*bh+5*bw+50);
		updateData();		
	}
	
	public void updateData(){
        updating=true;
        if(has_gr){
			TypUD.setSelectedIndex(GetType());
		}
        SetType(GetType());
        if(has_gr){
			ModUD.setSelected(GetModulated());
			ModSpUD.setValue(modspeed);
			if(!isconv){
				DecayUD.setValue(Decay);
				GainUD.setValue(Gain);
				InvUD.setSelected(Inverted);
				EvoUD.setSelected(Evolutive);
			}
		}
        for(int i=0;i<6;i++) Params[i].UpdateData();
        if(has_gr){			
			updatePreview();
			XLab.setText(String.format("X interval: (%.4f,%.4f)",-prevdim/2,prevdim/2));
			YLab.setText(String.format("Y interval: (%.4f,%.4f)",-prevdim/2,prevdim/2));
		}
        updating=false;
    }
   	
   	public void updateData_NT(){    
		updating=true;
		if(has_gr){
			TypUD.setSelectedIndex(GetType());
		}
		if(has_gr){
			ModUD.setSelected(GetModulated());
			ModSpUD.setValue(modspeed);
			if(!isconv){
				DecayUD.setValue(Decay);
				GainUD.setValue(Gain);
				InvUD.setSelected(Inverted);
				EvoUD.setSelected(Evolutive);
			}
		}
        for(int i=0;i<6;i++) Params[i].UpdateData();
        if(has_gr){
			updatePreview();
			XLab.setText(String.format("X interval: (%.4f,%.4f)",-prevdim/2,prevdim/2));
			YLab.setText(String.format("Y interval: (%.4f,%.4f)",-prevdim/2,prevdim/2));
		}
		updating=false;
	}
		
    public void setEnabled(boolean enabled){
		Ena=enabled;
		if(!has_gr) return;
		ModSpUD.setEnabled(enabled);
		ModUD.setEnabled(enabled);
		if(!isconv){
			GainUD.setEnabled(enabled);
			DecayUD.setEnabled(enabled);
			InvUD.setEnabled(enabled);
		    EvoUD.setEnabled(enabled);
        }
        TypUD.setEnabled(enabled);
        for(int i=0;i<6;i++) Params[i].SetEnabled(enabled);
       
        prevLab.setEnabled(enabled);
		if (enabled)   UpdateLock();
    }
    
    public boolean getEnabled(){
		return Ena;
	}
		
    private double gety(int Y){
        return -prevdim/2.+(dim-1-Y+0.5)*prevdim/(double)dim;
    }
    
    private double getx(int X){
        return -prevdim/2.+(X+0.5)*prevdim/(double)dim;
    }
    
    public void updatePreview(){
		if(!has_gr) return;
		if(trap!=null) trap.UpdateParams(0,isconv);
        int i,j;
        double y,x;
        double max,min;
        double d;
        int[] prow=new int[dim];
        max=min=getDistance(0, 0,0);
        for(i=0;i<dim;i++){
            y=gety(i);
            for(j=0;j<dim;j++){
                x=getx(j);
                d=getDistance(x, y,0);
                pmatrix[i*dim+j]=d;
                if(d>max) max=d;
                if(d<min) min=d;
            }
        }
        int t,p;
        for(i=0;i<dim;i++){
            for(j=0;j<dim;j++){
                t=(int)(256*(1-(pmatrix[i*dim+j]-min)/(max-min)));
                if (t>255) t=255;
                if(t<0) t=0;
                p=255<<24|t<<16|t<<8|t;
                prow[j]=p;                
            }
            prev.setRGB(0,i, dim, 1, prow, 0,dim);
        }
        ico.setImage(prev);
        prevLab.setIcon(ico);
        prevLab.updateUI();
        if(!updating)parent.UpdatePreview();
    }
    
    //listener functions
	//MouseWheelListener
	public void mouseWheelMoved(MouseWheelEvent e){
		if(!has_gr) return;
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("prevlab")){
			if(prevLab.isEnabled()){
				prevdim*=pow(2,e.getPreciseWheelRotation());
				String t=String.format("(%.4f,%.4f)",-prevdim/2,prevdim/2);
				XLab.setText("X Interval: "+t);
				YLab.setText("Y Interval: "+t);
				updatePreview();
			}
		}
		else if(cmd.equals("TypUD")){
			if(TypUD.isEnabled()){
				int t=(int) TypUD.getSelectedIndex();
				t+=e.getWheelRotation();
				if(t>TypH) t=TypH;
				if(t<TypL) t=TypL;
				TypUD.setSelectedIndex(t);			
			}
		}		
		else if(cmd.equals("ModSpUD")){
			if(ModSpUD.isEnabled()){
				double t=(double)ModSpUD.getValue();
				t-=0.25*e.getWheelRotation();
				ModSpUD.setValue(t);			
			}
		}
		else if(cmd.equals("GainUD")){
			if(GainUD.isEnabled()){
				double t=(double)GainUD.getValue();
				t-=0.25*e.getWheelRotation();
				if(t<0) t=0;
				GainUD.setValue(t);			
			}
		}
		else if(cmd.equals("DecayUD")){
			if(DecayUD.isEnabled()){
				double t=(double)DecayUD.getValue();
				t-=0.25*e.getWheelRotation();
				if(t<0) t=0;
				DecayUD.setValue(t);			
			}
		}
	}
		
	//changeListener
	public void stateChanged(ChangeEvent e){
		if(!has_gr) return;
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("ModSpUD")){
			if(!updating){
				modspeed=(double)ModSpUD.getValue();
				//Regenerate();
				updatePreview();
			}
		}
		if(cmd.equals("GainUD")){
			if(!updating){
				Gain=(double)GainUD.getValue();
				//Regenerate();
				updatePreview();
			}
		}
		if(cmd.equals("DecayUD")){
			if(!updating){
				Decay=(double)DecayUD.getValue();
				//Regenerate();
				updatePreview();
			}
		}
	}
	
	//actionListener
	public void actionPerformed(ActionEvent e){
		if(!has_gr) return;
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("TypUD")){
			if(!updating){
				SetType((int)TypUD.getSelectedIndex());
				UpdateLock();
				updatePreview();
			}//UpdateLock();
		}
		else if(cmd.equals("ModUD")){
			if(!updating){
				Modulated=ModUD.isSelected();
				updatePreview();
			}			
		}
		else if(cmd.equals("InvUD")){
			if(!updating){
				Inverted=InvUD.isSelected();
				updatePreview();
			}			
		}
		else if(cmd.equals("EvoUD")){
			if(!updating){
				SetEvolutive(EvoUD.isSelected());
				updatePreview();
			}			
		}
	}
    
    public void CloneFrom(Orbit other){
		SetType(other.type);
		Modulated=other.Modulated;
		Inverted=other.Inverted;
		Evolutive=other.Evolutive;
		modspeed=other.modspeed;
		Gain=other.Gain;
		Decay=other.Decay;
		for(int i=0;i<6;i++){
			Params[i].SetValue(other.Params[i].GetValue());
			Params[i].SetSpeed(other.Params[i].GetSpeed());
		}
		Regenerate();
    }
    
    
    /*******OLD METHODS ******/
    /*
      public void Print(BufferedWriter o) throws IOException{
        String temp;
        temp=String.format("%d\n", type);
        o.write(temp);
        temp=String.format("%b\n",Modulated);
        o.write(temp);
        for(int i=0;i<6;i++){
            temp=String.format("%.9f\n",Params[i].GetValue());
            temp=temp.replace(",",".");
            o.write(temp);
        }
        temp=String.format("%.9f\n",modspeed);
        temp=temp.replace(",",".");
        o.write(temp);
    }
     public int Read(BufferedReader i) throws IOException{
        int error=0;
        String temp=i.readLine();
        if(temp==null) return 1;
        SetType(Integer.parseInt(temp));
        temp=i.readLine();
        Modulated=Boolean.parseBoolean(temp);
        for(int j=0;j<6;j++){
            temp=i.readLine();
            if(temp==null) return 1;            
            Params[j].SetValue(Double.parseDouble(temp));
        }
        temp=i.readLine();
        if(temp==null) return 1;            
        modspeed=Double.parseDouble(temp);
        Regenerate();
        updateData();
        return 0;
    }
    
    */
}
