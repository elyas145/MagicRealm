package utils.string;

import java.util.List;

public class TextTools {

	public static void wrap(String msg, List<String> dest, int chars) {
		while (msg.length() > chars) { // split msg
			int i = chars;
			int j, k;
			k = i - 1;
			for (j = i; j > 0; --j) {
				if (msg.charAt(j) == ' ') {
					k = j - 1;
					j = 0;
				}
			}
			for (j = k; j > 0; --j) {
				if (msg.charAt(j) != ' ') {
					i = j + 1;
					j = 0;
				}
			}
			dest.add(msg.substring(0, i));
			msg = msg.substring(k + 1, msg.length());
			if (dest.size() >= chars) {
				i = msg.length();
			}
		}
		dest.add(msg);
	}

}
