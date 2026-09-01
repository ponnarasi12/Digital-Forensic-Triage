# FORTRAC — AI-Powered Digital Forensic Triage & Evidence Correlation System

> **Unique Selling Proposition (USP):**
> *"Our system reduces forensic investigation time by automatically correlating heterogeneous digital artifacts, prioritizing evidence, reconstructing event timelines, and generating explainable investigation hypotheses."*

---

## 📌 Project Overview
FORTRAC is an enterprise-grade digital forensics and incident response (DFIR) triage platform designed in alignment with **NIST SP 800-86** (*Guide to Integrating Forensic Techniques into Incident Response*).

It ingests raw, heterogeneous digital artifacts (Windows Event Logs, Prefetch files, Web Browser download histories, USB connection logs, and Sysmon network connections), normalizes them into unified schemas, scores risk dynamically using explainable heuristic algorithms, reconstructs sequential event timelines, constructs correlation graphs, and presents an interactive executive dashboard.

---

## 🚀 Core Features

1. **AI Evidence Scoring Engine ($0–100$):** Normalizes composite risk tiers (**CRITICAL**, **SUSPICIOUS**, **INFO**, **BENIGN**).
2. **Explainable AI ("Explain Why"):** Full transparency for risk scores—lists exact triggered rules, location heuristics, time proximity windows, and graph node relationships.
3. **Incident Timeline Reconstruction:** Chronological ordering of multi-source events highlighting key breach paths.
4. **Evidence Correlation Graph:** Node/edge visualization displaying artifact extraction, execution, and exfiltration links (`EXTRACTED_FROM`, `LOADED_BY`, `CONNECTED_TO`, `TIME_CORRELATED`).
5. **AI Investigation Assistant:** Synthesizes top evidence into plain-English incident summaries, attack vectors, and response action plans.
6. **Automated NIST PDF Reporter:** Programmatically generates formal forensic investigation PDF reports.

---

## 🛠️ Technology Stack
- **Backend:** Java 17, Spring Boot 3.2.3, Spring Data JPA, Spring Web, Thymeleaf
- **Database:** H2 (In-Memory File Persistence Mode for instant execution) & MySQL 8.0 support
- **Utilities:** Apache Commons CSV, OpenPDF (NIST PDF generator)
- **Frontend:** Tailwind CSS (Dark Forensic Theme), Vis-Network.js (Graph Visualizer), Vanilla JS Modal Engine

---

## 🏃 How to Run

1. Open PowerShell in the project root directory (`c:\Users\DELL\Desktop\Digital Forensic Triage`).
2. Run the launcher script:
   ```powershell
   .\run_fortrac.ps1
   ```
3. Open your browser and navigate to:
   - **Dashboard:** [http://localhost:8080](http://localhost:8080)
   - **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:fortracdb`, User: `sa`, Password: empty)

---

## 📡 REST API Endpoints

- `GET /api/cases/1/dashboard-metrics` — Total evidence, critical, suspicious, timeline counts.
- `GET /api/cases/1/top-priority` — Top 5 prioritized high-risk evidence items.
- `GET /api/cases/1/timeline` — Chronological timeline event stream.
- `GET /api/cases/1/graph` — Graph nodes and edges JSON payload.
- `GET /api/artifacts/{id}/explain` — **"Explain Why"** payload with rule justifications and confidence score.
- `GET /api/cases/1/ai-summary` — Reconstructed attack vector and AI recommendations.
- `GET /api/cases/1/report` — Download formal NIST PDF investigation report.
