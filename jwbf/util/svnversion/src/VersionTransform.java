
public class VersionTransform {

	
	public static void main(String [] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(getVersion(args[i]));
			break;
		}
		
		
		
	}
	private static int getVersion(final String v) {
		String ret = v;
		if(v.length() <= 0) {
			System.err.println("no Version");
		} else {
			int dots = v.indexOf(":") + 1;
			if (v.contains("M")) {
				ret = v.substring(dots  , v.indexOf("M"));
			} else if (v.contains("S")) {
				ret = v.substring(dots  , v.indexOf("S"));
			} else {
				ret = v.substring(dots  , v.length());
			}
			int version = Integer.parseInt(ret);
			version = version + 12000;

			return  version;
			
		}
		return 0;
		
	}
	
	
}
