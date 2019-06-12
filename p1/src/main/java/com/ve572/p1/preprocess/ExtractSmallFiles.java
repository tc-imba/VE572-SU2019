package com.ve572.p1.preprocess;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtractSmallFiles {

    public static void DeSerializeDirectory(String mainDirectory, String avroName) {

        Path mainPath = Paths.get(mainDirectory);
        mainPath.toFile().mkdirs();

        


    }

    public static void main(String[] args) throws Exception {
        DeSerializeDirectory("data3", "A.A.avro");
    }


}
