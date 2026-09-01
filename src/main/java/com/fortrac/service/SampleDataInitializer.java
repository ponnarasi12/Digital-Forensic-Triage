package com.fortrac.service;

import com.fortrac.model.*;
import com.fortrac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private EvidenceSourceRepository evidenceSourceRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private RiskFactorRepository riskFactorRepository;

    @Autowired
    private TimelineEventRepository timelineEventRepository;

    @Autowired
    private EvidenceLinkRepository evidenceLinkRepository;

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private AiAssistantService aiAssistantService;

    @Override
    public void run(String... args) throws Exception {
        if (caseRepository.count() > 0) return;

        // 1. Create Case #1042
        ForensicCase fCase = new ForensicCase("1042", "Ransomware & Exfiltration Incident", "Senior Investigator", "Suspicious workstation activity investigation");
        fCase = caseRepository.save(fCase);
        Long caseId = fCase.getId();

        // 2. Evidence Sources
        EvidenceSource srcUsb = evidenceSourceRepository.save(new EvidenceSource(caseId, "Windows Event Log (Setup/USB)", "EVTX", "sha256_usb_log_001"));
        EvidenceSource srcWeb = evidenceSourceRepository.save(new EvidenceSource(caseId, "Chrome History & Download DB", "BROWSER", "sha256_browser_002"));
        EvidenceSource srcPrefetch = evidenceSourceRepository.save(new EvidenceSource(caseId, "System Prefetch Artifacts", "PREFETCH", "sha256_prefetch_003"));
        EvidenceSource srcNet = evidenceSourceRepository.save(new EvidenceSource(caseId, "Sysmon Event ID 3 (Network)", "NETWORK", "sha256_network_004"));

        LocalDateTime baseTime = LocalDateTime.of(2026, 8, 22, 10, 12, 0);

        // 3. Core Evidence Items
        Artifact usbDev = new Artifact(caseId, srcUsb.getId(), "USB_DEVICE", "SanDisk Ultra USB 3.0", "E:\\ (Device Volume)", "a1b2c3d4e5f67890", baseTime);
        usbDev.setRiskScore(45);
        usbDev.setRiskLevel(RiskLevel.INFO);
        usbDev = artifactRepository.save(usbDev);

        Artifact zipFile = new Artifact(caseId, srcWeb.getId(), "DOWNLOAD", "confidential.zip", "C:\\Users\\Victim\\Downloads\\confidential.zip", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", baseTime.plusMinutes(5));
        zipFile = artifactRepository.save(zipFile);

        Artifact extractedDll = new Artifact(caseId, srcPrefetch.getId(), "FILE_EXTRACTION", "unknown.dll", "C:\\Users\\Victim\\AppData\\Local\\Temp\\unknown.dll", "94a8f692138bce11019284192849182491284918249182491824918249182491", baseTime.plusMinutes(6));
        extractedDll = artifactRepository.save(extractedDll);

        Artifact susExe = new Artifact(caseId, srcPrefetch.getId(), "PROCESS_EXEC", "suspicious.exe", "C:\\Users\\Victim\\AppData\\Local\\Temp\\suspicious.exe", "bad1042c0de94a8f692138bce1101928419284918249182491824918249182491", baseTime.plusMinutes(8));
        susExe = artifactRepository.save(susExe);

        Artifact netConn = new Artifact(caseId, srcNet.getId(), "NETWORK_CONN", "TCP 45.33.21.11:443", "45.33.21.11:443 (Outbound TLS)", "net_conn_c2_exfil_hash", baseTime.plusMinutes(9));
        netConn = artifactRepository.save(netConn);

        List<Artifact> coreArtifacts = List.of(usbDev, zipFile, extractedDll, susExe, netConn);

        // 4. Run Risk Scoring & Factor Generation
        for (Artifact a : coreArtifacts) {
            scoringService.evaluateAndScoreArtifact(a, coreArtifacts);
            artifactRepository.save(a);
        }

        // Adjust exact scores to match user dashboard requirements
        susExe.setRiskScore(94);
        susExe.setRiskLevel(RiskLevel.CRITICAL);
        susExe.setConfidencePct(91);
        artifactRepository.save(susExe);

        zipFile.setRiskScore(91);
        zipFile.setRiskLevel(RiskLevel.CRITICAL);
        zipFile.setConfidencePct(88);
        artifactRepository.save(zipFile);

        extractedDll.setRiskScore(78);
        extractedDll.setRiskLevel(RiskLevel.SUSPICIOUS);
        extractedDll.setConfidencePct(84);
        artifactRepository.save(extractedDll);

        // Seed Risk Factors for suspicious.exe "Explain Why"
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "QUICK_EXECUTION", "Executed shortly after browser download (2 mins)", 30));
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "TEMP_LOCATION", "Located in temporary directory (C:\\Users\\Victim\\AppData\\Local\\Temp)", 25));
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "UNKNOWN_HASH", "Unknown / unverified SHA-256 hash", 15));
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "C2_NETWORK_CONN", "Associated with suspicious network connection (192.168.1.105 -> 45.33.21.11:443)", 25));
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "INCIDENT_TIMING", "Created shortly before incident detection", 15));
        riskFactorRepository.save(new RiskFactor(susExe.getId(), "GRAPH_CORRELATION", "Connected to 3 other high-risk artifacts", 20));

        // 5. Seed Additional Background Artifacts to reach exact counts:
        // Critical: 17, Suspicious: 64, Total Evidence: 1,284
        List<Artifact> extraBatch = new ArrayList<>();
        // Add remaining 15 Critical items
        for (int i = 1; i <= 15; i++) {
            Artifact crit = new Artifact(caseId, srcPrefetch.getId(), "PROCESS_EXEC", "malware_payload_" + i + ".exe", "C:\\Windows\\Temp\\malware_" + i + ".exe", "hash_crit_" + i, baseTime.plusMinutes(i));
            crit.setRiskScore(82 + (i % 15));
            crit.setRiskLevel(RiskLevel.CRITICAL);
            extraBatch.add(crit);
        }
        // Add remaining 63 Suspicious items
        for (int i = 1; i <= 63; i++) {
            Artifact susp = new Artifact(caseId, srcWeb.getId(), "DOWNLOAD", "suspicious_script_" + i + ".ps1", "C:\\Users\\Victim\\Downloads\\script_" + i + ".ps1", "hash_susp_" + i, baseTime.plusMinutes(i % 30));
            susp.setRiskScore(55 + (i % 23));
            susp.setRiskLevel(RiskLevel.SUSPICIOUS);
            extraBatch.add(susp);
        }
        // Add Benign / Info items to make total 1,284
        int remainingBenign = 1284 - (5 + 15 + 63);
        for (int i = 1; i <= remainingBenign; i++) {
            Artifact benign = new Artifact(caseId, srcUsb.getId(), "FILE_META", "system_log_" + i + ".evtx", "C:\\Windows\\System32\\winevt\\Logs\\log_" + i + ".evtx", "hash_benign_" + i, baseTime.minusMinutes(i));
            benign.setRiskScore(5 + (i % 15));
            benign.setRiskLevel(RiskLevel.BENIGN);
            extraBatch.add(benign);
        }
        artifactRepository.saveAll(extraBatch);

        // 6. Timeline Events (Key Highlighted Stream)
        timelineEventRepository.save(new TimelineEvent(caseId, usbDev.getId(), baseTime, "USB Connected", "10:12 ─ USB Connected (SanDisk Ultra E:\\)", "INFO"));
        timelineEventRepository.save(new TimelineEvent(caseId, zipFile.getId(), baseTime.plusMinutes(5), "File Downloaded", "10:17 ─ File Downloaded (confidential.zip)", "HIGH"));
        timelineEventRepository.save(new TimelineEvent(caseId, extractedDll.getId(), baseTime.plusMinutes(6), "Archive Extracted", "10:18 ─ Archive Extracted (unknown.dll)", "HIGH"));
        timelineEventRepository.save(new TimelineEvent(caseId, susExe.getId(), baseTime.plusMinutes(8), "Suspicious Process", "10:20 ─ Suspicious Process (suspicious.exe)", "CRITICAL"));
        timelineEventRepository.save(new TimelineEvent(caseId, netConn.getId(), baseTime.plusMinutes(9), "External Connection", "10:21 ─ External Connection (45.33.21.11:443)", "CRITICAL"));

        // Add dummy timeline events to reach 327 timeline events count
        List<TimelineEvent> dummyTimeline = new ArrayList<>();
        for (int i = 6; i <= 327; i++) {
            dummyTimeline.add(new TimelineEvent(caseId, null, baseTime.plusSeconds(i * 10), "System Telemetry", "Event #" + i + " — Automated Windows Service Event", "LOW"));
        }
        timelineEventRepository.saveAll(dummyTimeline);

        // 7. Graph Correlation Edges
        evidenceLinkRepository.save(new EvidenceLink(caseId, usbDev.getId(), zipFile.getId(), "TIME_CORRELATED", 0.75));
        evidenceLinkRepository.save(new EvidenceLink(caseId, zipFile.getId(), extractedDll.getId(), "EXTRACTED_FROM", 0.95));
        evidenceLinkRepository.save(new EvidenceLink(caseId, extractedDll.getId(), susExe.getId(), "LOADED_BY", 0.90));
        evidenceLinkRepository.save(new EvidenceLink(caseId, susExe.getId(), netConn.getId(), "CONNECTED_TO", 0.92));

        // 8. Generate AI Summary
        aiAssistantService.generateCaseSummary(caseId);
    }
}
