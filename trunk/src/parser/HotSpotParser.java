package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import datastructure.Session;
import datastructure.Session.EntityName;
import datastructure.entity.Monitor;
import datastructure.entity.MyThread;
import datastructure.entity.StackDetail;

public class HotSpotParser
{
	private final static Charset	CHARACTER_SET		= Charset.forName( "UTF-8" );
	private final static String		SECTION_SEPERATOR	= "\r\n\r\n";
	private final static String		LINE_SEPERATOR		= "\r\n";
	private Session					session;
	public MyThread					currentThread;
	private File currentFile;

	// set Session

	public HotSpotParser( Session session )
	{
		this.session = session;
	}

	public void parseFile( File file )
	{
		this.currentFile = file;
		String content = readFile( file );
		String[] sections = splitContent( content );

		parseJVMSection( sections[ 0 ] );

		for ( int i = 1; i < sections.length; i++ )
			parseThreadSection( sections[ i ] );
	}

	private String readFile( File file )
	{
		String fileContent = "";
		try
		{
			FileInputStream fileInputStream = new FileInputStream( file );
			FileChannel channel = fileInputStream.getChannel();

			ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );

			while ( channel.read( byteBuffer ) > 0 )
			{
				byteBuffer.flip();
				fileContent = fileContent + CHARACTER_SET.decode( byteBuffer ).toString();
				byteBuffer.clear();
			}
		}
		catch ( FileNotFoundException e )
		{
			// To be Handled

		}
		catch ( IOException e )
		{
			// To be Handled

		}
		return fileContent;
	}

	private String[] splitContent( String str )
	{
		return str.split( SECTION_SEPERATOR );
	}

	private void parseJVMSection( String str )
	{
		Matcher m = LogSections.LogHeader.getMatcher( str );
		if ( m.matches() )
		{
			String datetime = m.group( ParserConstants.DATETIME );
			session.createLog( datetime, currentFile.getName() );
		}
		else
		{
			//TODO: Throw Exception
		}

	}

	private void parseThreadSection( String str )
	{
		String[] lines = str.split( LINE_SEPERATOR );

		String threadState = null;

		MyThread thread = parseThreadDetail( lines[ 0 ] );
		if ( thread == null )
			return;

		if ( lines.length > 1 )
			threadState = parseThreadState( lines[ 1 ] );

		ArrayList< StackDetail > dtlList = new ArrayList< StackDetail >();
		HashMap< String, ArrayList< Monitor > > monitorStatusMap = new HashMap< String, ArrayList< Monitor > >();

		for ( int i = 2; i < lines.length; i++ )
		{
			String line = lines[ i ];
			if ( LogSections.StackTrace.getMatcher( line ).matches() )
				dtlList.add( parseStackTrace( line ) );
			else
				parseMonitor( monitorStatusMap, line );
		}

		session.addSection( thread, monitorStatusMap, dtlList, threadState );

	}

	private MyThread parseThreadDetail( String str )
	{
		Matcher m = LogSections.ThreadDetail.getMatcher( str );

		if ( !m.matches() )
			return null;

		String threadId = m.group( ParserConstants.TID );

		MyThread currentThread = (MyThread)session.getEntity( EntityName.THREAD, threadId );

		if ( !currentThread.detailsSetFlag )
		{
			currentThread.threadName = m.group( ParserConstants.THREAD_NAME );
			currentThread.nativeThreadId = m.group( ParserConstants.NID );
			currentThread.daemonFlag = ( m.group( ParserConstants.IS_DAEMON ) == null ) ? false : true;
			currentThread.priority = Integer.parseInt( m.group( ParserConstants.PRIORITY ) );
			currentThread.detailsSetFlag = true;
		}

		return currentThread;
	}

	private String parseThreadState( String str )
	{
		Matcher m = LogSections.ThreadState.getMatcher( str );
		String threadState = m.group( ParserConstants.THREAD_STATE );
		return threadState;
	}

	private StackDetail parseStackTrace( String str )
	{
		Matcher m = LogSections.StackTrace.getMatcher( str );

		StackDetail dtl = new StackDetail();
		dtl.packageName = m.group( ParserConstants.METHOD_PATH );
		dtl.packageName = m.group( ParserConstants.CLASS_FILE );

		/*if ( m.groupCount() > 2 ) {
			String lineNo =  m.group( ParserConstants.LINE_NO );
			if (!lineNo.matches( " *" ))
				dtl.lineNo = Integer.parseInt( lineNo);
		}*/

		return dtl;

	}

	private void parseMonitor( Map< String, ArrayList< Monitor > > monitorStatusMap, String str )
	{
		Matcher m = LogSections.MonitorDetail.getMatcher( str );
		String id = m.group( ParserConstants.MONITOR_ID );
		Monitor monitor = (Monitor)session.getEntity( EntityName.MONITOR, id );

		if ( !monitor.detailsSetFlag )
		{
			monitor.type = m.group( ParserConstants.MONITOR_TYPE );
			monitor.detailsSetFlag = true;
		}

		String status = m.group( ParserConstants.MONITOR_STATUS );
		if ( monitor != null )
		{
			if ( monitorStatusMap.containsKey( status ) )
				monitorStatusMap.get( status ).add( monitor );
			else
				monitorStatusMap.put( status, new ArrayList< Monitor >( Arrays.asList( monitor ) ) );
		}

	}

}
