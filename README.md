Perfect 😎 — here’s your **fully enhanced GitHub-style README.md** version with badges, emojis, collapsible sections, and a clean, professional layout.
Everything is still Markdown-compliant, so you can copy it directly into your GitHub project.

---

````markdown
# 🌐 Distributed File Storage System (P2P BitTorrent Prototype)

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![License](https://img.shields.io/badge/License-MIT-blue)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![Status](https://img.shields.io/badge/Status-Prototype-green)

> A lightweight **distributed file storage system** inspired by Google Drive and BitTorrent — implemented entirely in **Java** using **peer-to-peer networking**.

---

## 📖 Overview

This project demonstrates how distributed file sharing works through **chunking**, **hashing**, **metadata generation**, and **P2P transfer**.  
Users can upload files, generate `.torrent` metadata, and share/download them over a local or public network — no central database required.

---

## ✨ Key Features

✅ File chunking with **SHA-256 hashing** for data integrity  
✅ `.torrent` metadata generation using **BEncode**  
✅ Peer-to-peer upload/download via **TCP sockets**  
✅ Basic **peer discovery** through trackers  
✅ Simple **command-line interface (CLI)** for testing

---

## ⚙️ Assumptions

- Files smaller than **1GB**
- Uses **local network** or public trackers for discovery
- Simplified BitTorrent model (no DHT or multi-file torrents yet)

---

## 🧠 Tech Stack

| Category | Technologies |
|-----------|---------------|
| **Language** | Java 17+ |
| **IDE** | IntelliJ IDEA Community Edition |
| **Build Tool** | Maven |
| **Core Libraries** | `java.io`, `java.net`, `java.security`, `java.util` |
| **External Libraries** | Apache Commons Lang `3.12.0` |

---

<details>
<summary>🗂️ <b>Project Structure</b></summary>

```text
DistributedFileSystem/
├── pom.xml                          # Maven configuration  
├── src/
│   └── com/wejden/distfs/           # Package
│       ├── FileChunker.java         # Chunking & hashing
│       ├── TorrentGenerator.java    # .torrent creation
│       ├── PeerHandler.java         # P2P server/client logic
│       └── TrackerAnnouncer.java    # Peer discovery
└── target/                          # Compiled classes (auto-generated)
````

</details>

---

## ⚡ How It Works

### 🧩 1. Upload Preparation

* Input file (e.g., `test.txt`)
* `FileChunker`: Splits file into 256KB chunks and computes SHA-256 hashes
* `TorrentGenerator`: Builds `.torrent` metadata using BEncode (file size, hashes, piece size, tracker URL)

### 🔄 2. P2P Sharing

* **Server (PeerHandler - server mode)**
  Loads chunks/hashes, listens on port `6881`, sends requested pieces
* **Client (PeerHandler - client mode)**
  Connects, requests chunk, verifies hash, and writes to disk in correct order

### 🌍 3. Peer Discovery

* `TrackerAnnouncer`:
  Extracts `info_hash` (SHA-1 of the info dictionary)
  Announces via HTTP/UDP → receives list of peers (IP:Port)
  *(Fallback to localhost for testing)*

---

## 🧪 Example Workflow

```bash
# 1️⃣ Create a test file
echo "Hello world!" > test.txt

# 2️⃣ Generate chunks and hashes
java com.wejden.distfs.FileChunker test.txt

# 3️⃣ Generate .torrent file
java com.wejden.distfs.TorrentGenerator test.txt test.torrent

# 4️⃣ Start server (Seeder)
java com.wejden.distfs.PeerHandler server test.txt

# 5️⃣ Start client (Leecher)
java com.wejden.distfs.PeerHandler client test.torrent
```

✅ The output file `downloaded.txt` should match the original `test.txt`.

---

## 🧾 Running Tests

| Test Type             | Description                                       | How to Run                      |
| --------------------- | ------------------------------------------------- | ------------------------------- |
| 🧱 Chunking           | Splits file into pieces and prints SHA-256 hashes | `FileChunker.main()`            |
| 🧩 Torrent Generation | Builds and prints `.torrent` metadata             | `TorrentGenerator.main()`       |
| 🔗 P2P Transfer       | Transfers file between peers over sockets         | Run `PeerHandler` in both modes |
| 🌐 Tracker Discovery  | Announces and retrieves peer list                 | `TrackerAnnouncer.main()`       |

---

<details>
<summary>🚀 <b>Future Improvements</b></summary>

* [ ] Support **multi-file torrents**
* [ ] Implement full **DHT peer discovery**
* [ ] Add **pause/resume** downloads
* [ ] Build **GUI interface** for non-technical users
* [ ] Add **encryption** and secure authentication

</details>

---

## 🧰 Requirements

* **Java JDK 17+**
* **Maven 3.8+**
* Internet connection (for tracker communication)
* Optional: **IntelliJ IDEA** or **VS Code** for running tests

---

## 💡 Inspiration

This project is inspired by the design of **BitTorrent** and **Google Drive**, demonstrating distributed data storage, integrity verification, and decentralized sharing.



