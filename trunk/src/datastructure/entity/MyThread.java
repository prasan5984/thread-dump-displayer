package datastructure.entity;

import datastructure.section.Section;

public class MyThread extends IdEntity
{
	public String	nativeThreadId;
	public boolean	daemonFlag;
	public int		priority;
	public String	threadName;
	public String	threadState;

	public MyThread( String id )
	{
		super( id );
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean compareOtherEntity( Section s, Entity e )
	{
		return s.log.equals( e );
	}

	public void removeSection( Section s )
	{
		this.sectionList.remove( s );
	}

}
