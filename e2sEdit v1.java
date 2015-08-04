package com.e2sEdit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.io.RandomAccessFile;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static JLabel label1;
	private static JLabel label2;
	private static JLabel infotext;
	private static JLabel freespace;
	private static JTable table1;
	private static JTable table2;
	private static JScrollPane pane1;
	private static JScrollPane pane2;
	private JFileChooser fc;
	private JFileChooser sc;
	private static JButton openButton;
	private static JButton openButton2;
	private static JButton replaceButton;
	private static JButton deleteButton;
	private static JButton playButton;
	private static JButton loopButton;
	private static JButton exportButton;
	private static String allfile = "";
	private static String allfilename = "";
	private static String sampfile = "";
	private static String sampfilename = "";
	private static int row = 0;
	private static int col = 0;
	private static Object currValue = "";

	String[] columnNames = {"#", "A#", "Sample Name", "Cat", "Start", "Length", "Loop", "End", "Lp", "St", "Ld", "Bitrate", "Tn"};
	static Object[][] sampledata = new Object[981][13];
	static Object[][] selectedsample = new Object[1][13];

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public GUI() {
		initGUI();
	}
	
	private void initGUI() {
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".all file", "all");
		fc.setFileFilter(filter);
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 700);
		frame.setVisible(true);
		frame.setTitle("e2sEdit v1");
		frame.setLayout(new FlowLayout());

		openButton = new JButton("Open a File...");
		frame.add(openButton);
		
		label1 = new JLabel("Please load an .all file!");
		frame.add(label1);
		
		freespace = new JLabel("");
		frame.add(freespace);
		
		label2 = new JLabel("");
		frame.add(label2);
		
		table1 = new JTable(sampledata, columnNames);
		table1.setPreferredScrollableViewportSize(new Dimension(750, 500));
		table1.setFillsViewportHeight(true);
		table1.getColumnModel().getColumn(0).setPreferredWidth(36);
		table1.getColumnModel().getColumn(1).setPreferredWidth(36);
		table1.getColumnModel().getColumn(2).setPreferredWidth(170);
		table1.getColumnModel().getColumn(3).setPreferredWidth(90);
		table1.getColumnModel().getColumn(4).setPreferredWidth(100);
		table1.getColumnModel().getColumn(5).setPreferredWidth(100);
		table1.getColumnModel().getColumn(6).setPreferredWidth(100);
		table1.getColumnModel().getColumn(7).setPreferredWidth(100);
		table1.getColumnModel().getColumn(8).setPreferredWidth(36);
		table1.getColumnModel().getColumn(9).setPreferredWidth(36);
		table1.getColumnModel().getColumn(10).setPreferredWidth(36);
		table1.getColumnModel().getColumn(11).setPreferredWidth(100);
		table1.getColumnModel().getColumn(12).setPreferredWidth(36);
		
		TableColumn catColumn = table1.getColumnModel().getColumn(3);
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("Kick");
		comboBox.addItem("Snare");
		comboBox.addItem("Clap");
		comboBox.addItem("HiHat");
		comboBox.addItem("Cymbal");
		comboBox.addItem("Hits");
		comboBox.addItem("Shots");
		comboBox.addItem("Voice");
		comboBox.addItem("SE");
		comboBox.addItem("FX");
		comboBox.addItem("Tom");
		comboBox.addItem("Perc.");
		comboBox.addItem("Phrase");
		comboBox.addItem("Loop");
		comboBox.addItem("PCM");
		comboBox.addItem("User");
		catColumn.setCellEditor(new DefaultCellEditor(comboBox));
		
		pane1 = new JScrollPane(table1);
        frame.add(pane1);
        
        table1.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
    		row = e.getFirstRow();
    		col = e.getColumn();
    		if (sampledata[row][4] != null) {
    		String cellValue = String.valueOf( table1.getValueAt(row, col) );
    		System.out.println("Value at (" + row + "," + col + ") changed to " + "\'" + cellValue + "\'");
    		
    		if (col == 0) {
    			infotext.setText("Can't edit sample number!");
	     		sampledata[row][0] = currValue;
	     		selectedsample[0][0] = currValue;
	            pane1.revalidate();
	            pane1.repaint();
	            pane2.revalidate();
	            pane2.repaint();
    		}
   
    		if (col == 1) {
    			infotext.setText("Can't edit absolute sample number!");
    			sampledata[row][1] = currValue;
    			selectedsample[0][1] = currValue;
    			pane1.revalidate();
    			pane1.repaint();
    			pane2.revalidate();
    			pane2.repaint();
    		}
    		
    		if (col == 2) {
    			try {
    				if (cellValue.equals("") || (cellValue.trim().length() == 0)) {
    					sampledata[row][2] = currValue;
    					System.out.println("Sample name can't be blank!");
    					return;
    				}
    				else {
					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
					raf.seek(62 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     		//write sample name
		     		int namelen = cellValue.length();
		     		if (namelen > 16) {
		     			cellValue = cellValue.substring(0, 16);
		        	}
		     		sampledata[row][2] = cellValue;
		     		selectedsample[0][2] = cellValue;
		     		infotext.setText("Name changed to " + cellValue);
		     		raf.writeBytes(cellValue);
		            //pad the name with 0s
		    		 for (int j=0; j<(16-namelen); j++) {
		                raf.write(0);
		            }
		    		raf.close();
		            pane2.revalidate();
		            pane2.repaint();
    				}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}

    		if (col == 3) {
    			try {
					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
					raf.seek(78 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     		//write category number
		     		sampledata[row][3] = cellValue;
		     		selectedsample[0][3] = cellValue;
		     		infotext.setText("Category changed to " + cellValue);
		     		raf.write(hex2dec(getCatNum(cellValue)));
		    		raf.close();
		            pane1.revalidate();
		            pane1.repaint();
		            pane2.revalidate();
		            pane2.repaint();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    		
    		if (col == 4) {
    			infotext.setText("Can't edit start position!");
	     		sampledata[row][4] = currValue;
	     		selectedsample[0][4] = currValue;
	            pane1.revalidate();
	            pane1.repaint();
	            pane2.revalidate();
	            pane2.repaint();
    		}
    		
    		if (col == 5) {
    			infotext.setText("Can't edit sample length!");
	     		sampledata[row][5] = currValue;
	     		selectedsample[0][5] = currValue;
	            pane1.revalidate();
	            pane1.repaint();
	            pane2.revalidate();
	            pane2.repaint();
    		}
    		
    		if (col == 6) {
    			try {
    				String c = (String)sampledata[row][6];
		     		int ci = Integer.parseInt(c);
    				if (ci > ((int)sampledata[row][5]-2)) {
    					sampledata[row][6] = ((int)sampledata[row][5]-2);
    					infotext.setText("Loop length can't exceed sample length!");
    					return;
    				}
    				if (ci < 0) {
    					sampledata[row][6] = currValue;
    					infotext.setText("Loop length can't be negative!");
    					return;
    				}
    				else {
					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
					raf.seek(104 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     		//write loop
		     		infotext.setText("Loop length changed to " + ci);
		     		sampledata[row][6] = ci;
		     		selectedsample[0][6] = ci;
		     		raf.writeInt(Integer.reverseBytes(ci));
		    		raf.close();
		            pane2.revalidate();
		            pane2.repaint();
    				}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    		
    		if (col == 7) {
    			try {
    				String c = (String)sampledata[row][7];
		     		int ci = Integer.parseInt(c);
		     		int s = (int) currValue;
		     		System.out.println("length: " + sampledata[row][5]);
		     		System.out.println("ct: " + ci);
		     		System.out.println("s: " + s);
    				if (ci > ((int)sampledata[row][5]-2)) {
    					sampledata[row][7] = s;
    					infotext.setText("End point can't exceed sample length!");
    					return;
    				}
    				if (ci < 0) {
    					sampledata[row][7] = currValue;
    					infotext.setText("End point can't be negative!");
    					return;
    				}
    				else {
					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
					raf.seek(108 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     		//write end
		     		infotext.setText("End point changed to " + ci);
		     		sampledata[row][7] = ci;
		     		selectedsample[0][7] = ci;
		     		raf.writeInt(Integer.reverseBytes(ci));
		    		raf.close();
		            pane2.revalidate();
		            pane2.repaint();
    				}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    		
    		if (col == 8) {
    			try {
    				String c = (String) sampledata[row][8];
    				System.out.println(currValue);
		     		String s = cellValue;
		     		Short st = Short.parseShort(s);
    				if (s.equals("1") || s.equals("0")) {
    					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
    					raf.seek(112 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
    		     		//write loop status
    		     		infotext.setText("Loop status is now " + s);
    		     		sampledata[row][8] = s;
    		     		selectedsample[0][8] = s;
    		     		raf.write(st);
    		    		raf.close();
    		            pane2.revalidate();
    		            pane2.repaint();
    					pane1.revalidate();
    					pane1.repaint();
    				}
    				else {		
					sampledata[row][8] = currValue;
					System.out.println(c);
					System.out.println(s);
					infotext.setText("Loop status must be a 1 or 0!");
		            pane2.revalidate();
		            pane2.repaint();
					pane1.revalidate();
					pane1.repaint();
    				}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    		
    		if (col == 10) {
    			try {
    				String c = (String) sampledata[row][10];
    				System.out.println(currValue);
		     		String s = cellValue;
		     		Short st = Short.parseShort(s);
    				if (s.equals("1") || s.equals("0")) {
    					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
    					raf.seek(126 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
    		     		//write loudness
    		     		infotext.setText("Loudness is now " + s);
    		     		sampledata[row][10] = s;
    		     		selectedsample[0][10] = s;
    		     		raf.write(st);
    		    		raf.close();
    		            pane2.revalidate();
    		            pane2.repaint();
    					pane1.revalidate();
    					pane1.repaint();
    				}
    				else {		
					sampledata[row][10] = currValue;
					System.out.println(c);
					System.out.println(s);
					infotext.setText("Loudness must be a 1 or 0!");
		            pane2.revalidate();
		            pane2.repaint();
					pane1.revalidate();
					pane1.repaint();
    				}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    		
    		if (col == 12) {
    			try {
		     		short s = Short.parseShort(cellValue);
    				if (s > -64 && s < 64) {
    					RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
    					raf.seek(137 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
    		     		//write tune
    		     		infotext.setText("Sample tune changed to " + s);
    		     		sampledata[row][12] = s;
    		     		selectedsample[0][12] = s;
    		     		raf.write(s);
    		    		raf.close();
    		            pane2.revalidate();
    		            pane2.repaint();
    					pane1.revalidate();
    					pane1.repaint();
    				}
    				else {		
					sampledata[row][12] = currValue;
					infotext.setText("Tune must be -63 to 63!");
		            pane2.revalidate();
		            pane2.repaint();
					pane1.revalidate();
					pane1.repaint();
    				}
		            
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
            }
    		else {
    			System.out.println("Shouldn't reach this!");
    		}
            }
          });
        
		infotext = new JLabel("e2sSample.all not found.");
		frame.add(infotext);
            
		table2 = new JTable(selectedsample, columnNames);
		table2.setPreferredScrollableViewportSize(new Dimension(750, 16));
		table2.setPreferredSize(new Dimension(750, 16));
		table2.getColumnModel().getColumn(0).setPreferredWidth(36);
		table2.getColumnModel().getColumn(1).setPreferredWidth(36);
		table2.getColumnModel().getColumn(2).setPreferredWidth(170);
		table2.getColumnModel().getColumn(3).setPreferredWidth(90);
		table2.getColumnModel().getColumn(4).setPreferredWidth(100);
		table2.getColumnModel().getColumn(5).setPreferredWidth(100);
		table2.getColumnModel().getColumn(6).setPreferredWidth(100);
		table2.getColumnModel().getColumn(7).setPreferredWidth(100);
		table2.getColumnModel().getColumn(8).setPreferredWidth(36);
		table2.getColumnModel().getColumn(9).setPreferredWidth(36);
		table2.getColumnModel().getColumn(10).setPreferredWidth(36);
		table2.getColumnModel().getColumn(11).setPreferredWidth(100);
		table2.getColumnModel().getColumn(12).setPreferredWidth(36);
		
		pane2 = new JScrollPane(table2);
        frame.add(pane2);
        
		sc = new JFileChooser();
		FileNameExtensionFilter sfilter = new FileNameExtensionFilter(".wav file", "wav");
		sc.setFileFilter(sfilter);
		
		openButton2 = new JButton("Load .all first");
		replaceButton = new JButton("Replace With...");
		deleteButton = new JButton("Delete");
		playButton = new JButton("Play");
		loopButton = new JButton("Make Loop");
		exportButton = new JButton("Export Sample");
		
		event3 ev3 = new event3();
		openButton.addActionListener(ev3);
		
		event4 ev4 = new event4();
		openButton2.addActionListener(ev4);
		
		event5 ev5 = new event5();
		replaceButton.addActionListener(ev5);
		
		event6 ev6 = new event6();
		deleteButton.addActionListener(ev6);
		
		event7 ev7 = new event7();
		playButton.addActionListener(ev7);
		
		event8 ev8 = new event8();
		loopButton.addActionListener(ev8);
		
		event9 ev9 = new event9();
		exportButton.addActionListener(ev9);

		MouseListener tableMouseListener = new MouseAdapter() {
		      @Override
		      public void mouseClicked(MouseEvent e) {
	            row = table1.rowAtPoint(e.getPoint());//get mouse-selected row
	            col = table1.columnAtPoint(e.getPoint());//get mouse-selected col
	            currValue = getValueAt(row, col);
	            System.out.println(row + ", " + col + " is "+ currValue);
	            update(row);

	            frame.revalidate();
	            frame.repaint();
	          }
		};
		table1.addMouseListener(tableMouseListener);
	}

	public Object getValueAt(int row, int col) {
	    return sampledata[row][col];
	}
	
	public void setValueAt(Object value, int row, int col) {
	    sampledata[row][col] = value;
	    System.out.println("alter here!");
	}
	
	public class event3 implements ActionListener {
		public void actionPerformed(ActionEvent ev3) {
	        //Handle .all open button action.
	        if (ev3.getSource() == openButton) {
	            int returnVal = fc.showOpenDialog(GUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                allfile = file.getAbsolutePath();
	                allfilename = file.getName();
	                try {
	                	sampledata = new Object[981][13];
	                	for (int j=0; j < 981; j++) {
	                		Arrays.fill(sampledata[j], null);
	                	}
	                	Arrays.fill(selectedsample[0], null);
	                	frame.setVisible(false);
	                	frame.removeAll();
	                	initGUI();
						process(allfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            } else {
	            }
	        }
	    }
	}
    
	public class event4 implements ActionListener {
		public void actionPerformed(ActionEvent ev4) {
	        //Handle sample open button action.
	        if (ev4.getSource() == openButton2) {
	            int returnVal = sc.showOpenDialog(GUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = sc.getSelectedFile();
	                try {
	                	sampfile = file.getAbsolutePath();
	                	sampfilename = file.getName();
	                	loadsample(sampfile, "insert");
					} catch (IOException e) {
						e.printStackTrace();
					}
	            } else {
	            }
	        }
	    }
	 
	}
	
	public class event5 implements ActionListener {
		public void actionPerformed(ActionEvent ev5) {		 
	        //Handle sample replace button action.
	        if (ev5.getSource() == replaceButton) {
	            int returnVal = sc.showOpenDialog(GUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = sc.getSelectedFile();
	                try {
	                	sampfile = file.getAbsolutePath();
	                	sampfilename = file.getName();
	                	loadsample(sampfile, "replace");
					} catch (IOException e) {
						e.printStackTrace();
					}
	            } else {
	            }
	        }
	    }
	}
	
	public class event6 implements ActionListener {
		public void actionPerformed(ActionEvent ev6) {
	        //Handle delete sample button action.
	        if (ev6.getSource() == deleteButton) {
	        	try {
					writeAll(0, false, "delete");
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	public class event7 implements ActionListener {
		public void actionPerformed(ActionEvent ev7) {			 
	        //Handle play sample button action.
	        if (ev7.getSource() == playButton) {
	        	PlaySound();
	        }
	    }	 
	}
	
	public class event8 implements ActionListener {
		public void actionPerformed(ActionEvent ev8) {			 
	        //Handle loop sample button action.
	        if (ev8.getSource() == loopButton) {
	        	loopSample();
	        }
	    }	 
	}
	
	public class event9 implements ActionListener {
		public void actionPerformed(ActionEvent ev9) {			 
	        //Handle loop sample button action.
	        if (ev9.getSource() == exportButton) {
	        	exportSample();
	        }
	    }	 
	}
	
	public static void main (String args[]) throws IOException {
		EventQueue.invokeLater(new Runnable() {		
            @Override
            public void run() {
            	GUI gui = new GUI();
    			if (new File("e2sSample.all").isFile() == true) {
    				try {
    					allfile = "e2sSample.all";
    					allfilename = "e2sSample.all";
						process(allfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
            	}
			});

	}
	
	static String str = "";
	static int totalsamples = 0;
    
    public static void process(String allfile) throws IOException {
		openButton2.setText("Load New .WAV ...");
    	label1.setText("Current .all: " + allfilename);
    	
    	char c = 0;
    	int id = 0;
    	int index = 0;
        try (RandomAccessFile raf = new RandomAccessFile(allfile, "r")) {
        
        //reset everything
        index = 0;
        raf.seek(index);
        str = "";
        totalsamples = 0;
        	
        for (int i=0; i<14; i++) {
            id = raf.read();
            c = (char)id;
            str += c;
        }
        
        if (str.equals("e2s sample all") ) {
        	int secondsLeft = 270-(int)((raf.length()-4096)/100000);
        	System.out.println("There are " + secondsLeft + " seconds left.");
        	infotext.setText("Choose a sample to edit.");
        	freespace.setText("Seconds left: " + secondsLeft);
        }
        else {
        	System.out.println("Not a valid .all file.");
        	infotext.setText("Not a valid .all file.");
        	return;
        }
        
		 index += 88;
		 
		 raf.seek(index);
        
        for (int i=0; i<981; i++) {
        	str = "";
         for (int j=0; j<4; j++) {
             str += String.format("%02X", raf.read());
         }
         if (hex2decrev(str) == 0) {
        	 System.out.println("Slot " + (i+19) + ": No sample loaded.");
         }
         else {
         System.out.println("Slot " + (i+19) + " start: " + hex2decrev(str));
         totalsamples += 1;
         }

         sampledata[i][0]=i+19;	//store sample #
         
         //store start
         if (hex2decrev(str) > 0) {
         sampledata[i][4]=hex2decrev(str);
         }
        }
        
        System.out.println("Total samples: " + totalsamples);
        label2.setText("Total samples: " + totalsamples);
        pane1.revalidate();
        pane1.repaint();
        getNames(allfile);
        }
        
		catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void loadsample(String file, String mode) throws IOException {
    	infotext.setText("Loaded " + sampfilename);
	 	int indexs = 0;
    	String str2 = "";
    	String s = "";
    	int rifflen = 0;
    	int datalen = 0;
    	int fmtlen = 0;
    	int bitrate = 0;
    	int samplenum = 0;
    	int absnum = 0;
    	short sampleshort = 0;
    	short absshort = 0;
    	int dataint = 0;
    	boolean isStereo = false;
    	int b = 0;
    	byte[] data = null;
       	RandomAccessFile tempfile = new RandomAccessFile("temp.wav", "rw");
    	
        	try (RandomAccessFile raf = new RandomAccessFile(sampfile, "r")) {
        		System.out.println("Loaded sample.");
        		
     		 raf.seek(indexs);
     		 
     		 for (int j=0; j<4; j++) {
                  str2 += String.format("%02X", raf.read());
              }
     		 
             if (str2.equals("52494646") ) {
             	System.out.println("Valid RIFF.");
             }
             else {
             	System.out.println("Not a valid RIFF file.");
             	infotext.setText("Not a valid RIFF file.");
             	return;
             }
             
             str2 = "";
             
     		 for (int j=0; j<4; j++) {
                 str2 += String.format("%02X", raf.read());
             }
     		 
     		 //total file length
     		 rifflen = hex2decrev(str2);
           	 System.out.println("RIFF length: " + rifflen);
           	 
       		//advance index to fmt length
      		 indexs = 16;
       		 raf.seek(indexs);
       		 
             str2 = "";          
     		 for (int j=0; j<4; j++) {
                 str2 += String.format("%02X", raf.read());
             }
     		 
     		 fmtlen = hex2decrev(str2);
           	 System.out.println("fmt length: " + fmtlen);
           	 
      		//advance index to stereo
     		 indexs += 6;
      		 raf.seek(indexs);
      		 
      		 str2 = String.format("%02X", raf.read());
      		 
      		System.out.println("stereo: " + str2);
      		
      		if (str2.equals("02")) {
      			isStereo = true;
      		}

    		 indexs += 2;	//advance index to bitrate
     		 raf.seek(indexs);
     		 
             str2 = "";
             
     		 for (int j=0; j<4; j++) {
                 str2 += String.format("%02X", raf.read());
             }

     		 bitrate = hex2decrev(str2);	//total bitrate

    		 indexs = fmtlen + 24;	//advance index to wav data length 
     		 raf.seek(indexs);
    		 
             str2 = "";
             
     		 for (int j=0; j<4; j++) {
                 str2 += String.format("%02X", raf.read());
             }

     		 datalen = hex2decrev(str2);	//total wav data length

    		 indexs = 0;	//return to file start
     		 raf.seek(indexs);
     		 
     		 //write RIFF block
     		 for (int j=0; j<4; j++) {
                 b = raf.read();
                 tempfile.write(b);
                 indexs +=1;
             }

     		 rifflen = 1224 + datalen;	//get total file length
     		 
       		 indexs += 4;	//advance past length
             raf.seek(indexs);

     		tempfile.writeInt(Integer.reverseBytes(rifflen));	//write total file length
     		
     		//write wavefmt block
    		 for (int j=0; j<(8); j++) {
                 b = raf.read();
                 tempfile.write(b);
                 indexs +=1;
             }
    		 
      		//write fmtlen - always 16
                  tempfile.write(16);
                  indexs +=1;
                  raf.seek(indexs);
                  
             //write up to data length
             for (int j=0; j<(19); j++) {
                     b = raf.read();
                     tempfile.write(b);
                     indexs +=1;
                 }
             
             indexs += fmtlen-16;
             raf.seek(indexs);

             //copy the rest of the wav data
             FileChannel inChannel = raf.getChannel();
             FileChannel outChannel = tempfile.getChannel();

             inChannel.transferTo(indexs, (datalen+8), outChannel);
     		 
     		 indexs += datalen+8;
             raf.seek(indexs);
     		
     		// write initial korg block data
     		s = "6B6F72679C04000065736C6994040000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write unit sample number
     		samplenum = (int)selectedsample[0][0]-1;
     		sampleshort = Short.reverseBytes((short)samplenum);
     		tempfile.writeShort(sampleshort);
     		
     		//write sample name
     		s = sampfilename.replaceFirst("[.][^.]+$", "");
     		int namelen = s.length();
     		if (namelen > 16) {
     		    s = s.substring(0, 16);
        	}
     		tempfile.writeBytes(s);
     		
     		//store the name in the table
     		sampledata[samplenum-18][2] = s;
     		
            //pad the name with 0s
    		 for (int j=0; j<(16-namelen); j++) {
                tempfile.write(0);
            }

     		//write category
     		s = "1100";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//store the category
     		sampledata[samplenum-18][3] = "User";
 		
     		//write absolute sample number
     		absnum = samplenum+50;
     		absshort = Short.reverseBytes((short)absnum);
     		tempfile.writeShort(absshort);
     		//store the abs #
        	sampledata[samplenum-18][1] = absnum;
     		
     		//write middle string
     		s = "0000007F0001000000000000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write no clue
     		s = "783DFFFF";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write some zeros
     		s = "000000000000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);

     		//write loop point
     		dataint = Integer.reverseBytes(datalen-2);
     		tempfile.writeInt(dataint);
     		//store loop point
     		sampledata[samplenum-18][6] = datalen-2;
     		
     		//write end point
     		dataint = Integer.reverseBytes(datalen-2);
     		tempfile.writeInt(dataint);
     		//store end point
     		sampledata[samplenum-18][7] = datalen-2;

     		//write one shot
     		s = "0100";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		//store the oneshot/loop
     		s = "01";
     		sampledata[samplenum-18][8] = hex2dec(s);
     		
     		//write more zeros
     		s = "000000000000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write data length
     		dataint = Integer.reverseBytes(datalen);
     		tempfile.writeInt(dataint);
     		//store length
     		sampledata[samplenum-18][5] = datalen;

     		//write a 01
     		s = "01";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write stereo	
     		if (isStereo == false) {
     			s = "00";
     		}
     		if (isStereo == true) {
     			s = "01";
     		}
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//store stereo
     		sampledata[samplenum-18][9] = hex2dec(s);
     		
     		//write loudness
     		s = "00";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		//store loudness
     		sampledata[samplenum-18][10] = hex2dec(s);
     		
     		//write end string
     		s = "01B0040000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		
     		//write sample rate
     		dataint = Integer.reverseBytes(bitrate);
     		tempfile.writeInt(dataint);
     		//store sample rate
     		sampledata[samplenum-18][11] = bitrate;

     		//write sample tune
     		s = "0000";
     		data = hexStringToByteArray(s);
     		tempfile.write(data);
     		//store sample tune
     		sampledata[samplenum-18][12] = hex2dec(s);
     		
     		//write sample number again
     		tempfile.writeShort(sampleshort);
     		
     		//write stripe data
     		s = "00";
   		 for (int j=0; j<(1092); j++) {
      		data = hexStringToByteArray(s);
      		tempfile.write(data);
         }
     		
           	tempfile.close();
    		
           	writeAll(rifflen, isStereo, mode);
           	
        	return;
        	}
		catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void writeAll(int rifflen, boolean isStereo, String mode) throws IOException {
    	String tempfile = "temp.wav";
    	String tempall = "tempall.all";
		int indexo = 0;
		int indexs = 0;
		int indexn = 0;
		int sampnum = 0;
		int prevstart = 0;
		int prevlen = 0;
		int nextstart = 0;
		int nextlen = 0;
		int nextsamp = 0;
		int newsamplen = 0;
		int startdiff = 0;
		int b = 0;
		int newstart = 0;
		int dataint = 0;
		String s = "";
		byte[] data = null;
		RandomAccessFile newall = new RandomAccessFile(tempall, "rw");
    	
		//if deleting, make a fake temp.wav
		if (mode.equals("delete")) {
			File file = new File("temp.wav");
			file.createNewFile();
			rifflen = (int)selectedsample[0][5];
            }
		
       	RandomAccessFile sample = new RandomAccessFile(tempfile, "r");
    	
        	try (RandomAccessFile oldall = new RandomAccessFile(allfile, "rw")) {
        		
        	sampnum = (int)selectedsample[0][0];	//get index of selected slot
        	
			oldall.seek(indexo);
			sample.seek(indexs);
			newall.seek(indexn);

			if (mode.equals("insert")) {
            for (int j=0; j<12+(sampnum*4); j++) {
                b = oldall.read();
                newall.write(b);
            }
			
            //get sample insert point (previous existing sample start + its length)      
            for (int j=sampnum; j>=19; j--) {
            	if (sampledata[j-19][4] != null) {
            		prevstart = (int)sampledata[j-19][4];
            		prevlen = (int)sampledata[j-19][5];
            		break;
            	}
            	else {
            		prevstart = 2864;
            		prevlen = 0;
            	}
            }
        	
        	newstart = prevstart + prevlen + 1232;
        	//write new sample start index
     		dataint = Integer.reverseBytes(newstart);
     		newall.writeInt(dataint);     		
        	sampledata[sampnum-19][4] = newstart;
     		
     		//write out following indices
            for (int j=sampnum+1; j<980; j++) {
            	if (sampledata[j-19][4] != null) {
            		dataint = (int)sampledata[j-19][4];
            		dataint = dataint + rifflen + 8;
            		dataint = Integer.reverseBytes(dataint);
            		newall.writeInt(dataint);
            	}
            	else {
            		dataint = 0;
            		dataint = Integer.reverseBytes(dataint);
            		newall.writeInt(dataint);
            	}
            }
            
            //fill in 164 00s
            for (int j=0; j<164; j++) {
     		s = "00";
     		data = hexStringToByteArray(s);
     		newall.write(data);
            }
            
			} //end insert index stuff
			
			// if replacing, just copy over original index
			if (mode.equals("replace")) {
	            for (int j=0; j<4096; j++) {
	                b = oldall.read();
	                newall.write(b);
	            }
	            newstart = (int)selectedsample[0][4];	            
			}
			
			// if deleting, just copy over original index
			if (mode.equals("delete")) {
	            for (int j=0; j<4096; j++) {
	                b = oldall.read();
	                newall.write(b);
	            }
	            newstart = (int)selectedsample[0][4];	            
			}

            oldall.seek(4096);	//move up to after indices
            
            FileChannel inChannel = oldall.getChannel();
            FileChannel outChannel = newall.getChannel();
            FileChannel sampChannel = sample.getChannel();
            
            ByteBuffer buf = ByteBuffer.allocate(48);
            inChannel.position(4096);
            outChannel.position(4096);
            
            // copy from end of index to start of new sample
            int bytesWritten = 4096;
            // long byteCount = inChannel.size();
            long byteCount = newstart;
            while (bytesWritten < byteCount) {
              bytesWritten += inChannel.transferTo(bytesWritten, byteCount - bytesWritten, outChannel);
            }
                      
            //check for following sample if inserting
            if (mode.equals("insert")) {
            for (int j=sampnum+1; j<981; j++) {
            	if (sampledata[j-19][4] != null) {
            		nextstart = (int)sampledata[j-19][4];
            		nextlen = (int)sampledata[j-19][5];
            		//move write position to next sample
            		 outChannel.position(nextstart);
            		 nextsamp = j-19;
            		 sampledata[j-19][4] = (nextstart);
            		break;
            	}
            }
            }
            
            //check for following sample if replacing
            if (mode.equals("replace")) {
            for (int j=sampnum+1; j<981; j++) {
            	if (sampledata[j-19][4] != null) {
            		nextstart = (int)sampledata[j-19][4];
            		startdiff = rifflen - (int)selectedsample[0][5] - 1224;
            		break;
            	}
            }
            }
            
            //check for following sample if deleting
            if (mode.equals("delete")) {
            for (int j=sampnum+1; j<981; j++) {
            	if (sampledata[j-19][4] != null) {
            		nextstart = (int)sampledata[j-19][4];
            		nextsamp = j-19;
            		break;
            	}
            }
            }
            
            //copy in temp.wav if inserting or replacing
            if (mode.equals("insert") || mode.equals("replace")) {
            	while (sampChannel.read(buf) >= 0 || buf.position() > 0)
            	{
            		buf.flip();
            		outChannel.write(buf);
            		buf.compact();
            	}
            }
            
            if (mode.equals("delete")) {
            	//make index of selected 0
            	newall.seek(12+(sampnum*4));
         		dataint = 0;
         		newall.writeInt(dataint);
            }
            
            //if there are any other samples, copy in and adjust index
            if (nextstart > 0) {
          //move read position to next sample and copy in remainder of old
            	if (mode.equals("insert") || mode.equals("replace")) {
            inChannel.position(nextstart);
            	}
            	if (mode.equals("delete")) {
            		inChannel.position(nextstart);
            		outChannel.position(outChannel.size());
            	}
            while (inChannel.read(buf) >= 0 || buf.position() > 0)
            {
              buf.flip();
              outChannel.write(buf);
              buf.compact();
            }
            
            if (mode.equals("insert")) {
            //make nextstart current start
            sampledata[sampnum-19][4] = nextstart;
    		newall.seek(88+((sampnum-19)*4));
     		dataint = Integer.reverseBytes(nextstart);
     		newall.writeInt(dataint);
            
            //now rewrite following indexes
            for (int j=sampnum+1; j<981; j++) {
            	if (sampledata[j-19][4] != null) {
            		newall.seek(88+((j-19)*4));
             		dataint = Integer.reverseBytes(((int)sampledata[j-19][4]+rifflen+8));
             		newall.writeInt(dataint);
             		sampledata[j-19][4] = ((int)sampledata[j-19][4]+rifflen+8);
            	}
            }
            }
            
            if (mode.equals("replace")) {
            //make nextstart current start and rewrite following indexes
            for (int j=sampnum+1; j<980; j++) {
            	if (sampledata[j-19][4] != null) {
            		newall.seek(88+((j-19)*4));
             		dataint = Integer.reverseBytes(((int)sampledata[j-19][4]+startdiff));
             		newall.writeInt(dataint);
             		sampledata[j-19][4] = ((int)sampledata[j-19][4]+startdiff);
            	}
            }
            }
            
            if (mode.equals("delete")) {
            //rewrite following indexes
            for (int j=sampnum+1; j<980; j++) {
            	if (sampledata[j-19][4] != null) {
            		newall.seek(88+((j-19)*4));
             		dataint = Integer.reverseBytes(((int)sampledata[j-19][4]-rifflen-1232));
             		newall.writeInt(dataint);
             		sampledata[j-19][4] = ((int)sampledata[j-19][4]-rifflen-1232);
            	}
            }
            }
            
            } // end of block if following samples
                     
            //if deleting, blank table of selected sample
            if (mode.equals("delete")) {   
            sampledata[sampnum-19][1] = null;
            sampledata[sampnum-19][2] = null;
            sampledata[sampnum-19][3] = null;
            sampledata[sampnum-19][4] = null;
            sampledata[sampnum-19][5] = null;
            sampledata[sampnum-19][6] = null;
            sampledata[sampnum-19][7] = null;
            sampledata[sampnum-19][8] = null;
            sampledata[sampnum-19][9] = null;
            sampledata[sampnum-19][10] = null;
            sampledata[sampnum-19][11] = null;
            sampledata[sampnum-19][12] = null;
            }

            //update freespace    
        	int secondsLeft = 270-(int)((newall.length()-4096)/100000);
        	freespace.setText("Seconds left: " + secondsLeft);
            
           	oldall.close();
           	newall.close();
           	sample.close();
           	
           	inChannel.close(); 
           	outChannel.close();
           	sampChannel.close();
           	
            //delete .all file
            File f = new File(allfile);
            f.delete();
            
            //delete sample file
            f = new File(tempfile);
            f.delete();
            
            //rename file
            File file = new File(tempall);
            File file2 = new File(allfile);
            file.renameTo(file2);
            
            //add to total samples if inserting
            if (mode.equals("insert")) {
            totalsamples += 1;
            }
            
            //subtract from total samples if deleting
            if (mode.equals("delete")) {
            totalsamples -= 1;
            }
            
            label2.setText("Total samples: " + totalsamples);

            pane1.revalidate();
            pane1.repaint();
            
            update(row);
           	
            if (mode.equals("insert") || mode.equals("replace")) {
            infotext.setText("Finished import of " + sampledata[sampnum-19][2] + ".wav");
            }
            
            if (mode.equals("delete")) {
            infotext.setText("Sample deleted.");
            }
    		 
        		return;
        	}
		catch (IOException e) {
			e.printStackTrace();
		}
    }
     
    private static void update(int row) {
    	 if (row > 481) {
         	if ( sampledata[row][4] == null) {
         		System.out.println("user row, no sample");
         		frame.add(openButton2);
         		frame.remove(playButton);
         		frame.remove(replaceButton);
         		frame.remove(deleteButton);
         		frame.remove(loopButton);
         		frame.remove(exportButton);
         	}
         	else {
         		System.out.println("user row, sample");
         		frame.remove(openButton2);
         		frame.add(playButton);
         		frame.add(replaceButton);
         		frame.add(deleteButton);
         		frame.add(loopButton);
         		frame.add(exportButton);
         		
         		if ((int)sampledata [row][8] == 0) {
         			loopButton.setText("Unloop");
         		}
         		else {
         			loopButton.setText("Make Loop");
         		}
         		
         	}
         	frame.revalidate();
         	frame.repaint();
         }
         else {
         	if ( sampledata[row][4] == null) {
         		System.out.println("factory row, no sample");
         		frame.remove(openButton2);
         		frame.remove(replaceButton);
         		frame.remove(playButton);
         		frame.remove(deleteButton);
         		frame.remove(loopButton);
         		frame.remove(exportButton);
         	}
         	else {
         		System.out.println("factory row, sample");
         		frame.remove(openButton2);
         		frame.remove(replaceButton);
         		frame.add(playButton);
         		frame.add(deleteButton);
         		frame.remove(loopButton);
         		frame.add(exportButton);
         	}
         	frame.revalidate();
         	frame.repaint();
         }
    	 
    	    selectedsample[0][0] = sampledata[row][0];
    	    selectedsample[0][1] = sampledata[row][1];
    	    selectedsample[0][2] = sampledata[row][2];
    	    selectedsample[0][3] = sampledata[row][3];
    	    selectedsample[0][4] = sampledata[row][4];
    	    selectedsample[0][5] = sampledata[row][5];
    	    selectedsample[0][6] = sampledata[row][6];
    	    selectedsample[0][7] = sampledata[row][7];
    	    selectedsample[0][8] = sampledata[row][8];
    	    selectedsample[0][9] = sampledata[row][9];
    	    selectedsample[0][10] = sampledata[row][10];
    	    selectedsample[0][11] = sampledata[row][11];
    	    selectedsample[0][12] = sampledata[row][12];
    	    
    	    pane2.revalidate();
    	    pane2.repaint();	 
    }

	public static void getNames(String allfile) throws IOException {
    	label1.setText("Current .all: " + allfilename);
    	int index = 0;
    	int datalength = 0;
    	int bitrate = 0;
    	String str2 = "";
        try (RandomAccessFile raf = new RandomAccessFile(allfile, "r")) {
		 
        for (int i=0; i<981; i++) {
        	if (sampledata[i][4] != null ) {
        		
		 index = (int)sampledata[i][4];	//advance index to RIFF (start)

		 index += 24; 	//advance index to bitrate
		 raf.seek(index);
		 
		 for (int j=0; j<4; j++) {
             str2 += String.format("%02X", raf.read());
         }
		 
	     bitrate = hex2decrev(str2);
         sampledata[i][11]=bitrate;	//store the bitrate in the table
		 
		 index += 16;	//advance index to data length 
		 raf.seek(index);
		 
		 for (int j=0; j<4; j++) {
             str2 += String.format("%02X", raf.read());
         }
	     
	     datalength = hex2decrev(str2);
         sampledata[i][5]=datalength;	//store the length in the table

	     index +=4;	//advance past data

	     index += datalength;	//advance to korg block

	     index += 18; //advance to sample name
	     raf.seek(index);
	     str2 = "";
	     
		 for (int j=0; j<16; j++) {
             str2 += String.format("%02X", raf.read());
         }
		 
		 str2 = hextoascii(str2);
         sampledata[i][2]=str2;	//store the name in the table
         
         //read the category number
		 str2 = String.format("%02X", raf.read()); 
		 str2 = getCat(str2);
 
		 //store the category in the table
		 sampledata[i][3]=str2;
		 
		 //advance to absolute#
	     index += 18;
	     raf.seek(index);
	     str2 = "";
	     
		 for (int j=0; j<4; j++) {
             str2 += String.format("%02X", raf.read());
         }
		 //store the absolute sample #
		 sampledata[i][1]=hex2decrev(str2);
         
		 //advance to loop point
	     index += 24;
	     raf.seek(index);
	     str2 = "";
	     
		 for (int j=0; j<4; j++) {
             str2 += String.format("%02X", raf.read());
         }
		 //store the loop point
		 sampledata[i][6]=hex2decrev(str2);
		 
	     //read end point
		 str2 = "";
		 for (int j=0; j<4; j++) {
             str2 += String.format("%02X", raf.read());
         }
		 //store the end point
		 sampledata[i][7]=hex2decrev(str2);
        
	     //read one shot / loop
         str2 = String.format("%02X", raf.read());
		 //store the oneshot/loop
		 sampledata[i][8]=hex2dec(str2);
		 
		 //advance to stereo/mono
	     index += 21;
	     raf.seek(index);
	     //read stereo/mono
         str2 = String.format("%02X", raf.read());
		 //store the stereo/mono
		 sampledata[i][9]=hex2dec(str2);
		 
		 //read loudness
         str2 = String.format("%02X", raf.read());
		 //store the loudness
		 sampledata[i][10]=hex2dec(str2);
		 
		 //advance to tune
	     index += 12;
	     raf.seek(index);
	     //read tune
         str2 = String.format("%02X", raf.read());
		 //store the tune
         int s = Short.valueOf(str2,16).byteValue();
         if (s > 129) { s = (short)(130 - s); }
		 sampledata[i][12]=s; 
       }
        	
        }
        pane1.revalidate();
        pane1.repaint();
        }
        
		catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static String hextoascii(String s) {
    	String output = "";
        for (int i = 0; i < s.length(); i+=2) {
            String str = s.substring(i, i+2);
            output = output += ((char)Integer.parseInt(str, 16));
        }
        return output;
    }
    
    public static int hex2dec(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    public static int hex2decrev(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        val = Integer.reverseBytes(val);
        return val;
    }
    
    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    static String getCat(String catNum) {    
	 if (catNum.equals("02")) {return "Kick";}
	 if (catNum.equals("03")) {return "Snare";}
	 if (catNum.equals("04")) {return "Clap";}
	 if (catNum.equals("05")) {return "HiHat";}
	 if (catNum.equals("06")) {return "Cymbal";}
	 if (catNum.equals("07")) {return "Hits";}
	 if (catNum.equals("08")) {return "Shots";}
	 if (catNum.equals("09")) {return "Voice";}
	 if (catNum.equals("0A")) {return "SE";}
	 if (catNum.equals("0B")) {return "FX";}
	 if (catNum.equals("0C")) {return "Tom";}
	 if (catNum.equals("0D")) {return "Perc.";}
	 if (catNum.equals("0E")) {return "Phrase";}
	 if (catNum.equals("0F")) {return "Loop";}
	 if (catNum.equals("10")) {return "PCM";}
	 if (catNum.equals("11")) {return "User";}
	 return "Error";
    }
    
    static String getCatNum(String cat) {    
	 if (cat.equals("Kick")) {return "02";}
	 if (cat.equals("Snare")) {return "03";}
	 if (cat.equals("Clap")) {return "04";}
	 if (cat.equals("HiHat")) {return "05";}
	 if (cat.equals("Cymbal")) {return "06";}
	 if (cat.equals("Hits")) {return "07";}
	 if (cat.equals("Shots")) {return "08";}
	 if (cat.equals("Voice")) {return "09";}
	 if (cat.equals("SE")) {return "0A";}
	 if (cat.equals("FX")) {return "0B";}
	 if (cat.equals("Tom")) {return "0C";}
	 if (cat.equals("Perc.")) {return "0D";}
	 if (cat.equals("Phrase")) {return "0E";}
	 if (cat.equals("Loop")) {return "0F";}
	 if (cat.equals("PCM")) {return "10";}
	 if (cat.equals("User")) {return "11";}
	 return "Error";
    }
    
    static void PlaySound() {
    	try {
    		int sampstart = 0;
    		int samplen = 0;
    		File file = new File("playtemp.wav");
    		
    		if (file.isFile() == true) {
    			file.delete();
    		}
	
    		file.createNewFile();	
    		
    		RandomAccessFile all = new RandomAccessFile(allfile, "r"); 
    		RandomAccessFile sample = new RandomAccessFile(file, "rw");
    		
    		sampstart = (int)selectedsample[0][4];
    		samplen = (int)selectedsample[0][5]+1232;

            FileChannel inChannel = all.getChannel();
            FileChannel outChannel = sample.getChannel();

            inChannel.transferTo(sampstart, samplen, outChannel);

           	all.close();
           	sample.close();
           	
            inChannel.close();
            outChannel.close();
    		
    		Clip clip = AudioSystem.getClip();
    		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
    		clip.open(stream);
    		clip.start();
    		
    		Thread.sleep(clip.getMicrosecondLength()/1000);
    		
            clip.drain();
            clip.close();
            
            stream.close();
            stream = null;
            file = null;
            
    	} catch (Exception e) {
    	}
    	System.gc();
		File file = new File("playtemp.wav");
    	file.delete();
    }
    
    static void loopSample() {
    	try { 		
    		if ((int)sampledata [row][8] == 0) {
    			
	     		Short s = 1;
	     		Integer i = ((int)sampledata[row][5]-2);
	     		
				RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
	     		
				raf.seek(104 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
	     		raf.writeInt(Integer.reverseBytes(i));		
				raf.seek(112 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     	raf.write(s);
		    	raf.close();
    			
    			sampledata[row][8] = 1;
    			sampledata[row][6] = ((int)sampledata[row][5]-2);
    			selectedsample[0][8] = 1;
    			selectedsample[0][6] = ((int)sampledata[row][5]-2);
    			infotext.setText("Sample will not loop.");
     			loopButton.setText("Make Loop");
    		}
    		else {
	     		Short s = 0;
	     		Integer i = 0;
				RandomAccessFile raf = new RandomAccessFile(allfile, "rw");
				
				raf.seek(104 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
	     		raf.writeInt(Integer.reverseBytes(i));		
				raf.seek(112 + ((int)sampledata[row][4]) + ((int)sampledata[row][5]));
		     	raf.write(s);
		     	
		    	raf.close();

    			sampledata[row][8] = 0;
    			sampledata[row][6] = 0;
    			selectedsample[0][8] = 0;
    			selectedsample[0][6] = 0;
    			infotext.setText("Sample will now loop.");
     			loopButton.setText("Unloop");

    		}
            pane2.revalidate();
            pane2.repaint();
			pane1.revalidate();
			pane1.repaint();

    	} catch (Exception e) {
    	}
    }

static void exportSample() {
	try {
		int sampstart = 0;
		int samplen = 0;
		String exportname = (String)selectedsample[0][2];
		exportname = exportname.replaceAll("[^\\x20-\\x7e]", "");
		exportname = exportname.concat(".wav");
		System.out.println(exportname);
		File file = new File(exportname);
		
		if (file.isFile() == true) {
			file.delete();
		}

		file.createNewFile();	
		
		RandomAccessFile all = new RandomAccessFile(allfile, "r"); 
		RandomAccessFile sample = new RandomAccessFile(file, "rw");
		
		sampstart = (int)selectedsample[0][4];
		samplen = (int)selectedsample[0][5]+1232;

        FileChannel inChannel = all.getChannel();
        FileChannel outChannel = sample.getChannel();

        inChannel.transferTo(sampstart, samplen, outChannel);

       	all.close();
       	sample.close();
       	
        inChannel.close();
        outChannel.close();
		
        file = null;
        
	} catch (Exception e) {
	}
}

}