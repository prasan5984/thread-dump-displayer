package main;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import src.extensions.JSplitPaneExtend;
import src.extensions.ThreadDumpCellRenderer;
import src.extensions.ThreadDumpHeaderRenderer;
import src.extensions.ThreadDumpTableModel;
import datastructure.Session;
import datastructure.entity.Log;
import datastructure.entity.MyThread;
import datastructure.section.Section;

public class ThreadDumpDisplayer implements ActionListener
{
	private JButton						openBtn, removeBtn, selectBtn, deselectBtn, displayTableBtn;
	private static JFileChooser			fileChooser		= new JFileChooser();
	private JFrame						startFrame;
	private JPanel						startPanel, buttonPanel, logListPanel;
	private JScrollPane					buttonPane, logDetailPane, logListPane;
	private JSplitPaneExtend			splitPaneLevel1, splitPaneLevel2;
	private GridBagConstraints			logListConstraint;
	private HashMap< JCheckBox, Log >	chkBoxLogMap	= new HashMap< JCheckBox, Log >();
	private Session						session;
	private HashMap< JCheckBox, File >	chkBoxFileMap	= new HashMap< JCheckBox, File >();

	private void initialize()
	{
		session = new Session();
	}

	public void showUI()
	{

		startFrame = new JFrame( "Thread Dump Displayer" );

		startPanel = new JPanel( new GridBagLayout() );

		startFrame.setLocationRelativeTo( null );
		startFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		startFrame.setSize( 500, 500 );
		//startFrame.add( startPanel );

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;

		JTextPane textPane = new JTextPane();
		textPane.setEditable( false );
		StyledDocument doc = textPane.getStyledDocument();

		try
		{
			Style s = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
			StyleConstants.setFontFamily( s, "Times" );
			StyleConstants.setFontSize( s, 13 );
			StyleConstants.setBold( s, true );
			StyleConstants.setItalic( s, true );
			StyleConstants.setAlignment( s, StyleConstants.ALIGN_CENTER );

			doc.insertString( doc.getLength(), " Welcome to Thread Dump Analyzer", s );

			StyleConstants.setBold( s, false );
			StyleConstants.setAlignment( s, StyleConstants.ALIGN_LEFT );

			doc.insertString( doc.getLength(), "\nThis application is for anlaysing HotSpot's thread dumps", s );

		}
		catch ( BadLocationException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startPanel.add( textPane );
		logDetailPane = new JScrollPane( startPanel );

		buttonPanel = new JPanel( new GridBagLayout() );

		GridBagConstraints listPaneConstraint = new GridBagConstraints();
		listPaneConstraint.fill = GridBagConstraints.HORIZONTAL;

		openBtn = new JButton( "Open Log File" );
		openBtn.addActionListener( this );
		buttonPanel.add( openBtn, listPaneConstraint );

		listPaneConstraint.gridy = 2;
		removeBtn = new JButton( "Remove" );
		removeBtn.setEnabled( false );
		removeBtn.addActionListener( this );
		buttonPanel.add( removeBtn, listPaneConstraint );

		listPaneConstraint.gridy = 3;
		selectBtn = new JButton( "Select All" );
		selectBtn.setEnabled( false );
		selectBtn.addActionListener( this );
		buttonPanel.add( selectBtn, listPaneConstraint );

		listPaneConstraint.gridy = 4;
		deselectBtn = new JButton( "Deselect All" );
		deselectBtn.setEnabled( false );
		deselectBtn.addActionListener( this );
		buttonPanel.add( deselectBtn, listPaneConstraint );

		listPaneConstraint.gridy = 5;
		displayTableBtn = new JButton( "Display Table" );
		displayTableBtn.setEnabled( false );
		displayTableBtn.addActionListener( this );
		buttonPanel.add( displayTableBtn, listPaneConstraint );

		buttonPane = new JScrollPane( buttonPanel );

		logListPanel = new JPanel();
		logListPanel.setLayout( new BoxLayout( logListPanel, BoxLayout.PAGE_AXIS ) );

		logListPane = new JScrollPane( logListPanel );

		logListConstraint = new GridBagConstraints();
		logListConstraint.fill = GridBagConstraints.HORIZONTAL;

		splitPaneLevel2 = new JSplitPaneExtend( JSplitPane.VERTICAL_SPLIT, logListPane, buttonPane );
		splitPaneLevel2.setResizeWeight( 0.95 );

		splitPaneLevel1 = new JSplitPaneExtend( JSplitPane.HORIZONTAL_SPLIT, logDetailPane, splitPaneLevel2 );
		splitPaneLevel1.setResizeWeight( 0.85 );

		startFrame.setContentPane( splitPaneLevel1 );
		startFrame.setExtendedState( JFrame.MAXIMIZED_BOTH );
		startFrame.setVisible( true );
	}

	public static void main( String[] args )
	{
		final ThreadDumpDisplayer displayer = new ThreadDumpDisplayer();
		displayer.initialize();

		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				displayer.showUI();
			}
		} );

	}

	private JCheckBox createCheckBoxes( String name )
	{
		JCheckBox chkBox = new JCheckBox( name );
		return chkBox;
	}

	private boolean enableButtons()
	{
		boolean enableFl = false;
		for ( JButton btn : new JButton[] { selectBtn, deselectBtn, removeBtn, displayTableBtn } )
			if ( !btn.isEnabled() )
			{
				btn.setEnabled( true );
				if ( !enableFl )
					enableFl = true;
			}
		return enableFl;
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		boolean repaintFl = false;

		if ( e.getSource().equals( openBtn ) )
		{
			int state = fileChooser.showOpenDialog( startFrame );

			if ( state == JFileChooser.APPROVE_OPTION )
			{
				File logFile = fileChooser.getSelectedFile();
				if ( !logFile.exists() )
				{
					showErrorDialog( "File " + logFile + "does not exist" );
					return;
				}

				if ( chkBoxFileMap.values().contains( logFile ) )
				{
					showErrorDialog( "File " + logFile + " already chosen" );
					return;
				}

				Log logEntity = session.processFile( logFile );
				JCheckBox chkBox = createCheckBoxes( logEntity.filename );
				chkBox.doClick();

				chkBoxLogMap.put( chkBox, logEntity );
				getSelectedLogList();

				logListPanel.add( chkBox );
				
				chkBoxFileMap.put( chkBox, logFile );
								
				logListPanel.revalidate();

				if ( enableButtons() )
					buttonPane.revalidate();
			}
		}

		else if ( e.getSource().equals( removeBtn ) )
		{
			//TODO: create a dialog box for warning

			for ( Entry< JCheckBox, Log > chkBoxEntry : chkBoxLogMap.entrySet() )
			{
				if ( chkBoxEntry.getKey().isSelected() )
				{
					logListPanel.remove( chkBoxEntry.getKey() );
					chkBoxEntry.getValue().removeLog();
					chkBoxFileMap.remove( chkBoxEntry.getKey() );
					chkBoxLogMap.remove( chkBoxEntry.getKey() );

					if ( !repaintFl )
						repaintFl = true;
				}
			}

			getSelectedLogList();
		}
		else if ( e.getSource().equals( selectBtn ) )
		{
			for ( JCheckBox chkBox : chkBoxLogMap.keySet() )
				if ( !chkBox.isSelected() )
					chkBox.doClick();
		}
		else if ( e.getSource().equals( deselectBtn ) )
		{
			for ( JCheckBox chkBox : chkBoxLogMap.keySet() )
				if ( chkBox.isSelected() )
					chkBox.doClick();
		}
		else if ( e.getSource().equals( displayTableBtn ) )
		{
			getSelectedLogList();
		}

		if ( repaintFl )
		{
			//TODO: Screen not refreshed. Check! 
			logListPanel.revalidate();
			//startFrame.validate();

		}

	}

	private void getSelectedLogList()
	{
		ArrayList< Log > selectedLogList = new ArrayList< Log >();
		Iterator< Entry< JCheckBox, Log >> iterator = chkBoxLogMap.entrySet().iterator();
		while ( iterator.hasNext() )
		{
			Entry< JCheckBox, Log > entry = iterator.next();
			if ( entry.getKey().isSelected() )
				selectedLogList.add( entry.getValue() );
		}

		if ( selectedLogList.size() > 0 )
			displayTable( selectedLogList );
		
		else
		{
			startPanel.removeAll();
			startPanel.repaint();
		}

	}

	private void displayTable( ArrayList< Log > logList )
	{
		ArrayList< Section > secList = new ArrayList< Section >();

		String labelString = "";

		for ( Log l : logList )
			labelString +=  l.filename + "(<i>" + l.datetime + "</i>) ";

		for ( MyThread thread : session.threadList )
			for ( Log l : logList )
			{
				secList.addAll( thread.getSection( l ) );
				//labelString += l.filename + "(" + l.datetime + ")";

			}

		ThreadDumpTableModel model = new ThreadDumpTableModel( ( logList.size() > 1 ), secList );
		ThreadDumpCellRenderer renderer = new ThreadDumpCellRenderer();

		JTable table = new JTable( model );

		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		for ( int i = 0; i < table.getColumnCount(); i++ )
		{
			int width;
			if ( i == 0 )
				width = 250;
			else
				width = 200;
			table.getColumnModel().getColumn( i ).setPreferredWidth( width );
		}

		for ( int i = 0; i < model.getColumnCount(); i++ )
			table.setDefaultRenderer( table.getColumnClass( i ), renderer );

		table.getTableHeader().setDefaultRenderer( new ThreadDumpHeaderRenderer() );

		startPanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = .9;
		//c.weighty = .4;
		c.gridwidth = 1;
		//c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets( 50, 10, 50, 10 );
			

		JLabel logLabel = new JLabel( );
		logLabel.setFont( new Font(logLabel.getFont().getName(), Font.PLAIN, logLabel.getFont().getSize()) );
		logLabel.setText( "<html><b>Log Files: </b>" + labelString + "</html>" );
		
		
		startPanel.add( logLabel, c );

		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 3;
		c.gridy = 1;
		c.insets = new Insets( 0, 10, 0, 10 );
		//c.anchor = GridBagConstraints.CENTER;

		startPanel.add( table.getTableHeader(), c );

		c = new GridBagConstraints();
		c.gridy = 2;
		c.weighty = .7;
		c.anchor = GridBagConstraints.PAGE_START;
		startPanel.add( table, c );

		startPanel.revalidate();
		

	}

	private void showErrorDialog( String message )
	{
		JOptionPane.showMessageDialog( startFrame, message, "Error", JOptionPane.ERROR_MESSAGE );

	}

}
