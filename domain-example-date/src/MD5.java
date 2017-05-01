

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String MD5String(String str)
	{
		try {
			MessageDigest msgDigest = MessageDigest.getInstance("MD5");
			msgDigest.reset();
			msgDigest.update(str.getBytes("UTF-8"));
			byte[] byteArrary = msgDigest.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for(int i=0; i<byteArrary.length;i++)
			{
				String tmp = Integer.toHexString(0xFF&byteArrary[i]);
				if (tmp.length() == 1)
				{
					md5StrBuff.append(0).append(tmp);
				}
				else
				{
					md5StrBuff.append(tmp);
				}
			}
			return md5StrBuff.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
