import java.nio.channels.Pipe;
import java.util.Random;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class BitMap3D extends BitMap {


    private double[] depthBuffer;
	private double xCam, yCam, zCam, rot, rSin, rCos, fov;
	
	public BitMap3D(int width, int height){
		super(width, height);
		depthBuffer = new double[width* height];
	}
    
	public void render(Game game) {
		fov = height;
		
//		xCam = game.time % 100.0 / 50;
//		yCam = game.time % 100.0 / 50;
//		zCam = Math.sin(game.time / 10.0) * 2;
		
		rot = Math.sin(game.time / 40.0) * 0.5;
		
		rSin = Math.sin(rot);
		rCos = Math.cos(rot);
		
		for(int y = 0; y < height; y++){
			
	       double yd = ((y + 0.5) - (height / 2)) / fov; 
	       double zd = (4 + zCam) / yd;
	       
	       if(yd < 0)
	    	   zd = (4 - zCam) / -yd;
	     
	       for(int x = 0; x < width; x++){
	    	 
	    	   double xd = (x - (width / 2)) / fov;
	    	   xd *= zd;
	    	   
	    	   double xx = xd * rCos - zd * rSin + (xCam + 0.5) * 8;
	    	   double yy = xd * rSin + zd * rCos + (yCam) * 8;
	    	   
	    	   int xPix = (int)xx * 2;
 	    	   int yPix = (int)yy * 2;
	    	   if(xx < 0){
	    		   xPix--;
	    	   }
	    	   if(yy < 0){
	    		   yPix--;
	    	   }
	    	   depthBuffer[x + y * width] = zd; 
         	   pixels[x + y * width] = Textures.floor.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floor.width];
	           }
		} 
		renderWall(0,2,1,2);
		
	}
	
	public void renderWall(double x0,double y0,double x1,double y1){
		
			double xo0 = x0 - 0.5 - xCam * 2;
			double u0  = -0.5 + zCam / 4; 
			double d0  = +0.5 + zCam / 4;
			double zo0 = y0 - yCam * 2;
			
			double xx0 = xo0 * rCos + zo0 * rSin;
			double zz0 = -xo0 * rSin + zo0 * rCos;
			
			double xo1 = x1 - 0.5 - xCam * 2;
			double u1  = -0.5 + zCam / 4; 
			double d1  = +0.5 + zCam / 4;
			double zo1 = y1 - yCam * 2;
			
			double xx1 = xo1 * rCos + zo1 * rSin;
			double zz1 = -xo1 * rSin + zo1 * rCos;
			
			double xPixel0 = xx0 / zz0 * fov + width / 2.0;
			double xPixel1 = xx1 / zz1 * fov + width / 2.0;
			
			if(xPixel0 > xPixel1)
			   return;
			int xp0 = (int)Math.round(xPixel0);
			int xp1 = (int)Math.round(xPixel1);
			    if(xp0 < 0)
			       xp0 = 0;		
			    if(xp1 > width)
			       xp1 = width;
			    
			double yPixel00 = (u0 / zz0 * fov + height / 2.0) + 0.5;
			double yPixel10 = (u1 / zz1 * fov + height / 2.0) + 0.5;
		    double yPixel01 = (d0 / zz0 * fov + height / 2.0) + 0.5;
			double yPixel11 = (d1 / zz1 * fov + height / 2.0) + 0.5;
			
			double iz0 = zz0; 
			double iz1 = zz1;
			
			for(int x = xp0; x < xp1; x++ ){
			  
			double p = (x - xPixel0) / (xPixel1 - xPixel0);
			
		    double yPixel0 = yPixel00 + (yPixel10 - yPixel00) * p;
		    double yPixel1 = yPixel01 + (yPixel11 - yPixel01) * p;
		    
			double iz = iz0 + (iz1 - iz0) * p;
		    System.out.println(iz);
			
		    if(yPixel0 > yPixel1)
				   return;
				int yp0 = (int)Math.floor(yPixel0);
				int yp1 = (int)Math.floor(yPixel1);
				    if(xp0 < 0)
				       xp0 = 0;				
				    if(xp1 > width)
				       xp1 = width;
				    
		    for(int y = yp0; y < yp1; y++){	    
		    	   depthBuffer[x + y * width] = iz * 10; 
	         	   pixels[x + y * width] = 0xff00ff;
		    }
	    }
	}
	
    public void drawFloor(double xx, double yy, double yd, int x, int y, int xPix, int yPix, int pX, int pY) {
		
   	  if(yd >= 0 && xx >= pX * 16 && xx < pX * 16 + 16 && yy >= pY * 16 && yy < pY * 16 + 16) {
   		   pixels[x + y * width] = Textures.floor.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floor.width];
   	     }
     }
    
    public void drawCelling(double xx, double yy, double yd, int x, int y, int xPix, int yPix, int pX, int pY){
    	
   	  if(yd <= 0 && xx >= pX * 16 && xx < pX * 16 + 16 && yy >= pY * 16 && yy < pY * 16 + 16){
    	   pixels[x + y * width] = Textures.floor.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floor.width]; 
 		}
    }
    
    public void renderFog() {

		for(int i = 0; i < depthBuffer.length; i++) {
			
			int color = pixels[i];
			int r = (color >> 16)& 0xff;
			int g = (color >> 8)& 0xff;
			int b = (color)& 0xff;
			
			
            double brightness = 255 -  (depthBuffer[i] * depthBuffer[i]) / 4.0;
            
            r = (int) (r / 255.0 * brightness);
            g = (int) (g / 255.0 * brightness);
            b = (int) (b / 255.0 * brightness);
        			
			pixels[i] = r << 16 | g << 8 | b ;
		}
	}	
	
    
}
