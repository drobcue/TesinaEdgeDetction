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


public class Sobel {
	private BufferedImage image = null;
	private int nrows=0, ncols=0;
	private double elapseTime = 0;
	public Sobel(String imgPath){
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
	
	public double getElapsedTime(){
		return this.elapseTime;
	}
	
	public BufferedImage getSobel(String option) throws Exception{
		
		long startTime, stopTime;
		
		
		if(option.toUpperCase()!="FULL" && option.toUpperCase()!="X" && option.toUpperCase()!="Y")
			throw new Exception();
		int[] imgArray1D = new int[ncols * nrows];
		int[][] Gx = new int[nrows][ncols],
				Gy = new int[nrows][ncols],
			    G = new int[nrows][ncols],
			    imgArray2D = new int[nrows][ncols];
		
		
		image.getData().getPixels(0, 0, ncols, nrows, imgArray1D);
		
	    
		int i = 0;
		for(int row = 0; row < imgArray2D.length; row++){
			for(int column = 0; column < imgArray2D[row].length; column++){
				
				//System.out.println(imgArray1D[i]);
				imgArray2D[row][column] = imgArray1D[i++];
			}
		}
		
		
		
		
		startTime=System.currentTimeMillis();
		for(i=0; i<nrows; i++){
			for(int j=0; j<ncols; j++){
				if(i==0 || i==nrows-1 || j==0 || j==ncols-1){
					Gx[i][j] = Gy[i][j] = G[i][j] = 0; //Image boundary cleared!
				}
				else{
					Gx[i][j] = imgArray2D[i+1][j-1] + 2*imgArray2D[i+1][j] + imgArray2D[i+1][j+1] -
							imgArray2D[i-1][j-1] - 2*imgArray2D[i-1][j] - imgArray2D[i-1][j+1];
		            Gy[i][j] = imgArray2D[i-1][j+1] + 2*imgArray2D[i][j+1] + imgArray2D[i+1][j+1] -
		            		imgArray2D[i-1][j-1] - 2*imgArray2D[i][j-1] - imgArray2D[i+1][j-1];
		            G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
		            if(G[i][j]>255 || G[i][j] < 0) System.out.println(G[i][j]);	//BORRAR
				}
			}
		}
		stopTime=System.currentTimeMillis();
		
		//System.out.println("Sobel execution time = " + (stopTime - startTime)/1000 + "." + (stopTime - startTime)%1000 +" seconds");
		//System.out.println("Sobel execution time = " + (stopTime - startTime) + " miliseconds");
	
		int[][] gSelected = null;
		if(option.toUpperCase()=="FULL"){
			gSelected = G;
		}
		else if(option.toUpperCase()=="X"){
			gSelected = Gx;
		}
		else if(option.toUpperCase()=="Y"){
			gSelected = Gy;
		}
		
		i = 0;
		for(int row = 0; row < imgArray2D.length; row++){
			for(int column = 0; column < imgArray2D[row].length; column++){
				//System.out.println(G[row][column]);
				imgArray1D[i++] = gSelected[row][column];
			}
		}
		
		BufferedImage tmpImage = new BufferedImage(ncols, nrows, BufferedImage.TYPE_BYTE_GRAY);
		
		//TEST
		
        WritableRaster raster = (WritableRaster) tmpImage.getData();
        raster.setPixels(0,0,ncols,nrows,imgArray1D);
        tmpImage.setData(raster);
		return tmpImage;		
	}
	
	public BufferedImage getSobel2(String option) throws Exception{
		
		double startTime, stopTime;
		
		
		if(option.toUpperCase()!="FULL" && option.toUpperCase()!="X" && option.toUpperCase()!="Y")
			throw new Exception();
		int[] imgArray1D = new int[ncols * nrows];
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
				//System.out.println(imgArray1D[i]);
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
					Gx[i][j] = imgArray2D[i+1][j-1] + 2*imgArray2D[i+1][j] + imgArray2D[i+1][j+1] -
							imgArray2D[i-1][j-1] - 2*imgArray2D[i-1][j] - imgArray2D[i-1][j+1];
		            Gy[i][j] = imgArray2D[i-1][j+1] + 2*imgArray2D[i][j+1] + imgArray2D[i+1][j+1] -
		            		imgArray2D[i-1][j-1] - 2*imgArray2D[i][j-1] - imgArray2D[i+1][j-1];
		            G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
		            if(G[i][j]>255){
		            	G[i][j] = 255;}
		            else if(G[i][j]<0){
		            	G[i][j] = 0;
		            }
		            	
		            	//System.out.println(G[i][j]);
		            tmpImage.setRGB(j, i, new Color(G[i][j],G[i][j],G[i][j]).getRGB());
				}
			}
		}
		stopTime=System.currentTimeMillis();
		this.elapseTime = ((double)stopTime - startTime)/1000;
		
		//System.out.println("Sobel execution time = " + (stopTime - startTime)/1000 + "." + (stopTime - startTime)%1000 +" seconds");
		System.out.println("Sobel execution time = " + ((double)stopTime - startTime)/1000 + " miliseconds");
	
//		int[][] gSelected = null;
//		if(option.toUpperCase()=="FULL"){
//			gSelected = G;
//		}
//		else if(option.toUpperCase()=="X"){
//			gSelected = Gx;
//		}
//		else if(option.toUpperCase()=="Y"){
//			gSelected = Gy;
//		}
//		
//		i = 0;
//		for(int row = 0; row < imgArray2D.length; row++){
//			for(int column = 0; column < imgArray2D[row].length; column++){
//				//System.out.println(G[row][column]);
//				imgArray1D[i++] = gSelected[row][column];
//			}
//		}
		
		
		
		//TEST
		
//        WritableRaster raster = (WritableRaster) tmpImage.getData();
//        raster.setPixels(0,0,ncols,nrows,imgArray1D);
//        tmpImage.setData(raster);
		return tmpImage;		
	}
	
	public BufferedImage getSobel(int width, int height, String option){		
		try {
			return getScaled(new Dimension(width, height), getSobel2(option));//CHANGE
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	public void saveSobel(String savePath){
		File outFile = new File(savePath);
		try {
			ImageIO.write(getSobel("FULL"), "png", outFile);//CHANGE
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
