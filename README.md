Distributed File Storage System (P2P BitTorrent Prototype)
----------------------------------------------------------

### Overview

This is a Java project implementing a simple distributed file storage system inspired by Google Drive, using the BitTorrent protocol. Users can upload files (chunked and hashed), generate .torrent metadata, share via P2P, and download/reassemble files. No database is used—everything relies on local files and peer-to-peer networking.

**Key Features**:

*   File chunking and SHA-256 hashing for integrity.

*   .torrent file generation with BEncode.

*   Basic P2P upload/download over TCP sockets.

*   Peer discovery via public trackers.

*   Command-line interface (CLI) for testing.


**Assumptions**:

*   Files <1GB, local network/public trackers for testing.

*   Simplified BitTorrent (no full DHT, multi-file support yet).


### Tech Stack

*   **Language**: Java 17+ (core networking and security).

*   **IDE**: IntelliJ Community Edition (free, with Maven support).

*   **Build Tool**: Maven (for dependencies).

*   **Libraries**:

    *   Core Java: java.io, java.net, java.security, java.util.

    *   External: Apache Commons Lang 3.12.0 (string utilities).


### Project Structure

text

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   DistributedFileSystem/  ├── pom.xml                  # Maven config  ├── src/  │   └── com/wejden/distfs/   # Package  │       ├── FileChunker.java # Chunking & hashing  │       ├── TorrentGenerator.java #.torrent creation  │       ├── PeerHandler.java # P2P server/client  │       └── TrackerAnnouncer.java #Peer discovery  └── target/                  # Compiled classes (auto-generated)   `

### How It Works

1.  **Upload Preparation**:

    *   Input: File (e.g., test.txt).

    *   FileChunker: Splits into 256KB chunks, computes SHA-256 hashes.

    *   TorrentGenerator: Builds BEncode .torrent (file length, concatenated hashes, piece size, tracker URL).

2.  **P2P Sharing**:

    *   Server (PeerHandler server ): Loads chunks/hashes, listens on port 6881. Accepts connections, sends requested chunks + hashes.

    *   Client (PeerHandler client

        ): Connects, requests chunk, verifies hash, writes to file at offset.

3.  **Peer Discovery**:

    *   TrackerAnnouncer : Extracts info\_hash (SHA-1 of info dict), announces via HTTP/UDP. Parses response for peer IPs:ports (fallback: localhost).


**Example**:

*   Create test.txt ("Hello world!").

*   Run FileChunker → Get hashes.

*   Run TorrentGenerator → test.torrent.

*   Run server on test.txt.

*   Run client to download → downloaded.txt matches original.


### Running Tests

*   **Chunking**: Run FileChunker.main() with arg → Prints chunks/hashes.

*   **Torrent Gen**: Run TorrentGenerator.main() with args .

*   **P2P**: Run server, then client (use hash from chunker).

*   **Discovery**: Run TrackerAnnouncer with args above → Prints peers (may be empty).