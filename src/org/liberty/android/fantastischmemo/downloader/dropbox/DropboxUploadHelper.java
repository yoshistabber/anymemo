package org.liberty.android.fantastischmemo.downloader.dropbox;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.myhttp.entity.mime.MultipartEntity;
import org.apache.myhttp.entity.mime.content.FileBody;
import org.json.JSONObject;
import org.liberty.android.fantastischmemo.AMEnv;
import android.content.Context;
import android.util.Log;

public class DropboxUploadHelper {

    private Context mContext;

    private final String authToken;
    private final String authTokenSecret;
    
    private final String FILE_UPLOAD_URL="https://api-content.dropbox.com/1/files_put/dropbox/";


    public DropboxUploadHelper(Context context, String authToken, String authTokenSecret) {
        this.authToken = authToken;
        this.authTokenSecret = authTokenSecret;
        mContext = context;
    }

    public boolean upload(String fileName, String filePath){
        
        HttpClient httpclient = new DefaultHttpClient();
        
        String headerValue = "OAuth oauth_version=\"1.0\", "
              + "oauth_signature_method=\"PLAINTEXT\", "
              + "oauth_consumer_key=\"" + AMEnv.DROPBOX_CONSUMER_KEY + "\", "
              + "oauth_token=\"" + authToken + "\", "
              + "oauth_signature=\"" + AMEnv.DROPBOX_CONSUMER_SECRET + "&"
              + authTokenSecret + "\"";

        try {
            String url = FILE_UPLOAD_URL + URLEncoder.encode(fileName, "utf-8"); 
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Authorization", headerValue);
            httppost.addHeader("Content-Type", "application/x-sqlite3");

            FileBody fileToUpload = new FileBody(new File(filePath));

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file to upload", fileToUpload);

            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                InputStream is = resEntity.getContent();
                String resultString = DropboxUtils.convertStreamToString(is);
                JSONObject jsonResponse = new JSONObject(resultString);
                if(jsonResponse.getString("modified") != null){
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return false;
    };
    
}
