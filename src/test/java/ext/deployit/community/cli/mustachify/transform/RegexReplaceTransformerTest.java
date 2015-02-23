/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

import ext.deployit.community.cli.mustachify.transform.RegexReplaceTransformer;
import ext.deployit.community.cli.mustachify.transform.RegexReplaceTransformer.RegexReplaceTransformerFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link RegexReplaceTransformer}
 */
public class RegexReplaceTransformerTest {

	@Test
	public void findsPattern() {
		RegexReplaceTransformer transformer = get("^.*$", "long");
		assertEquals("long", transformer.transform("A long .* string"));
	}

	@Test
	public void supportsMatchGroupReplacement() {
		RegexReplaceTransformer transformer = get("(long)", "l$1g");
		assertEquals("A llongg (llongg) string", transformer.transform("A long (long) string"));
	}

	@Test
	public void supportsMatchGroupReplacementBrackets() {
		RegexReplaceTransformer transformer = get("\\[([A-Z_\\]]+)\\]", "\\{\\{$1\\}\\}");
		assertEquals("A {{DATX}} string", transformer.transform("A [DATX] string"));
		assertEquals("A {{DATX_LOGIN_TEDI}} string", transformer.transform("A [DATX_LOGIN_TEDI] string"));
		assertEquals("A [abc_edf] string", transformer.transform("A [abc_edf] string"));
	}

	@Test
	public void supportsMatchGroupReplacementMineducFormat() {
		RegexReplaceTransformer transformer = get("ZZ_([A-Z_]+)", "\\{\\{$1\\}\\}");
		assertEquals("A {{HOME}} string", transformer.transform("A ZZ_HOME string"));
		assertEquals("A {{SCONET_HOME}} string", transformer.transform("A ZZ_SCONET_HOME string"));
		assertEquals("LDAP_URL=ldap://{{LDAPSRV_IP}}:{{LDAPSRV_PORT}}/", transformer.transform("LDAP_URL=ldap://ZZ_LDAPSRV_IP:ZZ_LDAPSRV_PORT/"));
	}



	private static RegexReplaceTransformer get(String patternToFind, String replacement) {
		return new RegexReplaceTransformer(ImmutableMap.of(
				"type", RegexReplaceTransformerFactory.TRANSFORMER_TYPE,
				"ci.type", "Dummy",
				"encoding", Charsets.UTF_8.toString(),
				"pattern", patternToFind,
				"replacement", replacement));
	}
}
