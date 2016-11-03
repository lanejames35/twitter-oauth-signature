/****************************************************************/
// Twitter OAuth Generator
// Author: James Lane
// Created: 1 November 2016
//
// This class creates an OAuth authorization header for use
// with Twitter REST API calls. The class is hard coded to use
// GET HTTPS requests and must be updated to use other methods.
//
// This class requires the Base64Coder class as there is no base 64
// encoder/decoder in the standard Java SDK up to Java 7.
// See http://www.source-code.biz/base64coder/java/
// Sice Java 8 there is java.util.Base64
//
// Users will need to enter their Twitter credentials where
// indicated in UPPERCASE
/***************************************************************/
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import biz.source_code.base64Coder.Base64Coder;
/****************************************************************/
public class OAuthSignature {
	public static String getHeader(String url) {
	/** 
	* Get the time for the timestamp 
	*/
		long millis = System.currentTimeMillis() / 1000;

	/**
	* Random string for oauth_nonce. 32 bytes of random data.
	*/
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random rand = new Random();
		char[] text = new char[32];
		for(int foo=0; foo < 32; foo++){
			text[foo] = characters.charAt(rand.nextInt(characters.length()));
		}
		String nonce_str = new String(text);
	/**
	* Listing of all parameters necessary to retrieve a token
	* (sorted lexicographically as demanded)
	*/
		String[][] data
			data = [
				/* Add any querystring or body parameters here*/
				["oauth_consumer_key","CONSUMER_KEY"],
				["oauth_nonce",nonce_str],
				["oauth_signature",""],
				["oauth_signature_method","HMAC-SHA1"],
				["oauth_timestamp",millis],
				["oauth_token","ACCESS_TOKEN"],
				["oauth_version","1.0"]
			];
	/**
	* Generation of the parameter string. Modify the IF statement when the size of the Array data is changed.
	*/
		StringBuilder my_string = new StringBuilder(); 
		for(int x=0; x < data.length; x++){
			if (x != 2){
				my_string.append(URLEncoder.encode(data[x][0],"UTF-8") + "=" + URLEncoder.encode(data[x][1],"UTF-8") + "&");
				}
			}
		/* Cut out the last apmersand */
		String parameter_string = my_string.substring(0,my_string.length()-1);
		/* Replace + with %20 */
		parameter_string = parameter_string.replaceAll("\\+","%20");
	/**
	* Generation of the signature base string
	*/
		String signature_base_string = "GET&" + URLEncoder.encode(url, "UTF-8") + "&" + URLEncoder.encode(parameter_string,"UTF-8");
	/**
	* Generate the Singing Key
	*/	
		String singing_key = URLEncoder.encode("CONSUMER_SECRET", "UTF-8")+"&"+URLEncoder.encode("TOKEN_SECRET", "UTF-8");
	/**
	* Sign the request
	*/
		Mac m = Mac.getInstance("HmacSHA1");
		m.init(new SecretKeySpec(singing_key.getBytes(), "HmacSHA1"));
		m.update(signature_base_string.getBytes());
		byte[] res = m.doFinal();
		data[2][1] = URLEncoder.encode(Base64Coder.encodeLines(res).trim());
	/**
    * Create the header for the request
    */
		String header = "OAuth ";
         for(String[] item : data) {
            header += item[0]+'=\"'+item[1]+'\", ';
         }
		/*Cut off last appended comma*/
        header = header.substring(0, header.length()-2);
		return header;
	}
/* Dummy constructor */
private Request(){}
} // end of OAuthSignature class