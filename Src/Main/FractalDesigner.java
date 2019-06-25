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
	
	
	public FractalDesigner(){
		setSize(800,600);
		mf=new MandelFrac(800,600);
		setLayout(new GridLayout(1,1,0,0));
		add(mf);
		addWindowListener(this);
		addKeyListener(mf);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	
	
	public static void main(String[] args) {
		 FractalDesigner Window=new FractalDesigner();
		 Window.setVisible(true);
	 }
	 
	public void 	windowActivated(WindowEvent e){}
	public void 	windowClosed(WindowEvent e){System.out.println("Here Closing");}
	public void 	windowClosing(WindowEvent e){
			mf.fracdrawer.goon=false;
			System.out.println("Bye Bye");
			System.exit(0);
		}
	public void 	windowDeactivated(WindowEvent e){}
	public void 	windowDeiconified(WindowEvent e){}
	public void 	windowIconified(WindowEvent e){}
	public void 	windowOpened(WindowEvent e){}
}
