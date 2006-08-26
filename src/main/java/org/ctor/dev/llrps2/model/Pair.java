package org.ctor.dev.llrps2.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Pair<T> {
    private final T first;

    private final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Pair)) {
            return false;
        }
        final Pair rhs = (Pair) other;
        return new EqualsBuilder().append(getFirst(), rhs.getFirst()).append(
                getSecond(), rhs.getSecond()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getFirst()).append(
                getSecond()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("first", getFirst()).append(
                "second", getSecond()).toString();
    }
}
