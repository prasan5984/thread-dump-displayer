package datastructure.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import datastructure.entity.Log;
import datastructure.entity.Monitor;
import datastructure.entity.MyThread;
import datastructure.entity.StackDetail;

public class Section
{
	public Log									log;
	public MyThread								thread;
	public Map< String, ArrayList< Monitor > >	monitorStatusMap;
	public ArrayList< StackDetail >				stackList	= new ArrayList< StackDetail >();
	public String								threadState;

	public String getMonitorInStatus( String status )
	{
		String monitorNames = "";

		ArrayList< Monitor > monitorList = monitorStatusMap.get( status );

		if ( monitorList != null )
		{
			for ( Monitor m : monitorList )
				monitorNames += m.getMonitorTypeName();
		}

		return monitorNames;

	}

	public void removeSection()
	{
		thread.removeSection( this );
		monitorStatusMap = new HashMap< String, ArrayList< Monitor > >();
	}

}
