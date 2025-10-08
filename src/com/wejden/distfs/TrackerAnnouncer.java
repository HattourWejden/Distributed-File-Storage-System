package com.wejden.distfs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class TrackerAnnouncer {
    public static void announce(String torrentPath, String trackerUrl, int localPort, long uploaded, long downloaded, long left) throws Exception {
        // Step 1: Parse .torrent to get info_hash (SHA-1 of 'info' dict)
        String infoDict = extractInfoDict(torrentPath);
        byte[] infoHashBytes = MessageDigest.getInstance("SHA-1").digest(infoDict.getBytes(StandardCharsets.UTF_8));
        String infoHash = Base64.getUrlEncoder().withoutPadding().encodeToString(infoHashBytes).replace("+", "%2B").replace("/", "%2F");

        // Step 2: Build announce URL (HTTP GET params)
        String params = "info_hash=" + URLEncoder.encode(infoHash, StandardCharsets.UTF_8.name()) +
                "&peer_id=-DF0001-" + generatePeerId() +
                "&port=" + localPort +
                "&uploaded=" + uploaded +
                "&downloaded=" + downloaded +
                "&left=" + left +
                "&compact=1&no_peer_id=1&event=started";
        URL url = new URL(trackerUrl + "?" + params);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        // Step 3: Read response (BEncoded peers)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Tracker response: " + response);
            // Mock parse: Assume compact peers (6 bytes each: IP + port)
            List<String> peers = parseCompactPeers(response.toString());
            System.out.println("Discovered peers: " + peers);
        } catch (Exception e) {
            System.err.println("Tracker error: " + e.getMessage());
            // Fallback: Mock local peer for testing
            System.out.println("Fallback peers: [localhost:6881]");
        }
    }

    // Simple .torrent parser: Extract 'info' dict as string (for hash)
    private static String extractInfoDict(String torrentPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(torrentPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            String bencode = new String(buffer, 0, bytesRead, StandardCharsets.ISO_8859_1);
            // Find 'd4:infod...' to 'ee' (simplified—assumes small torrent)
            int infoStart = bencode.indexOf("4:infod");
            if (infoStart == -1) throw new IOException("Invalid torrent");
            infoStart += 8; // Skip to after 'd'
            int infoEnd = bencode.indexOf("ee", infoStart);
            if (infoEnd == -1) throw new IOException("Invalid info dict");
            return bencode.substring(infoStart, infoEnd);
        }
    }

    // Generate random peer_id (BitTorrent standard: -Producer-0001- random)
    private static String generatePeerId() {
        return String.format("%04x%04x%04x", (int)(Math.random()*65536), (int)(Math.random()*65536), (int)(Math.random()*65536));
    }

    // Mock compact peers parse (real: BDecode full response)
    private static List<String> parseCompactPeers(String response) {
        // Simplified: Look for peer bytes after '6:peers' + length + :
        int peersStart = response.indexOf("6:peers");
        if (peersStart == -1) return List.of();
        peersStart += 8; // Skip key + length
        int colon = response.indexOf(":", peersStart);
        if (colon == -1) return List.of();
        String peerData = response.substring(colon + 1);
        // Assume even length, 6 bytes per peer (4 IP + 2 port)
        List<String> peers = new java.util.ArrayList<>();
        for (int i = 0; i < peerData.length(); i += 6) {
            if (i + 6 > peerData.length()) break;
            String ip = peerData.substring(i, i+4); // Mock IP
            int port = Integer.parseInt(peerData.substring(i+4, i+6), 16); // Mock port
            peers.add("192.168.1." + i + ":" + port); // Fake IPs for demo
        }
        return peers;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java TrackerAnnouncer <torrentPath> <trackerUrl> [port=6881 uploaded=0 downloaded=0 left=41]");
            return;
        }
        String torrentPath = args[0];
        String trackerUrl = args[1];
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 6881;
        long uploaded = args.length > 3 ? Long.parseLong(args[3]) : 0;
        long downloaded = args.length > 4 ? Long.parseLong(args[4]) : 0;
        long left = args.length > 5 ? Long.parseLong(args[5]) : 41; // Default small file
        announce(torrentPath, trackerUrl, port, uploaded, downloaded, left);
    }
}