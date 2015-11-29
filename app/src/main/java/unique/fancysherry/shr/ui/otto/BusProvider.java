package unique.fancysherry.shr.ui.otto;

import com.squareup.otto.Bus;

/**
 * Created by fancysherry on 15-11-29.
 */
public final class BusProvider {
  private static final Bus BUS = new Bus();

  public static Bus getInstance() {
    return BUS;
  }

  private BusProvider() {
    // No instances.
  }
}
