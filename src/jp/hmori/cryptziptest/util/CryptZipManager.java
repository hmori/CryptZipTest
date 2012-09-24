package jp.hmori.cryptziptest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.hishidama.zip.ZipEntry;
import jp.hishidama.zip.ZipFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CryptZipManager {

	private final File dataFile;

	private final String password;
	public final AssetManager assetManager;
	private static final int BUFFER_SIZE = 1024;

	public CryptZipManager(Context context, String filename, String password) throws IOException {
		super();
		this.password = password;
		this.assetManager = context.getResources().getAssets();
		File dir = context.getDir(filename.replace(".zip", ""), Context.MODE_PRIVATE);
		this.dataFile = new File(dir, filename);
		initData(filename);
	}

	public List<String> getEntryList() throws IOException {
		List<String> list = new ArrayList<String>();

		ZipFile zipFile = new ZipFile(dataFile, "UTF-8");
		zipFile.setPassword(password.getBytes("UTF-8"));
		zipFile.setCheckCrc(true);
		for (Iterator<ZipEntry> ite = zipFile.getEntriesIterator(); ite.hasNext();) {
			ZipEntry entry = ite.next();
			if (!entry.isDirectory()) {
				list.add(entry.getName());
			}
		}
		zipFile.close();
		
		Collections.sort(list);
		return list;
	}

	public Bitmap getBitmap(String entryPath) throws IOException {
		Bitmap bitmap = null;
		
		ZipFile zipFile = null;
		BufferedInputStream bis = null;
		try {
			zipFile = new ZipFile(dataFile, "UTF-8");
			zipFile.setPassword(password.getBytes("UTF-8"));
			zipFile.setCheckCrc(true);
			ZipEntry entry = zipFile.getEntry(entryPath);

			bis = new BufferedInputStream(zipFile.getInputStream(entry));
			bitmap = BitmapFactory.decodeStream(bis);
		} finally {
			if (bis != null) { try { bis.close(); } catch (IOException e) {} }
			if (zipFile != null) { try { zipFile.close(); } catch (IOException e) {} }
		}
		return bitmap;
	}

	public String getText(String entryPath) throws IOException {
		StringBuilder sb = new StringBuilder();

		ZipFile zipFile = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			zipFile = new ZipFile(dataFile, "UTF-8");
			zipFile.setPassword(password.getBytes("UTF-8"));
			zipFile.setCheckCrc(true);
			ZipEntry entry = zipFile.getEntry(entryPath);

			isr = new InputStreamReader(zipFile.getInputStream(entry), "UTF-8");
			br = new BufferedReader(isr);
			String text;
			while ((text = br.readLine()) != null) {
				sb.append(text);
			}
		} finally {
			if (isr != null) { try { isr.close(); } catch (IOException e) {} }
			if (br != null) { try { br.close(); } catch (IOException e) {} }
			if (zipFile != null) { try { zipFile.close(); } catch (IOException e) {} }
		}
		return sb.toString();
	}

	private void initData(String path) throws IOException {
		copyData(assetManager.open(path), new FileOutputStream(dataFile));
	}

	private void copyData(final InputStream in, final OutputStream out) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		try {
			writeData(bis, out);
		} finally {
			if (bis != null) { try { bis.close(); } catch (IOException ioe) {} }
		}
	}

	private void writeData(final InputStream is, final OutputStream os) throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(os);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ( (len = is.read(buffer, 0, buffer.length)) > 0) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
		} finally {
			if (bos != null) { try { bos.close(); } catch (IOException ioe) {} }
		}
	}
}
