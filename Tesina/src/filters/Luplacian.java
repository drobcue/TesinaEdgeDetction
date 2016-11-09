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

public class Luplacian {
	private BufferedImage image = null;
	private int nrows=0, ncols=0;
	public Luplacian(String imgPath){
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
	
	private int[][] getKernel(int size, double theta){
		int[][] tmpKernel = new int[size][size];
		int idxDiff=size/2;  //This is desplazaminto of kernel index
		for(int y=0; y<size; y++){
			for(int x=0; x<size; x++){
				tmpKernel[y][x]=Math.round((float)(-(1/(2*Math.PI*Math.pow(theta, 4)))*(1-((Math.pow(x-idxDiff, 2)+Math.pow(y-idxDiff, 2))/(2*Math.pow(theta, 2))))*(
						Math.pow(Math.E, -((Math.pow(x-idxDiff, 2)+Math.pow(y-idxDiff, 2))/(2*Math.pow(theta, 2)))))*1000));
			}
		}
		
		return tmpKernel;
	}
	
	private int getConValue(int x, int y, int[][] kernel, int[][] imageArr){
		int idxDiff=kernel.length/2;  //This is desplazaminto of kernel index
		int sumValue=0;
		for(int row=-idxDiff; row<idxDiff + 1; row++){
			for(int col=-idxDiff; col<idxDiff + 1; col++){
				sumValue += kernel[row + idxDiff][col + idxDiff]*imageArr[y+row][x+col];
			}
		}
		if(sumValue > 255){
			return 255;
		}
		else if(sumValue < 0){
			return 0;
		}
		else{
			return sumValue;
		}
	}
	
	public BufferedImage getLuplacian(String option) throws Exception{
		long startTime, stopTime;
		
		
		if(option.toUpperCase()!="POSITIVE" && option.toUpperCase()!="NEGATIVE")
			throw new Exception();
		int[] imgArray1D = new int[ncols * nrows];
		int[][] Gpos = new int[nrows][ncols],
				Gneg = new int[nrows][ncols],
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
					Gpos[i][j] = Gneg[i][j] = 0; //Image boundary cleared!
				}
				else{
					Gpos[i][j] = -4*imgArray2D[i][j] + imgArray2D[i+1][j]
							     + imgArray2D[i-1][j] + imgArray2D[i][j+1]
							     + imgArray2D[i][j-1];
		            Gneg[i][j] = 4*imgArray2D[i][j] - imgArray2D[i+1][j]
						         - imgArray2D[i-1][j] - imgArray2D[i][j+1]
						         - imgArray2D[i][j-1];
		            
				}
			}
		}
		stopTime=System.currentTimeMillis();
		
		//System.out.println("Roberts execution time = " + (stopTime - startTime)/1000 + "." + (stopTime - startTime)%1000 +" seconds");
		//System.out.println("Roberts execution time = " + (stopTime - startTime) + " miliseconds");
	
		int[][] gSelected = null;
		if(option.toUpperCase()=="POSITIVE"){
			gSelected = Gpos;
		}
		else if(option.toUpperCase()=="NEGATIVE"){
			gSelected = Gneg;
		}
		
		
		i = 0;
		for(int row = 0; row < imgArray2D.length; row++){
			for(int column = 0; column < imgArray2D[row].length; column++){
				//System.out.println(G[row][column]);
				imgArray1D[i++] = gSelected[row][column];
			}
		}
		
		BufferedImage tmpImage = new BufferedImage(ncols, nrows, BufferedImage.TYPE_BYTE_GRAY);
		
        WritableRaster raster = (WritableRaster) tmpImage.getData();
        raster.setPixels(0,0,ncols,nrows,imgArray1D);
        tmpImage.setData(raster);
		return tmpImage;		
	}
	
	public BufferedImage getLuplacian2(String option) throws Exception{
		long startTime = 0, stopTime = 0;
		
		
		if(option.toUpperCase()!="POSITIVE" && option.toUpperCase()!="NEGATIVE")
			throw new Exception();
		int[] imgArray1D = new int[ncols * nrows];
		int[][] Gpos = new int[nrows][ncols],
				Gneg = new int[nrows][ncols],
			    imgArray2D = new int[nrows][ncols];
		
		BufferedImage tmpImage = new BufferedImage(ncols, nrows, BufferedImage.TYPE_BYTE_GRAY);
		
		
		
		Color tmpColor;
	    
		int i = 0;
		for(int row = 0; row < nrows; row++){
			for(int column = 0; column < ncols; column++){
				tmpColor = new Color(image.getRGB(column, row));
				//System.out.println(imgArray1D[i]);
				imgArray2D[row][column] = tmpColor.getRed();
			}
		}
		
		
		if(option.toUpperCase()=="POSITIVE"){
			startTime=System.currentTimeMillis();
			for(i=0; i<nrows; i++){
				for(int j=0; j<ncols; j++){
					if(i==0 || i==nrows-1 || j==0 || j==ncols-1){
						Gpos[i][j] = 0; //Image boundary cleared!
					}
					else{						
						Gpos[i][j] = -4*imgArray2D[i][j] + imgArray2D[i+1][j]
							     + imgArray2D[i-1][j] + imgArray2D[i][j+1]
							     + imgArray2D[i][j-1];
						if(Gpos[i][j]>255)
							Gpos[i][j]=255;
			            else if(Gpos[i][j]<0)
			            	Gpos[i][j]=0;
			            tmpImage.setRGB(j, i, new Color(Gpos[i][j],Gpos[i][j],Gpos[i][j]).getRGB());			            
					}
				}
			}
			stopTime=System.currentTimeMillis();
		}
		else if(option.toUpperCase()=="NEGATIVE"){
			startTime=System.currentTimeMillis();
			for(i=0; i<nrows; i++){
				for(int j=0; j<ncols; j++){
					if(i==0 || i==nrows-1 || j==0 || j==ncols-1){
						Gneg[i][j] = 0; //Image boundary cleared!
					}
					else{
						
			            Gneg[i][j] = 4*imgArray2D[i][j] - imgArray2D[i+1][j]
							         - imgArray2D[i-1][j] - imgArray2D[i][j+1]
							         - imgArray2D[i][j-1];
			            if(Gneg[i][j]>255)
			            	Gneg[i][j]=255;
			            else if(Gneg[i][j]<0)
			            	Gneg[i][j]=0;
			            tmpImage.setRGB(j, i, new Color(Gneg[i][j],Gneg[i][j],Gneg[i][j]).getRGB());
			            
					}
				}
			}
			stopTime=System.currentTimeMillis();
		}		
		
		//System.out.println("Laplacian execution time = " + (stopTime - startTime)/1000 + "." + (stopTime - startTime)%1000 +" seconds");
		//System.out.println("Roberts execution time = " + (stopTime - startTime) + " miliseconds");
	
		int[][] gSelected = null;
		
		
		
		
		
		
//        WritableRaster raster = (WritableRaster) tmpImage.getData();
//        raster.setPixels(0,0,ncols,nrows,imgArray1D);
//        tmpImage.setData(raster);
		return tmpImage;		
	}
	
	public BufferedImage getLuplacian3(String option, double sigma, int kernelSize) throws Exception{
		long startTime = 0, stopTime = 0;
		
		
		if(option.toUpperCase()!="POSITIVE" && option.toUpperCase()!="NEGATIVE")
			throw new Exception();
		int[][] Gpos = new int[nrows][ncols],
				Gneg = new int[nrows][ncols],
			    imgArray2D = new int[nrows][ncols],
			    kernel=getKernel(kernelSize, sigma);
		int tmpConValue=0, 
			kernelDiff=kernel.length/2,
			lBound=kernelDiff, 
			uBound=kernelDiff,
			rBound=(ncols - kernelDiff) - 1,
			dBound=(nrows - kernelDiff) - 1;
		
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
		
		
		if(option.toUpperCase()=="POSITIVE"){
			startTime=System.currentTimeMillis();
			for(i=0; i<nrows; i++){
				for(int j=0; j<ncols; j++){
					if(i==0 || i==nrows-1 || j==0 || j==ncols-1){
						Gpos[i][j] = 0; //Image boundary cleared!
					}
					else{						
						Gpos[i][j] = -4*imgArray2D[i][j] + imgArray2D[i+1][j]
							     + imgArray2D[i-1][j] + imgArray2D[i][j+1]
							     + imgArray2D[i][j-1];
						if(Gpos[i][j]>255)
							Gpos[i][j]=255;
			            else if(Gpos[i][j]<0)
			            	Gpos[i][j]=0;
			            tmpImage.setRGB(j, i, new Color(Gpos[i][j],Gpos[i][j],Gpos[i][j]).getRGB());			            
					}
				}
			}
			stopTime=System.currentTimeMillis();
		}
		else if(option.toUpperCase()=="NEGATIVE"){
			startTime=System.currentTimeMillis();
			int borders = 0;
			for(i=0; i<nrows; i++){
				for(int j=0; j<ncols; j++){
					if(i<uBound || i>dBound || j<lBound || j>rBound){
						Gneg[i][j] = 0; //Image boundary cleared!
						tmpImage.setRGB(j, i, new Color(0, 0, 0).getRed());
					}
					else{
						tmpConValue = this.getConValue(j, i, kernel, imgArray2D);
			            tmpImage.setRGB(j, i, new Color(tmpConValue,tmpConValue,tmpConValue).getRGB());
			            
					}
				}
			}
			stopTime=System.currentTimeMillis();
		}		
		
		System.out.println("Laplacian execution time = " + (stopTime - startTime)/1000 + "." + (stopTime - startTime)%1000 +" seconds");
		//System.out.println("Roberts execution time = " + (stopTime - startTime) + " miliseconds");
	
		int[][] gSelected = null;
		
		
		
		
		
		
//        WritableRaster raster = (WritableRaster) tmpImage.getData();
//        raster.setPixels(0,0,ncols,nrows,imgArray1D);
//        tmpImage.setData(raster);
		return tmpImage;		
	}
	
	public BufferedImage getLuplacian(int width, int height, String option){		
		try {
			return getScaled(new Dimension(width, height), getLuplacian(option));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage getLuplacian3(int width, int height, double sigma, int kernelSize, String option){		
		try {
			return getScaled(new Dimension(width, height), getLuplacian3(option, sigma, kernelSize));
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
	
	public void saveLuplacian(String savePath){
		File outFile = new File(savePath);
		try {
			ImageIO.write(getLuplacian("NEGATIVE"), "png", outFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveLuplacian(String savePath, double sigma, int kernelSize){
		File outFile = new File(savePath);
		try {
			ImageIO.write(getLuplacian3("NEGATIVE", sigma, kernelSize), "png", outFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[]args){
		Luplacian lup = new Luplacian("Test.png");
		int[][] kernel = lup.getKernel(9, 0.5);
		for(int y=0; y<kernel.length; y++){
			for(int x=0; x<kernel[y].length; x++){
				System.out.print(kernel[y][x] + " ");
			}
			System.out.println();
		}
	}
}
