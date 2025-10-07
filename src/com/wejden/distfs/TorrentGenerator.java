package com.wejden.distfs;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TorrentGenerator {
    public static void generateTorrent(String filePath, String outputPath, List<String> pieceHashes, long fileLength, String trackerUrl) throws IOException {
        // Simple BEncode structure: d4:infod6:lengthli<fileLength>e6:pieces<length>:<concatenated hex hashes>12:piece lengthi<CHUNK_SIZE>e12:privatei0ee8:announce<length>:<trackerUrl>e
        StringBuilder bencode = new StringBuilder("d4:infod");
        bencode.append("6:length").append("i").append(fileLength).append("e");
        int piecesLength = pieceHashes.size() * 64; // Each SHA-256 hex hash is 64 chars
        bencode.append("6:pieces").append(piecesLength).append(":");
        for (String hash : pieceHashes) {
            bencode.append(hash);
        }
        bencode.append("12:piece length").append("i").append(FileChunker.CHUNK_SIZE).append("e");
        bencode.append("12:private").append("i0").append("ee");
        bencode.append("8:announce").append(trackerUrl.length()).append(":").append(trackerUrl);
        bencode.append("e");

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(bencode.toString().getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java TorrentGenerator <filePath> <outputTorrent> <trackerUrl>");
            return;
        }
        String filePath = args[0];
        String outputPath = args[1];
        String tracker = args[2];

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Error: File not found - " + filePath);
            return;
        }
        List<byte[]> chunks = FileChunker.chunkFile(filePath);
        List<String> hashes = FileChunker.hashChunks(chunks);
        generateTorrent(filePath, outputPath, hashes, file.length(), tracker);

        System.out.println("Torrent generated: " + outputPath);
    }
}
