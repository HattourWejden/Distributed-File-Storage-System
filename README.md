\# Distributed File Storage System (P2P BitTorrent Prototype)

\## Overview

This is a Java project implementing a simple distributed file storage system inspired by Google Drive, using the BitTorrent protocol. Users can upload files (chunked and hashed), generate .torrent metadata, share via P2P, and download/reassemble files. No database is used—everything relies on local files and peer-to-peer networking.

\*\*Key Features\*\*:

\* File chunking and SHA-256 hashing for integrity.

\* .torrent file generation with BEncode.

\* Basic P2P upload/download over TCP sockets.

\* Peer discovery via public trackers.

\* Command-line interface (CLI) for testing.

\*\*Assumptions\*\*:

\* Files <1GB, local network/public trackers for testing.

\* Simplified BitTorrent (no full DHT, multi-file support yet).

\## Tech Stack

\* \*\*Language\*\*: Java 17+ (core networking and security).

\* \*\*IDE\*\*: IntelliJ Community Edition (free, with Maven support).

\* \*\*Build Tool\*\*: Maven (for dependencies).

\* \*\*Libraries\*\*:

\* Core Java: \`java.io\`, \`java.net\`, \`java.security\`, \`java.util\`.

\* External: Apache Commons Lang 3.12.0 (string utilities).

\## Project Structure

\`\`\`

DistributedFileSystem/

├── pom.xml # Maven config

├── src/

│ └── com/wejden/distfs/ # Package

│ ├── FileChunker.java # Chunking & hashing

│ ├── TorrentGenerator.java # .torrent creation

│ ├── PeerHandler.java # P2P server/client

│ └── TrackerAnnouncer.java # Peer discovery

└── target/ # Compiled classes (auto-generated)

\`\`\`

\## Installation and Running the Project

\### Prerequisites

\- Java JDK 17+ (download from \[Adoptium\](https://adoptium.net/)).

\- IntelliJ IDEA Community Edition (free from \[JetBrains\](https://www.jetbrains.com/idea/)).

\- Git (for cloning).

\### Step 1: Clone the Repository

\`\`\`bash

git clone https://github.com/HattourWejden/Distributed-File-Storage-System.git

cd Distributed-File-Storage-System

\`\`\`

\### Step 2: Open in IntelliJ

1\. Launch IntelliJ.

2\. \*\*File > Open\*\* → Select the project folder → OK.

3\. IntelliJ detects Maven → Click \*\*Import Changes\*\* or \*\*Reload project\*\* (right-click pom.xml > Maven > Reload).

\### Step 3: Verify Setup

\- \*\*Build the Project\*\*: \*\*Build > Rebuild Project\*\* (no errors? Good!).

\- \*\*Run Hello World Test\*\*: Create a simple \`Main.java\` in \`src/com/wejden/distfs\`:

\`\`\`java

public class Main {

public static void main(String\[\] args) {

System.out.println("Project ready!");

}

}

\`\`\`

\- Right-click > Run 'Main.main()' → Console shows "Project ready!".

\### Step 4: Running the Project

Use the CLI (\`DistFS.main()\`) for workflows. Right-click \`DistFS.java\` > Run.

1\. \*\*Upload (Create Torrent)\*\*:

\- Run DistFS → Choose 1.

\- Inputs: File path (e.g., \`C:\\Users\\ASUS\\Desktop\\test.txt\`), Output torrent (e.g., \`test.torrent\`), Tracker (e.g., \`http://tracker.renfei.net:8080/announce\`).

\- Output: .torrent file generated.

2\. \*\*Serve (Start P2P Server)\*\*:

\- Choose 2.

\- Inputs: File path, Torrent path.

\- Output: "Server listening on port 6881..." (leave running).

3\. \*\*Download\*\*:

\- Choose 3 (new run if server active).

\- Inputs: Torrent path, Output file (e.g., \`downloaded.txt\`), Tracker.

\- Output: Announces to tracker, downloads from peers (fallback localhost), saves file.

\*\*Example Full Flow\*\*:

\- Upload test.txt → Get test.torrent.

\- Serve test.txt.

\- Download test.torrent → downloaded.txt matches original.

\*\*Troubleshooting\*\*:

\- "File not found": Use full paths (e.g., \`C:\\path\\to\\file\`).

\- Port busy: Change PORT=6882 in PeerHandler.java.

\- Tracker timeout: Use alternative (e.g., \`https://tracker.tamersunion.org:443/announce\`).

\## How It Works

1\. \*\*Upload Preparation\*\*:

\* Input: File (e.g., test.txt).

\* \`FileChunker\`: Splits into 256KB chunks, computes SHA-256 hashes.

\* \`TorrentGenerator\`: Builds BEncode .torrent (file length, concatenated hashes, piece size, tracker URL).

2\. \*\*P2P Sharing\*\*:

\* Server (\`PeerHandler server \`): Loads chunks/hashes, listens on port 6881. Accepts connections, sends requested chunks + hashes.

\* Client (\`PeerHandler client

\`): Connects, requests chunk, verifies hash, writes to file at offset.

3\. \*\*Peer Discovery\*\*:

\* \`TrackerAnnouncer \`: Extracts info\_hash (SHA-1 of info dict), announces via HTTP/UDP. Parses response for peer IPs:ports (fallback: localhost).

\*\*Example\*\*:

\* Create test.txt ("Hello world!").

\* Run FileChunker → Get hashes.

\* Run TorrentGenerator → test.torrent.

\* Run server on test.txt.

\* Run client to download → downloaded.txt matches original.

\## Running Tests

\* \*\*Chunking\*\*: Run FileChunker.main() with arg \`\` → Prints chunks/hashes.

\* \*\*Torrent Gen\*\*: Run TorrentGenerator.main() with args \` \`.

\* \*\*P2P\*\*: Run server, then client (use hash from chunker).

\* \*\*Discovery\*\*: Run TrackerAnnouncer with args above → Prints peers (may be empty).
