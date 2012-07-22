package foobar;

/**
 * State machine basic interface to process a u-prove request
 *
 * <p>Implementing this interface will require a wrapper class (say,
 * UProveProcessHandler), with a method (say, proceed()) which goes
 * like this:</p>
 *
 * <pre>
 *  public void proceed()
 *      throws UProveException
 *  {
 *      UProveProcessor processor = new InitialStateProcessor(all, necessary,
 *          arguments);
 *
 *      while (processor.process())
 *          processor = processor.next();
 *  }
 * </pre>
 *
 * <p>This means that it is the responsibility of interface implementations to
 * pass <b>all</b> necessary parameters for a processor to be able to spawn
 * another.
 * </p>
 */
public interface UProveProcessor
{
    /**
     * Complete one step of the U-prove process
     *
     * @return true if the processing should continue; false means the
     * processing is done
     * @throws UProveException an error occurred, processing cannot continue
     */
    public boolean process()
        throws UProveException;

    /**
     * Spawn a new processor to continue the process, if any
     *
     * <p>Note that a processor which returns {@code false} in
     * {@link #process()} should ideally throw an unchecked exception here
     * (say, {@link UnsupportedOperationException}.</p>
     *
     * @return the next processor to run
     */
    public UProveProcessor next();
}
