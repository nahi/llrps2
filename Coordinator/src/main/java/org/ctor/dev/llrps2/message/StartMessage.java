package org.ctor.dev.llrps2.message;

import java.io.Serializable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class StartMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private final String message;

    public static StartMessage create(String message) {
        Validate.notNull(message);
        return new StartMessage(message);
    }

    private StartMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
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
        return new EqualsBuilder().append(getMessage(), rhs.getMessage())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMessage()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("message", getMessage())
                .toString();
    }
}