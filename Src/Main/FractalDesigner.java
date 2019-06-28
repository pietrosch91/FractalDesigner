//import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
// import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
//import swingwtx.swing.border.*;
//import swingwtx.swing.text.*;

//import com.seaglasslookandfeel.*;
import javax.swing.UIManager.*;
import javax.swing.border.*;



public class FractalDesigner extends JFrame implements WindowListener {
	MandelFrac mf;
	
	
	public FractalDesigner(boolean PrinterSetup){
		setSize(800,600);
		mf=new MandelFrac(800,600,PrinterSetup);
		setLayout(new GridLayout(1,1,0,0));
		add(mf);
		addWindowListener(this);
		if(!PrinterSetup) addKeyListener(mf);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	
	
	public static void main(String[] args) {
		String op1="";
		if(args.length>0){
			op1=args[0];	
		}
		FractalDesigner Window;
		if(op1.equals("Printer")) Window=new FractalDesigner(true);
		else Window=new FractalDesigner(false);
		Window.setVisible(true);
	 }
	 
	public void 	windowActivated(WindowEvent e){}
	public void 	windowClosed(WindowEvent e){System.out.println("Here Closing");}
	public void 	windowClosing(WindowEvent e){
			if(mf.fracdrawer!=null)mf.fracdrawer.goon=false;
			System.out.println("Bye Bye");
			System.exit(0);
		}
	public void 	windowDeactivated(WindowEvent e){}
	public void 	windowDeiconified(WindowEvent e){}
	public void 	windowIconified(WindowEvent e){}
	public void 	windowOpened(WindowEvent e){}
}
