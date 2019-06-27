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
	String fontname="LouisaCP";
	String fonttype="otf";
	//static final String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	//String FontName;
	//int FontIndex
	String []Months={"January","February","March","April","May","June","July","August","September","October","November","December"};
	Color []theColors={Color.GRAY,Color.DARK_GRAY,Color.LIGHT_GRAY,Color.BLACK,Color.WHITE,Color.RED,Color.GREEN,Color.BLUE};
	Color []theFColors={Color.BLACK,Color.WHITE,Color.BLACK,Color.WHITE,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK};
	String []Cnames={"Gray","Dark Gray","Light Gray","Black","White","Red","Green","Blue"};
	int colorind;
	int ColorL,ColorH;
	
	String []Corners={"Upper Left","Upper Right","Lower Left","Lower Right"};
	static int bw=10;
	static int bh=20;
	String Datestring;
	MandelFrac Parent;
	double ImageHeightCM;
	double ImageWidthCM;
	double WMHeightCM;
	double SpaceXCM,SpaceYCM;
	boolean EnableWM;
	boolean darken,invert;
	int whichcorner;
	
	DateFormat month=new SimpleDateFormat("MM");
	DateFormat year = new SimpleDateFormat("yyyy");
		
	BufferedImage mySignature;
	
	
	
	boolean EverithingGood;
	
	int ActualFontSize;
	int posX,posY;
	Rectangle2D ActualRect;
	
	
	
	
	
	JPanel P0;
	
	int FUDL,FUDH;
	int CUDL,CUDH;
	JButton DateBtn;
	JTextField DateUD;
	JLabel IHL,WMHL,SXL,SYL,CL,EL,REPL,ColorLab,FL;
	JSpinner IHLUD,WMHUD,SXUD,SYUD;
	JComboBox CUD,ColorUD,FUD;
	JCheckBox ELUD;
	JCheckBox BORDUD,BLURUD;
	
	
	
	
	
	
	boolean Updating;
	
	public String GetMonth(Date d){
		String Full=Months[Integer.parseInt(month.format(d))-1];
		return Full.substring(0,3)+".";
	}
	
	
	public WaterMarkSettings(MandelFrac p){
		try{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontname+"."+fonttype)));
		}catch(FontFormatException e){
		}catch(IOException e){};
		Parent=p;
		whichcorner=3;
		//FontIndex=0;
		ImageHeightCM=100;
		darken=invert=false;
		WMHeightCM=1;
		SpaceXCM=SpaceYCM=2;
		colorind=0;		
		Date date = new Date();
		Datestring=GetMonth(date)+" "+year.format(date);
		EnableWM=false;
		Updating=false;
		BuildGraphics();			
		UpdateDataInit();
	}
	
	

//End of image management
	public void UpdateWMSize(){
		String defText="Pietro Ottanelli, "+Datestring;
		Graphics2D w = (Graphics2D) Parent.pic.getGraphics();
		int fsize=1;
		FontMetrics fontMetrics;
		double targetRatio=WMHeightCM/ImageHeightCM;
		while(true){
            w.setFont(new Font(fontname,0, fsize));
			fontMetrics = w.getFontMetrics();
			ActualRect = fontMetrics.getStringBounds(defText, w);
			double ratio=(double)ActualRect.getHeight()/(double)Parent.pic.getHeight();
			if(ratio>=targetRatio) break;
			fsize+=1;
		}
		ActualFontSize=fsize;
		w.dispose();	
	}
	
	
	public void UpdateWMPosition(){
		switch (whichcorner){
			case 0:
				posX=(int)round((double)Parent.imgW*SpaceXCM/ImageWidthCM);
				posY=(int)round((double)Parent.imgH*SpaceYCM/ImageHeightCM);
				break;
			case 1:
				posX=Parent.imgW-(int)round((double)Parent.imgW*SpaceXCM/ImageWidthCM+ActualRect.getWidth());
				posY=(int)round((double)Parent.imgH*SpaceYCM/ImageHeightCM);
				break;
			case 2:
				posX=(int)round((double)Parent.imgW*SpaceXCM/ImageWidthCM);
				posY=Parent.imgH-(int)round((double)Parent.imgH*SpaceYCM/ImageHeightCM+ActualRect.getHeight());
				break;
			case 3:
				posX=Parent.imgW-(int)round((double)Parent.imgW*SpaceXCM/ImageWidthCM+ActualRect.getWidth());
				posY=Parent.imgH-(int)round((double)Parent.imgH*SpaceYCM/ImageHeightCM+ActualRect.getHeight());
				break;
			default:
				posX=(int)round((double)Parent.imgW*SpaceXCM/ImageWidthCM);
				posY=(int)round((double)Parent.imgH*SpaceYCM/ImageHeightCM);
				break;
		}		
	}
	
	public void CheckBoundaries(){
		String Sres="Good Position";
		boolean res=true;
		if(posX<0 || posY<0){
			res=false;
			Sres="Bad Position";			
		}
		if(posX+ActualRect.getWidth()>Parent.imgW){
			res=false;
			Sres="Bad Position";	
		}
		if(posY+ActualRect.getHeight()>Parent.imgH){
			res=false;
			Sres="Bad Position";	
		}
		EverithingGood=res;
		REPL.setText(Sres);	
	}
	
	public void UpdateWMparams(boolean PosOnly){
		ImageWidthCM=ImageHeightCM*(double)Parent.imgW/(double)Parent.imgH;
		if(!PosOnly || ActualRect==null) UpdateWMSize();		
		UpdateWMPosition();
		CheckBoundaries();
	};
	
	
	public int GetCount(int i,int j,int radius){
		int result=0;
		if(mySignature.getRGB(i,j)==0) return 0;
		//System.out.printf("Found non zero pixel\n");
		int x,y,X,Y;
		for(x=-radius;x<=radius;x++){
			X=i+x;
			if(X<0) continue;
			if(X>=mySignature.getWidth())continue;
			for(y=-radius+abs(x);y<=radius-abs(x);y++){
				Y=j+y;
				if(Y<0) continue;
				if(Y>=mySignature.getHeight())continue;
				
				if(mySignature.getRGB(X,Y)==0) return 1;
			}
		}
		return 0;
	}
	
	public int GetSum(int i,int j,int radius){
		int result=0;
		int count=0;
		int base=0;
		//System.out.printf("Found non zero pixel\n");
		int x,y,X,Y;
		for(x=-radius;x<=radius;x++){
			X=i+x;
			if(X<0) continue;
			if(X>=mySignature.getWidth())continue;
			for(y=-radius+abs(x);y<=radius-abs(x);y++){
				Y=j+y;
				if(Y<0) continue;				
				if(Y>=mySignature.getHeight())continue;
				if((mySignature.getRGB(X,Y) ) !=0){
					base=mySignature.getRGB(X,Y);
					count++;
				}				
			}			
		}
		if(count==0){
			//System.out.println("Count=0");
			return 0;
		}
		int oldalpha=(mySignature.getRGB(i,j) >> 24) & 0xFF;
		int newalpha;
		if(oldalpha==255) newalpha=oldalpha;
		else newalpha=(int)round(255*(double)count/(4*radius+1));
		result=(base & 0xFFFFFF) | (newalpha<<24);
		return result;
	}
	

	
	public void Bordify(int rad){
		BufferedImage newimg=new BufferedImage(mySignature.getWidth(),mySignature.getHeight(),TYPE_INT_ARGB_PRE);
		for(int i=0;i<newimg.getWidth();i++){
			for(int j=0;j<newimg.getHeight();j++){
				newimg.setRGB(i,j,mySignature.getRGB(i,j)*GetCount(i,j,rad));
			}
		}
		mySignature=newimg;
	}
	
	public void Blurrify(int rad){
		BufferedImage newimg=new BufferedImage(mySignature.getWidth(),mySignature.getHeight(),TYPE_INT_ARGB_PRE);
		for(int i=0;i<newimg.getWidth();i++){
			for(int j=0;j<newimg.getHeight();j++){
				newimg.setRGB(i,j,GetSum(i,j,rad));
			}
		}
		mySignature=newimg;
	}
	
	public void DrawBorder(){
		int blk=(255<<24);
		for(int i=0;i<mySignature.getWidth();i++){
			mySignature.setRGB(i,0,blk);
			mySignature.setRGB(i,mySignature.getHeight()-1,blk);
		}
		for(int i=0;i<mySignature.getHeight();i++){
			mySignature.setRGB(0,i,blk);
			mySignature.setRGB(mySignature.getWidth()-1,i,blk);
		}
	}
	
	
	public void ApplyWM(){
		if(!EnableWM) return;
		UpdateWMparams(false);
		if(!EverithingGood) return;
		String defText="Pietro Ottanelli, "+Datestring;
		mySignature=new BufferedImage((int)ceil(ActualRect.getWidth())+10,(int)ceil(ActualRect.getHeight()),TYPE_INT_ARGB_PRE);
		Graphics2D w = (Graphics2D) mySignature.getGraphics();
		w.drawImage(mySignature, 0, 0, null);
        AlphaComposite alphaChannel = AlphaComposite.Src;
        w.setComposite(alphaChannel);
        w.setColor(theColors[colorind]);
        w.setFont(new Font(fontname, 0, ActualFontSize));
		w.drawString(defText, 5, (int)ceil(0.65*ActualRect.getHeight()));
        w.dispose();	
       // if(darken) Bordify(3);
       // if(invert) Blurrify(2);
      //  DrawBorder();
        Parent.PrintWM(mySignature,posX,posY,darken,invert);
	}
	
	public void PrintData (BufferedWriter o) throws IOException{
		o.write("#WATERMARK\n");
		String temp;
        temp=String.format("imgh %f\n",ImageHeightCM);
        temp=temp.replace(',','.');
        o.write(temp);
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
	    temp=String.format("color %d\n",colorind);
	    o.write(temp);
        //Parameters
       	temp=String.format("usewm %b\n",EnableWM);
		o.write(temp);		
		temp=String.format("darken %b\n",darken);
		o.write(temp);		
		temp=String.format("invert %b\n",invert);
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
			if(vname.equals("imgh")){
				ImageHeightCM=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("wmh")){
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
			else if(vname.equals("darken")){
				darken=Boolean.parseBoolean(st.nextToken());
			}
			else if(vname.equals("invert")){
				invert=Boolean.parseBoolean(st.nextToken());
			}
			else if(vname.equals("corner")){
				whichcorner=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("color")){
				colorind=Integer.parseInt(st.nextToken());
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
		IHLUD.setValue(ImageHeightCM);
		WMHUD.setValue(WMHeightCM);
		SXUD.setValue(SpaceXCM);
		SYUD.setValue(SpaceYCM);
		CUD.setSelectedIndex(whichcorner);
		ELUD.setSelected(EnableWM);
		BORDUD.setSelected(darken);
		BLURUD.setSelected(invert);
		ColorUD.setSelectedIndex(colorind);
		ColorUD.setBackground(theColors[colorind]);
		ColorUD.setForeground(theFColors[colorind]);
		DateUD.setText(Datestring);
		UpdateWMparams(false);
		Updating=false;		
	}
		
	public void UpdateDataInit(){
		Updating=true;
		IHLUD.setValue(ImageHeightCM);
		WMHUD.setValue(WMHeightCM);
		SXUD.setValue(SpaceXCM);
		SYUD.setValue(SpaceYCM);
		CUD.setSelectedIndex(whichcorner);
		ELUD.setSelected(EnableWM);
		BORDUD.setSelected(darken);
		BLURUD.setSelected(invert);
		ColorUD.setSelectedIndex(colorind);
		ColorUD.setBackground(theColors[colorind]);
		ColorUD.setForeground(theFColors[colorind]);
		DateUD.setText(Datestring);
		//UpdateWMparams(false);
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
		
		//First control->Desired image height
		c1.gridy=0;
		c1.gridx=0;
		IHL=new JLabel("Img Height(cm)");
		IHL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(IHL,20*bw,bh);
		P0.add(IHL,c1);

		c1.gridx=1;
		IHLUD=new JSpinner();
		ApplySize(IHLUD,20*bw,bh);
		IHLUD.setName("IHLUD");
		IHLUD.setModel(new SpinnerNumberModel(10.0d,1.0d,500.0d,1.0d));
		IHLUD.setEditor(new javax.swing.JSpinner.NumberEditor(IHLUD, "###0.00"));        		
		IHLUD.addChangeListener(this);
		IHLUD.addMouseWheelListener(this);
		P0.add(IHLUD,c1);	
		
		//Second : height of the watermark
		c1.gridy=1;
		c1.gridx=0;
		WMHL=new JLabel("Sign Height(cm)");
		WMHL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(WMHL,20*bw,bh);
		P0.add(WMHL,c1);

		c1.gridx=1;
		WMHUD=new JSpinner();
		ApplySize(WMHUD,20*bw,bh);
		WMHUD.setName("WMHUD");
		WMHUD.setModel(new SpinnerNumberModel(1.0d,0.2d,10.0d,0.2d));
		WMHUD.setEditor(new javax.swing.JSpinner.NumberEditor(WMHUD, "###0.00"));        		
		WMHUD.addChangeListener(this);
		WMHUD.addMouseWheelListener(this);
		P0.add(WMHUD,c1);	
		
		//Third X spacing
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
		SXUD.setModel(new SpinnerNumberModel(1.0d,1.0d,10.0d,0.2d));
		SXUD.setEditor(new javax.swing.JSpinner.NumberEditor(SXUD, "###0.00"));        		
		SXUD.addChangeListener(this);
		SXUD.addMouseWheelListener(this);
		P0.add(SXUD,c1);	
		
		//Fourth Y spacing
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
		SYUD.setModel(new SpinnerNumberModel(1.0d,1.0d,10.0d,0.2d));
		SYUD.setEditor(new javax.swing.JSpinner.NumberEditor(SYUD, "###0.00"));        		
		SYUD.addChangeListener(this);
		SYUD.addMouseWheelListener(this);
		P0.add(SYUD,c1);	
		
		//FIFTH Corner selection
		c1.gridy=4;
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
		CUDH=3;
		CUD.setModel(new DefaultComboBoxModel<>(Corners));
        CUD.addActionListener(this);
        CUD.addMouseWheelListener(this);
		P0.add(CUD,c1);	
		
		//Sixth Color selection
		c1.gridy=5;
		c1.gridx=0;
		ColorLab=new JLabel("Select Color");
		ColorLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(ColorLab,20*bw,bh);
		P0.add(ColorLab,c1);
		
		c1.gridx=1;
		ColorUD=new JComboBox();
		ColorUD.setName("ColorUD");
		ApplySize(ColorUD,20*bw,bh);
		ColorL=0;
		ColorH=7;
		ColorUD.setModel(new DefaultComboBoxModel<>(Cnames));
        ColorUD.addActionListener(this);
        ColorUD.addMouseWheelListener(this);
        ColorUD.setOpaque(true);
		P0.add(ColorUD,c1);	
		
		//Seventh Date Selection
		c1.gridy=6;
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
		
		
		
		//Eight EnableWM
		c1.gridy=7;
		c1.gridx=0;
		EL=new JLabel("Apply Watermark");
		EL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(EL,20*bw,bh);
		P0.add(EL,c1);
		
		c1.gridx=1;
		ELUD=new JCheckBox();
		ApplySize(ELUD,20*bw,bh);
		ELUD.setName("ELUD");
		ELUD.addActionListener(this);
		P0.add(ELUD,c1);	
		
		
		//Ninth Bord and invert
		c1.gridy=8;
		c1.gridx=0;
		BORDUD=new JCheckBox();
		ApplySize(BORDUD,20*bw,bh);
		BORDUD.setName("BORDUD");
		BORDUD.setText("Darken");
		BORDUD.addActionListener(this);
		P0.add(BORDUD,c1);
		
		c1.gridx=1;
		BLURUD=new JCheckBox();
		ApplySize(BLURUD,20*bw,bh);
		BLURUD.setName("BLURUD");
		BLURUD.setText("Invert");
		BLURUD.addActionListener(this);
		P0.add(BLURUD,c1);
		
		
		//Tenth Label
		c1.gridy=9;
		c1.gridx=0;
		c1.gridwidth=2;
		REPL=new JLabel();
		REPL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(REPL,40*bw,bh);
		P0.add(REPL,c1);			
		
	/*	//Temp
		c1.gridwidth=1;		
		c1.gridy=9;
		c1.gridx=0;
		FL=new JLabel("Select Font");
		FL.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(FL,20*bw,bh);
		P0.add(FL,c1);
		
		c1.gridx=1;
		FUD=new JComboBox();
		FUD.setName("FUD");
		ApplySize(FUD,20*bw,bh);
		FUDL=0;
		FUDH=fonts.length;
		FUD.setModel(new DefaultComboBoxModel<>(fonts));
        FUD.addActionListener(this);
        FUD.addMouseWheelListener(this);
        //FUD.setOpaque(true);
		P0.add(FUD,c1);	*/
		
		
		
		ApplySize(P0,45*bw,9*bh+70);		
		
		
		
		//end of fourth panel		
		//Addition to main
		add(P0);		
		//main resize
		setSize(P0.getWidth()+10,P0.getHeight()+10);	
	}	
	
	 public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("CUD")){
			if(!Updating){
				whichcorner=CUD.getSelectedIndex();
				UpdateWMparams(true);
			}
		}
		else if(cmd.equals("ColorUD")){
			if(!Updating){
				colorind=ColorUD.getSelectedIndex();
				ColorUD.setBackground(theColors[colorind]);
				ColorUD.setForeground(theFColors[colorind]);
				//UpdateWMparams(true);
			}
		}
	/*	else if(cmd.equals("FUD")){
			if(!Updating){
				FontIndex=FUD.getSelectedIndex();
				FUD.setFont(Font.getFont(fonts[FontIndex]));
				//UpdateWMparams(true);
			}
		}*/
		else if(cmd.equals("ELUD")){
			if(!Updating){
				EnableWM=ELUD.isSelected();
			}		
		}
		else if(cmd.equals("BORDUD")){
			if(!Updating){
				darken=BORDUD.isSelected();
			}		
		}
		else if(cmd.equals("BLURUD")){
			if(!Updating){
				invert=BLURUD.isSelected();
			}		
		}
		
		else if(cmd.equals("DateBtn")){
			Date date = new Date();
			Datestring=GetMonth(date)+" "+year.format(date);
			DateUD.setText(Datestring);
		}
		else if(cmd.equals("DateUD")){
			if(!Updating){
				Datestring=DateUD.getText();
				UpdateWMparams(false);
			}
		}
	}
		
	
    public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("IHLUD")){
			if(!Updating){
				ImageHeightCM=(double)IHLUD.getValue();
				UpdateWMparams(false);
			}
		}
		else if(cmd.equals("WMHUD")){
			if(!Updating){
				WMHeightCM=(double)WMHUD.getValue();	
				UpdateWMparams(false);
			}
		}
		else if(cmd.equals("SXUD")){
			if(!Updating){
				SpaceXCM=(double)SXUD.getValue();
				UpdateWMparams(true);
			}
		}
		else if(cmd.equals("SYUD")){
			if(!Updating){
				SpaceYCM=(double)SYUD.getValue();
				UpdateWMparams(true);
			}
		}		
	}
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("IHLUD")){
			double t=(double)IHLUD.getValue();
			t-=1*e.getWheelRotation();
			if(t<1) t=1;
			if(t>500) t=500;
			IHLUD.setValue(t);	
		}	
		else if(cmd.equals("WMHUD")){
			double t=(double)WMHUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<0.2) t=0.2;
			if(t>10) t=10;
			WMHUD.setValue(t);	
		}
		else if(cmd.equals("SXUD")){
			double t=(double)SXUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<1) t=1;
			if(t>10) t=10;
			SXUD.setValue(t);	
		}
		else if(cmd.equals("SYUD")){
			double t=(double)SYUD.getValue();
			t-=0.2*e.getWheelRotation();
			if(t<1) t=1;
			if(t>10) t=10;
			SYUD.setValue(t);	
		}
		else if(cmd.equals("CUD")){
			int t=(int) CUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>CUDH) t=CUDH;
			if(t<CUDL) t=CUDL;
			CUD.setSelectedIndex(t);		
		}
		else if(cmd.equals("ColorUD")){
			int t=(int) ColorUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>ColorH) t=ColorH;
			if(t<ColorL) t=ColorL;
			ColorUD.setSelectedIndex(t);		
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
