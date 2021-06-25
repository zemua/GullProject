package devs.mrp.gullproject.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;

import org.springframework.stereotype.Service;

@Service
public class ProxyUtils {

	public String UrlfyListOfParameter(String name, List<String> parameters) {
		StringBuilder builder = new StringBuilder();
		ListIterator<String> iterator = parameters.listIterator();
		try {
			if (iterator.hasNext()) {
				builder.append("?" + name + "=");
				builder.append(URLEncoder.encode(iterator.next(), StandardCharsets.UTF_8.toString()));
			}
			while (iterator.hasNext()) {
				builder.append(",");
				builder.append(URLEncoder.encode(iterator.next(), StandardCharsets.UTF_8.toString()));
			}
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
}
