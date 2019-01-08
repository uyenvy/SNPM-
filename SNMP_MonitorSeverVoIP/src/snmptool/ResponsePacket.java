package snmptool;

public class ResponsePacket {
	private String error;
	private String value;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ResponsePacket(String error, String value) {
		super();
		this.error = error;
		this.value = value;
	}
	@Override
	public String toString() {
		return "OIDValue [error=" + error + ", value=" + value + "]";
	}
	

}
