package dlt.client.tangle.hornet.model.transactions;

import dlt.client.tangle.hornet.enums.TransactionType;
import java.util.Objects;

/**
 *
 * @author Uellington Damasceno
 */
public abstract class TargetedTransaction extends Transaction {

  private final String target;

  public TargetedTransaction(
    String source,
    String group,
    TransactionType type,
    String target
  ) {
    super(source, group, type);
    this.target = target;
  }

  public final String getTarget() {
    return this.target;
  }
  
  public boolean isSameTarget(String target){
      return this.target.equals(target);
  }
  
   @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) return false; 
        final TargetedTransaction other = (TargetedTransaction) obj;
        return Objects.equals(this.target, other.target);
    }
}
