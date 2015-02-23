/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.base;

import com.google.common.base.Predicate;

/**
 * @author aphillips
 * @since Jul 31, 2011
 *
 */
public class Predicates2 {

    public static IsPredicate is(Object ref) {
        return new IsPredicate(ref);
    }
    
    private static class IsPredicate implements Predicate<Object> {
        private final Object ref;
        
        private IsPredicate(Object ref) {
            this.ref = ref;
        }
        
        @Override
        public boolean apply(Object input) {
            return (input == ref);
        }
    }
}
