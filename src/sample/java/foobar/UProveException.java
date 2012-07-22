package foobar;

public final class UProveException
    extends Exception
{
    public UProveException(final String message)
    {
        super(message);
    }

    public UProveException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
