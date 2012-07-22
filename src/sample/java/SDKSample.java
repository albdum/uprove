//*********************************************************
//
//    Copyright (c) Microsoft. All rights reserved.
//    This code is licensed under the Apache License Version 2.0.
//    THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
//    ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
//    IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
//    PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.
//
//*********************************************************

import com.microsoft.uprove.Device;
import com.microsoft.uprove.DeviceManager;
import com.microsoft.uprove.DeviceSetupParameters;
import com.microsoft.uprove.Issuer;
import com.microsoft.uprove.IssuerKeyAndParameters;
import com.microsoft.uprove.IssuerParameters;
import com.microsoft.uprove.IssuerProtocolParameters;
import com.microsoft.uprove.PresentationProof;
import com.microsoft.uprove.PresentationProtocol;
import com.microsoft.uprove.Prover;
import com.microsoft.uprove.ProverProtocolParameters;
import com.microsoft.uprove.UProveKeyAndToken;
import com.microsoft.uprove.UProveToken;
import foobar.IKAPBuilder;
import foobar.UProveException;

import java.io.IOException;

/**
 * This samples demonstrates how to issue and present U-Prove tokens.
 */
public class SDKSample
{

    /**
     * Runs the sample.
     */
    public static void Sample()
        throws UProveException
    {
        System.out.println("U-Prove Java SDK sample");

        /*
        * issuer parameters setup
        */
        final IKAPBuilder builder = IKAPBuilder.newBuilder();
        final IssuerKeyAndParameters ikap = builder.build();
        final IssuerParameters ip = ikap.getIssuerParameters();

        // issuer distributes the issuer parameters
        // prover and verifier should validate the issuer parameters upon reception
        //com:Just like an ssl key exchange. It speaks about two different things
        //prover and verifier should be independant proggrams running at different
        //server for different reassons
        try {
            ip.validate();
        } catch (IOException e) {
            throw new UProveException("issuer could not distribute parameters");
        }

        try {

            /*
             *  token issuance
             */

            System.out.println("Issuing U-Prove tokens");

            // protocol parameters
            byte[][] attributes = new byte[][]{
                "first attribute".getBytes(),
                "second attribute".getBytes()
            };
            byte[] tokenInformation = "token information".getBytes();
            int numberOfTokens = 5;
            //i believe number of tokens is just how many tokens it will create
            //consider that u can get as many tokens you want and you may choose
            //only to use one or nothing or 100 of them.Issuer should and does not
            //have any idea of what you will make of them

            // issuer generates first issuance message. That means the server!
            IssuerProtocolParameters issuerProtocolParams
                = new IssuerProtocolParameters();
            issuerProtocolParams.setIssuerKeyAndParameters(ikap);
            issuerProtocolParams.setNumberOfTokens(numberOfTokens);
            issuerProtocolParams.setTokenAttributes(attributes);
            issuerProtocolParams.setTokenInformation(tokenInformation);

            Issuer issuer = issuerProtocolParams.generate();
            byte[][] message1 = issuer.generateFirstMessage();

            // prover generates second issuance message. That means the user!
            //my guess is that this section should go to the client side of
            //proggram and should have as input the message1 from the issuer.
            byte[] proverInformation = "prover information".getBytes();

            ProverProtocolParameters proverProtocolParams
                = new ProverProtocolParameters();
            proverProtocolParams.setIssuerParameters(ip);
            proverProtocolParams.setNumberOfTokens(numberOfTokens);
            proverProtocolParams.setTokenAttributes(attributes);
            proverProtocolParams.setTokenInformation(tokenInformation);
            proverProtocolParams.setProverInformation(proverInformation);

            Prover prover = proverProtocolParams.generate();
            byte[][] message2 = prover.generateSecondMessage(message1);

            // issuer generates third issuance message.This should be at the
            //server side of proggram and should take as input the message2
            //from prover(client)
            byte[][] message3 = issuer.generateThirdMessage(message2);

            // prover generates the U-Prove tokens.That means that the user(prover)
            //takes as input the message3 and creates the upkt a.k.a the keytoken
            UProveKeyAndToken[] upkt = prover.generateTokens(message3);

            // application specific storage of keys, tokens, and attributes
            //here it should be some code on prover(client) side that could make
            //possible the saving of the upkt(maybe an xml scheme?)

            /*
             * token presentation
             */
             //this should be on the verification proccess it has nothing to do with
             //first part it should be another proggram that is responsible to give
             //the token that prover(client) has to some third party.

            System.out.println("Presenting a U-Prove token");

            // protocol parameters (shared by prover and verifier)
            //as it says prover and verifier now take place here
            //the disclosed part should be the number of elements inside the token
            //that the user(prover) wants to share with the verifier
            //Keep in mind that you could have an ID here with 10 elements and  user
            //wants to give only first two e.x. the name and last name
            int[] disclosed = new int[]{2};
            byte[] message = "message".getBytes();

            // prover chooses a token to use. I believe here that has something to do
            //with the number of tokens we created.
            UProveKeyAndToken keyAndToken = upkt[0];

            // prover generates the presentation proof. This part is a bit blur
            //but i believe all the variables are taken into account and somehow
            //compared with the keyandtoken so without communicating with the server
            //(issuer) the client(prover) can prove that those infos he gave are correct 
            PresentationProof proof
                = PresentationProtocol.generatePresentationProof(ip, disclosed,
                message, null, keyAndToken, attributes);

            // prover transmits the U-Prove token and presentation proof to the verifier
            //and sents the token to the verifier
            UProveToken token = keyAndToken.getToken();

            // verifier verifies the presentation proof.
            //verifier should be able to get the token prover sent and run the following
            PresentationProtocol.verifyPresentationProof(ip, disclosed,
                message, null, token, proof);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace(System.out);
            return;
        }

        System.out.println("Sample completed successfully");
    }

    /**
     * Runs the Device sample.
     */
    public static void DeviceSample()
        throws UProveException
    {
        System.out.println("U-Prove Java SDK Device sample");

        /*
        * issuer parameters setup
        */
        final IKAPBuilder builder = IKAPBuilder.newBuilder()
            .havingSupportDevice(true);
        final IssuerKeyAndParameters ikap = builder.build();
        final IssuerParameters ip = ikap.getIssuerParameters();


        // issuer distributes the issuer parameters
        // prover and verifier should validate the issuer parameters upon reception
        try {
            ip.validate();
        } catch (IOException e) {
            throw new UProveException("issuer could not distribute parameters");
        }

        try {

            /*
             *  token issuance
             */

            System.out.println("Issuing U-Prove tokens");

            // protocol parameters
            byte[][] attributes = new byte[][]{
                "first attribute".getBytes(),
                "second attribute".getBytes()
            };
            byte[] tokenInformation = "token information".getBytes();
            int numberOfTokens = 5;

            // Device setup
            DeviceSetupParameters deviceSetupParams
                = new DeviceSetupParameters();
            deviceSetupParams.setIssuerParameters(ip);

            Device device = deviceSetupParams.generate();

            int dIndex = ip.getProverIssuanceValues().length - 1;
            byte[] deviceZetaParameter
                = device
                .GetDeviceParameter(ip.getProverIssuanceValues()[dIndex]);
            byte[] devicePublicKey = device.GetDevicePublicKey();

            // issuer generates first issuance message
            IssuerProtocolParameters issuerProtocolParams
                = new IssuerProtocolParameters();
            issuerProtocolParams.setIssuerKeyAndParameters(ikap);
            issuerProtocolParams.setNumberOfTokens(numberOfTokens);
            issuerProtocolParams.setTokenAttributes(attributes);
            issuerProtocolParams.setTokenInformation(tokenInformation);
            issuerProtocolParams.setDevicePublicKey(devicePublicKey);

            Issuer issuer = issuerProtocolParams.generate();
            byte[][] message1 = issuer.generateFirstMessage();

            // prover generates second issuance message
            byte[] proverInformation = "prover information".getBytes();

            ProverProtocolParameters proverProtocolParams
                = new ProverProtocolParameters();
            proverProtocolParams.setIssuerParameters(ip);
            proverProtocolParams.setNumberOfTokens(numberOfTokens);
            proverProtocolParams.setTokenAttributes(attributes);
            proverProtocolParams.setTokenInformation(tokenInformation);
            proverProtocolParams.setProverInformation(proverInformation);
            proverProtocolParams
                .setDeviceParameters(devicePublicKey, deviceZetaParameter);

            Prover prover = proverProtocolParams.generate();
            byte[][] message2 = prover.generateSecondMessage(message1);

            // issuer generates third issuance message
            byte[][] message3 = issuer.generateThirdMessage(message2);

            // prover generates the U-Prove tokens
            UProveKeyAndToken[] upkt = prover.generateTokens(message3);

            // application specific storage of keys, tokens, and attributes

            /*
             * token presentation
             */

            System.out.println("Presenting a U-Prove token");

            // protocol parameters (shared by prover and verifier)
            int[] disclosed = new int[]{2};
            byte[] message = "message".getBytes();
            byte[] deviceMessage = "message for Device".getBytes();

            // prover chooses a token to use
            UProveKeyAndToken keyAndToken = upkt[0];

            // prover generates the presentation proof
            DeviceManager.RegisterDevice(device);
            PresentationProof proof
                = PresentationProtocol.generatePresentationProof(ip, disclosed,
                message, deviceMessage, keyAndToken, attributes);

            // prover transmits the U-Prove token and presentation proof to the verifier
            UProveToken token = keyAndToken.getToken();

            // verifier verifies the presentation proof
            PresentationProtocol
                .verifyPresentationProof(ip, disclosed, message, deviceMessage,
                    token, proof);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace(System.out);
            return;
        }

        System.out.println("Sample completed successfully");
    }


    public static void main(final String[] args)
        throws UProveException
    {
        System.out.println("--- SAMPLE BEGIN ---");
        Sample();
        System.out.println("--- SAMPLE END ---");
        System.out.println("--- DEVICE SAMPLE BEGIN ---");
        DeviceSample();
        System.out.println("--- DEVICE SAMPLE END ---");
    }

}
