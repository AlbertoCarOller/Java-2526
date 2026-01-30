package Tema4.CryptoAsimetricaSockets.EjemploProfesor;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Servidor {
    static void main(String[] var0) throws Exception {
        if (var0.length != 1) {
            System.err.println("Uso: java Servidor puerto");
            System.exit(1);
        }

        int var1 = Integer.parseInt(var0[0]);
        System.out.println("Generando par de claves RSA...");
        KeyPairGenerator var2 = KeyPairGenerator.getInstance("RSA");
        var2.initialize(1024);
        KeyPair var3 = var2.genKeyPair();
        System.out.println("Generada la clave asimétrica.");
        ServerSocket var4 = new ServerSocket(var1);
        System.out.println("Escuchando en el puerto " + var1 + "...");
        Socket var5 = var4.accept();
        DataOutputStream var6 = new DataOutputStream(var5.getOutputStream());
        System.out.println("Enviando mi clave pública.");
        byte[] var7 = var3.getPublic().getEncoded();
        var6.writeInt(var7.length);
        var6.write(var7);
        System.out.println("Recibiendo clave secreta del cliente...");
        DataInputStream var8 = new DataInputStream(var5.getInputStream());
        var7 = new byte[var8.readInt()];
        var8.readFully(var7);
        Cipher var9 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        System.out.println("Comenzamos a desencriptarla");
        var9.init(2, var3.getPrivate());
        byte[] var10 = var9.doFinal(var7);
        System.out.println("Ya la hemos desencriptado");
        byte[] var11 = new byte[8];
        SecureRandom var12 = new SecureRandom();
        var12.nextBytes(var11);
        var6.write(var11);
        SecretKeySpec var13 = new SecretKeySpec(var10, "Blowfish");
        System.out.println("Creando el cifrador de stream...");
        Cipher var14 = Cipher.getInstance("Blowfish/CFB8/NoPadding");
        IvParameterSpec var15 = new IvParameterSpec(var11);
        var14.init(2, var13, var15);
        CipherInputStream var16 = new CipherInputStream(var5.getInputStream(), var14);
        boolean var17 = false;

        for (int var19 = var16.read(); var19 != -1; var19 = var16.read()) {
            System.out.print((char) var19);
        }

        var16.close();
        var8.close();
        var6.close();
        var5.close();
    }
}