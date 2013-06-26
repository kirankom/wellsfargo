package org.wellsfargo.tealeaf;

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
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


public class TeaLeafLogParser extends Configured implements Tool {
	
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


job.setInputFormatClass (TextInputFormat.class);

job.setMapperClass(TeaLeafLogMapper.class);
//job.setPartitionerClass(HashPartitioner.class);

//job.setNumReduceTasks(Integer.parseInt(args[2]));
job.setNumReduceTasks(0);
job.setReducerClass(TeaLeafLogReducer.class);


job.setMapOutputKeyClass(BytesWritable.class);
job.setMapOutputValueClass(Text.class);


job.setOutputKeyClass(BytesWritable.class);
job.setOutputValueClass(Text.class);

//job.setOutputFormatClass(SequenceFileOutputFormat.class);


// Defines additional single text based output 'text' for the job
MultipleOutputs.addNamedOutput(job, "full1", SequenceFileOutputFormat.class,
BytesWritable.class, Text.class);

MultipleOutputs.addNamedOutput(job, "full2", SequenceFileOutputFormat.class,
BytesWritable.class, Text.class);

// Defines additional sequence-file based output 'sequence' for the job
MultipleOutputs.addNamedOutput(job, "blob",
  SequenceFileOutputFormat.class,
  BytesWritable.class, LogEntry.class);


return job.waitForCompletion(true) ? 0 : 1;

}


public static void main (String[] args) throws Exception {
	@SuppressWarnings("deprecation")
	int exitCode = ToolRunner.run(new TeaLeafLogParser(), args);
	System.exit(exitCode);
}

}
