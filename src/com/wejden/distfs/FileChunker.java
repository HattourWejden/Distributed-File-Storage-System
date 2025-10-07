package com.wejden.distfs;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileChunker {
    private static final int CHUNK_SIZE = 256 * 1024;
    private static final String HASH_ALGO = "SHA-256";

    public static List<byte[]> chunkFile(String filePath) throws IOException {
        List<byte[]> chunks = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                chunks.add(chunk);
            }
        }
        return chunks;
    }

    public static List<String> hashChunks(List<byte[]> chunks) throws NoSuchAlgorithmException {
        List<String> hashes = new ArrayList<>();
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGO);
        for (byte[] chunk : chunks) {
            byte[] hashBytes = digest.digest(chunk);
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b));
            }
            hashes.add(hexHash.toString());
        }
        return hashes;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java FileChunker <filePath>");
            return;
        }
        List<byte[]> chunks = chunkFile(args[0]);
        List<String> hashes = hashChunks(chunks);
        System.out.println("Chunks: " + chunks.size());
        System.out.println("Hashes: " + hashes);
    }
}