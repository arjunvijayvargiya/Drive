package drive.android.arvdroid.drive;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author arjun vijayvargiya
 * My trademark code
 */
public class VCFConverter {

    private Context mContext;
    private String[] phoneContents;

    public void getVCF(Context ctx)
    {
        mContext = ctx;
        //final String vfile = "rocket.vcf";
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        phones.moveToFirst();
        phoneContents =new String[phones.getCount()];
        for(int i =0;i<phones.getCount();i++)
        {
            String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd;
            try
            {
                fd = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = readBytes(fis);
                fis.read(buf);
                String VCard = new String(buf);
                /*
                String path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(path, true);
                mFileOutputStream.write(VCard.toString().getBytes());
                */
                phoneContents[i] = VCard.toString();
                phones.moveToNext();
                Log.d("Vcard",  VCard);
                //Toast.makeText(ctx,"Done.....",Toast.LENGTH_LONG).show();

            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        Toast.makeText(ctx,"Done.....",Toast.LENGTH_LONG).show();
    }

    public String[] getPhoneContents(){
        return phoneContents;
    }
    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
