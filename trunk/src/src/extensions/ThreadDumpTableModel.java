package src.extensions;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import datastructure.section.Section;

public class ThreadDumpTableModel extends AbstractTableModel
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public String[]	columnNames;
	public boolean	multiLogFlag;
	public ArrayList<Section> sectionList;

	public ThreadDumpTableModel( boolean flag, ArrayList<Section> sectionList )
	{
		super();

		multiLogFlag = flag;
		this.sectionList = sectionList;

		if ( multiLogFlag )
			columnNames = new String[] { "Thread", "Log", "Monitor-Locked", "Monitor-Waiting", "Monitor - Blocked" };
		else
			columnNames = new String[] { "Thread", "Monitor-Locked", "Monitor-Waiting", "Monitor - Blocked" };

	}

	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	@Override
	public int getRowCount()
	{
		return sectionList.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	
	}

	@Override
	public Object getValueAt( int rowIndex, int columnIndex )
	{
		Section s = sectionList.get( rowIndex );

		if (multiLogFlag && columnIndex > 0)
			if (columnIndex == 1)
				return s.log.datetime;
			else
				columnIndex = columnIndex-1;
		
		switch (columnIndex) {
		case 0:
			return s.thread.threadName;
		case 1:
			return s.getMonitorInStatus( "locked" );
		case 2:
			return s.getMonitorInStatus( "waiting on" );
		case 3:
			return s.getMonitorInStatus( "waiting to lock" );
		default:
			return null;
		}
		
	}

}
