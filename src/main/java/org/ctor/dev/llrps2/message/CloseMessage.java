package org.ctor.dev.llrps2.message;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CloseMessage extends StringMessage {
    private static final long serialVersionUID = 1;

    public static CloseMessage create(String message) {
        Validate.notNull(message);
        return new CloseMessage(message);
    }

    private CloseMessage(String message) {
        super(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CloseMessage)) {
            return false;
        }
        final CloseMessage rhs = (CloseMessage) other;
        return new EqualsBuilder().appendSuper(super.equals(rhs)).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .toString();
    }
}