package com.wejden.distfs;

import java.io.File;
import java.util.*;

public class DistFS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Distributed File System CLI ===");
        System.out.println("1: Upload (create chunks + torrent)");
        System.out.println("2: Serve (start P2P server)");
        System.out.println("3: Download (announce + pull from peers)");
        System.out.print("Choose (1-3): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            switch (choice) {
                case 1: // Upload
                    System.out.print("File path: ");
                    String uploadFile = scanner.nextLine();
                    System.out.print("Output torrent: ");
                    String torrentOut = scanner.nextLine();
                    System.out.print("Tracker URL: ");
                    String tracker = scanner.nextLine();
                    File file = new File(uploadFile);
                    if (!file.exists()) {
                        System.out.println("Error: File not found!");
                        break;
                    }
                    List<byte[]> chunks = FileChunker.chunkFile(uploadFile);
                    List<String> hashes = FileChunker.hashChunks(chunks);
                    TorrentGenerator.generateTorrent(uploadFile, torrentOut, hashes, file.length(), tracker);
                    System.out.println("Upload done! Torrent: " + torrentOut + " (Chunks: " + chunks.size() + ")");
                    break;
                case 2: // Serve
                    System.out.print("File path: ");
                    String serveFile = scanner.nextLine();
                    System.out.print("Torrent path (for hashes): ");
                    String serveTorrent = scanner.nextLine();
                    // Load hashes from torrent (simplified—re-chunk for now)
                    List<byte[]> serveChunks = FileChunker.chunkFile(serveFile);
                    List<String> serveHashes = FileChunker.hashChunks(serveChunks);
                    PeerHandler.startServer(serveFile, serveHashes);
                    break;
                case 3: // Download
                    System.out.print("Torrent path: ");
                    String downloadTorrent = scanner.nextLine();
                    System.out.print("Output file: ");
                    String outputFile = scanner.nextLine();
                    System.out.print("Tracker URL: ");
                    String downloadTracker = scanner.nextLine();
                    // Announce for peers
                    TrackerAnnouncer.announce(downloadTorrent, downloadTracker, 6881, 0, 0, new File(downloadTorrent).length()); // left = file size from torrent later
                    // Mock: Use fallback peer (localhost) for now; loop real peers later
                    System.out.println("Downloading from fallback peer (localhost)...");
                    // Load hashes from torrent (simplified—assume 1 chunk for test)
                    List<byte[]> dummyChunks = new ArrayList<>(); // Placeholder
                    List<String> downloadHashes = FileChunker.hashChunks(dummyChunks); // Real: parse torrent
                    // For test: Assume chunk 0, hash from FileChunker on original
                    String testHash = "d1123be2e9d260c904c6c1ef6c021d622863743e97e680125e3beb840459d4c8"; // Replace with real parse
                    PeerHandler.downloadChunk("localhost", 0, testHash, 0, outputFile);
                    System.out.println("Download done! File: " + outputFile);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}