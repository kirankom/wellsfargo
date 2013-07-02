package org.wellsfargo.tealeaf.secondJob;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.wellsfargo.tealeaf.LogEntry;

public class TeaLeafLogSecondMapper extends Mapper<BytesWritable, LogEntry, BytesWritable, Text> {
 
List<LogEntry> values = new ArrayList();

public static final BytesWritable SOME_KEY = new BytesWritable("abc".getBytes());
 
 @Override
  protected void map(BytesWritable key, LogEntry value, Context context)
      throws java.io.IOException, InterruptedException {
 
	 // context.write(key, value);
	 values.add(value);
	  
  }
 
 @Override
 protected void cleanup(Context context)
         throws IOException,InterruptedException
                {
	 for(LogEntry e:values)
	 				context.write(SOME_KEY, e.blob);
                }
 
  
}

