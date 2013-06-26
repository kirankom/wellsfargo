package org.wellsfargo.tealeaf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.EOFException;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class LogEntry implements Writable {
	public BooleanWritable isComplete = new BooleanWritable(false);
	public Text UID = new Text(" ");
	public Text HID=new Text(" ");
	public Text SID=new Text(" ");
	public Text request=new Text(" ");
	public Text response=new Text(" ");
	public Text blob=new Text(" ");
	public Text fileName = new Text(" ");
	public Text timestamp = new Text();
	public Text urlfield = new Text();
	public Text cookies = new Text();
	
	public String toString()
	{
		char delim = '\005';
		String out = UID.toString() + delim + HID + delim +SID + delim +request + delim +response
						+ delim + timestamp + delim +urlfield +delim+cookies;
		return out;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
			fileName.readFields(in);
			isComplete.readFields(in);
			UID.readFields(in);
			HID.readFields(in);
			SID.readFields(in);
			request.readFields(in);
			response.readFields(in);
			timestamp.readFields(in);
			urlfield.readFields(in);
			cookies.readFields(in);
			blob.readFields(in);	
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
		fileName.write(out);
		isComplete.write(out);
		UID.write(out);
		HID.write(out);
		SID.write(out);
		request.write(out);
		response.write(out);
		timestamp.write(out);
		urlfield.write(out);
		cookies.write(out);
		blob.write(out);
		
		
	}
	

}
