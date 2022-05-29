package com.kingslayer.hellopuppy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.opencsv.CSVWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MakeShifts extends Worker {

    public MakeShifts(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
//        Log.e("MakeShifts", "done.");
        // create csv with a line for every member of the group
        // run the python algorithm
        // create new schedule

        // first create file object for file placed at location
        // specified by filepath
        String DIRECTORY_PATH = System.getProperty("user.dir");
        assert DIRECTORY_PATH != null;
        File root = new File(DIRECTORY_PATH);
        File gpxfile = new File(root, "samples.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gpxfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writer!=null) {
            try {
                writer.append("First string is here to be written.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File("C:\\Users\\hadas\\Desktop\\a.csv");

//        try {
//            // create FileWriter object with file as parameter
//            FileWriter outputfile = new FileWriter(file);
//
//            // create CSVWriter object filewriter object as parameter
//            CSVWriter writer = new CSVWriter(outputfile);
//
//            // create a List which contains String array
//            List<String[]> data = new ArrayList<String[]>();
//            data.add(new String[] { "Name", "Class", "Marks" });
//            data.add(new String[] { "Aman", "10", "620" });
//            data.add(new String[] { "Suraj", "10", "630" });
//            writer.writeAll(data);
//
//            // closing writer connection
//            writer.close();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

        return Result.success();
    }
}
