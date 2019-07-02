import javax.swing.ImageIcon;
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





public class WaterMarkSettings extends JFrame implements ActionListener,MouseWheelListener,ChangeListener,WindowListener{
	//String fontname="LouisaCP";	
	//String fonttype="otf";
	int extrapix;
	boolean stop;
	SignatureRefresher sr;
	Color MyColor;
	boolean needrefresh;
	
	int XUL,YUL;
	int XSIZE,YSIZE;
	
	static int bw=20;
	static int bh=30;
	
	JPanel P0;
	
	//Corner selection
	JLabel CL;
	JComboBox CUD;
	int CUDL,CUDH;
	String []Corners={"Upper Left","Upper Middle","Upper Right","Lower Left","Lower Middle","Lower Right"};
	int whichcorner;
	
	//WMHeight selection
	JLabel WMHL;
	JSpinner WMHUD;
	double WMHeightCM;
		
	//Lateral Displacement
	JLabel SXL,SYL;
	JSpinner SXUD,SYUD;
	double SpaceXCM,SpaceYCM;
	
	//Color Selection
	JLabel RedL,GreenL,BlueL;
	JSlider RedUD,GreenUD,BlueUD;
	int RR,GG,BB;
	JLabel prevLab1;
	
	//FontSelection
	String []AvailableFonts;
	//int currentFont;
	int FontH;
	String cfname;
	JLabel FontL;
	JComboBox FontUD;
	
	//Author Selection
	int AuthH;
	JLabel AuthL;
	JComboBox AuthUD;
	String[]AvailableAuthors={"Pietro Ottanelli","Giulio Ottanelli"};
	String currentAuth;
	
	//DateSelection
	JButton DateBtn;
	JTextField DateUD;
	String []Months={"January","February","March","April","May","June","July","August","September","October","November","December"};
	String Datestring;
	DateFormat month=new SimpleDateFormat("MM");
	DateFormat year = new SimpleDateFormat("yyyy");
	
	//WM application Type
	JLabel TypL;
	String []TypeWM={"Normal","Darken","Lighten"};
	String Typestring;
	int TypH;
	JComboBox TypUD;
	
	
	//PreviewLab
	JLabel PrevLabMain;
	
	SignatureDrawer mysd;
	MandelFrac Parent;
	boolean Updating;
	
	public String GetMonth(Date d){
		String Full=Months[Integer.parseInt(month.format(d))-1];
		return Full.substring(0,3)+".";
	}
	
	
	//BlurRadius
	JLabel BlurL;
	JSpinner BlurUD;
	int BlurRadius;
	
	//rdeffect x-y
	JLabel X3dL;
	JSpinner X3dUD;
	int X3d;
	JLabel Y3dL;
	JSpinner Y3dUD;
	int Y3d;
	
	//Enable-disable
	JCheckBox Enable;
	JButton RefreshBtn;
	boolean EnableWM;
	
	JProgressBar RepBar;
	
	ImageIcon picIco;
	
	
	//OK
	public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}
	
	public WaterMarkSettings(MandelFrac p){
		stop=false;
		picIco=new ImageIcon();		
		LoadFonts();
		Parent=p;
		whichcorner=5;
		//FontIndex=0;
		WMHeightCM=1;
		SpaceXCM=SpaceYCM=2;
		RR=GG=BB=0;
		Date date = new Date();
		Datestring=GetMonth(date)+" "+year.format(date);
		BlurRadius=0;
		EnableWM=false;
		cfname="";
		currentAuth="";
		Typestring="";
		X3d=Y3d=0;
		Updating=false;
		BuildGraphics();
		mysd=new SignatureDrawer(p,this);
		extrapix=mysd.extrapix;
		UpdateData();
	}
	
	public void LoadFonts(){
		File folder = new File("Fonts");
		File[] listOfFiles = folder.listFiles();
		int count=0;
		for (int i = 0; i < listOfFiles.length; i++){
			if (listOfFiles[i].isFile()){
				System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".ttf") || listOfFiles[i].getName().endsWith(".otf")){
					count++;
					try{
						GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
						ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, listOfFiles[i]));
					}catch(FontFormatException e){
					}catch(IOException e){};
				}
			}
		}
		AvailableFonts=new String[count];
		count=0;
		for (int i = 0; i < listOfFiles.length; i++){
			if (listOfFiles[i].isFile()){
				//System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".ttf") || listOfFiles[i].getName().endsWith(".otf")){
					try{
						Font temp=Font.createFont(Font.TRUETYPE_FONT, listOfFiles[i]);
						AvailableFonts[count]=temp.getFontName();//listOfFiles[i].getName().substring(0,listOfFiles[i].getName().length()-4);
						count++;
					}catch(FontFormatException e){
					}catch(IOException e){};
				}
			}
		}	
		//now reordering
		String temp;
		int didsomething=0;
		while(true){
			didsomething=0;
			for(int i=0;i<count-1;i++){
				if(AvailableFonts[i].compareTo(AvailableFonts[i+1])>0){
					didsomething++;
					temp=AvailableFonts[i];
					AvailableFonts[i]=AvailableFonts[i+1];
					AvailableFonts[i+1]=temp;
				}
			}
			if(didsomething==0) break;
		}
	}
	
	public void SetColorRGB(int RGBval){
		if(RGBval==-1) return;
		RR=(RGBval>>16) & 0xFF;
		GG=(RGBval>>8) & 0xFF;
		BB=RGBval & 0xFF;
		RedUD.setValue(RR);
		BlueUD.setValue(BB);
		GreenUD.setValue(GG);	
	}
	
	public boolean LoadBackupCopy(File imageFile){
		try{
			mysd.BackupCopy=ImageIO.read(imageFile);
		}catch(IOException e){}
		if(mysd.BackupCopy!=null){
			XSIZE=mysd.BackupCopy.getWidth();
			YSIZE=mysd.BackupCopy.getHeight();
			switch (whichcorner){
			case 0:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 1:
				XUL=(Parent.imgW-XSIZE)/2;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 2:
				XUL=Parent.imgW-Parent.myprinter.CmToPixel(SpaceXCM)-XSIZE+extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 3:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			case 4:
				XUL=(Parent.imgW-XSIZE)/2;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			case 5:
				XUL=Parent.imgW-Parent.myprinter.CmToPixel(SpaceXCM)-XSIZE+extrapix;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			default:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			}
			RestoreBackup();
			ClearBackup();
			ApplySignature();
			return true;
		}
		return false;
	}
	
	
	public void PrepareSignatureAndULC(){
		if(needrefresh) mysd.Generate(currentAuth+" "+Datestring,cfname,0,Parent.myprinter.CmToPixel(WMHeightCM),MyColor,BlurRadius,X3d,Y3d);
		mysd.InitBackup();
		UpdateRepBar("Finding Upper Left corner!",1000);
		XSIZE=mysd.FinalImage.getWidth();
		YSIZE=mysd.FinalImage.getHeight();
		switch (whichcorner){
			case 0:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 1:
				XUL=(Parent.imgW-XSIZE)/2;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 2:
				XUL=Parent.imgW-Parent.myprinter.CmToPixel(SpaceXCM)-XSIZE+extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
			case 3:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			case 4:
				XUL=(Parent.imgW-XSIZE)/2;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			case 5:
				XUL=Parent.imgW-Parent.myprinter.CmToPixel(SpaceXCM)-XSIZE+extrapix;
				YUL=Parent.imgH-Parent.myprinter.CmToPixel(SpaceYCM)-YSIZE+extrapix;
				break;
			default:
				XUL=Parent.myprinter.CmToPixel(SpaceXCM)-extrapix;
				YUL=Parent.myprinter.CmToPixel(SpaceYCM)-extrapix;
				break;
		}	
		EndRefresh();
	}
	
	public void ClearBackup(){
		mysd.BackupCopy=null;
	}
	
	public void RestoreBackup(){
		System.out.printf("Restoring backup, width=%d Height=%d\n",XSIZE,YSIZE);
		if(mysd.BackupCopy==null) return;
		for(int i=0;i<XSIZE;i++){
				for(int j=0;j<YSIZE;j++){
					if(XUL+i>=Parent.imgW) continue;
					if(YUL+j>=Parent.imgH) continue;
					Parent.pic.setRGB(XUL+i,YUL+j,mysd.GetBackupPixel(i,j));
				}
		}		
	}
	
	public boolean BackupReady(){
		return mysd.BackupCopy!=null;
	}
	
	public void ApplySignature(){
		RestoreBackup();	
		if(EnableWM || !BackupReady()){
			PrepareSignatureAndULC();
			for(int i=0;i<XSIZE;i++){
				for(int j=0;j<YSIZE;j++){
					if(XUL+i>=Parent.imgW) continue;
					if(YUL+j>=Parent.imgH) continue;
					int Pixel=Parent.pic.getRGB(XUL+i,YUL+j);
					mysd.SetBackupPixel(i,j,Pixel);
					if(EnableWM) Parent.pic.setRGB(XUL+i,YUL+j,ApplySignature(XUL+i,YUL+j,Pixel));					
				}
			}			
		}
		Parent.Update();
	}
	
	public int ApplySignature(int i,int j,int Pixel){
		if(!EnableWM) return Pixel;	
		int xoff,yoff;
		xoff=i-XUL;
		yoff=j-YUL;
		if(xoff<0 || xoff>=XSIZE) return Pixel;
		if(yoff<0 || yoff>=YSIZE) return Pixel;
		return CombinePixel(xoff,yoff,Pixel);
	}
	
	//Modify here
	public int CombinePixel(int i,int j,int Pixel){
		int pix=mysd.GetPixel(i,j);
		if(pix==0) return Pixel;
		else{
			switch (TypUD.getSelectedIndex()){
				case 0:
					return CombNormal(pix,Pixel);
				case 1:
					return CombDarken(pix,Pixel);
				case 2:
					return CombLighten(pix,Pixel);
				default:
					return CombNormal(pix,Pixel);
			}
		}
	}
	
	public int CombNormal(int pix,int Pixel){
			int alpha=(pix>>24)& 0xFF;
			if(alpha==255) return pix;
			double alphaD=(double) alpha/255.;
			int	Rd=(int)round(alphaD*((double)((pix>>16) & 0xFF))+(1-alphaD)*((double)((Pixel>>16) & 0xFF)));
			int	Gd=(int)round(alphaD*((double)((pix>>8) & 0xFF))+(1-alphaD)*((double)((Pixel>>8) & 0xFF)));
			int	Bd=(int)round(alphaD*((double)((pix) & 0xFF))+(1-alphaD)*((double)((Pixel) & 0xFF)));
			return (255<<24) | Rd<<16 | Gd<<8 | Bd;
		}	
	
	public int CombDarken(int pix,int Pixel){
			int alpha=(pix>>24)& 0xFF;
			if(alpha==0) return Pixel;
			double alphaD=0.75*(1.-(double) alpha/255.)+0.25;
			int	Rd=(int)round(alphaD*((Pixel>>16) & 0xFF));
			int	Gd=(int)round(alphaD*((Pixel>>8) & 0xFF));
			int	Bd=(int)round(alphaD*((Pixel) & 0xFF));
			return (255<<24) | Rd<<16 | Gd<<8 | Bd;
		}	
	
	public int CombLighten(int pix,int Pixel){
			int alpha=(pix>>24)& 0xFF;
			if(alpha==0) return Pixel;
			double alphaD=0.75*(1.-(double) alpha/255.)+0.25;
			int	Rd=255-(int)round(alphaD*(255-((Pixel>>16) & 0xFF)));
			int	Gd=255-(int)round(alphaD*(255-((Pixel>>8) & 0xFF)));
			int	Bd=255-(int)round(alphaD*(255-((Pixel) & 0xFF)));
			return (255<<24) | Rd<<16 | Gd<<8 | Bd;
		}	
	
	
	
	
	
	
	public void PrintData (BufferedWriter o) throws IOException{
		o.write("#WATERMARK\n");
		String temp;
        temp=String.format("wmh %f\n",WMHeightCM);
        temp=temp.replace(',','.');
        o.write(temp);
        temp=String.format("sx %f\n",SpaceXCM);
        temp=temp.replace(',','.');
	    o.write(temp);
	    temp=String.format("sy %f\n",SpaceYCM);
	    temp=temp.replace(',','.');
	    o.write(temp);
	    temp=String.format("corner %d\n",whichcorner);
	    o.write(temp);
	    temp=String.format("blurr %d\n",BlurRadius);
	    o.write(temp);
	    temp=String.format("X3d %d\n",X3d);
	    o.write(temp);
	    temp=String.format("Y3d %d\n",Y3d);
	    o.write(temp);
	    temp=String.format("FontName %s\n",cfname);
	    o.write(temp);
	    temp=String.format("AuthName %s\n",currentAuth);
	    o.write(temp);	    
	    temp=String.format("Type %s\n",Typestring);
	    o.write(temp);
	    temp=String.format("colorR %d\n",RR);
	    o.write(temp);
	    temp=String.format("colorG %d\n",GG);
	    o.write(temp);
	    temp=String.format("colorB %d\n",BB);
	    o.write(temp);
        //Parameters
       	temp=String.format("usewm %b\n",EnableWM);
		o.write(temp);		
		temp=String.format("date %s\n",Datestring.replace(' ','_'));
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
			if(vname.equals("wmh")){
				WMHeightCM=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("sx")){
				SpaceXCM=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("sy")){
				SpaceYCM=Double.parseDouble(st.nextToken());
			}			
			else if(vname.equals("usewm")){
				EnableWM=Boolean.parseBoolean(st.nextToken());
			}
			else if(vname.equals("corner")){
				whichcorner=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("blurr")){
				BlurRadius=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("X3d")){
				X3d=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("Y3d")){
				Y3d=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("colorR")){
				RR=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("colorG")){
				GG=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("colorB")){
				BB=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("FontName")){
				cfname=st.nextToken();
			}
			else if(vname.equals("AuthName")){
				currentAuth=st.nextToken();
			}
			else if(vname.equals("Type")){
				Typestring=st.nextToken();
			}
			else if(vname.equals("date")){
				if(st.hasMoreTokens()){
					Datestring=st.nextToken();	
					Datestring=Datestring.replace('_',' ');
				}
				else Datestring="";
			}
			else{	
				System.out.printf("Reading WATERMARK, found unknown field %s\n",vname);
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
				if(temp.equals("#WATERMARK")){
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
	
	
	
	//OK
	public void UpdateData(){
		Updating=true;
		WMHUD.setValue(WMHeightCM);
		SXUD.setValue(SpaceXCM);
		SYUD.setValue(SpaceYCM);
		CUD.setSelectedIndex(whichcorner);
		Enable.setSelected(EnableWM);
		RedUD.setValue(RR);
		GreenUD.setValue(GG);
		BlueUD.setValue(BB);
		UpdateColorPrev();
		DateUD.setText(Datestring);
		BlurUD.setValue(BlurRadius);
		FontUD.setSelectedIndex(0);
		FontUD.setSelectedItem(cfname);
		cfname=(String)FontUD.getSelectedItem();
		AuthUD.setSelectedIndex(0);
		AuthUD.setSelectedItem(currentAuth);
		currentAuth=(String)AuthUD.getSelectedItem();
		TypUD.setSelectedIndex(0);
		TypUD.setSelectedItem(Typestring);
		Typestring=(String)TypUD.getSelectedItem();		
		X3dUD.setValue(X3d);
		Y3dUD.setValue(Y3d);
		RequestRefresh();
		UpdatePreview(false);
		Updating=false;		
	}	
	
	public void BuildGraphics(){
		setTitle("Water Mark Settings");
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
	
		//First Row Position Selection
		c1.gridy=0;
		c1.gridx=0;
		CL=new JLabel("Select Corner");
		CL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(CL,20*bw,bh);
		P0.add(CL,c1);
		
		c1.gridx=1;
		CUD=new JComboBox();
		CUD.setName("CUD");
		ApplySize(CUD,20*bw,bh);
		CUDL=0;
		CUDH=5;
		CUD.setModel(new DefaultComboBoxModel<>(Corners));
        CUD.addActionListener(this);
        CUD.addMouseWheelListener(this);
		P0.add(CUD,c1);	
	
		//Second row Height selection
		c1.gridy=1;
		c1.gridx=0;
		WMHL=new JLabel("Text Height(cm)");
		WMHL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(WMHL,20*bw,bh);
		P0.add(WMHL,c1);

		c1.gridx=1;
		WMHUD=new JSpinner();
		ApplySize(WMHUD,20*bw,bh);
		WMHUD.setName("WMHUD");
		WMHUD.setModel(new SpinnerNumberModel(1.0d,0.2d,30.0d,0.2d));
		WMHUD.setEditor(new javax.swing.JSpinner.NumberEditor(WMHUD, "###0.00"));        		
		WMHUD.addChangeListener(this);
		WMHUD.addMouseWheelListener(this);
		P0.add(WMHUD,c1);	
	
		//Third and Fourth row: lateral displacement
		c1.gridy=2;
		c1.gridx=0;
		SXL=new JLabel("X Spacing(cm)");
		SXL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(SXL,20*bw,bh);
		P0.add(SXL,c1);

		c1.gridx=1;
		SXUD=new JSpinner();
		ApplySize(SXUD,20*bw,bh);
		SXUD.setName("SXUD");
		SXUD.setModel(new SpinnerNumberModel(1.0d,0.2d,100.0d,0.2d));
		SXUD.setEditor(new javax.swing.JSpinner.NumberEditor(SXUD, "###0.00"));        		
		SXUD.addChangeListener(this);
		SXUD.addMouseWheelListener(this);
		P0.add(SXUD,c1);	
		
		c1.gridy=3;
		c1.gridx=0;
		SYL=new JLabel("Y Spacing(cm)");
		SYL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(SYL,20*bw,bh);
		P0.add(SYL,c1);

		c1.gridx=1;
		SYUD=new JSpinner();
		ApplySize(SYUD,20*bw,bh);
		SYUD.setName("SYUD");
		SYUD.setModel(new SpinnerNumberModel(1.0d,0.2d,100.0d,0.2d));
		SYUD.setEditor(new javax.swing.JSpinner.NumberEditor(SYUD, "###0.00"));        		
		SYUD.addChangeListener(this);
		SYUD.addMouseWheelListener(this);
		P0.add(SYUD,c1);	
	
		//Rows 5-6-7-8 RGB color and preview
		c1.gridy=4;
		c1.gridx=0;
		RedL=new JLabel("Red");
		RedL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(RedL,20*bw,bh);
		P0.add(RedL,c1);
		
		c1.gridx=1;
		RedUD=new JSlider();
		ApplySize(RedUD,20*bw,bh);
		RedUD.setMinimum(0);
		RedUD.setMaximum(255);
		RedUD.addChangeListener(this);
		RedUD.setName("RedUD");
		P0.add(RedUD,c1);
		
		c1.gridy=5;
		c1.gridx=0;
		GreenL=new JLabel("Green");
		GreenL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(GreenL,20*bw,bh);
		P0.add(GreenL,c1);
		
		c1.gridx=1;
		GreenUD=new JSlider();
		ApplySize(GreenUD,20*bw,bh);
		GreenUD.setMinimum(0);
		GreenUD.setMaximum(255);
		GreenUD.addChangeListener(this);
		GreenUD.setName("GreenUD");
		P0.add(GreenUD,c1);
		
		c1.gridy=6;
		c1.gridx=0;
		BlueL=new JLabel("Blue");
		BlueL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(BlueL,20*bw,bh);
		P0.add(BlueL,c1);
		
		c1.gridx=1;
		BlueUD=new JSlider();
		ApplySize(BlueUD,20*bw,bh);
		BlueUD.setMinimum(0);
		BlueUD.setMaximum(255);
		BlueUD.addChangeListener(this);
		BlueUD.setName("BlueUD");
		P0.add(BlueUD,c1);
		
		
		
		c1.gridy=7;
		c1.gridx=0;
		c1.gridwidth=2;
		prevLab1=new JLabel();
		prevLab1.setOpaque(true);
		ApplySize(prevLab1,40*bw,bh);
		P0.add(prevLab1,c1);
		c1.gridwidth=1;
		//Row 9 Date selection
		c1.gridy=8;
		c1.gridx=0;
		DateBtn=new JButton("Select Date");
		DateBtn.setName("DateBtn");
		DateBtn.addActionListener(this);
		ApplySize(DateBtn,20*bw,bh);
		P0.add(DateBtn,c1);
		
		c1.gridx=1;
		DateUD=new JTextField();
		DateUD.setName("DateUD");
		ApplySize(DateUD,20*bw,bh);
		DateUD.setFocusable(true);
		DateUD.addActionListener(this);
        P0.add(DateUD,c1);	
        
        //Row10 FontSelection 
        c1.gridy=9;
        c1.gridx=0;
        FontL=new JLabel("Font");
		FontL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(FontL,20*bw,bh);
		P0.add(FontL,c1);
		
		c1.gridx=1;
		FontUD=new JComboBox();
		FontUD.setName("FontUD");
		ApplySize(FontUD,20*bw,bh);
		FontH=AvailableFonts.length-1;
		FontUD.setModel(new DefaultComboBoxModel<>(AvailableFonts));
        FontUD.addActionListener(this);
        FontUD.addMouseWheelListener(this);
		P0.add(FontUD,c1);	
	
        //Row11 Author
        c1.gridy=10;
        c1.gridx=0;
        AuthL=new JLabel("Author");
		AuthL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(AuthL,20*bw,bh);
		P0.add(AuthL,c1);
		
		c1.gridx=1;
		AuthUD=new JComboBox();
		AuthUD.setName("AuthUD");
		ApplySize(AuthUD,20*bw,bh);
		AuthH=AvailableAuthors.length-1;
		AuthUD.setModel(new DefaultComboBoxModel<>(AvailableAuthors));
        AuthUD.addActionListener(this);
        AuthUD.addMouseWheelListener(this);
		P0.add(AuthUD,c1);	
		
        //Row12 DrawOption (TBI)
         c1.gridy=11;
        c1.gridx=0;
        TypL=new JLabel("Application");
		TypL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(TypL,20*bw,bh);
		P0.add(TypL,c1);
		
		c1.gridx=1;
		TypUD=new JComboBox();
		TypUD.setName("TypUD");
		ApplySize(TypUD,20*bw,bh);
		TypH=TypeWM.length-1;
		TypUD.setModel(new DefaultComboBoxModel<>(TypeWM));
        TypUD.addActionListener(this);
        TypUD.addMouseWheelListener(this);
		P0.add(TypUD,c1);	
        
        //Row13 BlurRadius
        c1.gridy=12;
		c1.gridx=0;
		BlurL=new JLabel("Blur Radius(px)");
		BlurL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(BlurL,20*bw,bh);
		P0.add(BlurL,c1);

		c1.gridx=1;
		BlurUD=new JSpinner();
		ApplySize(BlurUD,20*bw,bh);
		BlurUD.setName("BlurUD");
		BlurUD.setModel(new SpinnerNumberModel(0,-20,20,1));
		BlurUD.addChangeListener(this);
		BlurUD.addMouseWheelListener(this);
		P0.add(BlurUD,c1);	
		
		//Row14 X3d
        c1.gridy=13;
		c1.gridx=0;
		X3dL=new JLabel("3D effect Dx");
		X3dL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(X3dL,20*bw,bh);
		P0.add(X3dL,c1);

		c1.gridx=1;
		X3dUD=new JSpinner();
		ApplySize(X3dUD,20*bw,bh);
		X3dUD.setName("X3dUD");
		X3dUD.setModel(new SpinnerNumberModel(0,-20,20,1));
		X3dUD.addChangeListener(this);
		X3dUD.addMouseWheelListener(this);
		P0.add(X3dUD,c1);	
		
		//Row15 Y3d
        c1.gridy=14;
		c1.gridx=0;
		Y3dL=new JLabel("3D effect Dy");
		Y3dL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(Y3dL,20*bw,bh);
		P0.add(Y3dL,c1);

		c1.gridx=1;
		Y3dUD=new JSpinner();
		ApplySize(Y3dUD,20*bw,bh);
		Y3dUD.setName("Y3dUD");
		Y3dUD.setModel(new SpinnerNumberModel(0,-20,20,1));
		Y3dUD.addChangeListener(this);
		Y3dUD.addMouseWheelListener(this);
		P0.add(Y3dUD,c1);	
		
        //Row16 UseWatermark
        c1.gridy=15;
        c1.gridx=0;
        Enable=new JCheckBox("Apply Watermark");
        ApplySize(Enable,20*bw,bh);
		Enable.setName("Enable");
		Enable.addActionListener(this);
		P0.add(Enable,c1);	
		
		c1.gridx=1;
		RefreshBtn=new JButton("Refresh Prev.");
		RefreshBtn.setName("RefreshBtn");
		RefreshBtn.addActionListener(this);
		ApplySize(RefreshBtn,20*bw,bh);
		P0.add(RefreshBtn,c1);
		
        
        //Row17 preview
        c1.gridy=16;
        c1.gridx=0;
        c1.gridwidth=2;
        PrevLabMain=new JLabel();
        PrevLabMain.setOpaque(true);
        PrevLabMain.setBackground(Color.WHITE);
		ApplySize(PrevLabMain,40*bw,5*bh);
		P0.add(PrevLabMain,c1);
		
		//Row18 ProgressBar
        c1.gridy=17;
        c1.gridx=0;
        c1.gridwidth=2;
        RepBar=new JProgressBar(0,1000);
        RepBar.setStringPainted(true);
        RepBar.setString("OK");
        RepBar.setValue(100);
        ApplySize(RepBar,40*bw,1*bh);
        RepBar.updateUI();
		P0.add(RepBar,c1);
		
        
        
        
		ApplySize(P0,45*bw,22*bh+70);		
		//end of fourth panel		
		//Addition to main
		add(P0);		
		//main resize
		setSize(P0.getWidth()+10,P0.getHeight()+10);	
	}	
	
	void RequestRefresh(){
		needrefresh=true;
		UpdateRepBar("Refresh needed!",0);
	}
	
	void EndRefresh(){
		needrefresh=false;
		UpdateRepBar("Up To Date!",1000);
	}
	
	void UpdateRepBar(String data,int value){
		if(value<0) RepBar.setValue(0);
		else if(value>1000) RepBar.setValue(1000);
		else RepBar.setValue(value);
		if(data!=null) RepBar.setString(data);
		//RepBar.updateUI();
	}
	
	void UpdateColorPrev(){
		if(TypUD.getSelectedIndex()==0) MyColor=new Color(RR,GG,BB,255);
		else MyColor=Color.BLACK;
		prevLab1.setBackground(MyColor);		
	}
	
	 public static BufferedImage resizepic(BufferedImage image, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.createGraphics();
		//g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return bi;
    }
    
    public void UpdatePreview(boolean coloronly){
			UpdateColorPrev();
			if(coloronly) mysd.Redraw(currentAuth+" "+Datestring,cfname,0,MyColor,BlurRadius,X3d,Y3d);
			else{
				Parent.InitializePic();
				mysd.Generate(currentAuth+" "+Datestring,cfname,0,Parent.myprinter.CmToPixel(WMHeightCM),MyColor,BlurRadius,X3d,Y3d);				
			}	
			if(stop) return;
			UpdateRepBar("Resizing Pic to fit display area",1000);		
			double r1,r2;
            int hf,wf;
            hf=PrevLabMain.getHeight();
            wf=PrevLabMain.getWidth();
            r1=(double)mysd.FinalImage.getWidth()/(double)mysd.FinalImage.getHeight();
            r2=(double)wf/(double)hf;
            if (r1>r2) hf=(int)((double)wf/r1+0.5);
            if (r1<r2) wf=(int)((double)hf*r1+0.5);
            picIco.setImage(resizepic(mysd.FinalImage,wf,hf));        
            if(stop) return;
            PrevLabMain.setIcon(picIco);
            PrevLabMain.updateUI();          
            EndRefresh();
    }
    
    public void Upda(){
		if(sr!=null){
			while(!sr.getState().toString().equals("TERMINATED")){
				stop=true;
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){}
			}	
		}		
		stop=false;
		sr=new SignatureRefresher(this);
		sr.start();    
    }
    
	
	 public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("CUD")){
			if(!Updating){
				whichcorner=CUD.getSelectedIndex();				
			}
		}
		else if(cmd.equals("FontUD")){
			if(!Updating){
				cfname=(String)FontUD.getSelectedItem();
				RequestRefresh();
				Upda();
			}
			
		}
		else if(cmd.equals("AuthUD")){
			if(!Updating){
				currentAuth=(String)AuthUD.getSelectedItem();
				RequestRefresh();
				Upda();
			}			
		}
		else if(cmd.equals("TypUD")){
			if(!Updating){
				Typestring=(String)TypUD.getSelectedItem();
				UpdateColorPrev();
				RequestRefresh();
				Upda();
			}			
		}		
		else if(cmd.equals("DateBtn")){
			Date date = new Date();
			Datestring=GetMonth(date)+" "+year.format(date);
			DateUD.setText(Datestring);
		}
		else if(cmd.equals("RefreshBtn")){
			RequestRefresh();
			Upda();
		}
		else if(cmd.equals("DateUD")){
			if(!Updating){
				Datestring=DateUD.getText();	
				RequestRefresh();
			//	UpdatePreview(false);
			}
		}
		else if(cmd.equals("Enable")){
			if(!Updating){
				EnableWM=Enable.isSelected();				
			}
		}
	}
		
	
    public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("RedUD")){
			if(!Updating){
				RR=(int)RedUD.getValue();
				UpdateColorPrev();
				RequestRefresh();
				//UpdatePreview(true);
				
			}
		}
		else if(cmd.equals("BlueUD")){
			if(!Updating){
				BB=(int)BlueUD.getValue();
				UpdateColorPrev();
				RequestRefresh();
				//UpdatePreview(true);
			}
		}
		else if(cmd.equals("GreenUD")){
			if(!Updating){
				GG=(int)GreenUD.getValue();
				UpdateColorPrev();
				RequestRefresh();
				//UpdatePreview(true);
			}
		}
		else if(cmd.equals("SXUD")){
			if(!Updating){
				SpaceXCM=(double)SXUD.getValue();
			//	UpdateWMparams(true);
			}
		}
		else if(cmd.equals("SYUD")){
			if(!Updating){
				SpaceYCM=(double)SYUD.getValue();
				//UpdateWMparams(true);
			}
		}	
		else if(cmd.equals("WMHUD")){
			if(!Updating){
				WMHeightCM=(double)WMHUD.getValue();	
				RequestRefresh();
				//UpdatePreview(false);
			}
		}
		else if(cmd.equals("BlurUD")){
			if(!Updating){
				BlurRadius=(int)BlurUD.getValue();	
				RequestRefresh();
				//UpdatePreview(true);
			}
		}
		else if(cmd.equals("X3dUD")){
			if(!Updating){
				X3d=(int)X3dUD.getValue();	
				RequestRefresh();
				//UpdatePreview(true);
			}
		}
		else if(cmd.equals("Y3dUD")){
			if(!Updating){
				Y3d=(int)Y3dUD.getValue();	
				RequestRefresh();
				//UpdatePreview(true);
			}
		}
	}
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("WMHUD")){
			double t=(double)WMHUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<0.2) t=0.2;
			if(t>30) t=30;
			WMHUD.setValue(t);	
		}
		else if(cmd.equals("SXUD")){
			double t=(double)SXUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<0.2) t=0.2;
			if(t>100) t=100;
			SXUD.setValue(t);	
		}
		else if(cmd.equals("SYUD")){
			double t=(double)SYUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<0.2) t=0.2;
			if(t>100) t=100;
			SYUD.setValue(t);	
		}
		else if(cmd.equals("BlurUD")){
			int t=(int)BlurUD.getValue();
			t-=e.getWheelRotation();
			if(t<-20) t=-20;
			if(t>20) t=20;
			BlurUD.setValue(t);	
		}
		else if(cmd.equals("X3dUD")){
			int t=(int)X3dUD.getValue();
			t-=e.getWheelRotation();
			if(t<-20) t=-20;
			if(t>20) t=20;
			X3dUD.setValue(t);	
		}
		else if(cmd.equals("Y3dUD")){
			int t=(int)Y3dUD.getValue();
			t-=e.getWheelRotation();
			if(t<-20) t=-20;
			if(t>20) t=20;
			Y3dUD.setValue(t);	
		}		
		else if(cmd.equals("CUD")){
			int t=(int) CUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>CUDH) t=CUDH;
			if(t<CUDL) t=CUDL;
			CUD.setSelectedIndex(t);		
		}		
		else if(cmd.equals("FontUD")){
			int t=(int) FontUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>FontH) t=FontH;
			if(t<0) t=0;
			FontUD.setSelectedIndex(t);		
		}	
		else if(cmd.equals("TypUD")){
			int t=(int) TypUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>TypH) t=TypH;
			if(t<0) t=0;
			TypUD.setSelectedIndex(t);		
		}	
		else if(cmd.equals("AuthUD")){
			int t=(int) AuthUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>AuthH) t=AuthH;
			if(t<0) t=0;
			AuthUD.setSelectedIndex(t);		
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
