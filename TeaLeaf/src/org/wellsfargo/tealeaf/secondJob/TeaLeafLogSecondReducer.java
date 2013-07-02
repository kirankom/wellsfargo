package org.wellsfargo.tealeaf.secondJob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.wellsfargo.tealeaf.LogEntry;



public class TeaLeafLogSecondReducer 
extends Reducer<BytesWritable, LogEntry, BytesWritable, LogEntry> 
{
	 public static final BytesWritable NULL_KEY = new BytesWritable();
	 public static final BytesWritable SOME_KEY = new BytesWritable("abc".getBytes());

@Override
	
	public void reduce(BytesWritable key, Iterable<LogEntry> values,
			Context context)
			throws IOException, InterruptedException {
	
	if (key.equals(NULL_KEY))
		return;
	
	List<LogEntry> entries = new ArrayList<LogEntry>();
	
	
	for(LogEntry e : values)
	//	entries.add(e);
		context.write(key,e);
	
	
	for(LogEntry e : entries)
		context.write(key,e);
	
	Collections.sort(entries,new LogEntryComparator());
	
	for(LogEntry e : entries)
		context.write(key,e);
	

}

private LogEntry CombineLogs(LogEntry logEntry, LogEntry logEntry2) {
	// TODO Auto-generated method stub
	return null;
}

}
	
class LogEntryComparator implements Comparator<LogEntry>
{

	@Override	
	public int compare(LogEntry arg0, LogEntry arg1) {
		// TODO Auto-generated method stub
		
		return (arg0.fileName.toString() + arg0.top ).compareTo(arg1.fileName.toString()+arg1.top);
	}
	
}

