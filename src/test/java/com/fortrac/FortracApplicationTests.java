package com.fortrac;

import com.fortrac.model.Artifact;
import com.fortrac.model.ForensicCase;
import com.fortrac.model.RiskLevel;
import com.fortrac.repository.ArtifactRepository;
import com.fortrac.repository.CaseRepository;
import com.fortrac.service.ExplainabilityService;
import com.fortrac.service.ScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FortracApplicationTests {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private ExplainabilityService explainabilityService;

    @Test
    void contextLoads() {
        assertNotNull(caseRepository);
        assertNotNull(artifactRepository);
    }

    @Test
    void testScoringEngineRuleTrigger() {
        Artifact susExe = new Artifact(1L, 1L, "PROCESS_EXEC", "suspicious.exe", "C:\\Users\\Victim\\AppData\\Local\\Temp\\suspicious.exe", "bad1042c0de94a8f", LocalDateTime.now());
        Artifact downloadZip = new Artifact(1L, 1L, "DOWNLOAD", "confidential.zip", "C:\\Users\\Victim\\Downloads\\confidential.zip", "e3b0c44298fc1c14", LocalDateTime.now().minusMinutes(2));

        scoringService.evaluateAndScoreArtifact(susExe, List.of(downloadZip, susExe));

        assertTrue(susExe.getRiskScore() >= 50, "Risk score should be >= 50 due to temp dir and quick execution");
        assertEquals(RiskLevel.CRITICAL, susExe.getRiskLevel());
    }

    @Test
    void testExplainabilityPayloadGeneration() {
        List<Artifact> topList = artifactRepository.findByCaseIdOrderByRiskScoreDesc(1L);
        if (!topList.isEmpty()) {
            Artifact top = topList.get(0);
            Map<String, Object> payload = explainabilityService.getExplainabilityPayload(top.getId());
            assertNotNull(payload);
            assertEquals(top.getId(), payload.get("artifactId"));
            assertTrue(payload.containsKey("reasons"));
            assertTrue(payload.containsKey("confidencePct"));
        }
    }
}
