package org.wellsfargo.tealeaf;



import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class TeaLeafLogMapper extends Mapper<LongWritable, Text, BytesWritable, Text> {
 
 LogEntry entry ;//= new LogEntry();
 public static final BytesWritable NULL_KEY = new BytesWritable();
 public static final BytesWritable SOME_KEY = new BytesWritable("abc".getBytes());
 
 public static final char map_delim = '\001';
 
 StringBuilder blob = new StringBuilder();
 boolean inEnv=false;
 boolean inResponse=false;
 boolean inTimeStamp=false;
 boolean inURL=false;
 boolean inCookie=false;
 /*
  * (non-Javadoc)
  * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)
  * 
  */
 
 private MultipleOutputs<?, ?> mos;
 
 @Override
 public void setup(Context context) {
 
 mos = new MultipleOutputs(context);
 }
 
 protected void cleanup(Context context)
         throws IOException,InterruptedException
                {
	 				super.cleanup(context);
	 				
	 				if (entry !=null) //have we seen any text after final entry?
	 				  {
	 					  entry.isComplete.set(false);
	 					  FileSplit fileSplit = (FileSplit)context.getInputSplit();
	 					  entry.fileName.set(fileSplit.getPath().getName());
	 					  
	 					  mos.write("blob", SOME_KEY, entry);
	 					  
	 					  //context.write(SOME_KEY, new Text(entry.toString()));
	 				  }
	 				
                }
 
  protected void map(LongWritable key, Text value, Context context)
      throws java.io.IOException, InterruptedException {
 
	  String currVal=value.toString();
	  
	  if (currVal.contains("T$SP$01"))
	  {
		  if(entry!=null) // have i already built an entry?
			  {
			   	entry.isComplete.set(true);
			   	blob = new StringBuilder();
			    mos.write("full1", NULL_KEY, new Text(entry.toString()));
				  //context.write(NULL_KEY,entry);
				
			  }
		  	else
			  
			  if (blob.length() >0) //have we seen any text before this entry?
			  {
				  entry=new LogEntry();
				  entry.isComplete.set(false);
				  FileSplit fileSplit = (FileSplit)context.getInputSplit();
				  entry.fileName.set(fileSplit.getPath().getName());
				  entry.blob.set(blob.toString());
				  //context.write(SOME_KEY, entry);
				  mos.write("blob", SOME_KEY, entry);
				  blob = new StringBuilder();
				  
			  }
		  //setting entry to blank. will be built by subsequent rows.
		   entry= null;
		   inEnv=false;
		   inResponse=false;
		   inTimeStamp=false;
		   inURL=false;
		   inCookie=false;
		  return;
	  }
	  
	  
	  if (currVal.startsWith("TLTSID") && !inCookie)		//Cookie Section also has TLTSID
		  {
		  
		  if (entry !=null) //Is this a new entry - happens when T$SP$01 is not detected.
		  {
			  blob = new StringBuilder();
			  entry.isComplete.set(true);
			  mos.write("full1", NULL_KEY, new Text(entry.toString()));
			  //context.write(NULL_KEY, entry);
		  }
		  else
			  
			  if (blob.length() >0) //have we seen any text before this entry?
			  {
				  entry=new LogEntry();
				  entry.isComplete.set(false);
				  FileSplit fileSplit = (FileSplit)context.getInputSplit();
				  entry.fileName.set(fileSplit.getPath().getName());
				  entry.blob.set(blob.toString());
				  //context.write(SOME_KEY, entry);
				  mos.write("blob", SOME_KEY, entry);
				  blob = new StringBuilder();
			  }

		  entry= new LogEntry();
		  blob = new StringBuilder();
		  entry.SID = new Text(currVal.substring(7)); //ignore TLTSID=

		   inEnv=false;
		   inResponse=false;
		   inTimeStamp=false;
		   inURL=false;
		   inCookie=false;
		   
		  	return;
		  }
	  
	  if (currVal.startsWith("TLTHID"))		
	  {
	    if (entry == null)
	    {
	    	blob.append(currVal + "\n");
	    	return;
	    }
	  	entry.HID = new Text(currVal.substring(7)); //ignore TLTSID=
	  	return;
	  }
	  
	  if (currVal.startsWith("TLTUID"))		
	  {
		  if (entry == null)
		    {
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  
	  	entry.UID = new Text(currVal.substring(7)); //ignore TLTSID=
	  	return;
	  }
	  
	  if(currVal.matches("\\[.*\\]")) //reset if a new header is found
	  {
		  inEnv=false;
		  inResponse=false;
		  inTimeStamp=false;
		  inCookie=false;
		  inURL=false;
	  }
	  
	  if (currVal.equals("[env]"))
		  {
		  inEnv=true;
		  return;
		  }
	  
	  if (currVal.equals("[timestamp]"))
	  {
	  inTimeStamp=true;
	  return;
	  }
  

	  if (currVal.equals("[urlfield]"))
		  {
		  inURL=true;
		  return;
		  }
	  
	  
	  if (currVal.equals("[cookies]"))
	  {
		  inCookie=true;
		  return;
	  }
	  
	  if (currVal.equals("[ResponseHeaders]"))
	  {
	  inResponse=true;
	  return;
	  }
	  
	  if (inEnv)
	  {
		  if (entry == null) //case when log splits in the middle of an entry
		    {
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  
		  entry.request.append((map_delim + currVal).getBytes(),0,(map_delim + currVal).length());
		  return;
	  }
	  
	  if (inTimeStamp)
	  {
		  if (entry == null)
		    {
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  
		  entry.timestamp.append((map_delim + currVal).getBytes(),0,(map_delim + currVal).length());
		  return;
	  }
	  
	  if (inURL)
	  {
		  if (entry == null)
		    {
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  
		  entry.urlfield.append((map_delim + currVal).getBytes(),0,(map_delim + currVal).length());
		  return;
	  }
	  
	  if (inCookie)
	  {
		  if (entry == null)
		    {
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  
		  entry.cookies.append((map_delim + currVal).getBytes(),0,(map_delim + currVal).length());
		  return;
	  }
	  
	  
	  
	  if (inResponse)
	  {
		  if (entry == null)
		    {	
		    	blob.append(currVal + "\n");
		    	return;
		    }
		  // entry.response.append((map_delim + "key=val").getBytes(),0,8);
		    entry.response.append((map_delim + currVal).getBytes(),0,(map_delim + currVal).length());
		  return;
	  }
	  
	  blob.append(currVal + "\n"); //case when we cant figure out which section we are in.
	  		
   
  }
  
}