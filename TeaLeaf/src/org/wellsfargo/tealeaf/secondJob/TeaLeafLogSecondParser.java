package org.wellsfargo.tealeaf.secondJob;

/*
 * . 
 * 
 * 
 */


import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.wellsfargo.tealeaf.LogEntry;


public class TeaLeafLogSecondParser extends Configured implements Tool {
	
	//@SuppressWarnings("deprecation")
	
	public int run(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.printf("Usage: %s [generic options] <input> <output> <NumReduceTasks>\n",
					getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
	


Job job = new Job(getConf());
job.setJarByClass(getClass());
FileSystem fs = FileSystem.get(getConf());

FileInputFormat.addInputPath(job, new Path(args[0]));

if (fs.exists(new Path(args[1])))
		fs.delete(new Path(args[1]), true);

FileOutputFormat.setOutputPath(job, new Path(args[1]));


//job.setInputFormatClass (SequenceFileInputFormat.class);


job.setInputFormatClass(myCombinedInputFormat.class);

job.setMapperClass(TeaLeafLogSecondMapper.class);
//job.setPartitionerClass(HashPartitioner.class);

//job.setNumReduceTasks(Integer.parseInt(args[2]));
job.setNumReduceTasks(0);
job.setReducerClass(TeaLeafLogSecondReducer.class);


job.setMapOutputKeyClass(BytesWritable.class);
job.setMapOutputValueClass(Text.class);


job.setOutputKeyClass(BytesWritable.class);
job.setOutputValueClass(Text.class);

//job.setOutputFormatClass(SequenceFileOutputFormat.class);
job.setOutputFormatClass(TextOutputFormat.class);

/*
// Defines additional single text based output 'text' for the job
MultipleOutputs.addNamedOutput(job, "full", SequenceFileOutputFormat.class,
BytesWritable.class, LogEntry.class);

// Defines additional sequence-file based output 'sequence' for the job
MultipleOutputs.addNamedOutput(job, "blob",
  SequenceFileOutputFormat.class,
  BytesWritable.class, LogEntry.class);
*/

return job.waitForCompletion(true) ? 0 : 1;

}


public static void main (String[] args) throws Exception {
	//@SuppressWarnings("deprecation")
	int exitCode = ToolRunner.run(new TeaLeafLogSecondParser(), args);
	System.exit(exitCode);
}

}
