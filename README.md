# SSH Protector

A lightweight real-time SSH monitoring and threat detection tool built in Java.

SSH Protector continuously watches Linux authentication logs and detects suspicious SSH login activity such as brute-force attempts, failed login bursts, and unauthorized access patterns.

Built with a backend-first and event-driven design, the project focuses on real-time monitoring, extensibility, and lightweight security observability.

---

# 🚀 Why I Built This

This project was inspired by a real incident where one of our servers was compromised through repeated brute-force SSH login attempts because port 22 was publicly exposed.

While investigating the issue, I realized:
- brute-force attacks on SSH are extremely common,
- many systems expose SSH publicly,
- and lightweight real-time monitoring is often missing in smaller setups.

That motivated me to build **SSH Protector** — a tool focused on detecting suspicious SSH activity in real time and helping identify attacks before they become serious incidents.

The goal was to create something lightweight, extensible, and backend-focused while exploring:
- real-time log streaming,
- multithreading,
- event-driven systems,
- Linux internals,
- and security-focused backend engineering.

---

# ✨ Features

- Real-time SSH log monitoring
- Supports multiple log sources
- Detects:
  - Failed login attempts
  - Repeated authentication failures
  - Potential brute-force attacks
- Event-driven architecture
- Subscriber-based alert system
- Configurable through:
  - configuration files
  - command-line arguments
- Lightweight and extensible design
- Built with Java 17

---

# 🛠️ Tech Stack

- Java 17
- Multithreading
- Observer Pattern
- ProcessBuilder
- Linux journald
- File Watchers
- Maven

---

# 🧠 Architecture Overview

```text
Linux Logs / journald / Custom Sources
                  ↓
             LogWatcher
                  ↓
           Event Detection
                  ↓
            Subscribers
                  ↓
   Console / Alerts / Integrations
```

---

# 📌 Current Capabilities

## Log Sources

SSH Protector is designed to support multiple log sources and is not limited to only `auth.log`.

Currently supported:
- `/var/log/auth.log`
- `journald` (`journalctl -u ssh`)
- Custom log file paths
- Live log streaming

The architecture is extensible, making it easy to plug in additional log sources in the future.

---

## Detection

SSH Protector can currently identify:
- Failed password attempts
- Invalid users
- Repeated IP failures
- Suspicious authentication activity
- Potential brute-force behavior

---

## Configuration

SSH Protector supports flexible configuration through:
- Configuration files
- Command-line arguments

This allows customization of:
- log sources
- monitoring behavior
- detection thresholds
- subscriber settings

---

## Subscriber System

Currently supports:
- Console subscribers

Planned:
- Email alerts
- Slack/Discord integration
- Web dashboard
- IP blocking automation

---

# 🔍 Example Logs Detected

```bash
Failed password for invalid user admin from 192.168.1.10
Failed password for root from 10.0.0.5
Accepted password for shivam from localhost
```

---

# 🔒 Real World Use Case

SSH brute-force attacks are one of the most common attack vectors against publicly exposed Linux servers.

SSH Protector helps identify:
- repeated failed authentication attempts,
- suspicious login patterns,
- and unauthorized access activity

in real time before they escalate into serious security incidents.

---

# ⚙️ Running the Project

## Clone the repository

```bash
git clone https://github.com/Shivam-Jagtap/ssh-protector.git
```

## Build the project

```bash
mvn clean install
```

## Run

```bash
java -jar target/ssh-protector.jar
```

---

# 📚 What I Learned

This project helped me gain hands-on understanding of:
- Real-time log processing
- Linux authentication systems
- Java concurrency
- Observer pattern implementation
- Event-driven backend systems
- Streaming architecture concepts
- Security-focused backend engineering

---

# 🌱 Open Source & Contributions

SSH Protector is fully open source and licensed under the **Apache License 2.0**.

Contributions, suggestions, and improvements are always welcome.

If you're interested in:
- backend systems,
- Linux monitoring,
- security tooling,
- distributed systems,
- or event-driven architecture,

feel free to contribute.

You can:
- Open issues
- Suggest improvements
- Add new integrations
- Improve detection logic
- Enhance performance
- Add dashboards/alerts

---

# 📄 License

Licensed under the Apache License 2.0.

See the `LICENSE` file for more details.

---

# 🚧 Future Improvements

- Web dashboard
- Persistent event storage
- Alert throttling
- Geo-IP analysis
- ML-based anomaly detection
- Kubernetes deployment
- Docker support

---

# 👨‍💻 Author

**Shivam Jagtap**  
Backend Engineer | Java | Spring Boot | Distributed Systems

GitHub: https://github.com/Shivam-Jagtap  
Email: shivamjagtap42@gmail.com 
