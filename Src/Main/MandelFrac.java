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
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.concurrent.TimeUnit;
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

public class MandelFrac extends JPanel implements ActionListener,MouseListener,MouseWheelListener,ComponentListener,KeyListener{
	boolean PrinterVersion;
	String FractalName;
	String SubFractalName;
	//Control variables used by Formula
	Formula formula_Proto;
	Formula formula_used;
	int NIters;
	int RotID;
	double ParamR[];
	double ParamI[];
	String Special;
	
	boolean SH,CTRL;
	//Used by Iterator	
	boolean Alternate;
	boolean Conjugate;
	boolean Fixed;
	boolean useOrbits;
	
	//Used by DrawManager and InitDraw
	boolean ForceRes;
	int ForcedW,ForcedH;
	
	//Image frame variables
	double XUL,YUL,XDR,YDR,Cx,Cy,Pdim;
	//Label Frame variables
	double XUL_L,YUL_L,XDR_L,YDR_L;
	boolean AddFiligrana;

	//JLabel prevLab;
	
	OrbitConverger myorb_c;
	OrbitCombiner myorb;
	FractalControl mycontrol;
	Layer mylayer;
	FormulaEditor myformula;
	WaterMarkSettings mywm;
	PrinterManager myprinter;
	DrawManager fracdrawer;
	AdvLoader myloader;

	//Jbuttons and panel
	JPanel MenuPanel;
	JButton DrawBtn,StopBtn,UpdateBtn,ResizeBtn,CntrlBtn,FormulaBtn,ColorBtn,OrbitBtn,SaveImgBtn,SaveSetBtn,LoadSetBtn,CorbBtn,WMBtn,PrinterBtn,SendBtn;	
	JLabel PicLabel;
	
	int[] status;
	int nth;
    int next;
    int stopvar,endvar,repvar; //pointer to ext variables; 

	int Width,Height; //of the label
	int imgW,imgH;// of the image
    int[] Pixels;
    double[] Matrix;
    BufferedImage pic;
    BufferedImage filigrana;
    ImageIcon picIco;
    Iterator iter;
    
    boolean Starting;
    static String DocFolder;

	MandelFrac(int W,int H,boolean UsePrinter){
		PrinterVersion=UsePrinter;
		//AddFiligrana=true;
		JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        System.out.println(fw.getDefaultDirectory());
        
		if(PrinterVersion) DocFolder=fw.getDefaultDirectory().getPath()+File.separator+"MyFractals"+File.separator+"ReadyToPrint";
		else DocFolder=fw.getDefaultDirectory().getPath()+File.separator+"MyFractals";
        System.out.println(DocFolder);
        Path p=Paths.get(DocFolder); 
        File dir;
        if(Files.notExists(p)){
            dir=new File(DocFolder);
            dir.mkdir();
        }
        if(!PrinterVersion){
			p=Paths.get(DocFolder+File.separator+"Sets");
			System.out.println(DocFolder+File.separator+"Sets");
			if(Files.notExists(p)){
				dir=new File(DocFolder+File.separator+"Sets");
				dir.mkdir();
			}    
			p=Paths.get(DocFolder+File.separator+"Orbits");
			if(Files.notExists(p)){
				dir=new File(DocFolder+File.separator+"Orbits");
				dir.mkdir();
			}
			p=Paths.get(DocFolder+File.separator+"Colors");
			if(Files.notExists(p)){
				dir=new File(DocFolder+File.separator+"Colors");
				dir.mkdir();
			}
			p=Paths.get(DocFolder+File.separator+"Formulas");
			if(Files.notExists(p)){
				dir=new File(DocFolder+File.separator+"Formulas");
				dir.mkdir();
			}
			p=Paths.get(DocFolder+File.separator+"ConvOrbits");
			if(Files.notExists(p)){
				dir=new File(DocFolder+File.separator+"ConvOrbits");
				dir.mkdir();
			}
		}
        SH=CTRL=false;
		//Start set
		NIters=100;
		//BailOut=100;
		//JuliaMand=
		Alternate=Conjugate=Fixed=false;	
		ForceRes=PrinterVersion;//when using printer forceres is always true
		ForcedW=1920;
		ForcedH=1080;
		Cx=-0.5;
		Cy=0;
		RotID=0;
		//dimension of pic
		Width=W;
		Height=H-40;
		imgW=Width;
		imgH=Height;
		//ResetJuliaMand();
		myorb_c=new OrbitConverger(true);
		myorb=new OrbitCombiner(true);
		mylayer=new Layer();
		mycontrol=new FractalControl(this);
		myformula=new FormulaEditor(this);
		pic=new BufferedImage(imgW,imgH,TYPE_INT_ARGB_PRE);
		if(PrinterVersion){
			myprinter=new PrinterManager(this);
			mywm=new WaterMarkSettings(this);			
			myloader=new AdvLoader(this);
		}
		ParamR=new double[12];
		ParamI=new double[12];
		
		Matrix=new double[imgW*imgH];
		Pixels=new int[imgW*imgH];
        picIco=new ImageIcon();
       // iter=new std(this);
        status=new int[imgH];        
        Starting=true;
		BuildGraphics();		
		ApplySize(this,W,H);		
		//ResizeGraphics();
		try{
		TimeUnit.MILLISECONDS.sleep(100);
	}catch(InterruptedException e){}
		Starting=false;		
		//fracdrawer=new DrawManager(this);
		//fracdrawer.start();	
		if(!PrinterVersion) initDraw();
	}
	
	public void UpdateParentDataAndLoad(){
		FractalName=myloader.SelectedMain;
		SubFractalName=myloader.SelectedSub;
		if(!FractalName.isEmpty()){
			ReadBase();
			if(!SubFractalName.isEmpty() && !SubFractalName.equals("Default")) ReadWMP();
		}
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
		setLayout(new GridBagLayout());
		addComponentListener(this);
		//addKeyListener(this);
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=c.gridy=0;
		//TopPanel
		MenuPanel=new JPanel();
		if(!PrinterVersion) MenuPanel.setLayout(new GridLayout(1,12,1,1));
		else MenuPanel.setLayout(new GridLayout(1,6,1,1));
		DrawBtn=new JButton("Draw");
		DrawBtn.setFocusable(false);
		DrawBtn.setActionCommand("DrawBtn");
		DrawBtn.addActionListener(this);
		MenuPanel.add(DrawBtn);
		StopBtn=new JButton("Stop");
		StopBtn.setFocusable(false);
		StopBtn.setActionCommand("StopBtn");
		StopBtn.addActionListener(this);
		MenuPanel.add(StopBtn);
		if(!PrinterVersion){
			UpdateBtn=new JButton("Update");
			UpdateBtn.setFocusable(false);
			UpdateBtn.setActionCommand("UpdateBtn");
			UpdateBtn.addActionListener(this);
			MenuPanel.add(UpdateBtn);
			CntrlBtn=new JButton("Controls");
			CntrlBtn.setFocusable(false);
			CntrlBtn.setActionCommand("CntrlBtn");
			CntrlBtn.addActionListener(this);
			MenuPanel.add(CntrlBtn);
			FormulaBtn=new JButton("Formula");
			FormulaBtn.setFocusable(false);
			FormulaBtn.setActionCommand("FormulaBtn");
			FormulaBtn.addActionListener(this);
			MenuPanel.add(FormulaBtn);
			ColorBtn=new JButton("Colors");
			ColorBtn.setFocusable(false);
			ColorBtn.setActionCommand("ColorBtn");
			ColorBtn.addActionListener(this);
			MenuPanel.add(ColorBtn);
			OrbitBtn=new JButton("Orbits");
			OrbitBtn.setFocusable(false);
			OrbitBtn.setActionCommand("OrbitBtn");
			OrbitBtn.addActionListener(this);
			MenuPanel.add(OrbitBtn);
			CorbBtn=new JButton("Conv. Orb.");
			CorbBtn.setFocusable(false);
			CorbBtn.setActionCommand("CorbBtn");
			CorbBtn.addActionListener(this);
			MenuPanel.add(CorbBtn);
		}
		else{
			WMBtn=new JButton("WaterMark");
			WMBtn.setFocusable(false);
			WMBtn.setActionCommand("WMBtn");
			WMBtn.addActionListener(this);
			MenuPanel.add(WMBtn);
			PrinterBtn=new JButton("Printer");
			PrinterBtn.setFocusable(false);
			PrinterBtn.setActionCommand("PrinterBtn");
			PrinterBtn.addActionListener(this);
			MenuPanel.add(PrinterBtn);			
		}
		SaveImgBtn=new JButton("Save Img");
		SaveImgBtn.setFocusable(false);
		SaveImgBtn.setActionCommand("SaveImgBtn");
		SaveImgBtn.addActionListener(this);
		MenuPanel.add(SaveImgBtn);
		if(!PrinterVersion){
			SaveSetBtn=new JButton("Save Set");
			SaveSetBtn.setFocusable(false);
			SaveSetBtn.setActionCommand("SaveSetBtn");
			SaveSetBtn.addActionListener(this);
			MenuPanel.add(SaveSetBtn);
			SendBtn=new JButton("Send Set");
			SendBtn.setFocusable(false);
			SendBtn.setActionCommand("SendBtn");
			SendBtn.addActionListener(this);
			MenuPanel.add(SendBtn);
		}
		LoadSetBtn=new JButton("Load Set");
		LoadSetBtn.setFocusable(false);
		LoadSetBtn.setActionCommand("LoadSetBtn");
		LoadSetBtn.addActionListener(this);
		MenuPanel.add(LoadSetBtn);
		
		
		add(MenuPanel,c);
		c.gridy=1;
		PicLabel=new JLabel();
		PicLabel.setOpaque(true);
		PicLabel.setName("PicLabel");
		PicLabel.setHorizontalAlignment(SwingConstants.CENTER);
		if(!PrinterVersion){
			PicLabel.addMouseListener(this);
			PicLabel.addMouseWheelListener(this);
		}
	//	PicLabel.addKeyListener(this);
		add(PicLabel,c);		
	}
	
	public void ResizeGraphics(){
		int W=getWidth();
		int H=getHeight();
		ApplySize(MenuPanel,W,40);
		ApplySize(PicLabel,W,H-40);
		repaint();
		revalidate();		
	}


//Update routines to draw picture in label
    public static BufferedImage resizepic(BufferedImage image, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.createGraphics();
		//g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return bi;
    }
    
    public void Update(){
		//System.out.println("Update");
		/*if(AddFiligrana){
			//PrintFiligrana();
			addTextWatermark("Pietro Ottanelli");		}*/
		
        if(!ForceRes){
            picIco.setImage(pic);
           	PicLabel.setIcon(picIco);
			PicLabel.updateUI();
        }
        else{
            double r1,r2;
            int hf,wf;
            hf=PicLabel.getHeight();
            wf=PicLabel.getWidth();
            r1=(double)imgW/(double)imgH;
            r2=(double)wf/(double)hf;
            if (r1>r2) hf=(int)((double)wf/r1+0.5);
            if (r1<r2) wf=(int)((double)hf*r1+0.5);
            picIco.setImage(resizepic(pic,wf,hf));        
            PicLabel.setIcon(picIco);
            PicLabel.updateUI();           
        }           
    }
    
//Color Update           
    public void UpdateLine (int iline){
		if ((iline<imgH)&&(iline>=0)){
            if(!ForceRes) pic.setRGB(0, iline, imgW,1,Pixels,iline*imgW,imgW);
            status[iline]=2;
        }        
    }
    
//Conversion of label coordinates to coordinates
    private double GetX_L(int i){
        return XUL_L+(double)i/Width*(XDR_L-XUL_L);
    }
   
    private double GetY_L(int i){
        return YUL_L+(double)i/Height*(YDR_L-YUL_L);
    }
    
    //Image management
	public void GetCpDfromCorners(){
        Cx=(XUL+XDR)/2;
        Cy=(YUL+YDR)/2;
        double aspectRatio=(double)imgH/(double)imgW;
        if (((YUL-YDR)/(XDR-XUL))>aspectRatio) Pdim=(YUL-YDR)/((double)imgH-1);
        else Pdim=(XDR-XUL)/((double)imgW-1);        
    }
    
    public void GetCornersfromCpD(){
        XUL=Cx-Pdim*((double)imgW-1)/2;
        XDR=Cx+Pdim*((double)imgW-1)/2;
        YUL=Cy+Pdim*((double)imgH-1)/2;
        YDR=Cy-Pdim*((double)imgH-1)/2;
    }
    
    public void SetView(double xul,double yul,double xdr,double ydr){
		XUL=xul;
		YUL=yul;
		XDR=xdr;
		YDR=ydr;
		GetCpDfromCorners();
	}
    
    public void SetCenter(double newCx,double newCy){
		System.out.printf("Setting new coordinates (%f,%f)!\n",newCx,newCy);
		Cx=newCx;
		Cy=newCy;
		GetCornersfromCpD();
		//UpdateLabelCorners();
		//mycontrol.UpdateCoordinates();		
	}
	
	public void ApplyZoom(double G){
		double Scaling=3;
		if(CTRL) Scaling*=3;
		if(SH) Scaling/=3;
		Pdim*=Math.pow(10,G/Scaling);        
		GetCornersfromCpD();
		//UpdateLabelCorners();
		//mycontrol.UpdateCoordinates();				
	}
	
	public void SetPdim(double newdim){
		Pdim=newdim;
		GetCornersfromCpD();
		//UpdateLabelCorners();
		//mycontrol.UpdateCoordinates();				
	}
    
    public void UpdateLabelCorners(){
		if(!ForceRes){
			XUL_L=XUL;
			YUL_L=YUL;
			XDR_L=XDR;
			YDR_L=YDR;
		}
		else{
			double imgAR=(double)(XDR-XUL)/(double)(YUL-YDR);
			double AR=(double)(XDR_L-XUL_L)/(double)(YUL_L-YDR_L);
			if(AR>imgAR){
				YUL_L=YUL;
				YDR_L=YDR;
				XUL_L=Cx+(XUL-Cx)*AR/imgAR;
				XDR_L=Cx+(XDR-Cx)*AR/imgAR;				
			}
			else{
				XUL_L=XUL;
				XDR_L=XDR;
				YUL_L=Cy+(YUL-Cy)*imgAR/AR;
				YDR_L=Cy+(YDR-Cy)*imgAR/AR;
			}
		}		
	}
	
//Pixel Calculations
	private double GetX(int i){
        return XUL+(double)i/imgW*(XDR-XUL);
    }
   
    private double GetY(int i){
        return YUL+(double)i/imgH*(YDR-YUL);
    }
    
    public void DrawLine (int iline){
        int j,n;
        if ((iline<imgH)&&(iline>=0)){
			if(!ForceRes){
				for(j=0;j<imgW;j++){
					Matrix[iline*imgW+j]=iter.calcPoint_full(GetX(j),GetY(iline));
					//n=theLayer.GetColor(Matrix[iline*w+j]);
					Pixels[iline*imgW+j]=mylayer.GetColor(Matrix[iline*imgW+j]);
				}
				status[iline]=1;
			}
			else{
				int []locpix=new int[imgW];
				for(j=0;j<imgW;j++){
					locpix[j]=mylayer.GetColor(iter.calcPoint_full(GetX(j),GetY(iline)));
					if(PrinterVersion) locpix[j]=mywm.ApplySignature(j,iline,locpix[j],0);
				}
				pic.setRGB(0, iline, imgW,1,locpix,0,imgW);
				status[iline]=1;
            }           
        }
    }
    
    public void RecolorLine (int iline){
		if(ForceRes) return;
        int j,n;
        if ((iline<imgH)&&(iline>=0)){
            for(j=0;j<imgW;j++){
                //Matrix[iline*Width+j]=iter.calcPoint(GetX(j),GetY(iline));
                //n=theLayer.GetColor(Matrix[iline*w+j]);
                Pixels[iline*imgW+j]=mylayer.GetColor(Matrix[iline*imgW+j]);
            }
            status[iline]=1;
        }
    }
    
    
//Draw thread management
//Stop Draw Command
	public void StopDraw(){
		if(fracdrawer==null) return;
		fracdrawer.active=false; //should stop everything
		while(true){
			int nsleep=0;
			try{
				Thread.sleep(100);
				nsleep+=100;
			}catch (InterruptedException e){}
			if(fracdrawer.is_clear){
			    fracdrawer.goon=false;//terminate process
				Update();
				return;
			}
			if(nsleep>10000){
				System.out.printf("Warning, failed to correctly close drawing procedure! Trying to recover\n");
				Update();
				return;			
			}
		}		
	}

public void PrintData (BufferedWriter o) throws IOException{
	o.write("#MAIN\n");
	String temp=String.format("ForceRes %b\n",ForceRes);
	o.write(temp);
	temp=String.format("ForcedW %d\n",ForcedW);
	o.write(temp);
	temp=String.format("ForcedH %d\n",ForcedH);
	o.write(temp);
	temp=String.format("XUL %.15f\n",XUL);
	temp=temp.replace(',','.');
	o.write(temp);
	temp=String.format("YUL %.15f\n",YUL);
	temp=temp.replace(',','.');
	o.write(temp);
	temp=String.format("XDR %.15f\n",XDR);
	temp=temp.replace(',','.');
	o.write(temp);
	temp=String.format("YDR %.15f\n",YDR);
	temp=temp.replace(',','.');
	o.write(temp);
	temp=String.format("RotID %d\n",RotID);
	o.write(temp);
	o.write("#END\n");
}
	
void PrintToFile(BufferedWriter o) throws IOException{
	PrintData(o);	
}

//Non printer version only
void PrintFile(File f){
	try{
	    BufferedWriter o=new BufferedWriter(new FileWriter(f));
	    PrintToFile(o);
	    myformula.PrintToFile(o);
		mylayer.PrintToFile(o);
		myorb.PrintToFile(o);
		myorb_c.PrintToFile(o);
	    o.close();
	}catch(IOException e){}
}

//Printerversion only
void PrintFile(){
	String nf=DocFolder+File.separator+FractalName;
	Path p=Paths.get(nf); 
	File dir;
	if(Files.notExists(p)){
		dir=new File(nf);
		dir.mkdir();
	}
	File f=new File(DocFolder+File.separator+FractalName+File.separator+myprinter.GenerateName()+".frc");
	try{
	    BufferedWriter o=new BufferedWriter(new FileWriter(f));
	    PrintToFile(o);
	    myformula.PrintToFile(o);
		mylayer.PrintToFile(o);
		myorb.PrintToFile(o);
		myorb_c.PrintToFile(o);
		mywm.PrintToFile(o);
		myprinter.PrintToFile(o);
	    o.close();
	}catch(IOException e){}	
}



public void ReadData(BufferedReader i) throws IOException{
	StringTokenizer st;
	while(true){
		String temp=i.readLine();
		if(temp==null) return;
		if(temp.equals("#END")) return;
		st=new StringTokenizer(temp);
		String vname=st.nextToken();
		if(vname.equals("ForceRes")){
			if(!PrinterVersion) ForceRes=Boolean.parseBoolean(st.nextToken());
		}
		else if(vname.equals("ForcedW")){
			ForcedW=Integer.parseInt(st.nextToken());	
		}
		else if(vname.equals("ForcedH")){
			ForcedH=Integer.parseInt(st.nextToken());	
		}
		else if(vname.equals("XUL")){
			XUL=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("YUL")){
			YUL=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("XDR")){
			XDR=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("YDR")){
			YDR=Double.parseDouble(st.nextToken());
		}
		else if(vname.equals("RotID")){
			RotID=Integer.parseInt(st.nextToken());
		}
		else{	
			System.out.printf("Reading MAIN, found unknown field %s\n",vname);
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
				if(temp.equals("#MAIN")){
					ReadData(in);
					if(PrinterVersion) ForceRes=true;
					GetCpDfromCorners();
					GetCornersfromCpD();
					mycontrol.UpdateData();
					in.close();	
					return ;
				}
			}			
		} catch (IOException ex) {
		}
		return;
	}
	
//non Printer version only
public void ReadFile(File in){
	ReadFromFile(in);
	myformula.ReadFromFile(in);
	mylayer.ReadFromFile(in);
	myorb.ReadFromFile(in);		
	myorb_c.ReadFromFile(in);	
	StopDraw();
	initDraw();      
}

//non Printer version only
public void ReadBase(){
	File in=new File(DocFolder+File.separator+FractalName+".frc");
	ReadFromFile(in);
	myformula.ReadFromFile(in);
	mylayer.ReadFromFile(in);
	myorb.ReadFromFile(in);		
	myorb_c.ReadFromFile(in);
}

public void ReadWMP(){
	File in=new File(DocFolder+File.separator+FractalName+File.separator+SubFractalName+".frc");
	mywm.ReadFromFile(in);
	myprinter.ReadFromFile(in);
	StopDraw();
	initDraw();      
}


public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		System.out.printf("In ActionCommand %s\n",cmd);
	//	StringTokenizer st=new StringTokenizer(cmd);
//		String type=st.nextToken();
		if(cmd.equals("DrawBtn")){
			//System.out.println("Sending all settings to TriggerBox");
			//updated=true;
			//UpdateUI();
			initDraw();
		}
		else if(cmd.equals("StopBtn")){
			//System.out.println("Sending all settings to TriggerBox");
			//updated=true;
			//UpdateUI();
			StopDraw();
		}
		else if(cmd.equals("UpdateBtn")){
			initUpdate();
		}
		else if(cmd.equals("CntrlBtn")){
			mycontrol.setVisible(true);
		}
		else if(cmd.equals("FormulaBtn")){
			myformula.setVisible(true);
		}
		else if(cmd.equals("ColorBtn")){
			mylayer.setVisible(true);
		}
		else if(cmd.equals("OrbitBtn")){
			myorb.setVisible(true);
		}
		else if(cmd.equals("CorbBtn")){
			myorb_c.setVisible(true);
		}
		else if(cmd.equals("WMBtn")){
			mywm.setVisible(true);
		}
		else if(cmd.equals("PrinterBtn")){
			myprinter.setVisible(true);
		}
		else if (cmd.equals("SendBtn")){
			SendSet();		
		}
		else if(cmd.equals("SaveSetBtn")){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Sets"));
			fc.setFileFilter(new FileNameExtensionFilter("Fractal Set file", "frc"));
			fc.showSaveDialog(this);
			File f=fc.getSelectedFile();
			if(f==null)return;
			String ptitle=f.getPath();
			if (!ptitle.endsWith(".frc")) ptitle+=".frc";
			f=new File(ptitle);// TODO add your handling code here:			
			PrintFile(f);
		}
		else if(cmd.equals("LoadSetBtn")){
			if(!PrinterVersion){
				JFileChooser fc =new  JFileChooser();
				fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Sets"));
				fc.setFileFilter(new FileNameExtensionFilter("Fractal Set file", "frc"));
				fc.showOpenDialog(this);
				File f=fc.getSelectedFile();
				if(f==null)return;
				String ptitle=f.getPath();
				if (!ptitle.endsWith(".frc")) ptitle+=".frc";
				f=new File(ptitle);// TODO add your handling code here:			
				ReadFile(f);
			}
			else myloader.setVisible(true);
		}
		else if(cmd.equals("SaveImgBtn")){
			if(!PrinterVersion){
				JFileChooser fc =new  JFileChooser();
				fc.setCurrentDirectory(new File(MandelFrac.DocFolder));
				fc.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
				fc.showSaveDialog(this);			
				File f=fc.getSelectedFile();
				if(f!=null){
					String ptitle=f.getPath();
					if (!ptitle.endsWith(".png")) ptitle+=".png";
					f=new File(ptitle);
					try {
						ImageIO.write(pic,"png",f);// TODO add your handling code here:
					} catch (IOException ex) {}
					//also save settings
					char sep=File.separator.charAt(0);
					String temp=MandelFrac.DocFolder+File.separator+"Sets"+ptitle.substring(ptitle.lastIndexOf(sep),ptitle.length()-3)+"frc";
					f=new File(temp);
					PrintFile(f);
				}
			}
			else{
				String ptitle=myprinter.GenerateName();
				File f=new File(DocFolder+File.separator+FractalName+File.separator+myprinter.GenerateName()+".png");
				try {
					ImageIO.write(pic,"png",f);// TODO add your handling code here:
				} catch (IOException ex) {}
				PrintFile();
			}			
		}	
	}
	
	public void SendSet(){
		JFileChooser fc =new  JFileChooser();
		fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"ReadyToPrint"));
		fc.setFileFilter(new FileNameExtensionFilter("Fractal Set file", "frc"));
		fc.showSaveDialog(this);
		File f=fc.getSelectedFile();
		if(f==null)return;
		String ptitle=f.getPath();
		String realtitle=ptitle.substring(ptitle.lastIndexOf('/')+1);
		if(realtitle.endsWith(".frc")) realtitle=realtitle.substring(0,realtitle.length()-4);
		System.out.println(DocFolder+File.separator+"ReadyToPrint"+File.separator+realtitle+".frc");
		f=new File(DocFolder+File.separator+"ReadyToPrint"+File.separator+realtitle+".frc");// TODO add your handling code here:			
		PrintFile(f);	
		String nf=DocFolder+File.separator+"ReadyToPrint"+File.separator+realtitle;
		Path p=Paths.get(nf); 
		File dir;
		if(Files.notExists(p)){
			dir=new File(nf);
			dir.mkdir();
		}	
	}
	
	public void RefreshImageSize(){
		ForcedH=myprinter.GetHeight();
		ForcedW=myprinter.GetWidth();	
	}
	
	 public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In mouseClicked() : "+cmd);
		if(cmd.equals("PicLabel")){
			final Point mousePos = PicLabel.getMousePosition();
			StopDraw();
            SetCenter(GetX_L(mousePos.x),GetY_L(mousePos.y));
            mycontrol.UpdateData();
            initDraw();
        }
    }
    
     public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("PicLabel")){
		    final Point mousePos = PicLabel.getMousePosition();// TODO add your handling code here:
			StopDraw();
			SetCenter(GetX_L(mousePos.x),GetY_L(mousePos.y));
			ApplyZoom(e.getPreciseWheelRotation());
			mycontrol.UpdateData();
			initDraw();
		}		
	}
	
	 public void keyPressed(KeyEvent e) {
	//	System.out.printf("Pressed key %d\n",e.getKeyCode());
        int keyCode = e.getKeyCode();
        if (keyCode == 16) SH=true;
        if (keyCode == 17) CTRL=true;
	}
   
    public void keyReleased(KeyEvent e) {
         int keyCode = e.getKeyCode();
        if (keyCode == 16) SH=false;
        if (keyCode == 17) CTRL=false;
    }
	    
	public void keyTyped(KeyEvent e){
		char keyCode = e.getKeyChar();
		System.out.printf("Typed key %c\n",e.getKeyChar());
		if(keyCode == 'd') initDraw();
		if(keyCode == 'u') initUpdate();
		if(keyCode == 'c') mycontrol.setVisible(true);
		if(keyCode == 'f') myformula.setVisible(true);
		if(keyCode == 'l') mylayer.setVisible(true);
		if(keyCode == 'o') myorb.setVisible(true);
		if(keyCode == 's'){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder));
			fc.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
			fc.showSaveDialog(this);			
			File f=fc.getSelectedFile();
			if(f!=null){
				String ptitle=f.getPath();
				if (!ptitle.endsWith(".png")) ptitle+=".png";
				f=new File(ptitle);
				try {
					ImageIO.write(pic,"png",f);// TODO add your handling code here:
				} catch (IOException ex) {}
				//also save settings
				char sep=File.separator.charAt(0);
				String temp=MandelFrac.DocFolder+File.separator+"Sets"+ptitle.substring(ptitle.lastIndexOf(sep),ptitle.length()-3)+"frc";
				f=new File(temp);
				PrintFile(f);
			}
			//System.out.println(temp);			
		}		
	}
	
	 public void componentHidden(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {}

    public void componentResized(ComponentEvent e) {
		//if(!GStuff) return;
		System.out.println("In Component Resized");
	   if(Starting) return;
       ResizeGraphics();
       //Update dimensions of label
       Width=PicLabel.getWidth();
       Height=PicLabel.getHeight();
       if(!ForceRes){
		   //Set image same size of label
		   if(fracdrawer!=null) StopDraw();
		   imgW=Width;
		   imgH=Height;
		   GetCpDfromCorners();
		   GetCornersfromCpD();
		   UpdateLabelCorners();
		   mycontrol.UpdateData();
		   //if(fracdrawer!=null)initDraw();		
		   initDraw();
	   }
	   else{
		   //img doesnt change
		   UpdateLabelCorners();
		   Update();
	   }       
    }

    public void componentShown(ComponentEvent e) {}
    
    	
	public void initUpdate(){
		//allocate image
		fracdrawer=new DrawManager(this);
		fracdrawer.SetNLines(imgH,0);
		fracdrawer.coloronly=true;
		fracdrawer.active=true;//StartDraw here		
		fracdrawer.start();
	}
	
	//Service functions for drawing
	public void RefreshParameters(){
		for(int i=0;i<12;i++){
			ParamR[i]=myformula.GetParamR(i);
			ParamI[i]=myformula.GetParamI(i);
		}
		NIters=myformula.GetIters();
		Alternate=myformula.GetAlternate();
		Conjugate=myformula.GetConjugate();
		useOrbits=myformula.GetOrbit();
		Fixed=myformula.GetFixed();
		Special=myformula.GetSpecial();
		System.out.printf("Special=%s\n",Special);
	}
	
	public void InitializePic(){
		RefreshImageSize(); //Ask myprinter to calculate image dimensions
		imgW=ForcedW;
		imgH=ForcedH;
		if(imgW!=pic.getWidth() || imgH!=pic.getHeight()){
			pic=null;
			Matrix=null;
			Pixels=null;
			pic=new BufferedImage(imgW,imgH,TYPE_INT_ARGB_PRE);
		}	
	}
	
	//Init Draw Command		 
	public void initDraw(){
		
		//waitfor previous 
		if(fracdrawer==null) System.out.printf("FracDrawer not found\n");
		else{
			System.out.printf("Stopping Drawer %s\n",fracdrawer.getState().toString());
			if(!fracdrawer.getState().toString().equals("TERMINATED")) StopDraw();
		}
		if(PrinterVersion) ReadBase();
		//allocate image
		imgW=PicLabel.getWidth();
		imgH=PicLabel.getHeight();
        if (ForceRes){
			if(PrinterVersion) RefreshImageSize(); //Ask myprinter to calculate image dimensions
			imgW=ForcedW;
			imgH=ForcedH;
		}
		if(imgW==0 || imgH==0) return;
		GetCpDfromCorners();
		GetCornersfromCpD();
		UpdateLabelCorners();
		if(imgW!=pic.getWidth() || imgH!=pic.getHeight()){
			pic=null;
			Matrix=null;
			Pixels=null;
			pic=new BufferedImage(imgW,imgH,TYPE_INT_ARGB_PRE);
			if(!ForceRes){
				Matrix=new double[imgW*imgH];
				Pixels=new int[imgW*imgH];
			}
		}
		
		//Prepare watermark
		if(PrinterVersion && mywm.EnableWM) mywm.PrepareSignatureAndULC();
		
		//Specialize formula
		//Copy Parameters
		RefreshParameters();
		/*SpecializeFormula();		//Now the formula_Proto is specialized for the current fractal*/
		
		//Initialize Iterator
      
		iter=mylayer.GenerateIterator();
		iter.Specialize(this);
    
		//Prepare filigrana
	//	PrepareFiligrana();
		//set parameters of thread
		status=new int[imgH];
		for(int i=0;i<imgH;i++) status[i]=0;
		fracdrawer=new DrawManager(this);
		fracdrawer.SetNLines(imgH,0);
		fracdrawer.coloronly=false;
		fracdrawer.active=true;//StartDraw here		
		fracdrawer.start();
	}
	
	public void SetPrototype(Formula f){
		formula_Proto=new Formula();
		formula_Proto=f;
	}
	
	public Formula GetFormula(){
		Formula res=myformula.GenerateFormula();
		res.SetGeneral(this);
		return res;
	}
	/*
	private BufferedImage resizeImageWithHint(BufferedImage originalImage){
		double ratio=(double)originalImage.getHeight()/(double)originalImage.getWidth();
		int iW,iH;
		double IWD,IHD;
		IWD=0.05*(double)imgW;
		IHD=ratio*IWD;
		iW=(int)round(IWD);
		iH=(int)round(IHD);
		BufferedImage resizedImage = new BufferedImage(iW, iH, TYPE_INT_ARGB_PRE);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, iW, iH, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		return resizedImage;
    }	
	
	public void PrepareFiligrana(){
		try{
		int SpaceX=20;
		int SpaceY=20;
		File imageFile = new File("Binded-mark.png");
		BufferedImage orig = ImageIO.read(imageFile);
		filigrana = resizeImageWithHint(orig);	
		}catch(IOException e){}			
	}
	
	public void PrintFiligrana(){
		if(filigrana!=null){
			int SpaceX=20;
			int SpaceY=20;
			int iW,iH;
			iW=filigrana.getWidth();
			iH=filigrana.getHeight();
			for(int i=0;i<iW;i++){
				for(int j=0;j<iH;j++){
					int pix=filigrana.getRGB(i,j);
					int picpix=pic.getRGB(imgW-iW-SpaceX+i,imgH-iH-SpaceY+j);
					int newpix;
					int alpha=(pix>>24) & 0xFF;
					double alphaD;
					int Rd,Gd,Bd;
					if(alpha==0) continue;
					else if(alpha==255) newpix=pix;
					else{
						alphaD=(double) alpha/255.;
						Rd=(int)round(alphaD*((double)((pix>>16) & 0xFF))+(1-alphaD)*((double)((picpix>>16) & 0xFF)));
						Gd=(int)round(alphaD*((double)((pix>>8) & 0xFF))+(1-alphaD)*((double)((picpix>>8) & 0xFF)));
						Bd=(int)round(alphaD*((double)((pix) & 0xFF))+(1-alphaD)*((double)((picpix) & 0xFF)));
						newpix=(255<<24) | Rd<<16 | Gd<<8 | Bd;
					}
					//System.out.printf("%d %d %d %d %d %d\n",i,j,(pix>>24) & 0xFF,(pix>>16) & 0xFF,(pix>>8) & 0xFF,pix & 0xFF);
					if(pix!=0) pic.setRGB(imgW-iW-SpaceX+i,imgH-iH-SpaceY+j,newpix);
				}
			}
		}
	}
	
	public void PrintWM(BufferedImage Watermark, int posX,int posY,boolean useDarken,boolean Invert){
		if(useDarken) DarkenWM(Watermark,posX,posY,Invert);
		else if(Watermark!=null){
			//System.out.println("Watermark is present");
			int iW,iH;
			iW=Watermark.getWidth();
			iH=Watermark.getHeight();
			//System.out.printf("W=%d H=%d\n",iW,iH);
			for(int i=0;i<iW;i++){
				for(int j=0;j<iH;j++){
					int pix=Watermark.getRGB(i,j);
					int picpix=pic.getRGB(posX+i,posY+j);
					int newpix;
					int alpha=(pix>>24) & 0xFF;
					double alphaD;
					int Rd,Gd,Bd;
				//	System.out.printf("%d %d %d %d %d %d\n",i,j,(pix>>24) & 0xFF,(pix>>16) & 0xFF,(pix>>8) & 0xFF,pix & 0xFF);
					if(alpha==0) continue;
					else if(alpha==255) newpix=pix;
					else{
						alphaD=(double) alpha/255.;
						Rd=(int)round(alphaD*((double)((pix>>16) & 0xFF))+(1-alphaD)*((double)((picpix>>16) & 0xFF)));
						Gd=(int)round(alphaD*((double)((pix>>8) & 0xFF))+(1-alphaD)*((double)((picpix>>8) & 0xFF)));
						Bd=(int)round(alphaD*((double)((pix) & 0xFF))+(1-alphaD)*((double)((picpix) & 0xFF)));
						newpix=(255<<24) | Rd<<16 | Gd<<8 | Bd;
					}
					if(pix!=0) pic.setRGB(posX+i,posY+j,newpix);
				}
			}
		}
		else System.out.println("Watermark is null");
	}
	
	public void DarkenWM(BufferedImage Watermark, int posX,int posY,boolean Invert){
		if(Watermark!=null){
			//System.out.println("Watermark is present");
			int iW,iH;
			iW=Watermark.getWidth();
			iH=Watermark.getHeight();
			//System.out.printf("W=%d H=%d\n",iW,iH);
			for(int i=0;i<iW;i++){
				for(int j=0;j<iH;j++){
					int pix=Watermark.getRGB(i,j);
					int picpix=pic.getRGB(posX+i,posY+j);
					int newpix;
					int alpha=(picpix>>24) & 0xFF;
					//double alphaD;
					int Rd,Gd,Bd;
					Rd=(picpix>>16) & 0XFF;
					Gd=(picpix>>8) & 0XFF;
					Bd=(picpix) & 0XFF;
				//	System.out.printf("%d %d %d %d %d %d\n",i,j,(pix>>24) & 0xFF,(pix>>16) & 0xFF,(pix>>8) & 0xFF,pix & 0xFF);
					if(pix==0) continue;
					else{
						if(Invert){
							Rd=(int)round(128-0.7*(Rd-128));
							Gd=(int)round(128-0.7*(Gd-128));
							Bd=(int)round(128-0.7*(Bd-128));
						}
						else{
							Rd=(int)round(0.25*Rd);
							Gd=(int)round(0.25*Gd);
							Bd=(int)round(0.25*Bd);						
						}							
						newpix=(alpha<<24) | (Rd<<16) | (Gd<<8) | Bd;
						pic.setRGB(posX+i,posY+j,newpix);
					}
				}
			}
		}
		else System.out.println("Watermark is null");
	}
	
	
	
	
	///////************OLD METHODS********/////
/*
	///Save-load function
public void Print(File out){
	try{
	    BufferedWriter o=new BufferedWriter(new FileWriter(out));
	    String temp;
	    int i;
	    o.write("#MAIN\n");
	    temp=String.format("%b\n",ForceRes);
	    o.write(temp);
	    temp=String.format("%d\n",ForcedW);
	    o.write(temp);
	    temp=String.format("%d\n",ForcedH);
	    o.write(temp);
	    temp=String.format("%.15f\n",XUL);
	    temp=temp.replace(',','.');
	    o.write(temp);
	    temp=String.format("%.15f\n",YUL);
	    temp=temp.replace(',','.');
	    o.write(temp);
	    temp=String.format("%.15f\n",XDR);
	    temp=temp.replace(',','.');
	    o.write(temp);
	    temp=String.format("%.15f\n",YDR);
	    temp=temp.replace(',','.');
	    o.write(temp);
	    myformula.Print(o);
	    mylayer.Print(o);
	    myorb.Print(o);
	    o.close();
	}catch(IOException e){}
} 
 
	
public int Read_old(BufferedReader i) throws IOException{
	String temp;
    int error=0;
    temp=i.readLine();
    if(temp==null) return 1;
	ForceRes=Boolean.parseBoolean(temp);
	temp=i.readLine();
    if(temp==null) return 1;
	ForcedW=Integer.parseInt(temp);	
	temp=i.readLine();
    if(temp==null) return 1;
	ForcedH=Integer.parseInt(temp);	
	temp=i.readLine();
	if(temp==null) return 1;
	XUL=Double.parseDouble(temp);
	temp=i.readLine();
	if(temp==null) return 1;
	YUL=Double.parseDouble(temp);
	temp=i.readLine();
	if(temp==null) return 1;
	XDR=Double.parseDouble(temp);
	temp=i.readLine();
	if(temp==null) return 1;
	YDR=Double.parseDouble(temp);
	GetCpDfromCorners();
    GetCornersfromCpD();
    mycontrol.UpdateData();
	return 0;
}*/
	
	
	
}

	
	
	
	
	
	

		


	








