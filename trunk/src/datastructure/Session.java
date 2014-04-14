package datastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import parser.HotSpotParser;
import datastructure.entity.IdEntity;
import datastructure.entity.Log;
import datastructure.entity.Monitor;
import datastructure.entity.MyThread;
import datastructure.entity.StackDetail;
import datastructure.section.Section;

public class Session
{
	public ArrayList< MyThread >	threadList	= new ArrayList< MyThread >();
	ArrayList< Log >		logList		= new ArrayList< Log >();
	ArrayList< Monitor >	monitorList	= new ArrayList< Monitor >();
	private Log				currentLog;

	public enum EntityName
	{
		THREAD, MONITOR
	};

	public Log createLog( String datetime, String filename )
	{
		currentLog = new Log( datetime, filename );
		logList.add( currentLog );
		return currentLog;
	}

	public IdEntity getEntity( EntityName name, String id )
	{
		IdEntity entity;

		if ( name.equals( EntityName.MONITOR ) )
		{
			if ( ( entity = containsEntity( monitorList, id ) ) == null )
			{
				entity = new Monitor( id );
				monitorList.add( (Monitor)entity );
			}
		}
		else
		{
			if ( ( entity = containsEntity( threadList, id ) ) == null )
			{
				entity = new MyThread( id );
				threadList.add( (MyThread)entity );
			}
		}
		return entity;
	}

	public IdEntity containsEntity( ArrayList< ? extends IdEntity > list, String id )
	{
		for ( IdEntity entity : list )
			if ( entity.id.equals( id ) )
				return entity;
		return null;
	}

	public void addSection( MyThread t, Map< String, ArrayList< Monitor >> monitorStatusMap, ArrayList< StackDetail > stackList, String threadState )
	{
		Section s = new Section();
		s.log = currentLog;
		s.thread = t;
		s.monitorStatusMap = monitorStatusMap;
		s.stackList = stackList;
		s.threadState = threadState;

		t.addSection( s );
		currentLog.addSection( s );
	}

	public Log processFile( File f )
	{
		HotSpotParser parser = new HotSpotParser(this);
		parser.parseFile( f );
		
		return currentLog;
		
	}

}
