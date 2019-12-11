package com.networknt.schema.uri;

import java.net.URI;
import java.util.Comparator;
import java.util.Objects;

/**
 * Compare two URIs but ignore if one is http and the other is https
 */
public class HttpsAgnosticUriComparator implements Comparator<URI> {
    @Override
    public int compare(URI left, URI right) {
        int c;

        String leftScheme = trimS(left.getScheme().toLowerCase());
        String rightScheme = trimS(right.getScheme().toLowerCase());
        if ((c = compare(leftScheme, rightScheme)) != 0)
            return c;

        if (left.isOpaque()) {
            if (right.isOpaque()) {
                // Both opaque
                if ((c = compare(left.getSchemeSpecificPart(),
                        right.getSchemeSpecificPart())) != 0)
                    return c;
                return compare(left.getFragment(), right.getFragment());
            }
            return +1;                  // Opaque > hierarchical
        } else if (right.isOpaque()) {
            return -1;                  // Hierarchical < opaque
        }

        // Hierarchical
        if ((left.getHost() != null) && (right.getHost() != null)) {
            // Both server-based
            if ((c = compare(left.getUserInfo(), right.getUserInfo())) != 0)
                return c;
            if ((c = compare(left.getHost().toLowerCase(), right.getHost().toLowerCase())) != 0)
                return c;
            if ((c = left.getPort() - right.getPort()) != 0)
                return c;
        } else {
            // If one or both authorities are registry-based then we simply
            // compare them in the usual, case-sensitive way.  If one is
            // registry-based and one is server-based then the strings are
            // guaranteed to be unequal, hence the comparison will never return
            // zero and the compareTo and equals methods will remain
            // consistent.
            if ((c = compare(left.getAuthority(), right.getAuthority())) != 0) return c;
        }

        if ((c = compare(left.getPath(), right.getPath())) != 0) return c;
        if ((c = compare(left.getQuery(), right.getQuery())) != 0) return c;

        // both null and empty string mean an empty fragment, so normalise before comparing
        return compare(
                Objects.toString(left.getFragment(), ""),
                Objects.toString(right.getFragment(), ""));
    }

    private String trimS(String scheme) {
        if (scheme.endsWith("s")) {
            return scheme.substring(0, scheme.length() -1);
        } else {
            return scheme;
        }
    }

    private static int compare(String s, String t) {
        if (s == t) return 0;
        if (s != null) {
            if (t != null)
                return s.compareTo(t);
            else
                return +1;
        } else {
            return -1;
        }
    }

}
