package net.sourceforge.jwbf.core.actions;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public final class ParamTuple<T> {
  final String key;
  final Supplier<T> valueSupplier;

  public ParamTuple(String key, T value) {
    this(key, Suppliers.ofInstance(Preconditions.checkNotNull(value, "value must not be null")));
  }

  public ParamTuple(String key, Supplier<T> valueSupplier) {
    this.key = Preconditions.checkNotNull(key, "key must not be null");
    this.valueSupplier = Preconditions.checkNotNull(valueSupplier, "value must not be null");
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ParamTuple) {
      ParamTuple that = (ParamTuple) o;
      return Objects.equals(that.key, this.key) && //
          Objects.equals(that.valueSupplier, this.valueSupplier);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, valueSupplier);
  }

  @Override
  public String toString() {
    return "('" + key + "', '" + valueSupplier.get() + "')";
  }
}
