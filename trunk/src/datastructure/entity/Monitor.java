package datastructure.entity;

import datastructure.section.Section;

public class Monitor extends IdEntity
{
	public String	type;

	public Monitor( String id )
	{
		super( id );
	}

	@Override
	protected boolean compareOtherEntity( Section s, Entity e )
	{
		return false;
	}

	public String getMonitorTypeName()
	{
		String[] splits = type.split( "\\." );
		return splits[ splits.length - 1 ];

	}

}
