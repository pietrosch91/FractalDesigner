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
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;
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
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;




public class SignatureDrawer{
	static final int extrapix=40;
	Color BaseColor;
	BufferedImage FinalImage;
	BufferedImage BackupCopy;
	BufferedImage TempImage;
	MandelFrac Parent;
	WaterMarkSettings pwm;
	Rectangle2D rect;
	
	int FinalFontSize;
	int BLOffset;
	int StartOffset;
	int fW,fH;
	
	int RightMost,LeftMost;
	int Lower,Upper;
	int ActualH,ActualW;
	
	public String AdaptString(String input,String FontID){
		String res=input;
		if(FontID.equals("Level")){
			res=res.replace(' ','_');			
		}
		else if(FontID.equals("Diverplate")){
			res="<"+res.replace(' ','-')+">";
		}
		return res;
	}
	
	public SignatureDrawer(MandelFrac p,WaterMarkSettings pp){	
		Parent=p;
		pwm=pp;
	}
	
	public boolean checkLine(int iline){
		for(int i=0;i<TempImage.getWidth();i++) if(TempImage.getRGB(i,iline)!=0) return true;
		return false;
	}
	
	public boolean checkCol(int icol){
		for(int i=0;i<TempImage.getHeight();i++) if(TempImage.getRGB(icol,i)!=0) return true;
		return false;
	}
	
	public void FindLimits(){
		BLOffset=0;
		RightMost=0;
		LeftMost=0;
		Lower=0;
		Upper=0;
		for(Upper=0;Upper<TempImage.getHeight();Upper++) if(checkLine(Upper)) break;
		for(Lower=TempImage.getHeight()-1;Lower>=0;Lower--) if(checkLine(Lower)) break;
		for(LeftMost=0;LeftMost<TempImage.getWidth();LeftMost++) if(checkCol(LeftMost)) break;
		for(RightMost=TempImage.getWidth()-1;RightMost>=0;RightMost--) if(checkCol(RightMost)) break;
		BLOffset=TempImage.getHeight()/2-Upper;
		StartOffset=20-LeftMost;
		ActualH=Lower-Upper+1;
		ActualW=RightMost-LeftMost+1;		
	}
	
	public int GetPixel(int x,int y){
		if(x<0) return 0;
		if(x>=FinalImage.getWidth()) return 0;
		if(y<0) return 0;
		if(y>=FinalImage.getHeight()) return 0;
		return FinalImage.getRGB(x,y);	
	}
	
	public int GetBackupPixel(int x,int y){
		if(x<0) return 0;
		if(x>=BackupCopy.getWidth()) return 0;
		if(y<0) return 0;
		if(y>=BackupCopy.getHeight()) return 0;
		return BackupCopy.getRGB(x,y);	
	}
	
	public void SetBackupPixel(int x,int y,int pix){
		if(x<0) return;
		if(x>=BackupCopy.getWidth()) return;
		if(y<0) return;
		if(y>=BackupCopy.getHeight()) return;
		BackupCopy.setRGB(x,y,pix);	
	}
	
	public void InitBackup(){
		BackupCopy=new BufferedImage(FinalImage.getWidth(),FinalImage.getHeight(),TYPE_INT_ARGB_PRE);
	}
	
	
	public int GetCount(int i,int j,int radius){
		int result=0;
		if(FinalImage.getRGB(i,j)==0) return 0;
		//System.out.printf("Found non zero pixel\n");
		int x,y,X,Y;
		for(x=-radius;x<=radius;x++){
			X=i+x;
			if(X<0) continue;
			if(X>=FinalImage.getWidth())continue;
			for(y=-radius+abs(x);y<=radius-abs(x);y++){
				Y=j+y;
				if(Y<0) continue;
				if(Y>=FinalImage.getHeight())continue;
				
				if(FinalImage.getRGB(X,Y)==0) return 1;
			}
		}
		return 0;
	}
	
	

	
	public void Bordify(int rad){
		BufferedImage newimg=new BufferedImage(FinalImage.getWidth(),FinalImage.getHeight(),TYPE_INT_ARGB_PRE);
		for(int i=0;i<newimg.getWidth();i++){
			for(int j=0;j<newimg.getHeight();j++){
				newimg.setRGB(i,j,FinalImage.getRGB(i,j)*GetCount(i,j,rad));
			}
		}
		FinalImage=newimg;
	}
	
	public int GetMin(int i,int j,int radius){
		if(radius>0) if(FinalImage.getRGB(i,j)!=0) return 255;
		if(radius<0) if(FinalImage.getRGB(i,j)==0) return 0;
		int x,y,X,Y;
		double minDist=2*radius*radius;
		for(x=-abs(radius);x<=abs(radius);x++){
			X=i+x;
			if(X<0) continue;
			if(X>=FinalImage.getWidth())continue;
			for(y=-abs(radius);y<=abs(radius);y++){
				Y=j+y;
				if(Y<0) continue;				
				if(Y>=FinalImage.getHeight())continue;
				if(radius>0){
					if((FinalImage.getRGB(X,Y) ) !=0){
						if(x*x+y*y<minDist) minDist=x*x+y*y;
					}				
				}
				else{
					if((FinalImage.getRGB(X,Y) ) ==0){
						if(x*x+y*y<minDist) minDist=x*x+y*y;
					}				
				}
			}			
		}
		double ratio=sqrt(minDist)/(double)abs(radius);
		
		if(radius>0){
			if(minDist>radius*radius) return 0;
			return (int)round(255*(1+cos(ratio*PI))/2.);
		}
		else{
			if(minDist>radius*radius) return 255;
			return (int)round(255*(1+cos((1-ratio)*PI))/2.);
		}
	}	
	
	public void Blurrify(int rad){
		if(rad==0)return;
		int baseRGB=BaseColor.getRGB() & 0xFFFFFF;
		BufferedImage newimg=new BufferedImage(FinalImage.getWidth(),FinalImage.getHeight(),TYPE_INT_ARGB_PRE);
		for(int i=0;i<newimg.getWidth();i++){
			for(int j=0;j<newimg.getHeight();j++){
				newimg.setRGB(i,j,GetMin(i,j,rad)<<24 | baseRGB);
				if(pwm.stop) return;
			}
			double comp=100*(double)(i+1)/(double)newimg.getWidth();
			pwm.UpdateRepBar(String.format("Blurrifying (%.02f%%",comp),(int)round(10*comp));
		}
		FinalImage=newimg;
	}
	
	
	public void pic3dify(int deltaX,int deltaY){
		if(deltaX==0 && deltaY==0) return;
		int baseRGB=BaseColor.getRGB() & 0xFFFFFF;
		BufferedImage newimg=new BufferedImage(FinalImage.getWidth(),FinalImage.getHeight(),TYPE_INT_ARGB_PRE);
		for(int i=0;i<newimg.getWidth();i++){
			for(int j=0;j<newimg.getHeight();j++){
				int newalpha=((GetPixel(i,j)>>24) &0xFF)-((GetPixel(i+deltaX,j+deltaY)>>24) &0xFF);
				if(newalpha!=0) newalpha=(255+newalpha)>>1;
				newimg.setRGB(i,j,newalpha<<24 | baseRGB);
				if(pwm.stop) return;
			}
			double comp=100*(double)(i+1)/(double)newimg.getWidth();
			pwm.UpdateRepBar(String.format("3difying (%.02f%%",comp),(int)round(10*comp));
		}
		FinalImage=newimg;
	}
	
	
	public void DrawBorder(){
		int blk=(255<<24);
		for(int i=0;i<FinalImage.getWidth();i++){
			FinalImage.setRGB(i,0,blk);
			FinalImage.setRGB(i,FinalImage.getHeight()-1,blk);
		}
		for(int i=0;i<FinalImage.getHeight();i++){
			FinalImage.setRGB(0,i,blk);
			FinalImage.setRGB(FinalImage.getWidth()-1,i,blk);
		}
	}
	
	
	public void Generate(String tt,String FontName,int FontStyle,int FinalHpix,Color FC,int BlurRadius,int r3deltax,int r3deltay){
		String text=AdaptString(tt,FontName);
		BaseColor=FC;
		//Initialize template image
		pwm.UpdateRepBar("Checking Font Size 100",0);		
		FinalFontSize=100;
		FontMetrics fontMetrics;
		//First find exact Dimension with 100;
		Graphics2D w = (Graphics2D) Parent.pic.getGraphics();
		w.setFont(new Font(FontName,FontStyle, FinalFontSize));
		fontMetrics = w.getFontMetrics();
		rect = fontMetrics.getStringBounds(text, w);
		TempImage=new BufferedImage(3*(int)rect.getWidth()+10,2*(int)rect.getHeight()+10,TYPE_INT_ARGB_PRE);
		w=(Graphics2D) TempImage.getGraphics();
		w.setFont(new Font(FontName,FontStyle, FinalFontSize));
		AlphaComposite alphaChannel = AlphaComposite.Src;
		w.setComposite(alphaChannel);
		w.setColor(Color.BLACK);
		w.drawString(text, 5, (int)rect.getHeight());
		FindLimits();
		//Guess FOntSize
		FinalFontSize=(int)round((double)FinalHpix*100./(double)ActualH)-1;
		if(FinalFontSize<1) FinalFontSize=1;		
		System.out.printf("Guess for Font Size=%d\n",FinalFontSize);
		while(true){
			pwm.UpdateRepBar(String.format("Checking Font Size %d",FinalFontSize),0);
			//Find Horizontal occupancy suggested by graphics2d and build TempImage
			w = (Graphics2D) Parent.pic.getGraphics();
			w.setFont(new Font(FontName,FontStyle, FinalFontSize));
			fontMetrics = w.getFontMetrics();
			rect = fontMetrics.getStringBounds(text, w);
			TempImage=new BufferedImage(3*(int)rect.getWidth()+20,2*FinalHpix,TYPE_INT_ARGB_PRE);
			w=(Graphics2D) TempImage.getGraphics();
			w.setFont(new Font(FontName,FontStyle, FinalFontSize));
			alphaChannel = AlphaComposite.Src;
			w.setComposite(alphaChannel);
			w.setColor(Color.BLACK);
			w.drawString(text, 20, FinalHpix);
			FindLimits();
			if(ActualH>=FinalHpix){
				w.dispose();
				//TempImage.dispose();
				break;
			}
			FinalFontSize++;
		}
		pwm.UpdateRepBar(String.format("Found Font Size %d",FinalFontSize),0);
		System.out.printf("FinalFontSize=%d\n",FinalFontSize);
		//I got FinalFontSize as well as dimensions
		FinalImage=new BufferedImage(ActualW+2*extrapix,ActualH+2*extrapix,TYPE_INT_ARGB_PRE);
		//BackupCopy=new BufferedImage(ActualW+2*extrapix,ActualH+2*extrapix,TYPE_INT_ARGB_PRE);
		w = (Graphics2D) FinalImage.getGraphics();
		w.setFont(new Font(FontName,FontStyle, FinalFontSize));
		alphaChannel = AlphaComposite.Src;
		w.setComposite(alphaChannel);
		w.setColor(FC);
		w.drawString(text, StartOffset+extrapix, BLOffset+extrapix);
		w.dispose();
		//Bordify(1);
		Blurrify(BlurRadius);
		pic3dify(r3deltax,r3deltay);
		//DrawBorder();
	}	
	
	public void Redraw(String tt,String FontName,int FontStyle,Color FC,int BlurRadius,int r3deltax,int r3deltay){
		String text=AdaptString(tt,FontName);
		BaseColor=FC;
		pwm.UpdateRepBar(String.format("Found Font Size %d",FinalFontSize),0);
		FinalImage=new BufferedImage(ActualW+2*extrapix,ActualH+2*extrapix,TYPE_INT_ARGB_PRE);
		//FinalImage=new BufferedImage(ActualW+2*extrapix,ActualH+2*extrapix,TYPE_INT_ARGB_PRE);
		Graphics2D w = (Graphics2D) FinalImage.getGraphics();
		w.setFont(new Font(FontName,FontStyle, FinalFontSize));
		AlphaComposite alphaChannel = AlphaComposite.Src;
		w.setComposite(alphaChannel);
		w.setColor(FC);
		w.drawString(text, StartOffset+extrapix, BLOffset+extrapix);
		w.dispose();
		//Bordify(1);
		Blurrify(BlurRadius);
		pic3dify(r3deltax,r3deltay);
		//DrawBorder();
	}
}
