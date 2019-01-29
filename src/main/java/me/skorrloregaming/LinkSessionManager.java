package me.skorrloregaming;

public class LinkSessionManager {

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}

	public static byte[] decodeHex(char[] data) {
		try {
			int len = data.length;
			byte[] out = new byte[len >> 1];
			for (int i = 0, j = 0; j < len; i++) {
				int f = toDigit(data[j], j) << 4;
				j++;
				f = f | toDigit(data[j], j);
				j++;
				out[i] = (byte) (f & 0xFF);
			}
			return out;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected static int toDigit(char ch, int index) {
		return Character.digit(ch, 16);
	}

	public static String encodeHex(String str) {
		return new String(encodeHex(str.getBytes()));
	}

}
