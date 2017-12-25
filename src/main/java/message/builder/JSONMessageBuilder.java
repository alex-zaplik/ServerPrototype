package message.builder;

import org.json.JSONObject;

public class JSONMessageBuilder implements IMessageBuilder {

	private JSONObject data = new JSONObject();

	/**
	 * Puts a new object into the data set
	 *
	 * @param name      Name of the object (must me unique)
	 * @param o         The object
	 * @return          this
	 */
	@Override
	public IMessageBuilder put(String name, Object o) {
		data.put(name, o);
		return this;
	}

	/**
	 * Builds a JSON {@link String} based an the data passed via put
	 *
	 * @return          The JSON String that was built
	 */
	@Override
	public String get() {
		String result = data.toString();
		data = new JSONObject();
		return result;
	}
}
