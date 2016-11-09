package post;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

public class PostProcess {

	public static void markError(String imgPathOrig, BufferedImage resultImg, Map<String, Double> errorPer) throws Exception{
		int[] imgOrigArr = getImgArray(imgPathOrig, "ORIGINAL"); 
		int[] imgresultgArr = getImgArray(resultImg, "BORDERS"); 
		if(imgOrigArr.length!=imgresultgArr.length)
			throw new Exception();
		int width = resultImg.getWidth();
		int falsePositive = 0;
		int undetected = 0;
		int origBorders = 0;
		int finalBorders = 0;
		
		for(int i = 0; i<imgresultgArr.length; i++){
//			if(imgOrigArr[i]!=imgresultgArr[i]){
//				System.out.println(imgOrigArr[i]+" "+imgresultgArr[i]);
//				resultImg.setRGB(i % width, i / width, new Color(0,255,0).getRGB());
////				System.out.println("1");
//			}
//			else{
//				resultImg.setRGB(i % width, i / width, new Color(0,0,0).getRGB());
//			}
			
			if(imgOrigArr[i] == 255 && imgresultgArr[i] == 255){
				resultImg.setRGB(i % width, i / width, new Color(0,0,255).getRGB());
				origBorders++;
				finalBorders++;
			}
			else if(imgOrigArr[i] == 255){
				resultImg.setRGB(i % width, i / width, new Color(0,255,0).getRGB());
				origBorders++;
				undetected++;
			}
			else if(imgresultgArr[i] == 255){
				resultImg.setRGB(i % width, i / width, new Color(255,0,0).getRGB());
				falsePositive++;
				finalBorders++;
			}
				
				
		}
		resultImg.flush();
		if(errorPer != null){
			errorPer.put("UNDETECTED", ((double)undetected/origBorders)*100);
			errorPer.put("FALSE", ((double)falsePositive/finalBorders)*100);
		}
			
		
		System.out.println("There are a " + ((float)undetected/origBorders)*100 + "% of the edges undetected");
		System.out.println("There are a " + ((float)falsePositive/finalBorders)*100 + "% of false positives edges");
		System.out.println();
	}
	
	private static int[] getImgArray(String imgPath, String imageType) throws Exception{
		Image img = ImageIO.read(new File(imgPath));
		BufferedImage bImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D graph = bImg.createGraphics();
		graph.drawImage(img, 0, 0, null);
		graph.dispose();
		
		return getImgArray(bImg, imageType);
	}
	
	private static int[] getImgArray(BufferedImage bImg, String imageType) throws Exception{
		int[] imgArr;
		if(!(imageType.equalsIgnoreCase("ORIGINAL") || imageType.equalsIgnoreCase("BORDERS")))
			throw new Exception();
		imgArr = new int[bImg.getWidth()*bImg.getHeight()];
		int idx = 0;
		Color tmpColor;
		
		for(int row = 0; row < bImg.getHeight(); row++){
			for(int col = 0; col<bImg.getWidth(); col++){
				tmpColor = new Color(bImg.getRGB(col, row));
//				if(tmpColor.getRed() > 0 && imageType.equalsIgnoreCase("BORDERS")){
//					System.out.println(tmpColor.getRed());
//				}
					
				if((tmpColor.getRed() < 255 && imageType.equalsIgnoreCase("ORIGINAL")) ||
				   (tmpColor.getRed() > 0 && imageType.equalsIgnoreCase("BORDERS"))){
					imgArr[idx++] = 255;
					//System.out.println(imageType);
				}
										
				else {
					imgArr[idx++] = 0;
				}
					
			}
		}
		return imgArr;
	}
}
