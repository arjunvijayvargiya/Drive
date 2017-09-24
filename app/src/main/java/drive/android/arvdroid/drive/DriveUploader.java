package drive.android.arvdroid.drive;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
/**
 * @author arjun vijayvargiya
 */

public class DriveUploader {

    private Context ctx;
    private GoogleApiClient googleApiClient;
    private String[] phoneContents;
    private static final String TAG = "DRIVE UPLOADER:";

    DriveUploader(GoogleApiClient googleApiClient, Context ctx, String[] phoneContents){
           this.ctx =  ctx;
           this.googleApiClient = googleApiClient;
           this.phoneContents = phoneContents;
    }
    public void upload(){
        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public void showMessage(String message){
        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
    }

    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {

                                //writer.write("Hello World!");
                                for(int i=0;i<phoneContents.length;i++){
                                    writer.write(phoneContents[i]);
                                }
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("rocket")
                                    .setMimeType("text/x-vcard")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(googleApiClient)
                                    .createFile(googleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };
}
