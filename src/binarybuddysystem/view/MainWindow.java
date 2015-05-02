package binarybuddysystem.view;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import binarybuddysystem.MMU;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class MainWindow extends JFrame implements ActionListener
{
	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			System.err.println("Unable to set system Look & Feel");
		}
	}
	
	private MMU memory;
	
	private JScrollPane mvScroll;
	private MemoryView mv;
	private JScrollPane pvScroll;
	private ProcessView pv;
	
	private JPanel detailPanel;
	
	private JButton addBtn = new JButton("Add Process");
	private JButton rmBtn = new JButton("Remove Process");
	
	private JLabel pNameLabel = new JLabel("Process Name: ");
	private JTextField pName = new JTextField(16);
	private JLabel pSizeLabel = new JLabel("Process Size: ");
	private JFormattedTextField pSize = new JFormattedTextField();
	
	private int blockSize;
	
	private int[] colors = {0xFF0000, 0xFF8800, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF};
	
	public MainWindow(boolean auto, int memSize, int blkSize)
	{
		memory = new MMU(memSize, blkSize);
		blockSize = blkSize;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(1024, 480);
		setTitle("MMU View Window || Memory Size: " + memSize + " | Min Chunk Size: " + blkSize);
		
		mv = new MemoryView(memSize/blkSize);
		pv = new ProcessView(memSize/blkSize);
		detailPanel = new JPanel();
		detailPanel.setPreferredSize(new Dimension(600, 30));
		mvScroll = new JScrollPane(mv, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pvScroll = new JScrollPane(pv, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mvScroll.setPreferredSize(mv.getPreferredSize());
		pvScroll.setPreferredSize(pv.getPreferredSize());
		add(pvScroll, BorderLayout.CENTER);
		add(mvScroll, BorderLayout.NORTH);
		
		DefaultFormatterFactory format = new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("########")));
		
		pSize.setFormatterFactory(format);
		pSize.setColumns(8);
		
		if(!auto)
		{
			add(detailPanel, BorderLayout.SOUTH);
			addBtn.setActionCommand("AddProcess");
			addBtn.addActionListener(this);
			
			rmBtn.setActionCommand("RemoveProcess");
			rmBtn.addActionListener(this);
			
			detailPanel.add(pNameLabel);
			detailPanel.add(pName);
			detailPanel.add(pSizeLabel);
			detailPanel.add(pSize);
			detailPanel.add(addBtn);
			detailPanel.add(rmBtn);
		}
		
		setPreferredSize(new Dimension(660, mv.getPreferredSize().height + 115));
		setMinimumSize(new Dimension(600, mv.getPreferredSize().height + 90));
		
		pack();
		revalidate();
	}

	public MainWindow(boolean auto, int memSize, int blkSize, int colorSize)
	{
		memory = new MMU(memSize, blkSize);
		blockSize = blkSize;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(1024, 480);
		setTitle("MMU View Window || Memory Size: " + memSize + " | Min Chunk Size: " + blkSize);
		
		mv = new MemoryView(memSize/blkSize, colorSize);
		pv = new ProcessView(memSize/blkSize);
		mvScroll = new JScrollPane(mv, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pvScroll = new JScrollPane(pv, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mvScroll.setPreferredSize(mv.getPreferredSize());
		add(pvScroll, BorderLayout.CENTER);
		add(mvScroll, BorderLayout.NORTH);
		
		DefaultFormatterFactory format = new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("########")));
		
		pSize.setFormatterFactory(format);
		pSize.setColumns(8);
		
		if(!auto)
		{
			add(detailPanel, BorderLayout.SOUTH);
			pName.setText("Process Name");
			pSize.setText("Process Size");
			addBtn.setActionCommand("AddProcess");
			addBtn.addActionListener(this);
			
			rmBtn.setActionCommand("RemoveProcess");
			rmBtn.addActionListener(this);
			
			detailPanel.add(pName);
			detailPanel.add(pSize);
			detailPanel.add(addBtn);
			detailPanel.add(rmBtn);
		}
		
		setPreferredSize(new Dimension(660, mv.getPreferredSize().height + 115));
		setMinimumSize(new Dimension(600, mv.getPreferredSize().height + 90));
		
		pack();
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("AddProcess"))
		{
			try
			{
				if(!allocate(pName.getText(), Integer.parseInt(pSize.getText())))
				{
					//Replace with Dialog
					System.err.println("Unable to allocate");
				}
			}
			catch(NumberFormatException e1)
			{
				//Replace with Dialog
				System.err.println("Size must be a number");
			}
		}
		else if(e.getActionCommand().equals("RemoveProcess"))
		{
			if(!deallocate(pName.getText()))
			{
				//Replace with Dialog
				System.err.println("Unable to deallocate");
			}
		}
	}
	
	public boolean allocate(String name, int size)
	{
		int[] result = memory.allocate(name, size);
		
		if(result != null)
		{
			Block b = new Block(name, colors[result[0] % colors.length], result[1]);
			mv.addProcess(b, result[0]);
			pv.addProcess(b, result[0]);
			pv.revalidate();
			pv.repaint();
		}
		else
		{
			System.err.println("Failed to allocate - Name: \"" + name + "\"\tSize: " + size);
			return false;
		}
		
		return true;
	}
	
	public boolean deallocate(String name)
	{
		if(memory.getProcess(name) != null)
		{
			int index = mv.getProcessLoc(name);
			
			if(index != -1)
			{
				memory.deallocate(name);
				mv.removeProcess(index);
				pv.removeProcess(index);
			}
			else
			{
				System.err.println("Failed to deallocate");
				return false;
			}
			
			return true;
		}
		else
		{
			System.err.println("Failed to deallocate: Invalid name");
			return false;
		}
	}
}
