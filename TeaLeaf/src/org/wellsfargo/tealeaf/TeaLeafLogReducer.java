package org.wellsfargo.tealeaf;

import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;



public class TeaLeafLogReducer 
extends Reducer<BytesWritable, LogEntry, BytesWritable, Text> 
{
	 public static final BytesWritable NULL_KEY = new BytesWritable();
	 public static final BytesWritable SOME_KEY = new BytesWritable("abc".getBytes());

@Override
	
	public void reduce(BytesWritable key, Iterable<LogEntry> values,
			Context context)
			throws IOException, InterruptedException {
	
	if (key.equals(NULL_KEY))
	for(LogEntry entry : values)
		context.write(NULL_KEY, new Text(entry.toString()));
	

}

}
	

