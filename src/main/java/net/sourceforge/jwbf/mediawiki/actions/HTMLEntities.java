package net.sourceforge.jwbf.mediawiki.actions;

import java.util.Hashtable;

/**
 * Collection of static methods to convert special and extended
 * characters into HTML entitities and vice versa.<br/><br/>
 * Copyright (c) 2004-2005 Tecnick.com S.r.l (www.tecnick.com) Via Ugo Foscolo
 * n.19 - 09045 Quartu Sant'Elena (CA) - ITALY - www.tecnick.com -
 * info@tecnick.com<br/>
 * Project homepage: <a href="http://htmlentities.sourceforge.net" target="_blank">http://htmlentities.sourceforge.net</a>
 * <br/>
 *
 * License: http://www.gnu.org/copyleft/lesser.html LGPL
 * @author Nicola Asuni [www.tecnick.com].
 * @version 1.0.004
 */
final class HTMLEntities {

	/**
	 * Translation table for HTML entities.<br/>
	 * reference: W3C - Character entity references in HTML 4 [<a href="http://www.w3.org/TR/html401/sgml/entities.html" target="_blank">http://www.w3.org/TR/html401/sgml/entities.html</a>].
	 */
	private static final Object[][] HTML_ENTITIES_TABLE = {
		{ ("&Aacute;"), Integer.valueOf(193) },
		{ ("&aacute;"), Integer.valueOf(225) },
		{ ("&Acirc;"), Integer.valueOf(194) },
		{ ("&acirc;"), Integer.valueOf(226) },
		{ ("&acute;"), Integer.valueOf(180) },
		{ ("&AElig;"), Integer.valueOf(198) },
		{ ("&aelig;"), Integer.valueOf(230) },
		{ ("&Agrave;"), Integer.valueOf(192) },
		{ ("&agrave;"), Integer.valueOf(224) },
		{ ("&alefsym;"), Integer.valueOf(8501) },
		{ ("&Alpha;"), Integer.valueOf(913) },
		{ ("&alpha;"), Integer.valueOf(945) },
		{ ("&amp;"), Integer.valueOf(38) },
		{ ("&and;"), Integer.valueOf(8743) },
		{ ("&ang;"), Integer.valueOf(8736) },
		{ ("&Aring;"), Integer.valueOf(197) },
		{ ("&aring;"), Integer.valueOf(229) },
		{ ("&asymp;"), Integer.valueOf(8776) },
		{ ("&Atilde;"), Integer.valueOf(195) },
		{ ("&atilde;"), Integer.valueOf(227) },
		{ ("&Auml;"), Integer.valueOf(196) },
		{ ("&auml;"), Integer.valueOf(228) },
		{ ("&bdquo;"), Integer.valueOf(8222) },
		{ ("&Beta;"), Integer.valueOf(914) },
		{ ("&beta;"), Integer.valueOf(946) },
		{ ("&brvbar;"), Integer.valueOf(166) },
		{ ("&bull;"), Integer.valueOf(8226) },
		{ ("&cap;"), Integer.valueOf(8745) },
		{ ("&Ccedil;"), Integer.valueOf(199) },
		{ ("&ccedil;"), Integer.valueOf(231) },
		{ ("&cedil;"), Integer.valueOf(184) },
		{ ("&cent;"), Integer.valueOf(162) },
		{ ("&Chi;"), Integer.valueOf(935) },
		{ ("&chi;"), Integer.valueOf(967) },
		{ ("&circ;"), Integer.valueOf(710) },
		{ ("&clubs;"), Integer.valueOf(9827) },
		{ ("&cong;"), Integer.valueOf(8773) },
		{ ("&copy;"), Integer.valueOf(169) },
		{ ("&crarr;"), Integer.valueOf(8629) },
		{ ("&cup;"), Integer.valueOf(8746) },
		{ ("&curren;"), Integer.valueOf(164) },
		{ ("&dagger;"), Integer.valueOf(8224) },
		{ ("&Dagger;"), Integer.valueOf(8225) },
		{ ("&darr;"), Integer.valueOf(8595) },
		{ ("&dArr;"), Integer.valueOf(8659) },
		{ ("&deg;"), Integer.valueOf(176) },
		{ ("&Delta;"), Integer.valueOf(916) },
		{ ("&delta;"), Integer.valueOf(948) },
		{ ("&diams;"), Integer.valueOf(9830) },
		{ ("&divide;"), Integer.valueOf(247) },
		{ ("&Eacute;"), Integer.valueOf(201) },
		{ ("&eacute;"), Integer.valueOf(233) },
		{ ("&Ecirc;"), Integer.valueOf(202) },
		{ ("&ecirc;"), Integer.valueOf(234) },
		{ ("&Egrave;"), Integer.valueOf(200) },
		{ ("&egrave;"), Integer.valueOf(232) },
		{ ("&empty;"), Integer.valueOf(8709) },
		{ ("&emsp;"), Integer.valueOf(8195) },
		{ ("&ensp;"), Integer.valueOf(8194) },
		{ ("&Epsilon;"), Integer.valueOf(917) },
		{ ("&epsilon;"), Integer.valueOf(949) },
		{ ("&equiv;"), Integer.valueOf(8801) },
		{ ("&Eta;"), Integer.valueOf(919) },
		{ ("&eta;"), Integer.valueOf(951) },
		{ ("&ETH;"), Integer.valueOf(208) },
		{ ("&eth;"), Integer.valueOf(240) },
		{ ("&Euml;"), Integer.valueOf(203) },
		{ ("&euml;"), Integer.valueOf(235) },
		{ ("&euro;"), Integer.valueOf(8364) },
		{ ("&exist;"), Integer.valueOf(8707) },
		{ ("&fnof;"), Integer.valueOf(402) },
		{ ("&forall;"), Integer.valueOf(8704) },
		{ ("&frac12;"), Integer.valueOf(189) },
		{ ("&frac14;"), Integer.valueOf(188) },
		{ ("&frac34;"), Integer.valueOf(190) },
		{ ("&frasl;"), Integer.valueOf(8260) },
		{ ("&Gamma;"), Integer.valueOf(915) },
		{ ("&gamma;"), Integer.valueOf(947) },
		{ ("&ge;"), Integer.valueOf(8805) },
		{ ("&harr;"), Integer.valueOf(8596) },
		{ ("&hArr;"), Integer.valueOf(8660) },
		{ ("&hearts;"), Integer.valueOf(9829) },
		{ ("&hellip;"), Integer.valueOf(8230) },
		{ ("&Iacute;"), Integer.valueOf(205) },
		{ ("&iacute;"), Integer.valueOf(237) },
		{ ("&Icirc;"), Integer.valueOf(206) },
		{ ("&icirc;"), Integer.valueOf(238) },
		{ ("&iexcl;"), Integer.valueOf(161) },
		{ ("&Igrave;"), Integer.valueOf(204) },
		{ ("&igrave;"), Integer.valueOf(236) },
		{ ("&image;"), Integer.valueOf(8465) },
		{ ("&infin;"), Integer.valueOf(8734) },
		{ ("&int;"), Integer.valueOf(8747) },
		{ ("&Iota;"), Integer.valueOf(921) },
		{ ("&iota;"), Integer.valueOf(953) },
		{ ("&iquest;"), Integer.valueOf(191) },
		{ ("&isin;"), Integer.valueOf(8712) },
		{ ("&Iuml;"), Integer.valueOf(207) },
		{ ("&iuml;"), Integer.valueOf(239) },
		{ ("&Kappa;"), Integer.valueOf(922) },
		{ ("&kappa;"), Integer.valueOf(954) },
		{ ("&Lambda;"), Integer.valueOf(923) },
		{ ("&lambda;"), Integer.valueOf(955) },
		{ ("&lang;"), Integer.valueOf(9001) },
		{ ("&laquo;"), Integer.valueOf(171) },
		{ ("&larr;"), Integer.valueOf(8592) },
		{ ("&lArr;"), Integer.valueOf(8656) },
		{ ("&lceil;"), Integer.valueOf(8968) },
		{ ("&ldquo;"), Integer.valueOf(8220) },
		{ ("&le;"), Integer.valueOf(8804) },
		{ ("&lfloor;"), Integer.valueOf(8970) },
		{ ("&lowast;"), Integer.valueOf(8727) },
		{ ("&loz;"), Integer.valueOf(9674) },
		{ ("&lrm;"), Integer.valueOf(8206) },
		{ ("&lsaquo;"), Integer.valueOf(8249) },
		{ ("&lsquo;"), Integer.valueOf(8216) },
		{ ("&macr;"), Integer.valueOf(175) },
		{ ("&mdash;"), Integer.valueOf(8212) },
		{ ("&micro;"), Integer.valueOf(181) },
		{ ("&middot;"), Integer.valueOf(183) },
		{ ("&minus;"), Integer.valueOf(8722) },
		{ ("&Mu;"), Integer.valueOf(924) },
		{ ("&mu;"), Integer.valueOf(956) },
		{ ("&nabla;"), Integer.valueOf(8711) },
		{ ("&nbsp;"), Integer.valueOf(160) },
		{ ("&ndash;"), Integer.valueOf(8211) },
		{ ("&ne;"), Integer.valueOf(8800) },
		{ ("&ni;"), Integer.valueOf(8715) },
		{ ("&not;"), Integer.valueOf(172) },
		{ ("&notin;"), Integer.valueOf(8713) },
		{ ("&nsub;"), Integer.valueOf(8836) },
		{ ("&Ntilde;"), Integer.valueOf(209) },
		{ ("&ntilde;"), Integer.valueOf(241) },
		{ ("&Nu;"), Integer.valueOf(925) },
		{ ("&nu;"), Integer.valueOf(957) },
		{ ("&Oacute;"), Integer.valueOf(211) },
		{ ("&oacute;"), Integer.valueOf(243) },
		{ ("&Ocirc;"), Integer.valueOf(212) },
		{ ("&ocirc;"), Integer.valueOf(244) },
		{ ("&OElig;"), Integer.valueOf(338) },
		{ ("&oelig;"), Integer.valueOf(339) },
		{ ("&Ograve;"), Integer.valueOf(210) },
		{ ("&ograve;"), Integer.valueOf(242) },
		{ ("&oline;"), Integer.valueOf(8254) },
		{ ("&Omega;"), Integer.valueOf(937) },
		{ ("&omega;"), Integer.valueOf(969) },
		{ ("&Omicron;"), Integer.valueOf(927) },
		{ ("&omicron;"), Integer.valueOf(959) },
		{ ("&oplus;"), Integer.valueOf(8853) },
		{ ("&or;"), Integer.valueOf(8744) },
		{ ("&ordf;"), Integer.valueOf(170) },
		{ ("&ordm;"), Integer.valueOf(186) },
		{ ("&Oslash;"), Integer.valueOf(216) },
		{ ("&oslash;"), Integer.valueOf(248) },
		{ ("&Otilde;"), Integer.valueOf(213) },
		{ ("&otilde;"), Integer.valueOf(245) },
		{ ("&otimes;"), Integer.valueOf(8855) },
		{ ("&Ouml;"), Integer.valueOf(214) },
		{ ("&ouml;"), Integer.valueOf(246) },
		{ ("&para;"), Integer.valueOf(182) },
		{ ("&part;"), Integer.valueOf(8706) },
		{ ("&permil;"), Integer.valueOf(8240) },
		{ ("&perp;"), Integer.valueOf(8869) },
		{ ("&Phi;"), Integer.valueOf(934) },
		{ ("&phi;"), Integer.valueOf(966) },
		{ ("&Pi;"), Integer.valueOf(928) },
		{ ("&pi;"), Integer.valueOf(960) },
		{ ("&piv;"), Integer.valueOf(982) },
		{ ("&plusmn;"), Integer.valueOf(177) },
		{ ("&pound;"), Integer.valueOf(163) },
		{ ("&prime;"), Integer.valueOf(8242) },
		{ ("&Prime;"), Integer.valueOf(8243) },
		{ ("&prod;"), Integer.valueOf(8719) },
		{ ("&prop;"), Integer.valueOf(8733) },
		{ ("&Psi;"), Integer.valueOf(936) },
		{ ("&psi;"), Integer.valueOf(968) },
		{ ("&radic;"), Integer.valueOf(8730) },
		{ ("&rang;"), Integer.valueOf(9002) },
		{ ("&raquo;"), Integer.valueOf(187) },
		{ ("&rarr;"), Integer.valueOf(8594) },
		{ ("&rArr;"), Integer.valueOf(8658) },
		{ ("&rceil;"), Integer.valueOf(8969) },
		{ ("&rdquo;"), Integer.valueOf(8221) },
		{ ("&real;"), Integer.valueOf(8476) },
		{ ("&reg;"), Integer.valueOf(174) },
		{ ("&rfloor;"), Integer.valueOf(8971) },
		{ ("&Rho;"), Integer.valueOf(929) },
		{ ("&rho;"), Integer.valueOf(961) },
		{ ("&rlm;"), Integer.valueOf(8207) },
		{ ("&rsaquo;"), Integer.valueOf(8250) },
		{ ("&rsquo;"), Integer.valueOf(8217) },
		{ ("&sbquo;"), Integer.valueOf(8218) },
		{ ("&Scaron;"), Integer.valueOf(352) },
		{ ("&scaron;"), Integer.valueOf(353) },
		{ ("&sdot;"), Integer.valueOf(8901) },
		{ ("&sect;"), Integer.valueOf(167) },
		{ ("&shy;"), Integer.valueOf(173) },
		{ ("&Sigma;"), Integer.valueOf(931) },
		{ ("&sigma;"), Integer.valueOf(963) },
		{ ("&sigmaf;"), Integer.valueOf(962) },
		{ ("&sim;"), Integer.valueOf(8764) },
		{ ("&spades;"), Integer.valueOf(9824) },
		{ ("&sub;"), Integer.valueOf(8834) },
		{ ("&sube;"), Integer.valueOf(8838) },
		{ ("&sum;"), Integer.valueOf(8721) },
		{ ("&sup1;"), Integer.valueOf(185) },
		{ ("&sup2;"), Integer.valueOf(178) },
		{ ("&sup3;"), Integer.valueOf(179) },
		{ ("&sup;"), Integer.valueOf(8835) },
		{ ("&supe;"), Integer.valueOf(8839) },
		{ ("&szlig;"), Integer.valueOf(223) },
		{ ("&Tau;"), Integer.valueOf(932) },
		{ ("&tau;"), Integer.valueOf(964) },
		{ ("&there4;"), Integer.valueOf(8756) },
		{ ("&Theta;"), Integer.valueOf(920) },
		{ ("&theta;"), Integer.valueOf(952) },
		{ ("&thetasym;"), Integer.valueOf(977) },
		{ ("&thinsp;"), Integer.valueOf(8201) },
		{ ("&THORN;"), Integer.valueOf(222) },
		{ ("&thorn;"), Integer.valueOf(254) },
		{ ("&tilde;"), Integer.valueOf(732) },
		{ ("&times;"), Integer.valueOf(215) },
		{ ("&trade;"), Integer.valueOf(8482) },
		{ ("&Uacute;"), Integer.valueOf(218) },
		{ ("&uacute;"), Integer.valueOf(250) },
		{ ("&uarr;"), Integer.valueOf(8593) },
		{ ("&uArr;"), Integer.valueOf(8657) },
		{ ("&Ucirc;"), Integer.valueOf(219) },
		{ ("&ucirc;"), Integer.valueOf(251) },
		{ ("&Ugrave;"), Integer.valueOf(217) },
		{ ("&ugrave;"), Integer.valueOf(249) },
		{ ("&uml;"), Integer.valueOf(168) },
		{ ("&upsih;"), Integer.valueOf(978) },
		{ ("&Upsilon;"), Integer.valueOf(933) },
		{ ("&upsilon;"), Integer.valueOf(965) },
		{ ("&Uuml;"), Integer.valueOf(220) },
		{ ("&uuml;"), Integer.valueOf(252) },
		{ ("&weierp;"), Integer.valueOf(8472) },
		{ ("&Xi;"), Integer.valueOf(926) },
		{ ("&xi;"), Integer.valueOf(958) },
		{ ("&Yacute;"), Integer.valueOf(221) },
		{ ("&yacute;"), Integer.valueOf(253) },
		{ ("&yen;"), Integer.valueOf(165) },
		{ ("&yuml;"), Integer.valueOf(255) },
		{ ("&Yuml;"), Integer.valueOf(376) },
		{ ("&Zeta;"), Integer.valueOf(918) },
		{ ("&zeta;"), Integer.valueOf(950) },
		{ ("&zwj;"), Integer.valueOf(8205) },
		{ ("&zwnj;"), Integer.valueOf(8204) } };

	/**
	 * Map to convert extended characters in html entities.
	 */
	private static final Hashtable<Integer, String> HTMLENTITIES_MAP = new Hashtable<Integer, String>();

	/**
	 * Map to convert html entities in exteden characters.
	 */
	private static final Hashtable<String, Integer> UNHTMLENTITIES_MAP = new Hashtable<String, Integer>();

	//==============================================================================
	// METHODS
	//==============================================================================

	/**
	 * Initialize HTML translation maps.
	 */
	private HTMLEntities() {
		initializeEntitiesTables();
	}

	/**
	 * Initialize HTML entities table.
	 */
	private static void initializeEntitiesTables() {
		// initialize html translation maps
		for (int i = 0; i < HTML_ENTITIES_TABLE.length; ++i) {
			HTMLENTITIES_MAP.put((Integer) HTML_ENTITIES_TABLE[i][1],
					(String) HTML_ENTITIES_TABLE[i][0]);
			UNHTMLENTITIES_MAP.put((String) HTML_ENTITIES_TABLE[i][0],
					(Integer) HTML_ENTITIES_TABLE[i][1]);
		}
	}




	/**
	 * Convert HTML entities to special and extended unicode characters
	 * equivalents.
	 * @param str input string
	 * @return formatted string
	 * @see #htmlentities(String)
	 */
	public static String unhtmlentities(String str) {

		//initialize html translation maps table the first time is called
		if (HTMLENTITIES_MAP.isEmpty()) {
			initializeEntitiesTables();
		}

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);
			if (ch == '&') {
				int semi = str.indexOf(';', i + 1);
				if ((semi == -1) || ((semi - i) > 7)) {
					buf.append(ch);
					continue;
				}
				String entity = str.substring(i, semi + 1);
				Integer iso;
				if (entity.charAt(1) == ' ') {
					buf.append(ch);
					continue;
				}
				if (entity.charAt(1) == '#') {
					if (entity.charAt(2) == 'x') {
						iso = Integer.valueOf(Integer.parseInt(entity.substring(3, entity.length() - 1), 16));
					} else {
						iso = Integer.valueOf(entity.substring(2, entity.length() - 1));
					}
				} else {
					iso = UNHTMLENTITIES_MAP.get(entity);
				}
				if (iso == null) {
					buf.append(entity);
				} else {
					buf.append((char) (iso.intValue()));
				}
				i = semi;
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	// methods to convert special characters



	/**
	 * Replace single quotes HTML entities with equivalent character.
	 *
	 * @param str the input string
	 * @return string with replaced single quotes
	 */
	private static String unhtmlSingleQuotes(String str) {
		return str.replaceAll("&rsquo;", "\'");
	}


	/**
	 * Replace single quotes HTML entities with equivalent character.
	 *
	 * @param str the input string
	 * @return string with replaced single quotes
	 */
	private static String unhtmlDoubleQuotes(String str) {
		return str.replaceAll("&quot;", "\"");
	}



	/**
	 * Replace single and double quotes HTML entities with equivalent characters.
	 *
	 * @param str the input string
	 * @return string with replaced quotes
	 */
	public static String unhtmlQuotes(String str) {
		str = unhtmlDoubleQuotes(str); //convert double quotes
		str = unhtmlSingleQuotes(str); //convert single quotes
		return str;
	}









}
