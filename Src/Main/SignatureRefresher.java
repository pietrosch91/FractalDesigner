
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
//import swingwtx.swing.border.*;

//import com.seaglasslookandfeel.*;
import javax.swing.UIManager.*;

class SignatureRefresher extends Thread{
	WaterMarkSettings p;
		
	public SignatureRefresher(WaterMarkSettings P){
		p=P;
	}
	
	public void run(){
		p.UpdatePreview(false);
	}
}
