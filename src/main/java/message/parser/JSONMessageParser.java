package message.parser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSONMessageParser implements IMessagePraser {

	/**
	 * Returns a {@link Map}, where the key is the property name
	 *
	 * @param msg   JSON {@link String} to be parsed
	 * @return      {@link Map}, where the key is the property name
	 */
	@Override
	public Map<String, Object> parse(String msg) {
		JSONObject data = new JSONObject(msg);
		Map<String, Object> objects = new HashMap<>();

		for (String key : data.keySet()) {
			objects.put(key, data.get(key));
		}

		return objects;
	}
}
