package com.nebula.haseeb.winiff.persistence;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.nebula.haseeb.winiff.R;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;
import com.nebula.haseeb.winiff.utils.checklist.CheckableListItem;
import com.nebula.haseeb.winiff.utils.checklist.CustomAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haseeb on 4/5/16.
 */
public class MeasurementsFileManagementActivity extends Activity {
    ListView lv;
    CheckableListItem[] listItems;
    Map<String, File> filesList;

    private MeasurementsPersistenceManager persistenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager_main);
        persistenceManager = new MeasurementsPersistenceManager(this, (MeasurementsParameters) getIntent().getSerializableExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID));
        initializeButtons();
        initializeFilesList();
    }

    private void initializeButtons() {
        Button shareButton = (Button) findViewById(R.id.share_files);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sendIntent.setType("text/*");
                ArrayList<Uri> filesUris = getUrisOf(getSelectedFiles());
                if (filesUris.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Select some files to share! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUris);
                Intent chooser = Intent.createChooser(sendIntent, getString(R.string.share_files_string));
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Log.e(this.getClass().getName(), "Filed to share files");
                }
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete_files);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> filesList = getSelectedFiles();
                if (filesList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Select some files to delete! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (persistenceManager.deleteMeasurementFiles(getSelectedFiles())) {
                    initializeFilesList();
                    Log.i(this.getClass().getName(), "deletion successful");
                } else {
                    Toast.makeText(getApplicationContext(), "Error when deleting !!! ", Toast.LENGTH_LONG).show();
                    Log.e(this.getClass().getName(), "deletion failure");
                }
            }
        });
    }

    private ArrayList<Uri> getUrisOf(List<File> files) {
        ArrayList<Uri> filesUris = new ArrayList<>();
        for (File file : files)
            filesUris.add(Uri.fromFile(file));
        return filesUris;
    }

    private void initializeFilesList() {
        File[] allFiles = persistenceManager.getAllMeasurementFiles();
        filesList = new HashMap<>();
        listItems = new CheckableListItem[allFiles.length];
        for (int i = 0; i < allFiles.length; i++) {
            listItems[i] = new CheckableListItem(allFiles[i].getName(), false);
            filesList.put(allFiles[i].getName(), allFiles[i]);
        }
        lv = (ListView) findViewById(R.id.listView1);
        CustomAdapter adapter = new CustomAdapter(this, listItems);
        lv.setAdapter(adapter);
    }

    private List<File> getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();
        for (CheckableListItem item : listItems)
            if (item.getValue())
                selectedFiles.add(filesList.get(item.getName()));
        return selectedFiles;
    }
}
