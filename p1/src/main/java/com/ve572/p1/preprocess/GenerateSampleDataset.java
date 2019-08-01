package com.ve572.p1.preprocess;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import com.ve572.p1.preprocess.avro.MillionSong;

public class GenerateSampleDataset {

    private static final String NAME_NODE = "hdfs://hadoop-master:9000";
    private SortedSet<String> trackSet = new TreeSet<>();

    public void readTrackIds(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        Integer trackRowNumber = 0;
        Integer newTrackNumber = trackSet.size();
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                int pos = line.indexOf(".h5");
                if (pos > 0) {
                    line = line.substring(0, pos);
                }
                trackSet.add(line);
                ++trackRowNumber;
            }
        }
        newTrackNumber = trackSet.size() - newTrackNumber;
        System.out.println(filePath + ": " + trackRowNumber.toString() +
                " tracks, with " + newTrackNumber.toString() + " not duplicate");
    }

    public void generate(String filePath) throws URISyntaxException, IOException {
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        FileSystem hdfs = FileSystem.get(new URI(NAME_NODE), conf);

        DatumReader<MillionSong> datumReader = new SpecificDatumReader<>(MillionSong.class);

        System.out.println("total: " + ((Integer) trackSet.size()).toString() + " tracks");

        String avroFile = "";
        InputStream inputStream = null;
        HashMap<String, MillionSong> songHashMap = new HashMap<>();

        Integer avroNumber = 0;
        Integer foundNumber = 0;
        for (String trackId : trackSet) {
            String newAvroFile = "/msd/" + trackId.charAt(2) + "/" + trackId.charAt(3) + ".avro";
            String millionSongPath = "/" + trackId.charAt(2) + "/" + trackId.charAt(3)
                    + "/" + trackId.charAt(4) + "/" + trackId + ".h5";
            if (!avroFile.equals(newAvroFile)) {
//                System.out.println(trackId + " " + newAvroFile);

                if (inputStream != null) inputStream.close();
                songHashMap.clear();

                avroFile = newAvroFile;
                ++avroNumber;

                try {
                    inputStream = hdfs.open(new Path(avroFile));
                    DataFileStream<MillionSong> dataFileReader = new DataFileStream<>(inputStream, datumReader);
                    for (MillionSong song : dataFileReader) {
                        songHashMap.put(song.getFilename().toString(), song);
                    }
                } catch (IOException exception) {
                    System.err.println("error: " + avroFile + " not found!");
                }

            }
            MillionSong song = songHashMap.get(millionSongPath);
            if (song != null) {
                System.out.println(song.getFilename().toString() + " " + song.getChecksum().toString());
                ++foundNumber;
            } else {
                System.out.println(millionSongPath + " not found!");
            }
        }
        System.out.println("found: " + foundNumber.toString() + "/" + ((Integer) trackSet.size()).toString());
    }

    public static void main(String[] args) throws Exception {
        GenerateSampleDataset instance = new GenerateSampleDataset();
        instance.readTrackIds("pgroup1.txt");
        instance.readTrackIds("pgroup2.txt");
        instance.readTrackIds("pgroup4.txt");

        instance.generate("dataset.avro");
    }

}
