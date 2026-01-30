package Tema4.CryptoAsimetricaSockets.EjemploProfesor;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cliente {
    static void main(String[] var0) throws Exception {
        if (var0.length != 2) {
            System.err.println("Uso: java Cliente host puerto");
            System.exit(1);
        }

        String var1 = var0[0];
        int var2 = Integer.parseInt(var0[1]);
        System.out.println("Generando clave Simétrica Blowfish...");
        KeyGenerator var3 = KeyGenerator.getInstance("Blowfish");
        var3.init(128);
        SecretKey var4 = var3.generateKey();
        System.out.println("Intentando conexión a " + var1 + ", puerto " + var2 + ".");
        Socket var5 = new Socket(var1, var2);
        DataOutputStream var6 = new DataOutputStream(var5.getOutputStream());
        DataInputStream var7 = new DataInputStream(var5.getInputStream());
        System.out.println("Recibiendo clave pública del servidor.");
        byte[] var8 = new byte[var7.readInt()];
        var7.readFully(var8);
        KeyFactory var9 = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec var10 = new X509EncodedKeySpec(var8);
        PublicKey var11 = var9.generatePublic(var10);
        Cipher var12 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        var12.init(1, var11);
        byte[] var13 = var4.getEncoded();
        byte[] var14 = var12.doFinal(var13);
        System.out.println("Encriptada la clave simétrica");
        System.out.println("Enviando mi clave simétrica encriptada.");
        var6.writeInt(var14.length);
        var6.write(var14);
        byte[] var15 = new byte[8];
        var7.readFully(var15);
        System.out.println("Creando el cifrador de stream...");
        Cipher var16 = Cipher.getInstance("Blowfish/CFB8/NoPadding");
        IvParameterSpec var17 = new IvParameterSpec(var15);
        var16.init(1, var4, var17);
        CipherOutputStream var18 = new CipherOutputStream(var5.getOutputStream(), var16);
        String var19 = "Conexión establecida.\n\n";
        byte[] var20 = var19.getBytes();
        var18.write(var20);
        System.out.println("Conexión establecida.\n");
        boolean var21 = false;

        for (int var22 = System.in.read(); var22 != 126; var22 = System.in.read()) {
            var18.write(var22);
        }

        var18.close();
        var7.close();
        var6.close();
        var5.close();
    }
}