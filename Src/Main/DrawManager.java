
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

class DrawThread extends Thread{
	MandelFrac frcM;
	DrawManager parent;
	int index;
	boolean conly;
	int offset;
	int IlineUp,IlineDn;
	
	
	public DrawThread(DrawManager P,int Ind,int NLine,boolean coloronly){
		parent=P;
		frcM=P.ParentM;
		index=Ind;
		offset=NLine;
		IlineUp=P.upFirst+NLine;
		IlineDn=P.downFirst-NLine;
		conly=coloronly;
	}
	
	public void run(){
		if(!conly){
			frcM.DrawLine(IlineUp);
			if(IlineDn!=IlineUp)frcM.DrawLine(IlineDn);
		}
		else{
			frcM.RecolorLine(IlineUp);
			if(IlineDn!=IlineUp)frcM.RecolorLine(IlineDn);
		}
		parent.status[offset]=1;
		parent.drawerActive[index]=false;		
	}
}

class PaintThread extends Thread{
	MandelFrac frcM;
	DrawManager parent;
	
	int upFirst,downFirst;
	int Low;
	int Nlines;
	
	
	
	public PaintThread(DrawManager P,int lind,int nlin){
		parent=P;
		upFirst=P.upFirst;
		downFirst=P.downFirst;
		frcM=P.ParentM;
		Low=lind;
		Nlines=nlin;
	}
	
	public void run(){
		for(int i=0;i<Nlines;i++){
			frcM.UpdateLine(upFirst+Low+i);
			frcM.UpdateLine(downFirst-Low-i);
			parent.status[Low+i]=2;
		}
		frcM.Update();
		parent.painterActive=false;		
	}
}






public class DrawManager extends Thread {
	volatile boolean goon=true;
	volatile boolean active=false;
	volatile boolean coloronly=false;
	volatile int nlines=1;
	
	volatile int DrawCounter;
	volatile int ColorCounter;
	volatile int []status;
	
	//boolean oddLines;
	int ncall;
	int upFirst,downFirst;
	
	
	boolean stopsignal;
	int nth;
	volatile boolean lockdraw;
	DrawThread []Drawers;
	volatile boolean []drawerActive;
	
	volatile boolean is_clear;
	volatile int startcolor;
	volatile boolean lockcolor;
	PaintThread Painter;
	volatile boolean painterActive;
	
	MandelFrac ParentM;
	
	
	public DrawManager(MandelFrac p){
		super();
		is_clear=true;
		stopsignal=false;
		ParentM=p;
		DrawCounter=ColorCounter=0;
		nth=8;
		Drawers=new DrawThread[nth];
		drawerActive=new boolean[nth];
	}
	
	public void SetNLines(int nl,int init){
		if(!active){
			nlines=nl;
			
			//status=new int[nlines];
			//for(int i=0;i<nlines;i++) status[i]=init;
			if(nl%2==0){
				ncall=nl/2;
				upFirst=nl/2;
				downFirst=nl/2-1;
			}
			else{
				ncall=(nl+1)/2;
				upFirst=downFirst=(nl-1)/2;
			}			
			status=new int[ncall];
			for(int i=0;i<ncall;i++) status[i]=init;
			
		}
	}
	
	public int FindColorable(){
		startcolor=ColorCounter;
		int newcc=0;
		
		//for(int i=startcolor;i<nlines;i++){
		for(int i=startcolor;i<ncall;i++){
		
			if(status[i]==1) newcc++;
			else break;
		}
		ColorCounter=startcolor+newcc;	
		//System.out.println(""+newcc+" "+startcolor);
		return newcc;	
	}
	
	
	
	public void run(){
		while(goon){
			//System.out.printf("DrawManager is Alive\n");
			if(active){
				stopsignal=false;
				is_clear=false;
				//check if DrawThread are on
				for(int i=0;i<nth;i++){
					if(!lockdraw){
						if(!drawerActive[i]){
					 		//System.out.printf("Starting subthread %d Line %d\n",i,DrawCounter);
							drawerActive[i]=true;
							Drawers[i]=new DrawThread(this,i,DrawCounter++,coloronly); //draw line i;
							
							//if(DrawCounter==nlines) lockdraw=true;//drawing ended
							if(DrawCounter==ncall) lockdraw=true;//drawing ended
							
							Drawers[i].start();
						}						
					}					
				}
				//Update coloring indexes
				
				if(!lockcolor){//coloring not ended					
					if(!painterActive){//color thread inactive
						int ncolor=FindColorable();
						if(ncolor>0){//lines need to be drawed
						//	System.out.println(""+painterActive);
						//	System.out.printf("Starting color %d to %d\n",startcolor,startcolor+ncolor-1);
							painterActive=true;
							Painter=new PaintThread(this,startcolor,ncolor);
							//ColorCounter++;
							
							//if(ColorCounter==nlines) lockcolor=true;
							if(ColorCounter==ncall) lockcolor=true;
							
							Painter.start();
						}
					}
				}
				is_clear=false;
				is_clear=lockcolor && lockdraw && !painterActive;
				for(int i=0;i<nth;i++){
					is_clear=is_clear && !drawerActive[i];
				}
				if(is_clear){
					active=false;//Automatic stop at end				
					ParentM.Update();
					goon=false;
				}
			}
			else{
				boolean temp=!painterActive;
				for(int i=0;i<nth;i++){
					temp=temp && !drawerActive[i];
				}
				is_clear=temp;
				if(!stopsignal){
					stopsignal=true;
					System.out.println("Draw Stopped");
				}
				DrawCounter=ColorCounter=0;
				lockdraw=false;
				lockcolor=false;
			}
		}
		//if(ParentM.PrinterVersion)ParentM.mywm.ApplyWM();
		ParentM.Update();
		System.out.println("Closing Draw Manager thread");
	}
	
}
