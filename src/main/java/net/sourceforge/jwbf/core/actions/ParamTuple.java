package net.sourceforge.jwbf.core.actions;

import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.sourceforge.jwbf.core.internal.Checked;

public final class ParamTuple<T> {
  final String key;
  final Supplier<T> valueSupplier;

  public ParamTuple(String key, T value) {
    this(key, Suppliers.ofInstance(Checked.nonNull(value, "value")));
  }

  public ParamTuple(String key, Supplier<T> valueSupplier) {
    this.key = Checked.nonNull(key, "key");
    this.valueSupplier = Checked.nonNull(valueSupplier, "value");
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ParamTuple) {
      ParamTuple that = (ParamTuple) o;
      return Objects.equals(that.key, this.key)
          && //
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

  @VisibleForTesting
  public String key() {
    return key;
  }

  @VisibleForTesting
  public String value() {
    return (String) valueSupplier.get();
  }
}
