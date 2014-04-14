package datastructure.entity;

import java.util.ArrayList;

import datastructure.section.Section;

public abstract class Entity
{
	protected ArrayList< Section >	sectionList	= new ArrayList< Section >();

	public void addSection( Section section )
	{
		sectionList.add( section );
	}

	abstract protected boolean compareOtherEntity( Section s, Entity e );

	public ArrayList< Section > getSection( Entity entity )
	{

		ArrayList< Section > filteredSections = new ArrayList< Section >();

		for ( Section s : this.sectionList )
			if ( compareOtherEntity( s, entity ) )
				filteredSections.add( s );

		return filteredSections;

	}

}
