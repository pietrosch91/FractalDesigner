
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

public class FormulaEditor extends JFrame implements ActionListener,MouseWheelListener,WindowListener{
	static final int bw=120;
	static final int bh=20;
	JLabel FormulaLabel;
	JComboBox FormulaBox;
	JButton SaveBtn;
    JButton LoadBtn;
	int FormL,FormH;
	JPanel main;
	JPanel FormulaPanel;
	IterationPanel ip;
	BooleanPanel bp;
	JPanel ParaPanel;
	FormulaHeader fHeader;
	FormulaParameter[] fParameter;
	FormulaSpecial fSpec;
	FormulaManager fmanager;
	FormulaData fdata;
	int find;
	boolean Updating;
	MandelFrac client;
	
	public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}
		
    public FormulaEditor(MandelFrac c){
		Updating=false;
		fmanager=new FormulaManager();
		//find=0;
		SetClient(c);
        BuildGraphics();
        SetFormula(0);
        UpdateData();
    }
    
    public void SetClient(MandelFrac c){
		client=c;
	}
	
	
	public void PrintData (BufferedWriter o) throws IOException{
		o.write("#FORMULA\n");
		String temp;
        temp=String.format("find %d\n",find);
        o.write(temp);
        temp=String.format("niters %d\n",GetIters());
        o.write(temp);
        temp=String.format("alternate %b\n",GetAlternate());
	    o.write(temp);
	    temp=String.format("conjugate %b\n",GetConjugate());
	    o.write(temp);
	    temp=String.format("fixed %b\n",GetFixed());
	    o.write(temp);
	    temp=String.format("useorbit %b\n",GetOrbit());
	    o.write(temp);
        //Parameters
        for(int i=0;i<12;i++){
			temp=String.format("param %d %.15f %.15f\n",i,GetParamR(i),GetParamI(i));
			temp=temp.replace(',','.');
			o.write(temp);
		}
		temp=String.format("spec %s\n",fSpec.GetValue());
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
			if(vname.equals("find")){
				SetFormulaLoad(Integer.parseInt(st.nextToken()));
			}
			else if(vname.equals("niters")){
				ip.SetValue(Integer.parseInt(st.nextToken()));
			}
			else if(vname.equals("alternate")){
				bp.SetAlternate(Boolean.parseBoolean(st.nextToken()));
			}
			else if(vname.equals("conjugate")){
				bp.SetConjugate(Boolean.parseBoolean(st.nextToken()));        
			}
			else if(vname.equals("fixed")){
				bp.SetFixed(Boolean.parseBoolean(st.nextToken()));
			}
			else if(vname.equals("useorbit")){
				bp.SetOrbit(Boolean.parseBoolean(st.nextToken()));
			}
			else if(vname.equals("param")){
				int index=Integer.parseInt(st.nextToken());
				if(index>=12) continue;
				fParameter[index].SetValue(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));					
			}
			else if(vname.equals("spec")){
				if(st.hasMoreTokens()){
					fSpec.SetValue(st.nextToken());	
				}
				else fSpec.SetValue("");
			}
			else{	
				System.out.printf("Reading FORMULA, found unknown field %s\n",vname);
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
				if(temp.equals("#FORMULA")){
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
	
	
	
	
	
  
    

	   
    public void UpdateData(){
		Updating=true;
		FormulaBox.setSelectedIndex(find);	
		//for(int i=0;i<10;i++)fParameter[i].UpdateData();	
		//bp.UpdateData();
		Updating=false;
	}
	
	public void BuildGraphics(){
		setTitle("Formula Editor");
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new GridLayout(1,1,1,1));
		
		//Top Panel
		FormulaPanel=new JPanel();
		//FormulaPanel.setBorder(new LineBorder(Color.BLACK));
		FormulaPanel.setLayout(new GridBagLayout());
		ApplySize(FormulaPanel,6*bw+20,bh+10);
		GridBagConstraints c=new GridBagConstraints();
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.01;
		c.weightx=0.01;
		c.gridx=0;
		c.gridy=0;
		
		FormulaLabel=new JLabel("Formula");
		ApplySize(FormulaLabel,bw,bh);
		FormulaPanel.add(FormulaLabel,c);
		
		c.gridx=1;		
		FormulaBox=new JComboBox();
		FormulaBox.setName("FormulaBox");
		ApplySize(FormulaBox,3*bw,bh);
		FormL=0;
		FormH=fmanager.GetFormulaCount()-1;
		FormulaBox.setModel(new DefaultComboBoxModel<>(fmanager.GetListOfNames()));
        FormulaBox.addActionListener(this);
        FormulaBox.addMouseWheelListener(this);
		FormulaPanel.add(FormulaBox,c);
		
		c.gridx=2;
		SaveBtn=new JButton("Save");
		SaveBtn.setName("SaveBtn");
		ApplySize(SaveBtn,bw,bh);
		SaveBtn.addActionListener(this);
        FormulaPanel.add(SaveBtn,c);
        c.gridx=3;
        LoadBtn=new JButton("Load");
		LoadBtn.setName("LoadBtn");
		ApplySize(LoadBtn,bw,bh);
		LoadBtn.addActionListener(this);
		FormulaPanel.add(LoadBtn,c);
		
		//Iteration panel
		ip=new IterationPanel(bw,bh);
		ip.SetValue(100);
		
		//Boolean panel
		bp=new BooleanPanel(bw,bh);
		
		/*c.gridx=2;
		JLabel temp=new JLabel();
		ApplySize(temp,bw,bh);
		FormulaPanel.add(temp,c);*/
		
		//Parameter panel
		ParaPanel=new JPanel();
		//ParaPanel.setBorder(new LineBorder(Color.BLACK));
		ParaPanel.setLayout(new GridBagLayout());
		ApplySize(ParaPanel,5*bw+20,14*(bh+5)+30);
		c=new GridBagConstraints();
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.01;
		c.weightx=0.01;
		c.gridx=0;
		c.gridy=0;
		fHeader=new FormulaHeader(bw,bh);
		ParaPanel.add(fHeader,c);
		fParameter=new FormulaParameter[12];
		for(int i=0;i<12;i++){
			fParameter[i]=new FormulaParameter(i,bw,bh);
			c.gridy=i+1;
			ParaPanel.add(fParameter[i],c);
		}
		fSpec=new FormulaSpecial(bw,bh);
		c.gridy++;
		ParaPanel.add(fSpec,c);
				
		//main panel
		main=new JPanel();
		main.setBorder(new LineBorder(Color.BLACK));
		main.setLayout(new GridBagLayout());
		c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.1;
		c.weightx=0.1;
		c.gridx=0;
		c.gridy=0;
		main.add(FormulaPanel,c);
		c.gridy=1;
		c.gridheight=2;
		main.add(ip,c);
		c.gridy=3;
		c.gridheight=1;
		main.add(bp,c);
		c.gridy=4;
		c.gridheight=11;
		main.add(ParaPanel,c);	
		
		ApplySize(main,6*bw+30,17*(bh+5)+200);
		add(main);
		setSize(main.getWidth()+10,main.getHeight()+50);
		setResizable(false);
	}
	
	public void SetFormula(int indice){
		fdata=fmanager.GetData(indice);
		System.out.printf("Getting data for %s\n",fdata.GetName());
		int i;
		for(i=0;i<12;i++) fParameter[i].Rebuild(fdata);
		fSpec.Rebuild(fdata);
		if(client!=null){
			client.SetView(fdata.GetXUL(),fdata.GetYUL(),fdata.GetXDR(),fdata.GetYDR());
			//client.SetPrototype(fdata.GetFormula());
		}
		find=indice;
	}
	
	public void SetFormulaLoad(int indice){
		fdata=fmanager.GetData(indice);
		System.out.printf("Getting data for %s\n",fdata.GetName());
		int i;
		for(i=0;i<12;i++) fParameter[i].Rebuild(fdata);
		fSpec.Rebuild(fdata);
		find=indice;
	}
	
	public Formula GenerateFormula(){
		return fmanager.GenerateFormula(find);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("FormulaBox")){
			int t=(int) FormulaBox.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>FormH) t=FormH;
			if(t<FormL) t=FormL;
			FormulaBox.setSelectedIndex(t);		
		}
	}
	
	//ActionListener
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("FormulaBox")){
			if(!Updating){
				int typo=(int) FormulaBox.getSelectedIndex();
				SetFormula(typo);
			}
		}
		else if (cmd.equals("SaveBtn")){
			JFileChooser fc =new  JFileChooser();
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Formulas"));
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
			fc.setCurrentDirectory(new File(MandelFrac.DocFolder+File.separator+"Formulas"));
			fc.setFileFilter(new FileNameExtensionFilter("Fractal Data file", "frc"));
			fc.showOpenDialog(this);
			File f=fc.getSelectedFile();
			if(f==null)return;
			String ptitle=f.getPath();
			if (!ptitle.endsWith(".frc")) ptitle+=".frc";
			f=new File(ptitle);// TODO add your handling code here:
			ReadFromFile(f);				
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
	
	public double GetParamR(int index){
		if(index<0 || index>=12) return 0;
		return fParameter[index].GetValueR();	
	}
	
	public double GetParamI(int index){
		if(index<0 || index>=12) return 0;
		return fParameter[index].GetValueI();	
	}
	
	public String GetSpecial(){
		return fSpec.GetValue();	
	}
	
	
	public int GetIters(){
		return ip.GetValue();
	}
	
	public boolean GetAlternate(){
		return bp.GetAlternate();
	}
		
	public boolean GetConjugate(){
		return bp.GetConjugate();
	}	
	
	public boolean GetFixed(){
		return bp.GetFixed();
	}
	
	public boolean GetOrbit(){
		return bp.GetOrbit();
	}

	
//***************OLD METHODS****//
	/*  
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
				if(temp.equals("#FORMULA")){
					res=Read(in);
					in.close();	
					return res;
				}
			}			
		} catch (IOException ex) {
		}
		return 1;
	}
	
	
	//Save/load functions
    public void Print(BufferedWriter o) throws IOException{
		o.write("#FORMULA\n");		
        String temp;
        temp=String.format("%d\n",find);
        o.write(temp);
        temp=String.format("%d\n",GetIters());
        o.write(temp);
        temp=String.format("%b\n",GetAlternate());
	    o.write(temp);
	    temp=String.format("%b\n",GetConjugate());
	    o.write(temp);
	    temp=String.format("%b\n",GetFixed());
	    o.write(temp);
        //Parameters
        for(int i=0;i<12;i++){
			temp=String.format("%.15f\n%.15f\n",GetParamR(i),GetParamI(i));
			temp=temp.replace(',','.');
			o.write(temp);
		}
		temp=String.format("%s\n",fSpec.GetValue());
		o.write(temp);		
    }
    
     public int Read(BufferedReader i) throws IOException{
        String temp;
        int error=0;
        temp=i.readLine();
        if(temp==null) return 1;
        SetFormulaLoad(Integer.parseInt(temp));
		temp=i.readLine();
        if(temp==null) return 1;
        ip.SetValue(Integer.parseInt(temp));
        
        temp=i.readLine();
        if(temp==null) return 1;
        bp.SetAlternate(Boolean.parseBoolean(temp));
        temp=i.readLine();
        if(temp==null) return 1;
        bp.SetConjugate(Boolean.parseBoolean(temp));
        temp=i.readLine();
        if(temp==null) return 1;
        bp.SetFixed(Boolean.parseBoolean(temp));
        double vr,vi;
        for(int j=0;j<12;j++){
			temp=i.readLine();
			if(temp==null) return 1;
			vr=Double.parseDouble(temp);
			temp=i.readLine();
			if(temp==null) return 1;
			vi=Double.parseDouble(temp);
			fParameter[j].SetValue(vr,vi);		
		}
		temp=i.readLine();
		if(temp==null) return 1;
		fSpec.SetValue(temp);	
		UpdateData();        
        return 0;
    }
    
	*/
    
	
	
}
  
