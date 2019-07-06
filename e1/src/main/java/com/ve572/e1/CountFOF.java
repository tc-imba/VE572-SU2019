package com.ve572.e1;

import org.apache.commons.text.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CountFOF {

    public static class Map extends Mapper<Object, Text, Text, Text> {
        private Text resultKey = new Text();
        private Text resultValue = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), " ");
            String a = tokenizer.nextToken();
            String b = tokenizer.nextToken();
            String count = tokenizer.nextToken();
            resultKey.set(a);
            resultValue.set(b + " " + count);
            context.write(resultKey, resultValue);
            resultKey.set(b);
            resultValue.set(a + " " + count);
            context.write(resultKey, resultValue);
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        private Text resultValue = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            ArrayList<String> arrayList = new ArrayList<>();
            for (Text val : values) {
                arrayList.add(val.toString());
            }
            Collections.sort(arrayList);
            resultValue.set(String.join(", ", arrayList));
            context.write(key, resultValue);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapred.textoutputformat.separator", " ");
//        conf.set("mapreduce.output.textoutputformat.separator", ",");

        Job job = Job.getInstance(conf, "ve572e1ex2.3");
        job.setJarByClass(CountFOF.class);
        job.setMapperClass(CountFOF.Map.class);
        job.setReducerClass(CountFOF.Reduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path("output-2"));
        Path outputPath = new Path("output-3");
        FileSystem fileSystem = outputPath.getFileSystem(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        boolean exitCode = job.waitForCompletion(true);
        System.exit(exitCode ? 0 : 1);
    }
}
