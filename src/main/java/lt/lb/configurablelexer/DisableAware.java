package lt.lb.configurablelexer;

/**
 *
 * @author laim0nas100
 */
public interface DisableAware {

    /**
     *
     * @return if the functionality is disabled. In this case usually delegates.
     */
    public boolean isDisabled();

    public interface DisableAwareDefault extends DisableAware {

        @Override
        public default boolean isDisabled() {
            return false;
        }

    }
}
