package src.extensions;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;

import sun.swing.table.DefaultTableCellHeaderRenderer;

public class ThreadDumpHeaderRenderer extends DefaultTableCellHeaderRenderer
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
	{
		DefaultTableCellHeaderRenderer c = (DefaultTableCellHeaderRenderer)super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

		c.setVerticalAlignment( JLabel.CENTER );
		c.setHorizontalAlignment( JLabel.CENTER );
		
		Font f = new Font (c.getFont().getName(), Font.BOLD, c.getFont().getSize());
		c.setFont( f );
		
		return c;

	}

}
