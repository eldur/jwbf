public class VersionTransform {
	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("A version to transform must be given in a single argument.");
		}
		System.out.println(getVersion(args[0]));
	}

	private static int getVersion(final String v) {
		if (v == null || v.length() <= 0) {
			throw new IllegalArgumentException("The given version '" + v + "' is invalid.");
		}
		String ret = v;
		int dots = v.indexOf(":") + 1;
		if (v.contains("M")) {
			ret = v.substring(dots, v.indexOf("M"));
		} else if (v.contains("S")) {
			ret = v.substring(dots, v.indexOf("S"));
		} else {
			ret = v.substring(dots, v.length());
		}
		return Integer.parseInt(ret);
	}
}
