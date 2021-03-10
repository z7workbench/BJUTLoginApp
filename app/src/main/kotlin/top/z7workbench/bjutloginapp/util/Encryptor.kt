package top.z7workbench.bjutloginapp.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object Encryptor {
    const val algorithm = "AES"
    fun encrypt(data: String, secret: String): String {
        try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey(secret))
            val result = cipher.doFinal(data.toByteArray())
            return String(result,Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun decrypt(encrypted: String, secret: String): String {
        try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(secret))
            val result = cipher.doFinal(encrypted.toByteArray())
            return String(result,Charsets.UTF_8)
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun secretKey(secret: String) = SecretKeySpec(secret.toByteArray(), algorithm)

    fun randomSecretKey(): String {
        val str = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
        var secret = ""
        (0 until 20).forEach {
            val random = Random.nextInt(str.length)
            secret += str[random]
        }
        return secret
    }
}