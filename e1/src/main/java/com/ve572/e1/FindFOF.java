package com.ve572.e1;

import org.apache.commons.text.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;

public class FindFOF {
    private static String formPair(final String a, final String b) {
        if (a.compareTo(b) < 0) return a + "," + b;
        return b + "," + a;
    }

    public static class Map1 extends Mapper<Object, Text, Text, Text> {
        private Text resultKey = new Text();
        private Text resultValue = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");
            resultKey.set(tokenizer.nextToken());
            while (tokenizer.hasNext()) {
                resultValue.set(tokenizer.nextToken());
                context.write(resultKey, resultValue);
            }
        }
    }


    public static class Reduce1 extends Reducer<Text, Text, Text, IntWritable> {
        private Text resultKey = new Text();
        private IntWritable resultValue = new IntWritable();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            ArrayList<String> arrayList = new ArrayList<>();
            resultValue.set(0);
            for (Text val : values) {
                resultKey.set(formPair(key.toString(), val.toString()));
                context.write(resultKey, resultValue);
                arrayList.add(val.toString());
            }
            resultValue.set(1);
            for (int i = 0; i < arrayList.size(); i++) {
                for (int j = i + 1; j < arrayList.size(); j++) {
                    resultKey.set(formPair(arrayList.get(i), arrayList.get(j)));
                    context.write(resultKey, resultValue);
                }
            }
        }
    }

    public static class Map2 extends Mapper<Object, Text, Text, IntWritable> {
        private Text resultKey = new Text();
        private IntWritable resultValue = new IntWritable();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");
            resultKey.set(tokenizer.nextToken() + " " + tokenizer.nextToken());
            resultValue.set(Integer.parseInt(tokenizer.nextToken()));
            context.write(resultKey, resultValue);
        }
    }

    public static class Reduce2 extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable resultValue = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable val : values) {
                if (val.get() == 0) return;
                count += val.get();
            }
            resultValue.set(count);
            context.write(key, resultValue);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapred.textoutputformat.separator", ",");
//        conf.set("mapreduce.output.textoutputformat.separator", ",");

        Job job1 = Job.getInstance(conf, "ve572e1ex2.2");
        job1.setJarByClass(FindFOF.class);
        job1.setMapperClass(Map1.class);
        job1.setReducerClass(Reduce1.class);

        job1.setMapOutputValueClass(Text.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job1, new Path("data.txt"));
        Path outputPath1 = new Path("output-1");
        FileSystem fileSystem = outputPath1.getFileSystem(conf);
        if (fileSystem.exists(outputPath1)) {
            fileSystem.delete(outputPath1, true);
        }
        FileOutputFormat.setOutputPath(job1, outputPath1);

        boolean exitCode = job1.waitForCompletion(true);
        if (!exitCode) System.exit(1);

        conf.set("mapred.textoutputformat.separator", " ");

        Job job2 = Job.getInstance(conf, "ve572e1ex2.2");
        job2.setJarByClass(FindFOF.class);
        job2.setMapperClass(Map2.class);
        job2.setReducerClass(Reduce2.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job2, outputPath1);
        Path outputPath2 = new Path("output-2");
        if (fileSystem.exists(outputPath2)) {
            fileSystem.delete(outputPath2, true);
        }
        FileOutputFormat.setOutputPath(job2, outputPath2);
        exitCode = job2.waitForCompletion(true);

        System.exit(exitCode ? 0 : 1);
    }

}
