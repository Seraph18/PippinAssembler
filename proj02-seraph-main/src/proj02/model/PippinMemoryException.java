package proj02.model;

public class PippinMemoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PippinMemoryException() { super(); }

	public PippinMemoryException(String message) { super(message); }

	public PippinMemoryException(Throwable cause) { super(cause); }

	public PippinMemoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public PippinMemoryException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
