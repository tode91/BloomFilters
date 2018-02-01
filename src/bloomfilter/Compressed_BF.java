package bloomfilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class Compressed_BF {
		
	private int m;
	private int k;
	private int n;
	private double fp_obj;
	private int numberOfAddedElement;
	private String typeHashing;
	private byte[] map;
	private String compression;
	private String cl_type;
	
	/* Constructor */
	
	public Compressed_BF(StandardBloomFilter bf, String compression){
		m=bf.getM();
		k=bf.getK();
		n=bf.getNumberOfExpectedElement();
		fp_obj=bf.getProbabilityFPObj();
		typeHashing=bf.getTypeHashing();
		numberOfAddedElement=bf.getNumberOfAddedElement();
		this.compression=compression;
		map=compress(bf.getBitSet().toByteArray());
		bf=null;
	}
	
	public Compressed_BF(CountingBloomFilter bf,String compression){
		m=bf.getM();
		k=bf.getK();
		n=bf.getNumberOfExpectedElement();
		fp_obj=bf.getProbabilityFPObj();
		typeHashing=bf.getTypeHashing();
		numberOfAddedElement=bf.getNumberOfAddedElement();
		this.compression=compression;
		cl_type=bf.getTypeCounter().getSimpleName();
		switch(cl_type){
			case "Byte":{map=compress((byte[])bf.getBitSet());break;}
			case "Short":{map=compress(this.short2byte((short[])bf.getBitSet()));break;}
			case "Integer":{map=compress(this.int2byte((int[])bf.getBitSet()));break;}
		}
		map=compress(this.int2byte((int[])bf.getBitSet()));
		bf=null;
	}
	
	/* compress*/
	private byte[] compress(byte[] bt){
		switch(compression){
			case "GZIP": {
				return compressGZIP(bt);
			}
			case "DEFLATER": {
				return compressDEFLATER(bt);
			}
		}
		return null;
	}
	
	private byte[] decompress(byte[] bt){
		switch(compression){
			case "GZIP": {
				return decompressGZIP(bt);
			}
			case "DEFLATER": {
				return decompressDEFLATER(bt);
			}
		}
		return null;
	}
	
	private byte[] compressDEFLATER(byte[] bt){
		Deflater compress=new Deflater();
		compress.setLevel(Deflater.BEST_COMPRESSION);
		compress.setInput(bt);
		ByteArrayOutputStream output= new ByteArrayOutputStream(bt.length);
		compress.finish();
		byte[] buffer=new byte[1024];
		while(!compress.finished()){
			int count=compress.deflate(buffer);
			output.write(buffer,0,count);
		}
		try {
			output.close();
		} catch (IOException e) {
		}
		return output.toByteArray();	
	}
	
	private byte[] decompressDEFLATER(byte[] bt){
		Inflater decompress=new Inflater();
		decompress.setInput(bt);
		ByteArrayOutputStream output= new ByteArrayOutputStream(bt.length);
		byte[] buffer=new byte[1024];
		while(!decompress.finished()){
			int count=0;
			try {
				count = decompress.inflate(buffer);
			} catch (DataFormatException e) {
			}
			output.write(buffer,0,count);
		}
		try {
			output.close();
		} catch (IOException e) {
		}
		return output.toByteArray();	
	}
	
	private byte[] compressGZIP(byte[] bt){
		try{
			ByteArrayOutputStream compress =new ByteArrayOutputStream(bt.length);
			GZIPOutputStream gzip =new GZIPOutputStream(compress);
			gzip.write(bt);
			gzip.close();
			compress.close();
			return compress.toByteArray();
		}catch(Exception e){
			return null;
		}
	}
	
	private byte[] decompressGZIP(byte[] bt){
		byte[] buffer = new byte[1024];
		try{
			ByteArrayOutputStream decompress =new ByteArrayOutputStream();
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bt));
		    int index = -1;
		    while ( ( index = gzip.read( buffer ) ) != -1 ){
		    	decompress.write( buffer, 0, index );
		    }
		    return decompress.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/*method to convert array of byte into array of int and viceversa*/

	public int[] byte2int(byte[]src) {
        int dstLength = src.length >>> 2;
        int[]dst = new int[dstLength];
        
        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            int x = 0;
            x += (src[j++] & 0xff) << 0;
            x += (src[j++] & 0xff) << 8;
            x += (src[j++] & 0xff) << 16;
            x += (src[j++] & 0xff) << 24;
            dst[i] = x;
        }
        return dst;
    }
	
	public short[] byte2short(byte[]src) {
        int dstLength = src.length >>> 2;
        short[]dst = new short[dstLength];
        
        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            short x = 0;
            x += (src[j++] & 0xff) << 0;
            x += (src[j++] & 0xff) << 8;
            dst[i] = x;
        }
        return dst;
    }
	
	public byte[] int2byte(int[]src) {
	    int srcLength = src.length;
	    byte[]dst = new byte[srcLength << 2];
	    
	    for (int i=0; i<srcLength; i++) {
	        int x = src[i];
	        int j = i << 2;
	        dst[j++] = (byte) ((x >>> 0) & 0xff);           
	        dst[j++] = (byte) ((x >>> 8) & 0xff);
	        dst[j++] = (byte) ((x >>> 16) & 0xff);
	        dst[j++] = (byte) ((x >>> 24) & 0xff);
	    }
	    return dst;
	}
	
	public byte[] short2byte(short[]src) {
	    int srcLength = src.length;
	    byte[]dst = new byte[srcLength << 2];
	    
	    for (int i=0; i<srcLength; i++) {
	        int x = src[i];
	        int j = i << 2;
	        dst[j++] = (byte) ((x >>> 0) & 0xff);           
	        dst[j++] = (byte) ((x >>> 8) & 0xff);
	    }
	    return dst;
	}
	
	/* method to rebuilt Bloom Filter*/
	
	public StandardBloomFilter decompressBloomFilter(){
		return new StandardBloomFilter(m, n, k,fp_obj, typeHashing,BitSet.valueOf(decompress(map)),numberOfAddedElement);
	}
	
	public CountingBloomFilter decompressCountingBloomFilterInt(){
		try{
			switch(cl_type){
				case "Byte":{return new CountingBloomFilter(m, n, k,fp_obj, typeHashing,decompress(map),numberOfAddedElement,Class.forName(cl_type));}
				case "Short":{return new CountingBloomFilter(m, n, k,fp_obj, typeHashing,byte2short(decompress(map)),numberOfAddedElement,Class.forName(cl_type));}
				case "Integer":{return new CountingBloomFilter(m, n, k,fp_obj, typeHashing,byte2int(decompress(map)),numberOfAddedElement,Class.forName(cl_type));}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
}