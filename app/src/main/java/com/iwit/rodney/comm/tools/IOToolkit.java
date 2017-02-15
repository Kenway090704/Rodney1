/*
 * File name    : IOToolkit.java
 * Copyright    : (Shenzhen) I-WIT Digital Co. .All rights reserved
 * Project      : i43 Pet game
 * JDK          : JDK 1.6
 * SDK          : Android 2.3.3
 * Author       : daniel
 * Date         : Mar 21, 2012
 * Comments     : 
 * Mod date     : 
 * Mod by       : 
 * Mod reason   : 
 */

package com.iwit.rodney.comm.tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.util.Log;

/**
 * <Description> 处理解压缩
 * 
 * @author daniel
 * @date Mar 21, 2012
 * @version 1.0.0
 */
public class IOToolkit {

	public static short readCShort(InputStream in) throws IOException {

		int ch2 = in.read();
		int ch1 = in.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	public static short readShort(InputStream in) throws IOException {

		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	public static final void writeCShort(OutputStream out, int v)
			throws IOException {

		out.write((v >>> 0) & 0xFF);
		out.write((v >>> 8) & 0xFF);
	}

	public static String readString(DataInputStream in, int length)
			throws IOException {

		byte[] buffer = new byte[length];
		length = in.read(buffer);
		if (length == -1) {
			return null;
		}
		return new String(buffer);
	}

	public static short[] readShortArray(DataInputStream in, int length)
			throws IOException {

		short[] array = new short[length];
		for (int i = 0; i < length; i++) {
			array[i] = in.readShort();
		}
		return array;
	}

	public static String[] readLineArray(BufferedReader in, boolean trim)
			throws IOException {

		ArrayList<String> array = new ArrayList<String>();
		String line;
		while ((line = in.readLine()) != null) {
			if (trim) {
				line = line.trim();
			}
			array.add(line);
		}
		return array.toArray(new String[0]);
	}

	public static String[] readLineArray(InputStream in, boolean trim)
			throws IOException {

		return readLineArray(new BufferedReader(new InputStreamReader(in)),
				trim);
	}

	public static String[] readLineArray(File file, boolean trim)
			throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(file));
		String[] array = readLineArray(in, trim);
		in.close();
		return array;
	}

	public static void unzip(ZipFile zip, File path) throws IOException {
		Enumeration<? extends ZipEntry> emu = zip.entries();
		int size = 2048;
		while (emu.hasMoreElements()) {
			ZipEntry entry = emu.nextElement();
			if (entry.isDirectory()) {
				new File(path, entry.getName()).mkdirs();
				continue;
			}
			BufferedInputStream bis = new BufferedInputStream(
					zip.getInputStream(entry));
			File file = new File(path, entry.getName());
			File parent = file.getParentFile();
			if (parent != null && (!parent.exists())) {
				parent.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos, size);

			int count;
			byte data[] = new byte[size];
			while ((count = bis.read(data)) != -1) {
				bos.write(data, 0, count);
			}
			bos.flush();
			bos.close();
			bis.close();
		}
		zip.close();
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {

		byte buffer[] = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
	}

	public static boolean close(Closeable stream) {

		try {
			stream.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void copy(Context context, int resource, File file)
			throws IOException {

		InputStream in = context.getResources().openRawResource(resource);
		OutputStream out = new FileOutputStream(file);
		try {
			IOToolkit.copy(in, out);
		} catch (IOException e) {
			IOToolkit.close(in);
			IOToolkit.close(out);
			throw e;
		}
		IOToolkit.close(in);
		IOToolkit.close(out);
	}

	public static void unzip(Context context, int resource, File file)
			throws IOException {

		if (file.exists()) {
			if (file.isDirectory() == false) {
				file.delete();
				file.mkdirs();
			}
		} else {
			file.mkdirs();
		}
		File zip = new File(file, String.format("%d.zip", resource));

		try {
			if (zip.exists() == false) {
				IOToolkit.copy(context, resource, zip);
			}
			IOToolkit.unzip(new ZipFile(zip), file);
		} catch (IOException e) {
			throw e;
		} finally {
			zip.delete();
		}

	}

	public static boolean upZipFile(File zipFile, String folderPath) {
		boolean unzip = false;
			ZipFile zfile;
			try {
				zfile = new ZipFile(zipFile);
				@SuppressWarnings("rawtypes")
				Enumeration zList = zfile.entries();
				ZipEntry ze = null;
				byte[] buf = new byte[1024];
				while (zList.hasMoreElements()) {
					ze = (ZipEntry) zList.nextElement();
					if (ze.isDirectory()) {
						String dirstr = folderPath + ze.getName();
						Log.d("upZipFile", "ze.getName() = " + ze.getName());
						dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
						Log.d("upZipFile", "str = " + dirstr);
						File f = new File(dirstr);
						f.mkdir();
						continue;
					}
					Log.d("upZipFile", "ze.getName() = " + ze.getName());
					OutputStream os = new BufferedOutputStream(
							new FileOutputStream(getRealFileName(folderPath,
									ze.getName())));
					InputStream is = new BufferedInputStream(
							zfile.getInputStream(ze));
					int readLen = 0;
					while ((readLen = is.read(buf, 0, 1024)) != -1) {
						os.write(buf, 0, readLen);
					}
					is.close();
					os.close();
				}
				zfile.close();
				unzip = true;
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				Log.e("upZipFile", "解压失败原因: " + e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("upZipFile", "解压失败原因: " + e);
				e.printStackTrace();
			}
		return unzip;
	}

	public static boolean unzip(String zipFileName, String outputDirectory)
			throws IOException {
		boolean unzip = false;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			@SuppressWarnings("rawtypes")
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File dest = new File(outputDirectory);
			dest.mkdirs();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				String entryName = zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				try {
					if (zipEntry.isDirectory()) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length() - 1);
						File f = new File(outputDirectory + File.separator
								+ name);
						f.mkdirs();
					} else {
						int index = entryName.lastIndexOf("\\");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						index = entryName.lastIndexOf("/");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						File f = new File(outputDirectory + File.separator
								+ zipEntry.getName());
						// f.createNewFile();
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);
						int c;
						byte[] by = new byte[1024];
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}
					unzip = true;
				} catch (IOException ex) {
					ex.printStackTrace();
					throw new IOException("解压失败：" + ex.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new IOException("解压失败：" + ex.toString());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}
		return unzip;
	}

	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					substr = new String(substr.getBytes("8859_1"), "GB2312");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);
			}

			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				substr = new String(substr.getBytes("8859_1"), "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			return ret;
		}
		return ret;
	}
}
