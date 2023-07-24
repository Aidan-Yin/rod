import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * To show how to use secure socket
 * 
 * @author Yin
 * @date 2023-7-22
 */
public class socketExampleServer {
    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException{
        PrivateKey privateKey = RSA.getPrivateKeyFromBase64("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");
        PublicKey publicKey = RSA.getPublicKeyFromBase64("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAp+nv56xA7HS98ozsT8maEtJsSdXF1rQ79KePAhJdnrCylCicMh+Ga3HptDI9T0Ujjx8fDLlXJplktAGvF6mmGVs0eJDyKEiGjR1+uI6ogpHZxd1kiWNQRA7jS+7xe3Uf2VhvM2UY1DQIKJSyrHgOvji4AZeUtorCS2fhsZlKYz2y5AinGJdM9idXxylpFNTg6D46zsH+2uXswGVrnQkTpScc+v550JSQvxcAN1EFnotKuv1MVGsEQQ01MG34AzYtyJ/7IssLuecW0uq4uxR4B8os8eDc2bLGUDAZMa2mI85uu3S1V7HatgOEtqb38G8ql8pDyWAayLp/K9w4/0d9L1IejcDp5JBcx8y9PfQrhI17Og1viE0CcwI55P+jOuv86lkguNcE/zTZgrH+ciwxFierapu1wiGgYRv0798eCZ8z+Fum4rHNwAFmy6Gzrcb5NNPXot3Wq0DXBpaTsfB38yDauGWqSTDJP5aLAhOep7AnQWy70Gz7Eb0zaI15D3alNCxIn5ZUjYMlwTR7S5LQ6pfu6mHw1HaFS8a7Sq5jWFlQvroIEvDYQmYGJVYBbDtmyLdSHCCJfe937tNFxC5gZ2y6wPO9M/K7AdACEpXFWf16BUMLDXU6yG6vTuooVvLOfRX3OA94rc7xDBaYGRe0ld2WLfYtSaRFHCV3SlxIjA0CAwEAAQ==");
        SecureServerSocket serverSocket = new SecureServerSocket(publicKey, privateKey,8080);
        String[] validClients = {"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAlXFBhw7G0bpbtUOe5BPMxq11WtpJkVWOx5biiPX7GySeCHV6uoCip127E6j1L/XqHTDzErm93G/6eEtntjt7IvD9RoYkloBZLfevNuo7NzqFuIJu3y/y/u73mHC9sVU4mNuse93yPuMio+GEiKaDcrIFZmxXkeyhVnCxcXA+ISc2I/Ripwo19oahlihSMgNZP5IPDCGahASk3414eyk/W8asGTvN11PiEW+8xNFfwjDF9pBiIAOZaRk6HvhLQTJhQKN9sKy+333FffihS387R8IU1WevF8q8CUkNbq7FBFFUJDketaALABXSGgExW9KQdaHiDyps7XNHcQzOxre7VjPUACHuUgefS7a6bM5zVU3bjizD0PtaglwFNje47D4jvF6qgu8MSf1ZTqv5EWH/PpW/T39K11tJFGI3yAfyQzmyFqPOZqq1zBNcC5F2CFKp26td9z1+a86jBTqJ8crfeqOE26LdiB2esBAC0E1oIpC/HAvMZoMo2Cb320rL6zHwfaqmV3yE8M2BNj1RODmgZTVsOSmoLgPijdigB/PIEXt2OwLuBq4WXO5AH095xy591BISeTYaePyMOlKsGZtR2wnlcbNRx2D+gi9pkRWbbFhaHwy8RP8oOjf4qV1DHTrutB3d/KLo7CpCAN8dOftmpAswc6+++kVnV/SqW7FDCWMCAwEAAQ=="};
        while (true){
            System.out.println("a");
            SecureSocket secureSocket = serverSocket.accept(validClients);
            System.out.println("accepted");
            new Thread(new Runnable() {
                @Override
                public void run(){
                    try {
                        // byte[] data = secureSocket.recvall();
                        // System.out.println(new String(data));
                        System.out.println("Sended");
                        secureSocket.sendall("Hello, client!".getBytes());
                        secureSocket.sendall("Hello, client!".getBytes());
                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                            | InvalidAlgorithmParameterException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
