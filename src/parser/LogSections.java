package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LogSections
{
	LogHeader("([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}).*\\r\\n.*"), ThreadDetail(
			"\"(.*)\"( daemon)? prio=([0-9]*) tid=(.*?) nid=(.*?) (.*)"), ThreadState(
			" *java.lang.Thread.State: (RUNNABLE|WAITING|BLOCKED|TIMED_WAITING).*"), StackTrace(".*at (.*)\\((.*?):?([0-9]*)?\\)"), MonitorDetail(
			".*- (waiting on|waiting to lock|locked) <(.*)> \\(a (.*)\\)");

	private Pattern	p;

	private LogSections( String s )
	{
		p = Pattern.compile( s );
	}

	public Matcher getMatcher( String line )
	{
		Matcher m = p.matcher( line );
		m.matches();
		return m;
	}

}
