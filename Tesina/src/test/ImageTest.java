package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import filters.Luplacian;
import filters.Prewitt;
import filters.Roberts;
import filters.Sobel;

public class ImageTest extends JFrame {

	private Container contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageTest frame = new ImageTest();
					
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private double getError(String imgPath1, String imgPath2) throws Exception{
		int [] imgArr1, imgArr2;
		imgArr1 = getImgArray(imgPath1, "ORIGINAL");
		imgArr2 = getImgArray(imgPath2, "BORDERS");
		
		return getError(imgArr1, imgArr2);
	}
	
	private double getError(int[] arr1, int[] arr2) throws Exception{
		if(arr1.length!=arr2.length)
			throw new Exception();
		int match = 0;
		
		for(int i = 0; i<arr1.length; i++){
			if(arr1[i]==arr2[i])
				match++;
		}
		
		System.out.println(arr1.length);
		System.out.println(match);
		
		return ((double)Math.abs(match - arr1.length)/arr1.length) * 100;
	}
	
	private void markError(String imgPathOrig, BufferedImage resultImg) throws Exception{
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
		System.out.println("There are a " + ((float)undetected/origBorders)*100 + "% of the edges undetected");
		System.out.println("There are a " + ((float)falsePositive/finalBorders)*100 + "% of false positives edges");
		System.out.println();
	}
	
	
	private int[] getImgArray(String imgPath, String imageType) throws Exception{
		Image img = ImageIO.read(new File(imgPath));
		BufferedImage bImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D graph = bImg.createGraphics();
		graph.drawImage(img, 0, 0, null);
		graph.dispose();
		
		return getImgArray(bImg, imageType);
	}
	
	private int[] getImgArray(BufferedImage bImg, String imageType) throws Exception{
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

	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public ImageTest() throws Exception {
		final int imageWidth=500, imageHeight=500;
		String path = "Shapes Test.png";
	
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		Container panelInfo = new JPanel();
		panelInfo.setBackground(Color.red);
		this.setContentPane(contentPane);
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
//		JPanel tmpPanel = new JPanel();
//		tmpPanel.setBackground(Color.red);
//		
//		
//		JPanel cntPanel = new JPanel();
//		cntPanel.setBackground(Color.BLACK);
//		cntPanel.add(tmpPanel);
//		
//		this.getContentPane().add(cntPanel);
//		tmpPanel.setBounds(0, 0, 255, 255);
		
//		JPanel sobelPanel = new JPanel(),
//			   prewittPanel = new JPanel(),
//			   robertsPanel = new JPanel();
		
		SpringLayout layout = new SpringLayout();
//		SpringLayout sobelLayout = new SpringLayout(),
//				     prewittLayout = new SpringLayout(),
//				     robertsLayout = new SpringLayout();
		
		Sobel sobel = new Sobel(path);
		Prewitt prewitt = new Prewitt(path);
		Roberts roberts = new Roberts(path);
		Luplacian lup = new Luplacian(path);
		
		BufferedImage biSobelFull = sobel.getSobel2("FULL"),
					  biSobelX = sobel.getSobel(imageWidth, imageHeight, "X"),
					  biSobelY = sobel.getSobel(imageWidth, imageHeight, "Y"),
				      biGrayscale = sobel.getGrayscale(imageWidth, imageHeight),
				      biPrewittFull = prewitt.getPrewitt("FULL"),
				      biPrewittX = prewitt.getPrewitt(imageWidth, imageHeight, "X"),
				      biPrewittY = prewitt.getPrewitt(imageWidth, imageHeight, "Y"),
//				      biRobertsFull = roberts.getRoberts(imageWidth, imageHeight, "FULL"),
//				      biRobertsX = roberts.getRoberts(imageWidth, imageHeight, "X"),
//				      biRobertsY = roberts.getRoberts(imageWidth, imageHeight, "Y");
						      biRobertsFull = roberts.getRoberts("FULL"),
						      biRobertsX = lup.getLuplacian(imageWidth, imageHeight, "NEGATIVE"),
						      biRobertsY = lup.getLuplacian3("NEGATIVE", 0.85, 9);
		//lup.saveLuplacian("FOTO_LUP2_0.85.png", 0.85);
		//sobel.saveSobel("Sobel1.png");
				      
		
		JLabel lblImageSobelGrayscale = new JLabel(new ImageIcon(biGrayscale)),
			   lblImageSobelX = new JLabel(new ImageIcon(biSobelX)),
			   lblImageSobelY = new JLabel(new ImageIcon(biSobelY)),
//			   lblImageSobelFull = new JLabel(new ImageIcon(biSobelFull)),
			   lblImageSobelFull = new JLabel(new ImageIcon(biRobertsY)),
			   lblImagePrewittGrayscale = new JLabel(new ImageIcon(biGrayscale)),
		       lblImagePrewittFull = new JLabel(new ImageIcon(biPrewittFull)),
		       lblImagePrewittX = new JLabel(new ImageIcon(biPrewittX)),
		       lblImagePrewittY = new JLabel(new ImageIcon(biPrewittY)),
		       lblImageRobertsGrayscale = new JLabel(new ImageIcon(biGrayscale)),
		       lblImageRobertsFull = new JLabel(new ImageIcon(biRobertsFull)),
		       lblImageRobertsY = new JLabel(new ImageIcon(biRobertsY)),
		       lblImageRobertsX = new JLabel(new ImageIcon(biRobertsX));
		       
		contentPane.setLayout(layout);
//		sobelPanel.setLayout(sobelLayout);
//		prewittPanel.setLayout(prewittLayout);
//		robertsPanel.setLayout(robertsLayout);
		
		
		JSeparator sobelSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		
//		contentPane.add(lblImageSobelGrayscale);
		contentPane.add(lblImageSobelFull);
//		contentPane.add(lblImageSobelY);
//		contentPane.add(lblImageSobelX);
//		contentPane.add(lblImagePrewittGrayscale);
//		contentPane.add(lblImagePrewittFull);
//		contentPane.add(lblImagePrewittX);
//		contentPane.add(lblImagePrewittY);
//		contentPane.add(lblImageRobertsGrayscale);
//		contentPane.add(lblImageRobertsFull);
//		contentPane.add(lblImageRobertsX);
//		contentPane.add(lblImageRobertsY);
//		contentPane.add(sobelSeparator);
		//contentPane.add(panelInfo);
//		contentPane.add(sobelPanel);
//		contentPane.add(prewittPanel);
//		contentPane.add(robertsPanel);
//		
		
		
//		contentPane.add(lblImageGrayscale);
//		contentPane.add(lblImageSobelFull);
//		contentPane.add(lblImageSobelY);
//		contentPane.add(lblImageSobelX);
		
		
		
		//Set Constarint for Sobel Panel		
//		layout.putConstraint(SpringLayout.WEST, lblImageSobelGrayscale, 
//				                  5, 
//				                  SpringLayout.WEST, contentPane);
//		layout.putConstraint(SpringLayout.NORTH, lblImageSobelGrayscale, 
//                                  5, 
//                                  SpringLayout.NORTH, contentPane);
//		layout.putConstraint(SpringLayout.WEST, lblImageSobelY, 
//                				  5, 
//                                  SpringLayout.EAST, lblImageSobelGrayscale);
//		layout.putConstraint(SpringLayout.NORTH, lblImageSobelY, 
//				                  5, 
//                                  SpringLayout.NORTH, contentPane);
//		layout.putConstraint(SpringLayout.WEST, lblImageSobelX, 
//                                  5, 
//                                  SpringLayout.EAST, lblImageSobelY);
//		layout.putConstraint(SpringLayout.NORTH, lblImageSobelX, 
//                                  5, 
//                                  SpringLayout.NORTH, contentPane);
//		layout.putConstraint(SpringLayout.WEST, lblImageSobelFull, 
//                                  5, 
//                                  SpringLayout.WEST, lblImageSobelX);
		layout.putConstraint(SpringLayout.NORTH, contentPane, 
                                  5, 
                                  SpringLayout.NORTH, contentPane);
//		
//		layout.putConstraint(SpringLayout.WEST, sobelSeparator, 
//                            5, 
//                            SpringLayout.WEST, contentPane);
//		layout.putConstraint(SpringLayout.EAST, sobelSeparator, 
//                             5, 
//                             SpringLayout.EAST, contentPane);
//		layout.putConstraint(SpringLayout.NORTH, sobelSeparator, 
//                             5, 
//                             SpringLayout.SOUTH, lblImageSobelFull);
//		
//		
//		
//		//Set Constraint for prewitt Layout
//				layout.putConstraint(SpringLayout.WEST, lblImagePrewittGrayscale, 
//						                    5, 
//						                    SpringLayout.WEST, contentPane);
//				layout.putConstraint(SpringLayout.NORTH, lblImagePrewittGrayscale, 
//		                                    5, 
//		                                    SpringLayout.SOUTH, sobelSeparator);
//				layout.putConstraint(SpringLayout.WEST, lblImagePrewittY, 
//		                                    5, 
//		                                    SpringLayout.EAST, lblImagePrewittGrayscale);
//				layout.putConstraint(SpringLayout.NORTH, lblImagePrewittY, 
//		                                    5, 
//		                                    SpringLayout.SOUTH, sobelSeparator);
//				layout.putConstraint(SpringLayout.WEST, lblImagePrewittX, 
//		                                    5, 
//		                                    SpringLayout.EAST, lblImagePrewittY);
//				layout.putConstraint(SpringLayout.NORTH, lblImagePrewittX, 
//		                                    5, 
//		                                    SpringLayout.SOUTH, sobelSeparator);
//				layout.putConstraint(SpringLayout.WEST, lblImagePrewittFull, 
//		                                    5, 
//		                                    SpringLayout.EAST, lblImagePrewittX);
//				layout.putConstraint(SpringLayout.NORTH, lblImagePrewittFull, 
//		                                    5, 
//		                                    SpringLayout.SOUTH, sobelSeparator);
		
//		//Set Constraint for prewitt Layout
//		layout.putConstraint(SpringLayout.WEST, lblImagePrewittGrayscale, 
//				                    5, 
//				                    SpringLayout.WEST, contentPane);
//		layout.putConstraint(SpringLayout.NORTH, lblImagePrewittGrayscale, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImageSobelGrayscale);
//		layout.putConstraint(SpringLayout.WEST, lblImagePrewittY, 
//                                    5, 
//                                    SpringLayout.EAST, lblImagePrewittGrayscale);
//		layout.putConstraint(SpringLayout.NORTH, lblImagePrewittY, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImageSobelY);
//		layout.putConstraint(SpringLayout.WEST, lblImagePrewittX, 
//                                    5, 
//                                    SpringLayout.EAST, lblImagePrewittY);
//		layout.putConstraint(SpringLayout.NORTH, lblImagePrewittX, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImageSobelX);
//		layout.putConstraint(SpringLayout.WEST, lblImagePrewittFull, 
//                                    5, 
//                                    SpringLayout.EAST, lblImagePrewittX);
//		layout.putConstraint(SpringLayout.NORTH, lblImagePrewittFull, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImageSobelFull);
        
        //Set Constraint for roberts Layout
//		layout.putConstraint(SpringLayout.WEST, lblImageRobertsGrayscale, 
//                                    5, 
//                                    SpringLayout.WEST, contentPane);
//		layout.putConstraint(SpringLayout.NORTH, lblImageRobertsGrayscale, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImagePrewittGrayscale);
//		layout.putConstraint(SpringLayout.WEST, lblImageRobertsY, 
//                                    5, 
//                                    SpringLayout.WEST, contentPane);
//		layout.putConstraint(SpringLayout.NORTH, lblImageRobertsY, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImagePrewittY);
//		layout.putConstraint(SpringLayout.WEST, lblImageRobertsX, 
//                                    5, 
//                                    SpringLayout.EAST, lblImageRobertsY);
//		layout.putConstraint(SpringLayout.NORTH, lblImageRobertsX, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImagePrewittX);
//		layout.putConstraint(SpringLayout.WEST, lblImageRobertsFull, 
//                                    5, 
//                                    SpringLayout.EAST, lblImageRobertsX);
//		layout.putConstraint(SpringLayout.NORTH, lblImageRobertsFull, 
//                                    5, 
//                                    SpringLayout.SOUTH, lblImagePrewittFull);
		
//		layout.putConstraint(SpringLayout.WEST, panelInfo, 
//							 5, 
//							 SpringLayout.EAST, lblImageSobelFull);
//		layout.putConstraint(SpringLayout.NORTH, panelInfo, 
//				             5, 
//				             SpringLayout.NORTH, contentPane);
//		layout.putConstraint(SpringLayout.SOUTH, panelInfo, 
//	                         5, 
//	                         SpringLayout.SOUTH, contentPane);
		
		
        
     
        
        
		
		//this.getContentPane().add(new ImagePanel(255,255));
		
		this.setSize(contentPane.WIDTH, contentPane.HEIGHT);
		
		//System.out.println("The precentage of error is: " + getError("Shapes Test Original.png", "Sobel1.png"));
		markError("Shapes Test Original.png", biRobertsY);
		lblImageSobelFull.repaint();
		this.update(getGraphics());
		this.validate();
	}
	
	

}
