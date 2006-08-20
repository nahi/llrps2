package org.ctor.dev.llrps2.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MovePair {
    private final Move left;

    private final Move right;

    public static MovePair create(Move left, Move right) {
        Validate.notNull(left);
        Validate.notNull(right);
        return new MovePair(left, right);
    }
    
    private MovePair(Move left, Move right) {
        this.right = right;
        this.left = left;
    }

    public Move getLeft() {
        return left;
    }

    public Move getRight() {
        return right;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MovePair)) {
            return false;
        }
        final MovePair rhs = (MovePair) other;
        return new EqualsBuilder().append(getLeft(), rhs.getLeft()).append(
                getRight(), rhs.getRight()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getLeft()).append(getRight())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("left", getLeft()).append(
                "right", getRight()).toString();
    }
}
