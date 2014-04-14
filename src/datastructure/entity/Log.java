package datastructure.entity;

import datastructure.section.Section;

public class Log extends Entity
{
	public final String	datetime, filename;

	public Log( String datetime, String filename )
	{
		this.datetime = datetime;
		this.filename = filename;
	}

	@Override
	protected boolean compareOtherEntity( Section s, Entity e )
	{
		return s.thread.equals( e );
	}

	public void removeLog()
	{
		for ( Section s : sectionList )
			s.removeSection();
	}

}
