package foobar;

import com.microsoft.uprove.IssuerKeyAndParameters;
import com.microsoft.uprove.IssuerSetupParameters;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public final class IKAPBuilder
{
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final byte[] UID_AS_BYTES = "unique UID".getBytes();
    private static final byte[] SPECIFICATION_AS_BYTES
        = "specification".getBytes();

    private final IssuerSetupParameters parameters;

    private IKAPBuilder()
    {
        parameters = new IssuerSetupParameters();

        /*
         * All setters below are pretty much hardcoded from the sample code
         */
        parameters.setEncodingBytes(new byte[] { 1, 0 });
        parameters.setHashAlgorithmUID(HASH_ALGORITHM);
        parameters.setParametersUID(UID_AS_BYTES);
        parameters.setSpecification(SPECIFICATION_AS_BYTES);
    }

    public static IKAPBuilder newBuilder()
    {
        return new IKAPBuilder();
    }

    public IKAPBuilder havingSupportDevice(final boolean supportDevice)
    {
        parameters.setSupportDevice(supportDevice);
        return this;
    }

    public IssuerKeyAndParameters build()
        throws UProveException
    {
        try {
            return parameters.generate();
        } catch (NoSuchProviderException e) {
            throw new UProveException("cannot build IKAP params", e);
        } catch (NoSuchAlgorithmException e) {
            throw new UProveException("cannot build IKAP params", e);
        }
    }
}
