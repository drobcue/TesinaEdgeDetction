package filters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

public class Prewitt {
	private BufferedImage image = null;
	private int nrows=0, ncols=0; 
	private double elapseTime = 0;
	public Prewitt(String imgPath){
		try{
			//Read Image
			Image tmpImage = ImageIO.read(new File(imgPath));
			//Get dimensions
			this.ncols=tmpImage.getWidth(null);
			this.nrows=tmpImage.getHeight(null);
			//Create a Grayscale filter to apply to the image
			ImageFilter filter = new GrayFilter(true, 50);  
			ImageProducer producer = new FilteredImageSource(tmpImage.getSource(), filter);  
			tmpImage = Toolkit.getDefaultToolkit().createImage(producer);
			tmpImage.flush();
			this.image = new BufferedImage(ncols, nrows, BufferedImage.TYPE_BYTE_GRAY);	
		    Graphics2D graph = image.createGraphics();
		    graph.drawImage(tmpImage, 0, 0, null);
		    graph.dispose();
		} catch(IllegalArgumentException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public double getElapseTime(){
		return this.elapseTime;
	}
	
	/**
	 * 
	 * @param option This is the option to filter. Accepted Values:
	 * <br>FULL<br/>HORIZONTAL<br>VERTICAL
	 * @return A BufferedImage representation of the border.
	 * @throws Exception if the option value is not define.
	 */
	public BufferedImage getPrewitt(String option) throws Exception{		
		if(option.toUpperCase()!="FULL" && option.toUpperCase()!="X" && option.toUpperCase()!="Y")
			throw new Exception();
		long startTime, stopTime;
		int[][] Gx = new int[nrows][ncols],
				Gy = new int[nrows][ncols],
			    G = new int[nrows][ncols],
			    imgArray2D = new int[nrows][ncols];		
		BufferedImage tmpImage = new BufferedImage(ncols, nrows, BufferedImage.TYPE_INT_RGB);
	
		Color tmpColor;	    
		int i = 0;
		for(int row = 0; row < nrows; row++){
			for(int column = 0; column < ncols; column++){
				tmpColor = new Color(image.getRGB(column, row));
				imgArray2D[row][column] = tmpColor.getRed();
			}
		}		
		
		startTime=System.currentTimeMillis();
		for(i=0; i<nrows; i++){
			for(int j=0; j<ncols; j++){
				if(i==0 || i==nrows-1 || j==0 || j==ncols-1){
					Gx[i][j] = Gy[i][j] = G[i][j] = 0; //Image boundary cleared!
				}
				else{
					Gx[i][j] = imgArray2D[i+1][j-1] + imgArray2D[i+1][j] + imgArray2D[i+1][j+1] -
							imgArray2D[i-1][j-1] - imgArray2D[i-1][j] - imgArray2D[i-1][j+1];
					if(Gx[i][j]>255)
		            	Gx[i][j] = 255;
		            else if(G[i][j]<0)
		            	Gx[i][j] = 0;
					
		            Gy[i][j] = imgArray2D[i-1][j+1] + imgArray2D[i][j+1] + imgArray2D[i+1][j+1] -
		            		imgArray2D[i-1][j-1] - imgArray2D[i][j-1] - imgArray2D[i+1][j-1];
		            
		            if(Gy[i][j]>255)
		            	Gy[i][j] = 255;
		            else if(G[i][j]<0)
		            	Gy[i][j] = 0;
		            
		            G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
		            
		            if(G[i][j]>255)
		            	G[i][j] = 255;
		            else if(G[i][j]<0)
		            	G[i][j] = 0;
		            
		            tmpImage.setRGB(j, i, new Color(G[i][j],G[i][j],G[i][j]).getRGB());
				}
			}
		}
		stopTime=System.currentTimeMillis();
		this.elapseTime = stopTime - startTime;

		return tmpImage;		
	}
	
	/**
	 * 
	 * @param width The width value for the image to be return.
	 * @param height The height value for the image to be return.
	 * @param option This is the option to filter. Accepted Values:
	 * <br>FULL<br/>HORIZONTAL<br>VERTICAL
	 * @return A scaled BufferedImage representation of the border.
	 * @throws Exception Exception if the option value is not define.
	 */
	public BufferedImage getPrewitt(int width, int height, String option) throws Exception{		
		return getScaled(new Dimension(width, height), getPrewitt(option));		
	}
	
	private BufferedImage getScaled(Dimension d, BufferedImage img){
		Image tmpImg = img.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_BYTE_GRAY);
		
		Graphics2D graph = dimg.createGraphics();
		graph.drawImage(tmpImg, 0, 0, null);
		graph.dispose();
		
		return dimg;
	}
	
	public BufferedImage getGrayscale(){
		return this.image;
	}
	
	public BufferedImage getGrayscale(int width, int height){
		return getScaled(new Dimension(width, height), getGrayscale());
	}
	

}
