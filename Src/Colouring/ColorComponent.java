import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.floor;
import static java.lang.Math.log;
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
/**
 *
 * @author pietro
 */
interface Colorfunction{
    int getColor(double val, double speed, double phase,int min,int max,double power);
    void reload(ColorComponent host);
}

class Sine implements Colorfunction {
    int ncomp;
    double[] coeff;
    double offset,norm;
        
    public Sine(ColorComponent host){
        ncomp=host.N;
        coeff=new double[10];
        for(int i=0;i<ncomp;i++) coeff[i]=host.params[i];
        normalize();
    }
    
    @Override
    public void reload(ColorComponent host){
        ncomp=host.N;
        for(int i=0;i<ncomp;i++) coeff[i]=host.params[i];
        normalize();
    }
    
    
    public void normalize(){
        double t,Min,Max,x;
        Max=Min=0;
        int i,j;
        for (i=0;i<10000;i++){
            x=PI*2*(double)i/1000.;
            t=sin (x);
            for(j=0;j<ncomp;j++)t+=sin(coeff[j]*x);
            if (i==0) Max=Min=t;
            if (t>Max) Max=t;
            if (t<Min) Min=t;
        }
        offset=Min;
        norm=1./(Max-Min);   
    }   
    
    @Override
    public int getColor(double val, double speed, double phase,int min, int max,double power){
        int res;
        double t;
        double x= Math.PI * 2 * speed * val / 256.+phase*Math.PI;
        t=sin (x);
        for(int i=0;i<ncomp;i++) t+=sin(coeff[i]*x);
        res=(int)(min+(max-min)*pow(norm*(t-offset),power));
        if(res<0)res=0;
        if(res>255) res=255;
        return res;
    }   
}

class Tri implements Colorfunction {
	int ncomp;
    double[] coeff;
    double offset,norm;
	
    public Tri(ColorComponent host){
        ncomp=host.N;
        coeff=new double[10];
        for(int i=0;i<ncomp;i++) coeff[i]=host.params[i];
        normalize();
    }       
    
    public double tri(double val){
			double valn=val/PI;
			while(valn<=-1)valn+=2;
			while(valn>1) valn-=2;
			return 1-2*abs(valn);
		}    
    
    @Override
    public void reload(ColorComponent host){
        ncomp=host.N;
        for(int i=0;i<ncomp;i++) coeff[i]=host.params[i];
        normalize();
    }
    
    public void normalize(){
        double t,Min,Max,x;
        Max=Min=0;
        int i,j;
        for (i=0;i<10000;i++){
            x=PI*2*(double)i/1000.;
            t=tri(x);
            for(j=0;j<ncomp;j++)t+=tri(coeff[j]*x);
            if (i==0) Max=Min=t;
            if (t>Max) Max=t;
            if (t<Min) Min=t;
        }
        offset=Min;
        norm=1./(Max-Min);   
    }   
        
    @Override
    public int getColor(double val, double speed, double phase,int min, int max,double power){
		int res;
        double t;
        double x= Math.PI * 2 * speed * val / 256.+phase*Math.PI;
		t=tri (x);
        for(int i=0;i<ncomp;i++) t+=tri(coeff[i]*x);
        res=(int)(min + (max-min) * pow(norm*(t-offset),power));
        return res;                
    }   
}

class Spline implements Colorfunction {
    int npoints;
    double delta;
    public double[] Y;
    double norm;
    double offset;
    
    int mymod(int a,int b){
        int res=a%b;
        if (res<0) res+=b;
        if(res==b) res=0;
        return res;
    }
    
    double mymod(double a,double b){
        double res=a%b;
        if (res<0) res+=b;
        if(res==b) res=0;
        return res;
    }
    
    public Spline(ColorComponent host){
        npoints=host.N;
        Y=new double[13];
        double[] y=new double[27];
        double temp;
        int i,j;
        for(i=0;i<27;i++) y[i]=host.params[mymod(i-8,npoints)];
        double[] h={-2,10,-39,146,-545,2307,-7603,28376,-7603,2307,-545,146,-39,10,-2};
        for(i=0;i<npoints+3;i++){
            temp=0;
            for(j=0;j<15;j++) temp+=h[j]*y[14-j+i];
            Y[i]=temp;
        }
        delta=1./(double)npoints;
        normalize();
        }
    
    @Override
    public void reload(ColorComponent host){
		npoints=host.N;
        if(npoints==0)return;
        //y=new double[13];
         double[] y=new double[27];
        double temp;
        int i,j;
        for(i=0;i<27;i++) y[i]=host.params[mymod(i-8,npoints)];
        //for(i=0;i<8;i++) y[i]=y[8+(i-8)%npoints];
        //for(i=8+npoints;i<27;i++) y[i]=y[8+(i-8)%npoints];
        double[] h={-2,10,-39,146,-545,2307,-7603,28376,-7603,2307,-545,146,-39,10,-2};
        for(i=0;i<npoints+3;i++){
            temp=0;
            for(j=0;j<15;j++) temp+=h[j]*y[14-j+i];
            Y[i]=temp;
        }
        delta=1./(double)npoints;
        normalize();
    }
    
    public void normalize(){
        int j;
        double x=0;
        double Max,Min;
        Max=Min=Y[2]/6.+2*Y[1]/3.+Y[0]/6.;
        for(j=0;j<1000;j++){
            x=0.001*j;
            int i0=(int)(x*npoints);
            double d=(x-i0*delta)/delta;
            i0++;
            double t=Y[i0+2]*pow(d,3.)/6.+Y[i0+1]*(-pow(d,3)/2.+pow(d,2)/2.+d/2.+1./6.)+Y[i0]*(pow(d,3)/2-pow(d,2)+2./3.)+Y[i0-1]*(pow(1-d,3)/6);
            if (t>Max) Max=t;
            if (t<Min) Min=t;
        }
        offset=Min;
        norm=1/(Max-Min);
    }
    
    
    @Override
    public int getColor(double val, double speed, double phase,int min, int max,double power){
        int res;
        double valr=mymod(speed*val/256.+phase/2.,1.);//(0,1(
        //if (valr<0) valr+=1;
        int i0=(int)(valr*npoints);
        double d=(valr-i0*delta)/delta; 
        i0++;
        double t=Y[i0+2]*pow(d,3.)/6.+Y[i0+1]*(-pow(d,3.)/2.+pow(d,2.)/2.+d/2.+1./6.)+Y[i0]*(pow(d,3.)/2.-pow(d,2.)+2./3.)+Y[i0-1]*(pow(1-d,3.)/6.);
        res=(int)(min+(max-min)*pow(norm*(t-offset),power));  
        if (res<0) res=0;
        if (res>255) res=255;
        return res;
    }   
}

class Poly implements Colorfunction {
    int npoints;
    double delta;
    public double[][] Y;
    double norm;
    double offset;
    
    int mymod(int a,int b){
        int res=a%b;
        if (res<0) res+=b;
        if(res==b) res=0;
        return res;
    }
    
    double mymod(double a,double b){
        double res=a%b;
        if (res<0) res+=b;
        if(res==b) res=0;
        return res;
    }
    
    public Poly(ColorComponent host){
        Y=new double[4][10];
        reload(host);
        }
    
    @Override
    public void reload(ColorComponent host){
		npoints=host.N;
        if(npoints==0)return;
        //y=new double[13];
        double[] y=new double[13];
        double temp;
        int i,j;
        double d1,d2,y1,y2;
        double a,b,c,d;
        for(i=1;i<=npoints;i++) y[i]=host.params[i-1];
        y[0]=y[npoints];
        y[npoints+1]=y[1];
        y[npoints+2]=y[2];
        for(i=0;i<npoints;i++){
            y1=y[i+1];
            y2=y[i+2];
            if (y1==y2) d1=d2=0;
            else if (y1>y2){
               d1=0;
               if(y[i]>y1) d1=(y2-y[i])/2.;
               d2=0;
               if(y[i+3]<y2) d2=(y[i+3]-y1)/2.;
            }
            else{
               d1=0;
               if(y[i]<y1) d1=(y2-y[i])/2.;
               d2=0;
               if(y[i+3]>y2) d2=(y[i+3]-y1)/2.;                
            }
            d=y1;
            c=d1;
            b=3*y2-3*y1-2*d1-d2;
            a=y2-y1-d1-b;
            Y[3][i]=a;
            Y[2][i]=b;
            Y[1][i]=c;
            Y[0][i]=d;
        }        
        delta=1./(double)npoints;
        normalize();
    }
    
    public void normalize(){
        int j;
        double x=0;
        double Max,Min;
        Max=Min=Y[0][0];
        for(j=0;j<1000;j++){
            x=0.001*j;
            int i0=(int)(x*npoints);
            double d=(x-i0*delta)/delta;
            double t=Y[3][i0]*pow(d,3)+Y[2][i0]*pow(d,2)+Y[1][i0]*d+Y[0][i0];
            if (t>Max) Max=t;
            if (t<Min) Min=t;
        }
        offset=Min;
        norm=1/(Max-Min);
    }    
    
    @Override
    public int getColor(double val, double speed, double phase,int min, int max,double power){
        int res;
        double valr=mymod(speed*val/256.+phase/2.,1.);//(0,1(
        //if (valr<0) valr+=1;
        int i0=(int)(valr*npoints);
        double d=(valr-i0*delta)/delta; 
        double t=Y[3][i0]*pow(d,3)+Y[2][i0]*pow(d,2)+Y[1][i0]*d+Y[0][i0];
        res=(int)(min+(max-min)*pow(norm*(t-offset),power));  
        if (res<0) res=0;
        if (res>255) res=255;
        return res;
    }   
}



public class ColorComponent extends JPanel implements ActionListener,ChangeListener,MouseWheelListener {
	static String []SpfString={"0.001","1","1000"};
	static String []ExtraStr={"# Comp.","# Comp.","# Points","# Points"};
	int bw,bh;
    int type;
    Colorfunction fsine;
    Colorfunction ftri;
    Colorfunction fspline; 
    Colorfunction fpoly;
    Colorfunction actual;
    int N;
    double[] params;
    int max,min;
    double speed,phase;
    double power;
    
    //graphic stuff
    int rgb;//0->Blue,1->Green,2->Red
    Layer parent;
    BufferedImage prev;
    ImageIcon ico;
    //double phaseprev;
    int[] boundaries;
    boolean updating;
    JLabel TypLab,MinLab,MaxLab,PowLab,SpdLab,SpfLab,SpfUD,PhaseLab;
    JSpinner MinUD,MaxUD,PowUD,SpdUD,PhaseUD;
    JComboBox TypUD;
    
    JLabel ExtraLab;
    JSpinner ExtraUD;
    JLabel []ExtrasLab;
    JSpinner []ExtrasUD;   
    int Extranum,ExtraL;
    
    
    int MinL,MinH;
    int MaxL,MaxH;
    double PowL,PowH;
    double SpdL,SpdH;
    int SpfL,SpfH,SpfVal;
    double PhaseL,PhaseH;
    int TypL,TypH;
    
    JLabel PicLab;
    
    
    public void PrevPeriod(){
        double s=speed;
        int i,j;
        int[] pixels;
        double x;
        int ymin,ymax;
        for(i=0;i<3*bw+1;i++) boundaries[i]=0;
        for(i=0;i<3*bw+1;i++){
            x=256.*(i/(3.*bw))/s;
            boundaries[i]=127-(int)(GetComponent(x)/2);
        }       
        pixels=new int[3*bw*128];
        for(i=0;i<3*bw;i++){
            for(j=0;j<128;j++){
                pixels[j*3*bw+i]=255<<24;
            }
        }
        for(i=0;i<3*bw;i++){
            ymin=ymax=boundaries[i];
            if (boundaries[i+1]<ymin) ymin=boundaries[i+1];
            if (boundaries[i+1]>ymax) ymax=boundaries[i+1];
            for(j=ymin;j<=ymax;j++) pixels[j*3*bw+i]|=255<<(8*rgb);
        }
        prev.setRGB(0,0, 3*bw, 128, pixels, 0 , 3*bw);
        ico.setImage(prev);
        PicLab.setIcon(ico);
        PicLab.updateUI();
    }
    
    public void setRGB(int rrggbb){
		rgb=rrggbb%3;
		PrevPeriod();
	}
	
    public ColorComponent(Layer p){
		parent=p;
		updating=false;
		bw=Layer.bw;bh=Layer.bh;
		boundaries=new int[3*bw+1];
		prev=new BufferedImage(3*bw,128,TYPE_INT_ARGB_PRE);
        ico=new ImageIcon();        
        type=0;
        N=1;
        params=new double[10];
        for(int i=0;i<10;i++) params[i]=0;
        power=1;
        max=255;
        min=0;
        speed=10;
        phase=0;
        fsine=new Sine(this);
        ftri=new Tri(this);
        fspline=new Spline(this);
        fpoly=new Poly(this);
        actual=fsine; 
        BuildGraphics();
    }
    
    public int GetComponent(double val){
        return actual.getColor(val, speed, phase, min, max,power);
    }
    
    public void setType(String temp){
        if (temp=="Sine"){
			ExtraL=0;
			ExtraUD.setModel(new SpinnerNumberModel(0, 0, 10, 1));
			ExtraUD.setValue(0);
			type=0;
            actual=fsine;
        }
        if (temp=="Triangular"){
			ExtraL=0;
			ExtraUD.setModel(new SpinnerNumberModel(0, 0, 10, 1));
			ExtraUD.setValue(0);
            type=1;
            actual=ftri;
        }
        if (temp=="Splines"){
			ExtraL=1;
			ExtraUD.setModel(new SpinnerNumberModel(1, 1, 10, 1));
			ExtraUD.setValue(1);
            type=2;
            actual=fspline;
        }
        if (temp=="Poly"){
			ExtraL=1;
			ExtraUD.setModel(new SpinnerNumberModel(1, 1, 10, 1));
			ExtraUD.setValue(1);
            type=3;
            actual=fpoly;
        }
    }
    
    public void Regenerate(){
        fsine.reload(this);
        ftri.reload(this);
        fspline.reload(this);
        fpoly.reload(this);
    }
    
    public void setType(int t){
        type=t;
        ExtraLab.setText(ExtraStr[t]);
        switch (t){
            case 0:
                actual=fsine;
                ExtraL=N=0;
				ExtraUD.setModel(new SpinnerNumberModel(0, 0, 10, 1));
				UpdateLock();
				Regenerate();
				//ExtraUD.setValue(0);
                break;
            case 1:
                actual=ftri;
                ExtraL=N=0;
				ExtraUD.setModel(new SpinnerNumberModel(0, 0, 10, 1));
				UpdateLock();
				Regenerate();
				//ExtraUD.setValue(0);
                break;
            case 2:
				ExtraL=N=1;
				ExtraUD.setModel(new SpinnerNumberModel(1, 1, 10, 1));
				UpdateLock();
				Regenerate();
				//ExtraUD.setValue(1);
                actual=fspline;
                break;
            case 3:
				ExtraL=N=1;
				ExtraUD.setModel(new SpinnerNumberModel(1, 1, 10, 1));
				UpdateLock();
				Regenerate();
			    actual=fpoly;
                break;
            default:
                actual=fsine;
                ExtraL=N=0;
				ExtraUD.setModel(new SpinnerNumberModel(0, 0, 10, 1));
				UpdateLock();
				Regenerate();
				break;
        }              
    }
    
    //Save/load function
    
	  
  
    
	public void PrintData (BufferedWriter o) throws IOException{
		String temp;
		temp=String.format("#RGB%d\n",rgb);
		o.write(temp);		
        temp=String.format("type %d\n",type);
        o.write(temp);
        temp=String.format("max %d\n",max);
        o.write(temp);
		temp=String.format("min %d\n",min);
        o.write(temp);
        temp=String.format("speed %.9f\n",speed);
        temp=temp.replace(',', '.');        
        o.write(temp);
		temp=String.format("phase %.9f\n",phase);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("power %.9f\n",power);
        temp=temp.replace(',', '.');
        o.write(temp);
		temp=String.format("Npar %d\n",N);
        o.write(temp);
        for(int i=0;i<10;i++){
			temp=String.format("param %d %.15f\n",i,params[i]);
			temp=temp.replace(',','.');
			o.write(temp);
		}	
		o.write("#END\n");
	}
	
	void PrintToFile(BufferedWriter o) throws IOException{
		PrintData(o);			
	}

	public void ReadData(BufferedReader i) throws IOException{
		StringTokenizer st;
		String ccomp=String.format("#RGB%d",rgb);
		while(true){
			String temp=i.readLine();
			if(temp==null) return;
			if(temp.equals("#END")) return;
			st=new StringTokenizer(temp);
			String vname=st.nextToken();
			if(vname.equals("type")){
				setType(Integer.parseInt(st.nextToken()));
			}
			else if(vname.equals("max")){
				max=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("min")){
				min=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("speed")){
				speed=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("power")){
				power=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("phase")){
				phase=Double.parseDouble(st.nextToken());
			}
			else if(vname.equals("Npar")){
				N=Integer.parseInt(st.nextToken());
			}
			else if(vname.equals("param")){
				int index=Integer.parseInt(st.nextToken());
				if(index>=10) continue;
				params[index]=Double.parseDouble(st.nextToken());			
			}
		else{	
			System.out.printf("Reading %s, found unknown field %s\n",ccomp,vname);
		}		
	}
}
 
public void ReadFromFile(File f){	
		int res;
		String ccomp=String.format("#RGB%d",rgb);
		System.out.printf("Searching %s\n",ccomp);
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			while(true){
				String temp=in.readLine();
				if(temp==null){
					in.close();
					break;
				}
				else if(temp.equals(ccomp)){
					System.out.printf("Found %s\n",ccomp);
					ReadData(in);
					in.close();	
					break;
				}
			}			
		} catch (IOException ex) {
		}		
		Regenerate();
        UpdateData();
		return;
	}
	
    
    
    
    
    
  
 
    
    public void UpdateData(){
        updating=true;
        //Speed
        double t=log(speed)/log(1000);
        String temp;
        double newdata=pow(1000,floor(t));
		SpfVal=(int)floor(t);
		SpfUD.setText(SpfString[SpfVal+1]);
		SpdUD.setValue(speed/newdata);
		PowUD.setValue(power);
        //MAX & MIN
        MinUD.setValue(min);
        MaxUD.setValue(max);
        //Type
        TypUD.setSelectedIndex(type);
        //Phase
        PhaseUD.setValue(phase);
        //Parameters
        ExtraUD.setValue(N);
        for(int i=0;i<10;i++)ExtrasUD[i].setValue(params[i]);
        UpdateLock();
        PrevPeriod();
        updating=false;
    }
        
    public void DrawOnOscillo(int[] pix,int w,int h,int rgb){
        int i,j;
        int[] boundaries=new int[w+1];
        double bdouble;
        double x;
        int Min,Max;
        for(i=0;i<w+1;i++) boundaries[i]=0;
        for(i=0;i<w+1;i++){
            x=257.*((double)i/(double)w);
            bdouble=((double)h-2.)-((double)h-4.)*GetComponent(x)/256.;
            boundaries[i]=(int)bdouble;
         //   /*if(boundaries[i]<=0)*/ System.out.printf("boundary[%d]=%d\n",i,boundaries[i]);
        }       
        for(i=0;i<w;i++){
            Min=Max=boundaries[i];
            if (boundaries[i+1]<Min) Min=boundaries[i+1];
            if (boundaries[i+1]>Max) Max=boundaries[i+1];
            for(j=Min;j<=Max;j++) pix[j*w+i]|=255<<(8*rgb);
        }
    }
    
    public void ApplySize(JComponent target,int dimx,int dimy){
		Dimension d=new Dimension(dimx,dimy);
		target.setMinimumSize(d);
		target.setMaximumSize(d);
		target.setPreferredSize(d);
		target.setSize(d);
		target.setFocusable(false);
	}
		
		
		
    public void BuildGraphics(){
		setBorder(new LineBorder(Color.BLACK));
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridheight=1;
		c.gridwidth=1;
		c.weighty=0.02;
		c.gridy=-1;
		
		//new line
		c.gridy++;
		TypLab=new JLabel("Type");
		ApplySize(TypLab,bw,bh);
		c.gridx=0;
		add(TypLab,c);
		TypUD=new JComboBox();
		ApplySize(TypUD,2*bw,bh);
		TypUD.setName("TypUD");
		TypL=0;
		TypH=3;
		TypUD.setModel(new DefaultComboBoxModel<>(new String[] { "Sine", "Triangular", "Splines", "Poly" }));
        TypUD.addActionListener(this);
        TypUD.addMouseWheelListener(this);
        c.gridx=1;
        add(TypUD,c);
                		
		//new line
		c.gridy++;
		MinLab=new JLabel("Min");
		ApplySize(MinLab,bw,bh);
		c.gridx=0;
		add(MinLab,c);
		MinUD=new JSpinner();
		ApplySize(MinUD,2*bw,bh);
		MinUD.setName("MinUD");
		MinL=0;
		MinH=255;
		MinUD.setModel(new SpinnerNumberModel(0, 0, 255, 1));
        MinUD.addChangeListener(this);
        MinUD.addMouseWheelListener(this);
        c.gridx=1;
        add(MinUD,c);
        
        //new line
        c.gridy++;
		MaxLab=new JLabel("Max");
		ApplySize(MaxLab,bw,bh);
		c.gridx=0;
		add(MaxLab,c);
		MaxUD=new JSpinner();
		ApplySize(MaxUD,2*bw,bh);
		MaxUD.setName("MaxUD");
		MaxL=0;
		MaxH=255;
		MaxUD.setModel(new SpinnerNumberModel(255, 0, 255, 1));
        MaxUD.addChangeListener(this);
        MaxUD.addMouseWheelListener(this);
        c.gridx=1;
        add(MaxUD,c);
        
        //new Line
        c.gridy++;
		PowLab=new JLabel("Power");
		ApplySize(PowLab,bw,bh);
		c.gridx=0;
		add(PowLab,c);
		PowUD=new JSpinner();
		ApplySize(PowUD,2*bw,bh);
		PowUD.setName("PowUD");
		PowL=0;
		PowH=100;
		PowUD.setModel(new SpinnerNumberModel(1.0d, 0.0d, 100.0d, 0.01d));
        PowUD.setEditor(new JSpinner.NumberEditor(PowUD, "##0.00"));
		PowUD.addChangeListener(this);
        PowUD.addMouseWheelListener(this);
        c.gridx=1;
        add(PowUD,c);
        
        //new Line
        c.gridy++;
		SpdLab=new JLabel("Speed");
		ApplySize(SpdLab,bw,bh);
		c.gridx=0;
		add(SpdLab,c);
		SpdUD=new JSpinner();
		ApplySize(SpdUD,2*bw,bh);
		SpdUD.setName("SpdUD");
		SpdL=0;
		SpdH=999.999;
		SpdUD.setModel(new SpinnerNumberModel(10.0d, 0.0d, 999.999d, 0.001d));
        SpdUD.setEditor(new JSpinner.NumberEditor(SpdUD, "##0.000"));
		SpdUD.addChangeListener(this);
        SpdUD.addMouseWheelListener(this);
        c.gridx=1;
        add(SpdUD,c);
        
        //new Line
        c.gridy++;
		SpfLab=new JLabel("Sp. Mult.");
		ApplySize(SpfLab,bw,bh);
		c.gridx=0;
		add(SpfLab,c);
		SpfUD=new JLabel("1");
		SpfUD.setBorder(new LineBorder(Color.BLACK));
		SpfUD.setHorizontalAlignment(SwingConstants.CENTER);
		ApplySize(SpfUD,2*bw,bh);
		SpfUD.setName("SpfUD");
		SpfL=-1;
		SpfH=1;
		SpfVal=0;
		SpfUD.addMouseWheelListener(this);
        c.gridx=1;
        add(SpfUD,c);
        
        //new Line
        c.gridy++;
		PhaseLab=new JLabel("Phase/PI");
		ApplySize(PhaseLab,bw,bh);
		c.gridx=0;
		add(PhaseLab,c);
		PhaseUD=new JSpinner();
		ApplySize(PhaseUD,2*bw,bh);
		PhaseUD.setName("PhaseUD");
		PhaseL=0;
		PhaseH=2;
		PhaseUD.setModel(new SpinnerNumberModel(0.0d, 0.0d, 2.0d, 0.001d));
        PhaseUD.setEditor(new JSpinner.NumberEditor(PhaseUD, "0.000"));
		PhaseUD.addChangeListener(this);
        PhaseUD.addMouseWheelListener(this);
		c.gridx=1;
        add(PhaseUD,c);
        
        //new line
        c.gridy++;
		ExtraLab=new JLabel("# Comp.");
		ApplySize(ExtraLab,bw,bh);
		c.gridx=0;
		add(ExtraLab,c);
		ExtraUD=new JSpinner();
		ApplySize(ExtraUD,2*bw,bh);
		ExtraUD.setName("ExtraUD");
		ExtraL=0;
		Extranum=10;
		ExtraUD.setModel(new SpinnerNumberModel(1, 0, 10, 1));
        ExtraUD.addChangeListener(this);
        ExtraUD.addMouseWheelListener(this);
		c.gridx=1;
        add(ExtraUD,c);
        
        ExtrasLab=new JLabel[Extranum];
        ExtrasUD=new JSpinner[Extranum];
        for(int i=0;i<Extranum;i++){
			c.gridy++;
			ExtrasLab[i]=new JLabel(String.format("Val %d",i));
			ApplySize(ExtrasLab[i],bw,bh);
			c.gridx=0;
			add(ExtrasLab[i],c);
			ExtrasUD[i]=new JSpinner();
			ApplySize(ExtrasUD[i],2*bw,bh);
			ExtrasUD[i].setName(String.format("ExtrasUD%d",i));
			ExtrasUD[i].setModel(new SpinnerNumberModel(0.0d,null,null,0.1d));
			ExtrasUD[i].addChangeListener(this);
			ExtrasUD[i].addMouseWheelListener(this);
			c.gridx=1;
			add(ExtrasUD[i],c);
		}
		
		//new line
        c.gridy++;
		PicLab=new JLabel("");
		ApplySize(PicLab,3*bw,128);
		c.gridx=0;
		c.gridwidth=2;
		add(PicLab,c);
		c.gridwidth=1;
        
        
        
        ApplySize(this,3*bw+10,18*bh+150);
        UpdateLock();
        PrevPeriod();
	}
		
	private void UpdateSpeed(){
        /*String temp;
        double f=Double.parseDouble(jTextField1.getText());
        double sp=f*(double)jSpinner15.getValue();
        switch ((int)(1000*f)){
            case 1000:
                temp=String.format("%.3f",sp);
                temp=temp.replace(',','.');
                break;
            case 1000000:
                temp=String.format("%d",(int)sp);
                temp=temp.replace(',','.');    
                break;
            case 1:
                temp=String.format("%.6f",sp);
                temp=temp.replace(',','.');
                break;
            default:
                temp=String.format("%.3f",sp);
                temp=temp.replace(',','.');
                break;
        }*/
        //jTextField2.setText(temp);
        if (!updating){
            speed=(double)SpdUD.getValue()*pow(10,3*SpfVal);
            parent.DoPreview();
            PrevPeriod();
        }
    }
    
    public void UpdateLock(){
		for(int i=0;i<Extranum;i++){
			if(i<N) ExtrasUD[i].setEnabled(true);
			else ExtrasUD[i].setEnabled(false);
		}
	}
		
	//listener functions
	//MouseWheelListener
	public void mouseWheelMoved(MouseWheelEvent e){
		String cmd=e.getComponent().getName();
		System.out.println("In mouseWheelMoved() : "+cmd);
		if(cmd.equals("MinUD")){
			int t=(int) MinUD.getValue();
			t-=e.getWheelRotation();
			if(t>MinH) t=MinH;
			if(t<MinL) t=MinL;
			MinUD.setValue(t);				
		}
		else if(cmd.equals("MaxUD")){
			int t=(int) MaxUD.getValue();
			t-=e.getWheelRotation();
			if(t>MaxH) t=MaxH;
			if(t<MaxL) t=MaxL;
			MaxUD.setValue(t);				
		}	
		else if(cmd.equals("PowUD")){
			double t=(double) PowUD.getValue();
			t-=e.getPreciseWheelRotation();
			if(t>PowH) t=PowH;
			if(t<PowL) t=PowL;
			PowUD.setValue(t);				
		}
		else if(cmd.equals("SpdUD")){
			double t=(double) SpdUD.getValue();
			t-=e.getPreciseWheelRotation();
			if(t>SpdH) t=SpdH;
			if(t<SpdL) t=SpdL;
			SpdUD.setValue(t);				
		}
		else if(cmd.equals("SpfUD")){
			int t=SpfVal;
			t-=e.getPreciseWheelRotation();
			if(t>SpfH) t=SpfH;
			if(t<SpfL) t=SpfL;
			SpfVal=t;
			SpfUD.setText(SpfString[SpfVal+1]);
			UpdateSpeed();
		}
		else if(cmd.equals("PhaseUD")){
			double t=(double) PhaseUD.getValue();
			t-=0.25*(double)e.getPreciseWheelRotation();
			if(t>PhaseH) t-=2;
			if(t<PhaseL) t+=2;
			PhaseUD.setValue(t);		
		}
		else if(cmd.equals("TypUD")){
			int t=(int) TypUD.getSelectedIndex();
			t+=e.getWheelRotation();
			if(t>TypH) t=TypH;
			if(t<TypL) t=TypL;
			TypUD.setSelectedIndex(t);		
		}
		else if(cmd.equals("ExtraUD")){
			int t=(int) ExtraUD.getValue();
			t-=e.getWheelRotation();
			if(t>Extranum) t=Extranum;
			if(t<ExtraL) t=ExtraL;
			ExtraUD.setValue(t);		
		}
		else if(cmd.length()>8 && cmd.substring(0,8).equals("ExtrasUD")){
			int sub=Integer.parseInt(cmd.substring(8));
			if(ExtrasUD[sub].isEnabled()){
				double t=(double)ExtrasUD[sub].getValue();
				t-=e.getWheelRotation();
				ExtrasUD[sub].setValue(t);			
			}
		}
		
	}
		
	//changeListener
	public void stateChanged(ChangeEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In stateChanged() : "+cmd);
		if(cmd.equals("MinUD")){
			if(!updating){
				min=(int)MinUD.getValue();
				PrevPeriod();
				parent.DoPreview();
			}
            int cmax=(int)MaxUD.getValue();
            int cmin=(int)MinUD.getValue();
            MaxL=cmin;
            MaxUD.setModel(new SpinnerNumberModel(cmax,cmin,255,1)); 
		}
		else if(cmd.equals("MaxUD")){
			if(!updating){
				max=(int)MaxUD.getValue();
				PrevPeriod();
				parent.DoPreview();
			}
            int cmax=(int)MaxUD.getValue();
            int cmin=(int)MinUD.getValue();
            MinH=cmax;
            MinUD.setModel(new SpinnerNumberModel(cmin,0,cmax,1)); 
		}
		else if(cmd.equals("PowUD")){
			if(!updating){
				power=(double)PowUD.getValue();
				parent.DoPreview();
				PrevPeriod();           
			}
		}
		else if(cmd.equals("SpdUD")){
			if(!updating){
				UpdateSpeed();				
			}
		}
		else if(cmd.equals("PhaseUD")){
			if(!updating){
				phase=(double)PhaseUD.getValue();
				parent.DoPreview();
				PrevPeriod();           
			}
		}
		else if(cmd.equals("ExtraUD")){
			if(!updating){
				N=(int)ExtraUD.getValue();
				UpdateLock();
				Regenerate();
				parent.DoPreview();
				PrevPeriod();           
			}
		}
		else if(cmd.length()>8 && cmd.substring(0,8).equals("ExtrasUD")){
			int sub=Integer.parseInt(cmd.substring(8));
			if(!updating){
				params[sub]=(double)ExtrasUD[sub].getValue();
				Regenerate();
				parent.DoPreview();
				PrevPeriod();
			}
		}
	}
	
	//actionListener
	public void actionPerformed(ActionEvent e){
		String cmd=((Component)e.getSource()).getName();
		System.out.println("In actionPerformed() : "+cmd);
		if(cmd.equals("TypUD")){
			if(!updating){
				setType((int)TypUD.getSelectedIndex());
				PrevPeriod();
				parent.DoPreview();
			}
			//UpdateLock();
		}
	}
	
	
	/********OLD METHODS********/
	/*
	  public void Print(BufferedWriter o) throws IOException{
        String temp;
        temp=String.format("%d\n",type);
        o.write(temp);
        temp=String.format("%d\n",max);
        o.write(temp);
        temp=String.format("%d\n",min);
        o.write(temp);
        temp=String.format("%.9f\n",speed);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%.9f\n",phase);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%.9f\n",power);
        temp=temp.replace(',', '.');
        o.write(temp);
        temp=String.format("%d\n",N);
        o.write(temp);
        for(int i=0;i<10;i++){
            temp=String.format("%.9f\n", params[i]);
            temp=temp.replace(',', '.');
            o.write(temp);
        }
    }
    
       
    public int Read(BufferedReader i) throws IOException{
        String t;
        int error=0;
        double[]vals=new double[7];
        int j;
        for(j=0;j<7;j++) vals[j]=0;
        for(j=0;((j<7)&&(error==0));j++){
            t=i.readLine();
            if(t==null){
                error++;
                continue;
            }
            vals[j]=Double.parseDouble(t);               
        }
        setType((int)vals[0]);
        max=(int)vals[1];
        min=(int)vals[2];
        speed=vals[3];
        phase=vals[4];
        power=vals[5];
        N=(int)vals[6];
        for(j=0;j<10;j++){
            t=i.readLine();
            params[j]=Double.parseDouble(t);
        }   
        Regenerate();
        UpdateData();
        return error;
    }
    */
	
	
	
}


