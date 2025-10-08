package com.wejden.distfs;

import java.io.*;
import java.net.*;
import java.util.List;

public class PeerHandler {
    private static final int PORT = 6881; // Default BitTorrent-like port
    private static final int BUFFER_SIZE = 4096; // 4KB buffer for transfers

    // Server side: Listen for connections, receive requests, send chunks
    public static void startServer(String filePath, List<String> expectedHashes) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server listening on port " + PORT + " for file: " + filePath);
        List<byte[]> allChunks = FileChunker.chunkFile(filePath); // Load chunks once
        if (allChunks.size() != expectedHashes.size()) {
            System.out.println("Mismatch: Chunks vs hashes!");
            return;
        }
        while (true) { // Accept multiple clients
            Socket client = server.accept();
            System.out.println("Client connected from " + client.getInetAddress());

            try (DataInputStream in = new DataInputStream(client.getInputStream());
                 DataOutputStream out = new DataOutputStream(client.getOutputStream());
                 RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {

                // Simple handshake: Send "OK" (mock BitTorrent handshake)
                out.writeUTF("OK");

                // Receive bitfield request (mock: client sends chunk index as int)
                int chunkIndex = in.readInt();
                if (chunkIndex < 0 || chunkIndex >= allChunks.size()) {
                    System.out.println("Invalid chunk index: " + chunkIndex);
                    continue;
                }

                // Send chunk length first
                byte[] chunk = allChunks.get(chunkIndex);
                out.writeInt(chunk.length);

                // Seek and send chunk bytes
                long offset = chunkIndex * FileChunker.CHUNK_SIZE;
                raf.seek(offset);
                raf.readFully(chunk); // Read exact chunk
                out.write(chunk);

                // Optional: Send hash for verification (client-side check)
                out.writeUTF(expectedHashes.get(chunkIndex));

                System.out.println("Sent chunk " + chunkIndex + " (" + chunk.length + " bytes)");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        }
    }

    // Client side: Connect to peer, request chunk by index, receive + verify
    public static void downloadChunk(String peerHost, int chunkIndex, String expectedHash, long offset, String outputPath) throws Exception {
        Socket socket = new Socket(peerHost, PORT);
        System.out.println("Connecting to peer " + peerHost + " for chunk " + chunkIndex);

        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             RandomAccessFile raf = new RandomAccessFile(outputPath, "rw")) {

            // Handshake: Read "OK"
            String handshake = in.readUTF();
            if (!"OK".equals(handshake)) {
                throw new IOException("Handshake failed: " + handshake);
            }

            // Send chunk index request
            out.writeInt(chunkIndex);

            // Receive chunk length
            int chunkLen = in.readInt();
            byte[] chunk = new byte[chunkLen];

            // Receive chunk bytes
            in.readFully(chunk);

            // Verify hash
            String receivedHash = in.readUTF();
            List<String> computedHashes = FileChunker.hashChunks(List.of(chunk));
            if (!computedHashes.get(0).equals(expectedHash) || !receivedHash.equals(expectedHash)) {
                throw new IOException("Hash mismatch for chunk " + chunkIndex + "! Expected: " + expectedHash);
            }

            // Write to output file at offset
            raf.seek(offset);
            raf.write(chunk);
            System.out.println("Downloaded and verified chunk " + chunkIndex + " (" + chunkLen + " bytes)");
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java PeerHandler <mode> [args...]");
            System.out.println("Modes: server <filePath> <torrentPath> | client <peerHost> <chunkIndex> <expectedHash> <offset> <outputPath>");
            return;
        }

        String mode = args[0];
        if ("server".equals(mode)) {
            if (args.length != 3) {
                System.out.println("Server usage: java PeerHandler server <filePath> <torrentPath>");
                return;
            }
            String filePath = args[1];
            // Mock: Load hashes from torrent (in real: parse .torrent)
            List<byte[]> chunks = FileChunker.chunkFile(filePath);
            List<String> hashes = FileChunker.hashChunks(chunks);
            startServer(filePath, hashes);
        } else if ("client".equals(mode)) {
            if (args.length != 6) {
                System.out.println("Client usage: java PeerHandler client <peerHost> <chunkIndex> <expectedHash> <offset> <outputPath>");
                return;
            }
            String peerHost = args[1];
            int chunkIndex = Integer.parseInt(args[2]);
            String expectedHash = args[3];
            long offset = Long.parseLong(args[4]);
            String outputPath = args[5];
            downloadChunk(peerHost, chunkIndex, expectedHash, offset, outputPath);
        }
    }
}
