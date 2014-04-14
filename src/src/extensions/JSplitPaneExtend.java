package src.extensions;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;

public class JSplitPaneExtend extends JSplitPane
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6005408916338872725L;

	   public JSplitPaneExtend( int verticalSplit, Component logListPane, Component buttonPane )
	{
		super(verticalSplit, logListPane, buttonPane);
	}

	public void setDividerLocation(int location) {
			int                 oldValue = getDividerLocation();
			//setDividerLocation( location );

			// Notify UI.
		        SplitPaneUI         ui = getUI();

		        if (ui != null) {
		            ui.setDividerLocation(this, oldValue);
		        }

			// Then listeners
			//firePropertyChange(DIVIDER_LOCATION_PROPERTY, oldValue, location);

			// And update the last divider location.
			setLastDividerLocation(oldValue);
		    }
	
	
}
