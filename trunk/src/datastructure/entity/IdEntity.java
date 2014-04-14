package datastructure.entity;

public abstract class IdEntity extends Entity
{
	public final String	id;
	public boolean detailsSetFlag = false;

	public IdEntity( String id )
	{
		this.id = id;
	}

}
