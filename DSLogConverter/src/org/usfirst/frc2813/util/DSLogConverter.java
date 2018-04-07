package org.usfirst.frc2813.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DSLogConverter {
	private final String filename;
	private final String outputFilename;
	private final int fileLength; 
	private final File file;
	private final byte[] buffer;
	private int offset = 0;
	private int version = 0;
	private Date date = null;
	private int milliseconds = 0;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm:ss");
	private static final String LINE = "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n";

	public DSLogConverter(String filename) throws IOException {
		this.filename = filename;
		this.outputFilename = filename.endsWith(".dsevents") ? filename.replaceAll(".dsevents$", ".log") : filename + ".log";
		this.file = new File(filename);
		this.fileLength = (int) file.length();
		this.buffer = new byte[fileLength];
		System.out.println("Processing " + filename + "\n      Into " + outputFilename);
		readFile();
	}

	private void readFile() 
			throws IOException 
	{
		FileInputStream in = new FileInputStream(file);
		// Read the whole file
		int bytesRead = in.read(buffer);
		if(bytesRead != file.length()) {
			in.close();
			throw new RuntimeException("Error reading the file: " + filename);
		}
		// Close the file
		in.close();
	}
	
	public int readVersion() {
		return readUint32(); 
	}

	// NB: Big endian
	private int readUint32() {
		int x = 0;
		x |= (((int)buffer[offset+0] & 0xff) << 24);
		x |= (((int)buffer[offset+1] & 0xff) << 16);
		x |= (((int)buffer[offset+2] & 0xff) <<  8);
		x |= (((int)buffer[offset+3] & 0xff) <<  0);
		offset += 4;
		return (int)x;
	}

	// NB: Big endian
	private long readUint64() {
		long x = 0;
		x |= (((long)buffer[offset+0] & 0xff) << 56);
		x |= (((long)buffer[offset+1] & 0xff) << 48);
		x |= (((long)buffer[offset+2] & 0xff) << 40);
		x |= (((long)buffer[offset+3] & 0xff) << 32);
		x |= (((long)buffer[offset+4] & 0xff) << 24);
		x |= (((long)buffer[offset+5] & 0xff) << 16);
		x |= (((long)buffer[offset+6] & 0xff) <<  8);
		x |= (((long)buffer[offset+7] & 0xff) <<  0);
		offset += 8;
		return x & 0xffffffffffffffffl;
	}
	
	private String[] readMessageBatch() {
		int messageLength = readUint32();
		if((offset+messageLength) > fileLength) {
			System.out.println("ERROR: Invalid length.  Wanted " + messageLength + " but got " + (fileLength - offset));
			messageLength = fileLength - offset;
		}
		String messageBatch = new String(buffer,offset,messageLength);
		String[] messages = messageBatch.replaceAll("^<TagVersion>1 ", "").split(".<TagVersion>1 ");
		for(int i = 0; i < messages.length; i++) {
			messages[i] = messages[i].replaceAll("<time> ", "T=").replaceAll("<message> ", "");
		}
		offset += messageLength;
		return messages;
	}

	private Date readDate() {
		// Convert to milliseconds since epoch
		java.util.Date time = new java.util.Date((long)readUint64()*1000);
		// Epoch is 1904, not 1970 so adjust
		int year_offset = 1904-1970;
		time.setYear(time.getYear() + year_offset);
		// Return the result
		return time;
	}

	private int readMilliseconds() {
		long raw = readUint64();
		// convert from Java's lame signed-only idea of a long int to an unsigned 64-bit int
		BigDecimal d = new BigDecimal(raw & ~0x8000000000000000l);
		if((raw & 0x8000000000000000l) != 0) {
			d.add(new BigDecimal("9223372036854775808"));
		}
		// Now return three decimal places of fractional seconds where one second is MAX_UINT64
		return d.multiply(new BigDecimal(1000)).divide(new BigDecimal("18446744073709551615"), BigDecimal.ROUND_HALF_UP).intValue();
	}

	private String formatTimestamp(Date time, int milliseconds) {
		return String.format("%s.%03d", dateFormat.format(time), milliseconds);
	}

	private void convert() 
		throws IOException 
	{
		FileOutputStream f = new FileOutputStream(outputFilename);
		OutputStreamWriter osw = new OutputStreamWriter(f);
		// Parse the file
		this.version = readVersion(); 
		this.date = readDate();
		this.milliseconds = readMilliseconds();
		osw.write(LINE);
		osw.write("Original: " + filename + "\n");
		osw.write("Version: " + version + "\n");
		osw.write("Timestamp: " + formatTimestamp(date, milliseconds) + "\n");
		osw.write(LINE);
		if(this.version != 3) {
			osw.write("Unsupported Version.");
		} else {
			while(offset < fileLength) {
				// Read the timing info
				Date msgTime = readDate();
				int msgMilliseconds = readMilliseconds();
				// Read the messages
				for(String msg : readMessageBatch()) {
					osw.write(formatTimestamp(msgTime, msgMilliseconds) + " " + msg + "\n");
				}
			}
		}
		osw.write(LINE);
		osw.write("End of file.\n");
		osw.write(LINE);
		osw.flush();
		f.flush();
		f.close();
	}

	private static void process(String path) throws IOException {
		File f = new File(path);
		if(f.isDirectory()) {
			for(String filename : f.list()) {
				if(filename.endsWith(".dsevents")) {
					new DSLogConverter(f.getPath() + File.separatorChar + filename).convert();
				}
			}
		} else {
			new DSLogConverter(path).convert();
		}
	}
	
	public static void main(String[] args) throws IOException {
		try {
			File f = null;
			if(args.length == 0) {
				process("C:\\Users\\Public\\Documents\\FRC\\Log Files");
			} else {
				for(String arg : args) {
					process(arg);
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}
}
