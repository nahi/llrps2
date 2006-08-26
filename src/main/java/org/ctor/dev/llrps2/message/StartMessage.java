package org.ctor.dev.llrps2.message;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class StartMessage extends StringMessage {
    private static final long serialVersionUID = 1;

    public static StartMessage create(String message) {
        Validate.notNull(message);
        return new StartMessage(message);
    }

    private StartMessage(String message) {
        super(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof StartMessage)) {
            return false;
        }
        final StartMessage rhs = (StartMessage) other;
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