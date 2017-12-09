package message.builder;

public interface IMessageBuilder {

	/**
	 * Puts a new object into the data set
	 *
	 * @param name      Name of the object (must me unique)
	 * @param o         The object
	 * @return          this
	 */
	IMessageBuilder put(String name, Object o);

	/**
	 * Builds a {@link String} based an the data passed via put
	 *
	 * @return          The String that was built
	 */
	String get();
}
