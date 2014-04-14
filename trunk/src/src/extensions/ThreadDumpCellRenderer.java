package src.extensions;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ThreadDumpCellRenderer extends DefaultTableCellRenderer
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
	{
		DefaultTableCellRenderer c = (DefaultTableCellRenderer)super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

		c.setVerticalAlignment( JLabel.CENTER );
		c.setHorizontalAlignment( JLabel.CENTER );
		
		return c;

	}
}
