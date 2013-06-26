package org.wellsfargo.tealeaf.secondJob;



import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.wellsfargo.tealeaf.LogEntry;

public class TeaLeafLogSecondMapper extends Mapper<BytesWritable, LogEntry, BytesWritable, LogEntry> {
 

 
 
  protected void map(BytesWritable key, LogEntry value, Context context)
      throws java.io.IOException, InterruptedException {
 
	  
	  context.write(key, value);
	  
  }
  
}