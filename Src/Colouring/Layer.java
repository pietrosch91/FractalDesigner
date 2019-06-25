
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





public class Layer extends JFrame implements ActionListener,MouseWheelListener,WindowListener{
	static final int bw=80;
	static final int bh=20;
	BufferedImage picprev;
    BufferedImage osci;
    ImageIcon picosci;
    ImageIcon picIcoprev;
    int []Pixels;
    JLabel osciLab,prevLab;
    ColoringManager cman;
        
    ColorComponent Red,Green,Blue;
    //String cType;
    int cind;
    int alpha;
    
    JPanel main;
    //Top Panel
    JPanel TopPane;
    JComboBox TypBox;
    int TypL,TypH;
    JButton SaveBtn;
    JButton LoadBtn;
    
    //Params Panel
    JPanel ParPane;
    JLabel ParTitle;
    ColoringData cdata;
    ColoringParameter []cPar;
   // JLabel []ParLab;
    //JSpinner []ParUD;
    //int ZeroL,ZeroH;
    //int colorselector;
    //double []colorpars;
    
	boolean Updating;
	public int GetColor(double val){
        if (val>=1e20) return 0xff000000;
        return alpha<<24 | Red.GetComponent(val)<<16 | Green.GetComponent(val)<<8 | Blue.GetComponent(val);
    }
    
    public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}
	
    public Layer(){
		cman=new ColoringManager();
		Updating=false;
		//colorpars=new double[7];
        Red=new ColorComponent(this);
        Red.setRGB(2);
        Green=new ColorComponent(this);
        Green.setRGB(1);
        Blue=new ColorComponent(this);
        Blue.setRGB(0);
        //cType="n";
        cind=0;
        alpha=255;
        picprev=new BufferedImage(12*bw+70,100,TYPE_INT_ARGB_PRE);
        osci=new BufferedImage(12*bw+70,128,TYPE_INT_ARGB_PRE);
        picosci=new ImageIcon();
        picIcoprev=new ImageIcon();
        Pixels=new int[12*bw+70];
        BuildGraphics();
        SetColoring(0);
        UpdateData();
    }
    
    public void SetColoring(int indice){
		cdata=cman.GetData(indice);
		System.out.printf("Getting data for %s\n",cdata.GetName());
		int i;
		for(i=0;i<17;i++) cPar[i].Rebuild(cdata);
		cind=indice;
	}
	
	//Save-load

    
	public void PrintData (BufferedWriter o) throws IOException{
		o.write("#COLORS\n");
		String temp;
        temp=String.format("cind %d\n",cind);
        o.write(temp);
        temp=String.format("alpha %d\n",alpha);
        o.write(temp);
        for(int i=0;i<17;i++){
			temp=String.format("param %d %.15f\n",i,GetParam(i));
			temp=temp.replace(',','.');
			o.write(temp);
		}	
		o.write("#END\n");
	}
	
	void PrintToFile(BufferedWriter o) throws IOException{
		PrintData(o);	
		Red.PrintToFile(o);
		Green.PrintToFile(o);
		Blue.PrintToFile(o);
	}


public void ReadData(BufferedReader i) throws IOException{
	StringTokenizer st;
	while(true){
		String temp=i.readLine();
		if(temp==null) return;
		if(temp.equals("#END")) return;
		st=new StringTokenizer(temp);
		String vname=st.nextToken();
		if(vname.equals("cind")){
			SetColoring(Integer.parseInt(st.nextToken()));
		}
		else if(vname.equals("alpha")){
			alpha=Integer.parseInt(st.nextToken());
		}
		else if(vname.equals("param")){
			int index=Integer.parseInt(st.nextToken());
			if(index>=17) continue;
			cPar[index].SetValue(Double.parseDouble(st.nextToken()));			
		}
		else{	
			System.out.printf("Reading COLORS, found unknown field %s\n",vname);
		}		
	}
}
 
public void ReadFromFile(File f){	
		int res;
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			System.out.printf("Searching #COLORS\n");
			while(true){
				String temp=in.readLine();
				if(temp==null){
					in.close();
					break;
				}
				else if(temp.equals("#COLORS")){
					System.out.printf("Found #COLORS\n");
					ReadData(in);
					in.close();	
					break;
				}
			}			
		} catch (IOException ex) {
		}
		Red.ReadFromFile(f);
		Green.ReadFromFile(f);
		Blue.ReadFromFile(f);
		UpdateData();
		DoPreview();
		return;
	}
	

   
    public void UpdateData(){
		Updating=true;
		TypBox.setSelectedIndex(cind);		
		//for(int i=0;i<17;i++) cPar[i].UpdateData();
		/*ParUD[0].setValue(colorselector);	
		for(int i=0;i<6;i++) ParUD[i+1].setValue(colorpars[i]);*/
		Updating=false;
	}
    
  /*  public void PrintColor(BufferedWriter o) throws IOException {
        String temp;
        temp=String.format("%d\n",alpha);
        o.write(temp);
        Red.Print(o);
        Green.Print(o);
        Blue.Print(o);
    }
    
    public int ReadColor(BufferedReader i) throws IOException{
        String temp;
        temp=i.readLine();
        if(temp==null) return 1;
        alpha=Integer.parseInt(temp);
        if(Red.Read(i)>0) return 1;
        if(Green.Read(i)>0) return 1;
        if(Blue.Read(i)>0) return 1;
        return 0;
    }*/
	
	//Previevs
	public void DoPreview(){
        int i;
        double V;
        for(i=0;i<12*bw+70;i++){
            V=256*(double)i/(double)(12*bw+70);
            Pixels[i]=GetColor(V);
        }
        for(i=0;i<100;i++) picprev.setRGB(0,i,12*bw+70,1,Pixels,0,12*bw+70);
        picIcoprev.setImage(picprev);
        prevLab.setIcon(picIcoprev); 
        prevLab.updateUI();
        DoOsci();
    }
    
    public void DoOsci(){
		int vdim=128;
        int np=(12*bw+70)*vdim;
        int i;
        int[] temp=new int[np];
        for(i=0;i<np;i++) temp[i]=255<<24;
        Red.DrawOnOscillo(temp, 12*bw+70,vdim, 2);
        Green.DrawOnOscillo(temp, 12*bw+70,vdim, 1);
        Blue.DrawOnOscillo(temp, 12*bw+70,vdim, 0);
        osci.setRGB(0,0,12*bw+70,vdim, temp,0,12*bw+70);
        picosci.setImage(osci);
        //System.out.printf("%d\n",osciLab.getHeight());
        osciLab.setIcon(picosci);
        osciLab.updateUI();       
    }
    
    public void BuildGraphics(){
		setTitle("Coloring Editor");
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
		c.weighty=0.01;
		c.weightx=0.01;
		GridBagConstraints c1=new GridBagConstraints();
		
		c.gridy=0;
		c.gridx=0;
		TypBox=new JComboBox();
		TypBox.setName("TypBox");
		ApplySize(TypBox,3*bw+10,bh);
		TypL=0;
		TypH=cman.GetColoringCount()-1;
		TypBox.setModel(new DefaultComboBoxModel<>(cman.GetListOfNames()));
        TypBox.addActionListener(this);
        TypBox.addMouseWheelListener(this);
		main.add(TypBox,c);
		c.gridx++;
		JLabel temp=new JLabel();
		c.gridx++;
		SaveBtn=new JButton("Save Colors");
		SaveBtn.setName("SaveBtn");
		ApplySize(SaveBtn,3*bw+10,bh);
		SaveBtn.addActionListener(this);
        main.add(SaveBtn,c);
        c.gridx++;
        LoadBtn=new JButton("Load Colors");
		LoadBtn.setName("LoadBtn");
		ApplySize(LoadBtn,3*bw+10,bh);
		LoadBtn.addActionListener(this);
		main.add(LoadBtn,c);
		c.gridwidth=1;		
		c.gridy=1;
		c.gridx=0;
		main.add(Red,c);
		c.gridx=1;
		main.add(Green,c);
		c.gridx=2;
		main.add(Blue,c);
		
		c.gridx=3;
		//Begin parpane
		ParPane=new JPanel();
		ParPane.setBorder(new LineBorder(Color.BLACK));
		ApplySize(ParPane,3*bw+10,18*bh+150);
		ParPane.setLayout(new GridBagLayout());
		c1.fill=GridBagConstraints.NONE;
		c1.gridheight=1;
		c1.gridwidth=1;
		c1.gridy=0;
		c1.gridx=0;
		c1.weighty=0.02;
		ParTitle=new JLabel("Extra Parameters");
		ParTitle.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(ParTitle,3*bw,bh);
		c1.gridwidth=1;
		ParPane.add(ParTitle,c1);
		c1.gridwidth=1;
		
		cPar=new ColoringParameter[17];
		for(int i =0;i<17;i++){
			c1.gridy++;
			cPar[i]=new ColoringParameter(i,bw,bh);
			c1.gridx=0;
			ParPane.add(cPar[i],c1);
		}
		main.add(ParPane,c);
				
		//End parpane
		//labels for images
		c.gridy=2;
		osciLab=new JLabel("");
		//osciLab.setBorder(new LineBorder(Color.BLACK));
		ApplySize(osciLab,12*bw+70,128);
		c.gridx=0;
		c.gridwidth=4;
		main.add(osciLab,c);
		c.gridy=3;
		prevLab=new JLabel("");
		ApplySize(prevLab,12*bw+70,100);
		c.gridx=0;
		c.gridwidth=4;
		main.add(prevLab,c);
		ApplySize(main,12*bw+80,18*bh+2*bw+350);
		add(main);
		setSize(main.getWidth()+10,main.getHeight()+10);
		setResizable(false);
		//
		DoPreview();
	}
		
	//listener functions
	//MouseWheelListener
	public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("TypBox")){
			int t=(int) TypBox.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>TypH) t=TypH;
			if(t<TypL) t=TypL;
			TypBox.setSelectedIndex(t);		
		}
	}
	
	//ActionListener
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("TypBox")){
			if(!Updating){
				cind=TypBox.getSelectedIndex();
				SetColoring(cind);
			}
			//UpdateLock();
		}
		else if (cmd.equals("SaveBtn")){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Colors"));
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
		else if (cmd.equals("LoadBtn")){
			JFileChooser fc =new JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Colors"));
			fc.setFileFilter(new FileNameExtensionFilter("Fractal Data file", "frc"));
			fc.showOpenDialog(this);
			File f=fc.getSelectedFile();
			if(f==null)return;
			String ptitle=f.getPath();
			if (!ptitle.endsWith(".frc")) ptitle+=".frc";
			f=new File(ptitle);// TODO add your handling code here:
			ReadFromFile(f);				
			DoPreview();				
		}
	}
	
	public Iterator GenerateIterator(){
		return cman.GenerateIterator(cind);
	}
	
	public double GetParam(int index){
		if(index<0 || index>=17) return 0;
		return cPar[index].GetValue();	
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
	
	
	
	//******OLD METHODS*/
	/*
	
		
    //Save/load functions
    public void Print(BufferedWriter o) throws IOException{
		o.write("#COLORS\n");
        String temp;
        temp=String.format("%d\n",cind);
        o.write(temp);
        temp=String.format("%d\n",alpha);
        o.write(temp);
        //parameter
        for(int i=0;i<17;i++){
			temp=String.format("%.15f\n",GetParam(i));
			temp=temp.replace(',','.');
			o.write(temp);
		}	
        //color components
        Red.Print(o);
        Green.Print(o);
        Blue.Print(o);        
          
    }
    
    	
	 
    public int Read(BufferedReader i) throws IOException{
        String temp;
        int error=0;
        temp=i.readLine();
        if(temp==null) return 1;
        SetColoring(Integer.parseInt(temp));
        temp=i.readLine();
        if(temp==null) return 1;
        alpha=Integer.parseInt(temp);
        for(int j=0;j<17;j++){
			temp=i.readLine();
			if(temp==null) return 1;
			cPar[j].SetValue(Double.parseDouble(temp));
		}        
        if(Red.Read(i)>0) return 1;
        if(Green.Read(i)>0) return 1;
        if(Blue.Read(i)>0) return 1;        
		UpdateData();     
        return 0;
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
				if(temp.equals("#COLORS")){
					res=Read(in);
					in.close();	
					return res;
				}
			}			
		} catch (IOException ex) {
		}
		return 1;
	}
    
    */
}
