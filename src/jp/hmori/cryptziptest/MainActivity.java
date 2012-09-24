package jp.hmori.cryptziptest;

import jp.hmori.cryptziptest.util.CryptZipManager;
import android.os.Bundle;
import android.os.Process;
import android.app.Activity;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String ZIP_FILENAME = "data.zip";
	private static final String ZIP_PASSWORD = "password";

	private CryptZipManager cryptZipManager;

	private ImageView imageView;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			cryptZipManager = new CryptZipManager(this, ZIP_FILENAME, ZIP_PASSWORD);

			imageView = (ImageView)findViewById(R.id.imageView);
			textView = (TextView)findViewById(R.id.textView);


			ListView listView = (ListView)findViewById(R.id.listView);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_row);
			for (String text : cryptZipManager.getEntryList()) {
				adapter.add(text);
			}
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					ListView listView = (ListView) parent;
					String item = (String) listView.getItemAtPosition(position);

					clearContent();

					long start = System.currentTimeMillis();
					try {
						if (item.endsWith(".jpg")) {
							imageView.setImageBitmap(cryptZipManager.getBitmap(item));
						} else {
							textView.setText(cryptZipManager.getText(item));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					long end = System.currentTimeMillis();
					Toast.makeText(MainActivity.this, "decrypt time : "+ (end-start) +" ms", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearContent() {
		imageView.setImageBitmap(null);
		textView.setText(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		android.os.Process.killProcess(Process.myPid());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


}
