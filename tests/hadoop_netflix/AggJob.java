import java.io.IOException;
import java.util.*;
import java.text.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.partition.*;

public class AggJob extends Configured implements Tool {

  public static class Map extends Mapper<Object, Text, Text, Text> {

    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException{
      String[] lineBuf = value.toString().trim().split(" ");
      Text newKey = new Text(lineBuf[0] + " " + lineBuf[2]);
      context.write(newKey, new Text(lineBuf[1]));
    }

  }

  public static class Reduce extends Reducer<Text, Text, NullWritable, Text>{

    public void reduce(Text key, Iterable<Text> values, Context context)
      throws IOException, InterruptedException {
      int agg = 0;
      for (Text text : values) {
        agg += Integer.valueOf(text.toString());
      }
      context.write(NullWritable.get(),
                    new Text(key.toString() + " " + String.valueOf(agg)));
    }

  }

  public int run(String[] args) throws Exception{
    Configuration conf = new Configuration();
    Job job = new Job(conf, "AggJob");
    job.setJarByClass(AggJob.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(Text.class);
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
    job.setNumReduceTasks(Integer.valueOf(args[2]));
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    return (job.waitForCompletion(true) ? 0 : 1);
  }

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new AggJob(), args);
    System.exit(res);
  }

}