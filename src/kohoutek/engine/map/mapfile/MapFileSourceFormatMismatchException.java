package kohoutek.engine.map.mapfile;

public class MapFileSourceFormatMismatchException extends Exception {
	private static final long serialVersionUID = 1L;
	
	   public MapFileSourceFormatMismatchException() {

	    }

	    public MapFileSourceFormatMismatchException(String message) {
	        super(message);

	    }

	    public MapFileSourceFormatMismatchException(Throwable cause) {
	        super(cause);
	    }

	    public MapFileSourceFormatMismatchException(String message, Throwable cause) {
	        super(message, cause);
	    }

}
