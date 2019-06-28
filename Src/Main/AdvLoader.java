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





public class AdvLoader extends JFrame implements ActionListener,MouseWheelListener,WindowListener{
	String[] MainNames;
	String[] SubNames;
	JPanel P0;
	String MainFolder;
	String SubFolder;
	String SelectedMain;
	String SelectedSub;
	MandelFrac Parent;
	
	static final int bw=10;
	static final int bh=20;
	
	int NMain,Nsub;
	
	JButton QuitBtn;
	JButton LoadBtn;
	JLabel MainLab,SubLab;
	JComboBox MainUD,SubUD;	
	boolean Updating;
	
	
	
	public AdvLoader(MandelFrac p){
		Parent=p;
		MainFolder=Parent.DocFolder;
		MainNames=GetMainList();
		SubFolder="";
		SelectedMain="";
		SelectedSub="";
		Updating=false;
		BuildGraphics();			
	}
	
	public void UpdateAvailability(){
		if(SelectedMain.isEmpty()){
			SubUD.setEnabled(false);
			LoadBtn.setEnabled(false);
		}
		else{
			SubUD.setEnabled(true);
			LoadBtn.setEnabled(true);
			if(SelectedSub.isEmpty()) LoadBtn.setEnabled(false);
		}
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
	
	String [] GetMainList(){
		File folder = new File(MainFolder);
		File[] listOfFiles = folder.listFiles();
		int count=0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".frc")) count++;
			}
		}
		String [] res=new String[count+1];
		res[0]="";
		count=1;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".frc")){
					String data=listOfFiles[i].getName();
					System.out.println(data);
					res[count]=data.substring(0,data.length()-4);
					System.out.println(res[count]);
					count++;
				}
			}
		}
		return res;
	}
	
	String [] GetSubList(){
		if(SelectedMain.isEmpty()) return new String[1];
		File folder = new File(SubFolder);
		File[] listOfFiles = folder.listFiles();
		int count=0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".frc")) count++;
			}
		}
		String [] res=new String[count+1];
		res[0]="Default";
		count=1;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				if(listOfFiles[i].getName().endsWith(".frc")){
					String data=listOfFiles[i].getName();
					res[count]=data.substring(0,data.length()-4);
					System.out.println(res[count]);
					count++;
				}
			}
		}
		return res;
	}
	
	
	public void BuildGraphics(){
		setTitle("Advanced Loader");
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
		
		//First control->Main selection
		c1.gridy=0;
		c1.gridx=0;
		MainLab=new JLabel("Img Name");
		MainLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(MainLab,20*bw,bh);
		P0.add(MainLab,c1);
		
		c1.gridwidth=3;
		c1.gridx=1;
		MainUD=new JComboBox();
		MainUD.setName("MainUD");
		ApplySize(MainUD,40*bw,bh);
		NMain=MainNames.length;
		MainUD.setModel(new DefaultComboBoxModel<>(MainNames));
		MainUD.setSelectedIndex(0);
        MainUD.addActionListener(this);
        MainUD.addMouseWheelListener(this);
		P0.add(MainUD,c1);	
		
		c1.gridwidth=1;
		//Second : sub file
		c1.gridy=1;
		c1.gridx=0;
		SubLab=new JLabel("Size select");
		SubLab.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(SubLab,20*bw,bh);
		P0.add(SubLab,c1);

		c1.gridwidth=3;
		c1.gridx=1;
		SubUD=new JComboBox();
		SubUD.setName("SubUD");
		ApplySize(SubUD,40*bw,bh);
		SubNames=GetSubList();
		Nsub=SubNames.length;
		SubUD.setModel(new DefaultComboBoxModel<>(SubNames));
		SubUD.setSelectedIndex(0);
        SubUD.addActionListener(this);
        SubUD.addMouseWheelListener(this);
		P0.add(SubUD,c1);	
		
		//Third Buttons
		c1.gridwidth=2;
		c1.gridy=2;
		c1.gridx=0;
		QuitBtn=new JButton("Quit");
		QuitBtn.setName("QuitBtn");
		QuitBtn.addActionListener(this);
		ApplySize(QuitBtn,30*bw,bh);
		P0.add(QuitBtn,c1);

		c1.gridx=2;
		LoadBtn=new JButton("Load");
		LoadBtn.setName("LoadBtn");
		LoadBtn.addActionListener(this);
		ApplySize(LoadBtn,30*bw,bh);
		P0.add(LoadBtn,c1);
				
		ApplySize(P0,65*bw,3*bh+70);		
		//Addition to main
		add(P0);		
		//main resize
		setSize(P0.getWidth()+10,P0.getHeight()+10);	
		UpdateAvailability();
	}	
	
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("MainUD")){
			SelectedMain=(String)MainUD.getSelectedItem();
			SubFolder=MainFolder+File.separator+SelectedMain;
			SubNames=GetSubList();
			Nsub=SubNames.length;
			SubUD.setModel(new DefaultComboBoxModel<>(SubNames));
			SubUD.setSelectedIndex(0);
			UpdateAvailability();			
		}
		else if(cmd.equals("SubUD")){
			SelectedSub=(String)SubUD.getSelectedItem();
			UpdateAvailability();			
		}
		else if(cmd.equals("QuitBtn")){
			setVisible(false);
		}
		else if(cmd.equals("LoadBtn")){
			setVisible(false);
			Parent.UpdateParentDataAndLoad();
		}	
	}
		
	
  
    
    public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("MainUD")){
			int t=MainUD.getSelectedIndex();
			t+=1*e.getWheelRotation();
			if(t<0) t=0;
			if(t>=NMain) t=NMain-1;
			MainUD.setSelectedIndex(t);
		}	
		else if(cmd.equals("SubUD")){
			int t=SubUD.getSelectedIndex();
			t+=1*e.getWheelRotation();
			if(t<0) t=0;
			if(t>=Nsub) t=Nsub-1;
			SubUD.setSelectedIndex(t);
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
