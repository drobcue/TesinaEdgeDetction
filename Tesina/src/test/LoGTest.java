package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JProgressBar;

import filters.Luplacian;
import filters.Sobel;

import javax.swing.JCheckBox;
import javax.swing.JTextPane;

import post.PostProcess;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class LoGTest extends JFrame {

	private JPanel contentPane;
	private Object imgAlg ;
	private JLabel lblLapImage;
	private JTextPane textPane;
	private JComboBox cmbImage,
	                  cmbFilter;
	private ActionListener aList = new DoEdge();
	private JCheckBox chckbxMarkDiference;
	private JSlider sldrVarience,
					sldrKernelSize;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoGTest frame = new LoGTest("Sobel");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoGTest(String className) {
			
		
		try {
			instImgAlg(className);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(ERROR);
		}
		
		SpringLayout contentLayout = new SpringLayout(),
				     sl_contentPanel = new SpringLayout();
		JLabel prevLabel = new JLabel("PREVIEW");
		
		JPanel contentPanel = new JPanel();
		contentPane = new JPanel();
		
		
		
		//contentPane.setBackground(Color.red);
		contentPanel.setBackground(Color.blue);
		
		//contentPane.setLayout(contentLayout);
		contentPanel.setLayout(sl_contentPanel);
		contentPane=contentPanel;
		
		//PREVIEW LABEL CONTRAINT
		contentLayout.putConstraint(SpringLayout.WEST, prevLabel,
				                    5, 
				                    SpringLayout.WEST, contentPane);
		contentLayout.putConstraint(SpringLayout.NORTH, prevLabel,
                                    5, 
                                    SpringLayout.NORTH, contentPane);
		
		//OPTIONS PANEL CONSTRAINT
		contentLayout.putConstraint(SpringLayout.WEST, contentPanel,
                                    5, 
                                    SpringLayout.WEST, contentPane);
		contentLayout.putConstraint(SpringLayout.NORTH, contentPanel,
                                    5, 
                                    SpringLayout.SOUTH, prevLabel);
		contentLayout.putConstraint(SpringLayout.SOUTH, contentPanel,
                                    5, 
                                    SpringLayout.SOUTH, contentPane);
		
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 450, 300);
		
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		contentPane.setLayout(contentLayout);
		setContentPane(contentPane);
		
		lblLapImage = new JLabel();
		
		JScrollPane imagePanel = new JScrollPane(lblLapImage);
		imagePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		imagePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, imagePanel, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, imagePanel, -5, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, imagePanel, -225, SpringLayout.EAST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, imagePanel, 5, SpringLayout.WEST, contentPanel);
		//imagePanel.setLayout(new BorderLayout());
		contentPanel.add(imagePanel);
		
//		JScrollPane scrollPane = new JScrollPane(imagePanel);
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel panelOptions = new JPanel();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, panelOptions, -300, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, panelOptions, 5, SpringLayout.EAST, imagePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, panelOptions, -5, SpringLayout.SOUTH, contentPanel);
		
//		lblLapImage = new JLabel();
//		imagePanel.add(lblLapImage, BorderLayout.CENTER);
		sl_contentPanel.putConstraint(SpringLayout.EAST, panelOptions, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(panelOptions);
		SpringLayout sl_panelOptions = new SpringLayout();
		panelOptions.setLayout(sl_panelOptions);
		
		sldrKernelSize = new JSlider();
		sldrKernelSize.setPaintTicks(true);
		sl_panelOptions.putConstraint(SpringLayout.WEST, sldrKernelSize, 5, SpringLayout.WEST, panelOptions);
		sldrKernelSize.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sldrKernelSize.setValue(3);
		sldrKernelSize.setPaintLabels(true);
		sldrKernelSize.setMajorTickSpacing(2);
		sldrKernelSize.setMinimum(3);
		sldrKernelSize.setMaximum(15);
		panelOptions.add(sldrKernelSize);
		
		sldrVarience = new JSlider();
		sl_panelOptions.putConstraint(SpringLayout.WEST, sldrVarience, 5, SpringLayout.WEST, panelOptions);
		sldrVarience.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sldrVarience.setPaintLabels(true);
		sldrVarience.setMajorTickSpacing(50);
		sldrVarience.setPaintTicks(true);
		sldrVarience.setValue(0);
		sldrVarience.setMaximum(150);
		panelOptions.add(sldrVarience);
		if(!className.equals("Laplacian"))
			sldrVarience.setEnabled(false);
		sldrVarience.addChangeListener(new VarianceChange());
		
		JLabel lblVariance = new JLabel("Variance");
		sl_panelOptions.putConstraint(SpringLayout.NORTH, lblVariance, 25, SpringLayout.SOUTH, sldrKernelSize);
		sl_panelOptions.putConstraint(SpringLayout.WEST, lblVariance, 55, SpringLayout.WEST, panelOptions);
		sl_panelOptions.putConstraint(SpringLayout.NORTH, sldrVarience, 5, SpringLayout.SOUTH, lblVariance);
		panelOptions.add(lblVariance);
		
		JLabel lblVarianceValue = new JLabel("0.00");
		sl_panelOptions.putConstraint(SpringLayout.NORTH, lblVarianceValue, 25, SpringLayout.SOUTH, sldrKernelSize);
		sl_panelOptions.putConstraint(SpringLayout.WEST, lblVarianceValue, 5, SpringLayout.EAST, lblVariance);
		panelOptions.add(lblVarianceValue);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(aList);
		btnNewButton.setActionCommand("DoEdge");		
		sl_panelOptions.putConstraint(SpringLayout.SOUTH, btnNewButton, -5, SpringLayout.SOUTH, panelOptions);
		sl_panelOptions.putConstraint(SpringLayout.EAST, btnNewButton, -5, SpringLayout.EAST, panelOptions);
		panelOptions.add(btnNewButton);
		
		JLabel lblKernelSize = new JLabel("Kernel Size");
		sl_panelOptions.putConstraint(SpringLayout.NORTH, sldrKernelSize, 5, SpringLayout.SOUTH, lblKernelSize);
		sl_panelOptions.putConstraint(SpringLayout.NORTH, lblKernelSize, 5, SpringLayout.NORTH, panelOptions);
		sl_panelOptions.putConstraint(SpringLayout.WEST, lblKernelSize, 55, SpringLayout.WEST, panelOptions);
		panelOptions.add(lblKernelSize);
		
		chckbxMarkDiference = new JCheckBox("Mark Diference");
		sl_panelOptions.putConstraint(SpringLayout.NORTH, chckbxMarkDiference, 25, SpringLayout.SOUTH, sldrVarience);
		sl_panelOptions.putConstraint(SpringLayout.WEST, chckbxMarkDiference, 5, SpringLayout.WEST, panelOptions);
		panelOptions.add(chckbxMarkDiference,6);
		
		JPanel infoPanel = new JPanel();
		sl_contentPanel.putConstraint(SpringLayout.WEST, infoPanel, 5, SpringLayout.EAST, imagePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, infoPanel, -5, SpringLayout.NORTH, panelOptions);
		sl_contentPanel.putConstraint(SpringLayout.EAST, infoPanel, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(infoPanel);
		infoPanel.setLayout(new BorderLayout(0, 0));
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		infoPanel.add(textPane, BorderLayout.CENTER);
		
		JPanel inputOptionsPane = new JPanel();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, infoPanel, 5, SpringLayout.SOUTH, inputOptionsPane);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, inputOptionsPane, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, inputOptionsPane, 5, SpringLayout.EAST, imagePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, inputOptionsPane, 200, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, inputOptionsPane, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(inputOptionsPane);
		SpringLayout sl_inputOptionsPane = new SpringLayout();
		inputOptionsPane.setLayout(sl_inputOptionsPane);
		
		JLabel lblImage = new JLabel("Image");
		sl_inputOptionsPane.putConstraint(SpringLayout.NORTH, lblImage, 5, SpringLayout.NORTH, inputOptionsPane);
		sl_inputOptionsPane.putConstraint(SpringLayout.WEST, lblImage, 5, SpringLayout.WEST, inputOptionsPane);
		inputOptionsPane.add(lblImage);
		
		JLabel lblFilter = new JLabel("Filter");
		sl_inputOptionsPane.putConstraint(SpringLayout.NORTH, lblFilter, 25, SpringLayout.SOUTH, lblImage);
		sl_inputOptionsPane.putConstraint(SpringLayout.WEST, lblFilter, 0, SpringLayout.WEST, lblImage);
		inputOptionsPane.add(lblFilter);
		
		cmbImage = new JComboBox();
		cmbImage.setModel(new DefaultComboBoxModel(new String[] {"Shapes Test.png", "FOTO.jpg", "Test.png", "3D-shapes.png"}));
		cmbImage.addActionListener(aList);
		cmbImage.setActionCommand("Image");
		sl_inputOptionsPane.putConstraint(SpringLayout.NORTH, cmbImage, 0, SpringLayout.NORTH, lblImage);
		sl_inputOptionsPane.putConstraint(SpringLayout.WEST, cmbImage, 5, SpringLayout.EAST, lblImage);
		inputOptionsPane.add(cmbImage);
		
		cmbFilter = new JComboBox();
		cmbFilter.setModel(new DefaultComboBoxModel(new String[] {"Laplacian", "Sobel"}));
		cmbFilter.addActionListener(aList);
		cmbFilter.setActionCommand("Filter");
		sl_inputOptionsPane.putConstraint(SpringLayout.NORTH, cmbFilter, 0, SpringLayout.NORTH, lblFilter);
		sl_inputOptionsPane.putConstraint(SpringLayout.WEST, cmbFilter, 5, SpringLayout.EAST, lblFilter);
		inputOptionsPane.add(cmbFilter);
		
//		JProgressBar progressBar = new JProgressBar();
//		sl_panelOptions.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.NORTH, btnNewButton);
//		sl_panelOptions.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, panelOptions);
//		panelOptions.add(progressBar);
		
		
	}
	
	private void instImgAlg(String className) throws Exception {
		if(className.equals("Laplacian"))
			imgAlg = new Luplacian("Shapes Test.png");
		else if(className.equals("Sobel"))
			imgAlg = new Sobel("Shapes Test.png");
		else 
			throw new Exception("Invalid Class: " + className + " is not a valid class");
		
	}

	private class VarianceChange implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			double value;
			JSlider slider = (JSlider) e.getSource();
			JLabel lblVariance = (JLabel)slider.getParent().getComponent(3);
			value = slider.getValue();
			value = value / 100;
			lblVariance.setText(String.valueOf(value));
			//lblVariance.repaint();
			
			
		}
		
	}
	
	private class DoEdge implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			if(e.getActionCommand().equals("Image")){
				String imgPath = cmbImage.getSelectedItem().toString();
				if(imgPath.equals("Shapes Test.png")){
					chckbxMarkDiference.setEnabled(true);
				}
				else{
					chckbxMarkDiference.setSelected(false);
					chckbxMarkDiference.setEnabled(false);
				}
			}
			else if(e.getActionCommand().equals("DoEdge")){
				BufferedImage img = null;
				try {
					img = getBImg(e);
					lblLapImage.setIcon(new ImageIcon(img));
					lblLapImage.repaint();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			else if(e.getActionCommand().equals("Filter")){
				if(cmbFilter.getSelectedItem().toString().equals("Laplacian")){
					sldrVarience.setEnabled(true);
				}
				else{
					sldrVarience.setValue(0);
					sldrVarience.setEnabled(false);
				}
			}
		}

		private BufferedImage getBImg(ActionEvent e) throws Exception {
			Map<String, Double> errorPer = new HashMap<String, Double>();
			String imgPath = cmbImage.getSelectedItem().toString();
			int kernelSize = sldrKernelSize.getValue();
			if (cmbFilter.getSelectedItem().toString().equals("Laplacian")){
				Luplacian lap = new Luplacian(imgPath);				
				double value;
				JSlider slider = ((JSlider)((JButton)e.getSource()).getParent().getComponent(1));
				value = slider.getValue();
				value = value / 100;
				BufferedImage tmpImg = lap.getLuplacian3("NEGATIVE", value, kernelSize);
				String msg = "Filter : Sobel\n";
				msg += "Run Time : " + ((Sobel)imgAlg).getElapsedTime() + " seconds\n";
				
				if (((JCheckBox)((JButton)e.getSource()).getParent().getComponent(6)).isSelected()){
					PostProcess.markError("Shapes Test Original.png", tmpImg, errorPer);
					msg += "Undetected edges % : " + 
							String.format("%1$.2f", errorPer.get("UNDETECTED"))+ "%\n";
					msg += "False positives edges % : " + String.format("%1$.2f", errorPer.get("FALSE")) + "%";
				}
				textPane.setText(msg);
				return tmpImg;
			}
			else if (cmbFilter.getSelectedItem().toString().equals("Sobel")){
				Sobel sobel = new Sobel(imgPath);
				BufferedImage tmpImg = sobel.getSobel2("FULL");
				String msg = "Filter : Sobel\n";
				msg += "Run Time : " + ((Sobel)imgAlg).getElapsedTime() + " seconds\n";
				
				if (((JCheckBox)((JButton)e.getSource()).getParent().getComponent(6)).isSelected()){
					PostProcess.markError("Shapes Test Original.png", tmpImg, errorPer);
					msg += "Undetected edges % : " + 
							String.format("%1$.2f", errorPer.get("UNDETECTED"))+ "%\n";
					msg += "False positives edges % : " + String.format("%1$.2f", errorPer.get("FALSE")) + "%";
				}
				textPane.setText(msg);
				return tmpImg;
			}
				
			return null;
		}
		
	}
}
